package uk.ac.ed.inf.pepa.largescale;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.expressions.DivisionExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;
import uk.ac.ed.inf.pepa.largescale.expressions.ExpressionVisitor;
import uk.ac.ed.inf.pepa.largescale.expressions.MinimumExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.MultiplicationExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.RateExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.SubtractionExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.SummationExpression;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class MatlabConverter {

	private static final boolean REDUCE_PROBLEM = false;

	public static void main(String[] args)
			throws DifferentialAnalysisException, InterruptedException,
			IOException {
		String modelText = PepaTools.readText(args[0]);
		ModelNode model = (ModelNode) PepaTools.parse(modelText);
		IParametricDerivationGraph graph = ParametricDerivationGraphBuilder
				.createDerivationGraph(model, null);
		MatlabConverter mc = new MatlabConverter(graph);
		String matlab = mc.getMatlabModel();
		//System.out.println(matlab);
		PrintWriter w = new PrintWriter(new FileWriter("/Users/Mirco/Desktop/" + args[1] + ".m"));
		w.print(matlab);
		w.close();
	}

	private IParametricDerivationGraph graph;

	private ArrayList<Coordinate[]> list;

	private StringBuffer model = new StringBuffer();

	public MatlabConverter(IParametricDerivationGraph graph)
			throws DifferentialAnalysisException {
		if (graph == null)
			throw new NullPointerException();
		this.graph = graph;
		// coordinates for each sequential component
		list = getCoordinatesPerComponent();
		int problemSize = graph.getInitialState().length;
		// overall expression for each element
		Expression[] expression = new Expression[problemSize];
		for (int i = 0; i < expression.length; i++)
			expression[i] = new RateExpression(0);
		// main cycle going through all generating functions
		for (IGeneratingFunction function : graph.getGeneratingFunctions()) {
			short[] jump = function.getJump();
			for (int i = 0; i < jump.length; i++) {
				if (jump[i] == -1)
					expression[i] = new SubtractionExpression(expression[i],
							function.getRate());
				else if (jump[i] == +1)
					expression[i] = new SummationExpression(expression[i],
							function.getRate());
			}
		}
		if (REDUCE_PROBLEM) {
			// one equation per sequential component is redundant
			int reducedProblemSize = problemSize - list.size();
			double[] initialState = new double[reducedProblemSize];
			Expression[] reducedExpression = new Expression[reducedProblemSize];
			for (int i = 0, k = 0, z = 0; i < list.size(); i++) {
				Coordinate[] coordinates = list.get(i);
				for (int j = 1; j < coordinates.length; j++) {
					// System.err.printf("k: %d, z: %d\n", k,z);
					reducedExpression[k] = expression[z];
					initialState[k] = graph.getInitialState()[z];
					k++;
					z++;
				}
				z++;
			}
			createModel(initialState, reducedExpression);
		} else {
			createModel(graph.getInitialState(), expression);
		}

	}

	private void createModel(double[] initialState, Expression[] expressions)
			throws DifferentialAnalysisException {
		int[][] jacobianHint = new int[initialState.length][initialState.length];
		for (int i = 0; i < initialState.length; i++)
			for (int j = 0; j < initialState.length; j++)
				jacobianHint[i][j] = 0;
		// initial state
		model.append("x0 = [");
		for (double d : initialState)
			model.append(d + " ");
		model.append("]';\n");
		// vector field
		model.append("dx = zeros(" + initialState.length + ",1);\n");
		for (int i = 0; i < expressions.length; i++) {
			PrintVisitor v = new PrintVisitor();
			expressions[i].accept(v);
			model.append(new Formatter().format("dx(%d) = %s;\n", i + 1, v
					.getString()));
		}

	}

	private int getNewIndex(int oldIndex) {
		if (!REDUCE_PROBLEM)
			return oldIndex;
		int diff = 0;
		for (int i = 0; i < list.size(); i++, diff++) {
			Coordinate[] coordinates = this.list.get(i);
			for (Coordinate c : coordinates)
				if (c.getCoordinate() == oldIndex)
					return oldIndex - diff;
		}
		throw new IllegalStateException();
	}

	private boolean removedFromList(int coordinate) {
		if (!REDUCE_PROBLEM)
			return false;
		int current = 0;
		for (int i = 0; i < list.size(); i++) {
			current += this.list.get(i).length;
			if (coordinate == current - 1)
				return true;
		}
		return false;
	}

	private int[] oldIndicesOfRemoved(int coordinate) {
		if (!REDUCE_PROBLEM)
			throw new IllegalStateException();
		IntegerArray indices = new IntegerArray(10);
		int current = 0;
		for (int i = 0; i < list.size(); i++) {
			current += this.list.get(i).length;
			if (coordinate == current - 1) {
				Coordinate[] coordinates = list.get(i);
				for (Coordinate c : coordinates)
					if (coordinate != c.getCoordinate())
						indices.add(c.getCoordinate());
				return indices.toArray();
			}
		}
		throw new IllegalStateException("Coordinate not removed");
	}

	private int populationLevelOfRemovedCoordinate(int coordinate) {
		if (!REDUCE_PROBLEM)
			throw new IllegalStateException();
		int current = 0;
		for (int i = 0; i < list.size(); i++) {
			current += this.list.get(i).length;
			if (coordinate == current - 1) {
				return graph.getSequentialComponents()[i]
						.getInitialPopulationLevel();
			}
		}
		throw new IllegalStateException("Coordinate not removed");

	}

	private class PrintVisitor implements ExpressionVisitor {

		private StringBuffer buf = new StringBuffer();

		public String getString() {
			return buf.toString();
		}

		public void visitCoordinate(Coordinate coordinate)
				throws DifferentialAnalysisException {
			int oldIndex = coordinate.getCoordinate();
			int BASE = 1;
			if (!removedFromList(oldIndex)) {
				buf.append("x(" + (getNewIndex(oldIndex) + BASE) + ")");
			} else {
				// sum of all other components
				buf.append("(");
				buf.append(populationLevelOfRemovedCoordinate(oldIndex));
				int[] indices = oldIndicesOfRemoved(oldIndex);
				for (int i : indices)
					buf.append(" - x(" + (getNewIndex(i) + BASE) + ")");
				buf.append(")");
			}
		}

		public void visitDivisionExpression(DivisionExpression div)
				throws DifferentialAnalysisException {
			PrintVisitor lhs = new PrintVisitor();
			PrintVisitor rhs = new PrintVisitor();
			div.getLhs().accept(lhs);
			div.getRhs().accept(rhs);
			buf.append("div(" + lhs.getString() + "," + rhs.getString() + ")");
		}

		public void visitMinimumExpression(MinimumExpression min)
				throws DifferentialAnalysisException {
			PrintVisitor lhs = new PrintVisitor();
			PrintVisitor rhs = new PrintVisitor();
			min.getLhs().accept(lhs);
			min.getRhs().accept(rhs);
			buf.append("min(" + lhs.getString() + "," + rhs.getString() + ")");
		}

		public void visitMultiplicationExpression(MultiplicationExpression mult)
				throws DifferentialAnalysisException {
			PrintVisitor lhs = new PrintVisitor();
			PrintVisitor rhs = new PrintVisitor();
			mult.getLhs().accept(lhs);
			mult.getRhs().accept(rhs);
			buf.append(lhs.getString() + "*" + rhs.getString());
		}

		public void visitRateExpression(RateExpression rate)
				throws DifferentialAnalysisException {
			buf.append(rate.getRate());
		}

		public void visitSubtractionExpression(
				SubtractionExpression subtractionExpression)
				throws DifferentialAnalysisException {
			PrintVisitor lhs = new PrintVisitor();
			PrintVisitor rhs = new PrintVisitor();
			subtractionExpression.getLhs().accept(lhs);
			subtractionExpression.getRhs().accept(rhs);
			buf.append(lhs.getString() + "-" + rhs.getString());
		}

		public void visitSummationExpression(SummationExpression sum)
				throws DifferentialAnalysisException {
			PrintVisitor lhs = new PrintVisitor();
			PrintVisitor rhs = new PrintVisitor();
			sum.getLhs().accept(lhs);
			sum.getRhs().accept(rhs);
			buf.append(lhs.getString() + "+" + rhs.getString());
		}

	}

	private ArrayList<Coordinate[]> getCoordinatesPerComponent() {
		ISequentialComponent[] sequentialComponents = graph
				.getSequentialComponents();
		ArrayList<Coordinate[]> list = new ArrayList<Coordinate[]>(
				sequentialComponents.length);
		for (int i = 0; i < sequentialComponents.length; i++) {
			ISequentialComponent component = sequentialComponents[i];
			Coordinate[] coordinates = new Coordinate[component
					.getComponentMapping().size()];
			int c = 0;
			for (Entry<Short, Coordinate> entry : component
					.getComponentMapping()) {
				coordinates[c++] = entry.getValue();
			}
			list.add(coordinates);
		}
		return list;
	}

	public String getMatlabModel() {
		return model.toString();
	}

}
