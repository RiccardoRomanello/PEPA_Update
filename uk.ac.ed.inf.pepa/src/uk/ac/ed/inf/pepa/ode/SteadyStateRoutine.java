package uk.ac.ed.inf.pepa.ode;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;

public class SteadyStateRoutine {

	class SteadyStateConvergenceCallback implements ISolutionRoutineCallback {

		private double convergenceRequired;

		private boolean hasConverged = false;

		private IProgressMonitor monitor;

		public SteadyStateConvergenceCallback(double convergenceNorm,
				IProgressMonitor internalMonitor) {
			this.convergenceRequired = convergenceNorm;
			this.monitor = internalMonitor;
		}

		public boolean hasConverged() {
			return hasConverged;
		}

		public void timePointComputed(double timePoint, double[] solution)
				throws DifferentialAnalysisException {

			lastTimePoint = timePoint;
			if (lastSolution == null) {
				lastSolution = new double[solution.length];
				System.arraycopy(solution, 0, lastSolution, 0, solution.length);
				return;
			}
			lastError = computeDifference(lastSolution, solution);
			System.arraycopy(solution, 0, lastSolution, 0, solution.length);
			// System.out.println(timePoint + ":" + lastError);
			if (lastError <= convergenceRequired) {
				this.hasConverged = true;
				this.monitor.setCanceled(true);
			}
			monitor.worked(1);
		}

		private double computeDifference(double[] lastSolution,
				double[] solution) {
			double normZero = 0.0d;
			//double normEuclidean = 0.0d;
			//double[] vf = null;
			//try {
			//	vf = solver.evaluateVectorField(0, lastSolution);
			//} catch (DifferentialAnalysisException e) {
			//	// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			for (int i = 0; i < solution.length; i++) {
				// System.out.println(solution[i] + " - " + lastSolution[i]);
				double compDifference = solution[i] - lastSolution[i];
				normZero += Math.abs(compDifference);
				//normEuclidean += Math.pow(vf[i], 2);
			}
			//normEuclidean = Math.sqrt(normEuclidean);
			//System.out.printf("Time %f Relative error: %e Absolute error: %e\n",
			//		 lastTimePoint, normZero, normEuclidean);
			return normZero;
		}

	}

	private OptionMap map;

	private IParametricDerivationGraph graph;

	private double[] lastSolution;

	private double lastError;

	private double lastTimePoint;

	private IODESolver solver;

	public SteadyStateRoutine(OptionMap map, IParametricDerivationGraph graph) {
		if (map == null) {
			map = new OptionMap();
			map.put(OptionMap.ODE_INTERPOLATION,
					OptionMap.ODE_INTERPOLATION_OFF);
		}
		if (map.get(OptionMap.ODE_INTERPOLATION).equals(
				OptionMap.ODE_INTERPOLATION_ON))
			throw new IllegalArgumentException();
		this.map = map;
		this.graph = graph;
	}

	public double getConvergenceNorm() {
		return lastError;
	}

	public double[] getSolution() {
		return lastSolution;
	}

	public double getTimePoint() {
		return lastTimePoint;
	}

	public void obtainSteadyState(final IProgressMonitor monitor)
			throws DifferentialAnalysisException, InterruptedException {
		final SteadyStateConvergenceCallback callback = new SteadyStateConvergenceCallback(
				(Double) map.get(OptionMap.ODE_STEADY_STATE_NORM), monitor);
		monitor.beginTask(IProgressMonitor.UNKNOWN);
		solver = ODESolverFactory.create(graph);
		try {
			solver.solve(map, callback, new IProgressMonitor() {

				public void worked(int worked) {
				}

				public void setCanceled(boolean state) {
				}

				public boolean isCanceled() {
					return monitor.isCanceled() || callback.hasConverged();
				}

				public void done() {
				}

				public void beginTask(int amount) {
				}
			});
			if (!callback.hasConverged())
				throw new DifferentialAnalysisException(
						"Steady-state analysis not converged after t = "
								+ lastTimePoint + ". Norm = " + lastError,
						DifferentialAnalysisException.NOT_CONVERGED);
		} catch (InterruptedException e) {
			if (!callback.hasConverged())
				throw e; // interrupted by user
			// else it has converged
		} finally {
			monitor.done();
		}
		// System.out.println("Last time: " + lastTimePoint);
		// System.out.println("Last error: " + lastError);
	}
}
