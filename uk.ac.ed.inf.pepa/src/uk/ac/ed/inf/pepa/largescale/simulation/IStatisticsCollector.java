package uk.ac.ed.inf.pepa.largescale.simulation;

/**
 * An IStatisticsCollector is used when one wishes to compute
 * functions of point estimates. For instance, it is useful for the 
 * computation of the average response time, which is a function of
 * two point estimates.
 * 
 * @see AverageResponseTimeCollector
 * @author Mirco
 *
 */
public interface IStatisticsCollector {
	
	public double computeObservation(double[] estimates);
	
}
