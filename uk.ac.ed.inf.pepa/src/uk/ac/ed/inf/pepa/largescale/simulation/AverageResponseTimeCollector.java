package uk.ac.ed.inf.pepa.largescale.simulation;


/**
 * Convergence checker for average response time. It is different because it is
 * a function of two means
 * 
 * @author Mirco
 * 
 */
public class AverageResponseTimeCollector implements IStatisticsCollector {

	private int userIndex;

	private int throughputIndex;

	public AverageResponseTimeCollector(int userIndex, int throughputIndex) {
		if (userIndex < 0 || throughputIndex < 0)
			throw new IllegalArgumentException();
		this.userIndex = userIndex;
		this.throughputIndex = throughputIndex;

	}

	public double computeObservation(double[] estimates) {
		double w = estimates[userIndex] / estimates[throughputIndex];
		if (Double.isNaN(w))
			throw new IllegalStateException();
		return w;
	}

}