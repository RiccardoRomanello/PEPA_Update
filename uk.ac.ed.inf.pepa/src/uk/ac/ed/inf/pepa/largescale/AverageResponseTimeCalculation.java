package uk.ac.ed.inf.pepa.largescale;

import java.util.Arrays;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.expressions.EvaluatorVisitor;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class AverageResponseTimeCalculation {

	private IParametricDerivationGraph derivationGraph;

	private int[] inSystemIndices;

	private int[] outSystemIndices;

	/**
	 * Calculates the average response time for a sequential component. It is
	 * responsibility of the client to ensure that the solution represents a
	 * steady-state condition for the model
	 * 
	 * @param componentIndex
	 *            index of the sequential component
	 * @param inSystemIndices
	 *            indices in the numerical vector form of the local states in
	 *            the system
	 * @param derivationGraph
	 *            parametric derivation graph
	 * @param steadyStateSolution
	 *            steady-state solution of the differential equation.
	 * @return the average response time
	 * @throws DifferentialAnalysisException
	 */
	public AverageResponseTimeCalculation(int componentIndex,
			int[] inSystemIndices, IParametricDerivationGraph derivationGraph) {
		if (derivationGraph == null)
			throw new NullPointerException("Null pointer to derivation graph");
		if (componentIndex < 0
				|| componentIndex > derivationGraph.getSequentialComponents().length - 1)
			throw new IllegalArgumentException("Illegal component index");
		if (inSystemIndices.length <= 0)
			throw new IllegalArgumentException(
					"At least one component must be in the system");
		System.err.println("comp index: " + componentIndex);
		System.err.print("in index:");
		for (int k : inSystemIndices)
			System.err.println(" " + k + " ");
		this.derivationGraph = derivationGraph;
		this.inSystemIndices = inSystemIndices;
		boolean[] foundInIndex = new boolean[inSystemIndices.length];
		Arrays.fill(foundInIndex, false);
		IntegerArray out = new IntegerArray(10);
		ISequentialComponent c = derivationGraph.getSequentialComponents()[componentIndex];
		for (Entry<Short, Coordinate> entry : c.getComponentMapping()) {
			int coordinate = entry.getValue().getCoordinate();
			boolean coordinateFound = false;
			for (int i = 0; i < inSystemIndices.length; i++) {
				if (inSystemIndices[i] == coordinate) {
					if (foundInIndex[i] == true)
						throw new IllegalArgumentException(
								"Replicated indices in the insystem array");
					foundInIndex[i] = true;
					coordinateFound = true;
					break;
				}
			}
			if (!coordinateFound)
				out.add(coordinate);
		}
		for (boolean b : foundInIndex)
			if (b == false)
				throw new IllegalArgumentException("Index not found");
		this.outSystemIndices = out.toArray();

		debug("InSystem:");
		for (int i : inSystemIndices)
			debug("" + i);
		debug("OutSystem:");
		for (int i : outSystemIndices)
			debug("" + i);
	}

	public IPointEstimator getUsersInSystemEstimator() {
		return new IPointEstimator() {

			public double computeEstimate(double timePoint, double[] solution)
					throws DifferentialAnalysisException {
				double usersInSystem = 0.0;
				for (int i = 0; i < inSystemIndices.length; i++)
					usersInSystem += solution[inSystemIndices[i]];
				return usersInSystem;
			}
		};
	}

	public IPointEstimator getIncomingThroughputEstimator() {
		return new IPointEstimator() {
			public double computeEstimate(double timePoint, double[] solution)
					throws DifferentialAnalysisException {

				double inletThroughput = 0.0;
				for (IGeneratingFunction f : derivationGraph
						.getGeneratingFunctions()) {
					//debug("Analysing jump:");
					//for (short s : f.getJump())
					//	debug("" + s);
					if (CapacityUtilisationCalculation.isLocalJump(
							outSystemIndices, f.getJump()) == true
							&& CapacityUtilisationCalculation.isIncompingJump(
									inSystemIndices, f.getJump()) == true) {
						//debug("... satifies");
						inletThroughput += new EvaluatorVisitor(f.getRate(),
								solution).getResult();
					} //else
						//debug("... does not satify");

				}
				return inletThroughput;
			}

		};
	}

	private static void debug(String message) {
		// System.err.println(message);
	}
}
