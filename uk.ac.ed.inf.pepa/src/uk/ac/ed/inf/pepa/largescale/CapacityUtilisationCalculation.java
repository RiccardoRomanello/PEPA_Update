package uk.ac.ed.inf.pepa.largescale;

import java.util.Map;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.expressions.EvaluatorVisitor;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class CapacityUtilisationCalculation implements IPointEstimator {

	private ISequentialComponent component;

	private IParametricDerivationGraph graph;

	private int[] indices;

	public CapacityUtilisationCalculation(int componentIndex,
			IParametricDerivationGraph graph) {
		int maxIndex = graph.getSequentialComponents().length - 1;
		if (componentIndex > maxIndex)
			throw new IllegalArgumentException(
					"Bad component index. Maximum is " + maxIndex);
		this.component = graph.getSequentialComponents()[componentIndex];
		this.graph = graph;
		IntegerArray involvedIndices = new IntegerArray(
				graph.getInitialState().length);
		for (Map.Entry<Short, Coordinate> entry : component
				.getComponentMapping())
			involvedIndices.add(entry.getValue().getCoordinate());
		this.indices = involvedIndices.toArray();
	}


	public double computeEstimate(double timePoint, double[] solution)
			throws DifferentialAnalysisException {
		double currentUtilisation = 0;
		for (IGeneratingFunction f : graph.getGeneratingFunctions()) {
			if (isLocalJump(indices, f.getJump())) {
				currentUtilisation += new EvaluatorVisitor(f.getRate(),
						solution).getResult();
			}
		}
		double denominator = 0;
		for (short s : this.component.getActionAlphabet()) {
			denominator += new EvaluatorVisitor(component.getApparentRate(s),
					solution).getResult();
		}
		return (currentUtilisation / denominator);
	}

	static boolean isLocalJump(int[] indices, short[] jump) {
		for (int i : indices)
			if (jump[i] == -1)
				return true;
		return false;
	}

	static boolean isIncompingJump(int[] indices, short[] jump) {
		for (int i : indices)
			if (jump[i] == 1)
				return true;
		return false;
	}

}
