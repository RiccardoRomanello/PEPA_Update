package uk.ac.ed.inf.pepa.largescale.simulation;

/**
 * Default checker, calculates the maximum of the confidence interval errors for
 * all estimates
 */
class DefaultConvergenceChecker implements IConvergenceChecker {
	private int[] excludedFromConvergence;
	
	public DefaultConvergenceChecker() {
		this(new int[0]);
	}
	
	public DefaultConvergenceChecker(int[] excludedFromConvergence) {
		if (excludedFromConvergence == null)
			throw new NullPointerException();
		this.excludedFromConvergence = excludedFromConvergence;
	}
	
	public double computeConvergenceError(
			AbstractStochasticSimulation simulation) {
		double maximumConfidenceInterval = Double.MIN_VALUE;
		double[] ci = new double[2];
		for (int i = 0; i < simulation.getNumberOfObservers(); i++) {
			// if a collector is excluded from convergence
			if (isExcluded(i))
				continue;
			
			for (int j = 0; j < simulation.getNumberOfTimePoints(); j++) {
				simulation.confidenceInterval(i, j, ci);
				double error = ci[1] / ci[0] * 100;
				if (ci[1] == 0 && ci[0] == 0)
					error = 0;
				maximumConfidenceInterval = Math.max(maximumConfidenceInterval,
						error);
			}
		}
		return maximumConfidenceInterval;
	}
	
	private boolean isExcluded(int index) {
		for (int e : excludedFromConvergence) {
			if (e == index)
				return true;
		}
		return false;
	}

}
