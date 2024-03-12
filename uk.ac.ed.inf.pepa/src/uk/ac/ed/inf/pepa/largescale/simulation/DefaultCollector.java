package uk.ac.ed.inf.pepa.largescale.simulation;

import uk.ac.ed.inf.pepa.largescale.IPointEstimator;

public class DefaultCollector implements IStatisticsCollector {

	private int pointEstimateIndex;

	/**
	 * Creates as many collectors as the number of estimators, in order.
	 * 
	 * @param estimators
	 * @return
	 */
	public static IStatisticsCollector[] create(IPointEstimator[] estimators) {
		IStatisticsCollector[] collectors = new IStatisticsCollector[estimators.length];
		for (int i = 0; i < estimators.length; i++)
			collectors[i] = new DefaultCollector(i);
		return collectors;
	}

	public DefaultCollector(int pointEstimateIndex) {
		this.pointEstimateIndex = pointEstimateIndex;
	}

	public double computeObservation(double[] estimates) {
		return estimates[pointEstimateIndex];
	}

}
