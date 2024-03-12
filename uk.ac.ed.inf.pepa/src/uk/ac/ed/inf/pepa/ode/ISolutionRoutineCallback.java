package uk.ac.ed.inf.pepa.ode;


public interface ISolutionRoutineCallback {
	
	/**
	 * Informs that a time point has been computed 
	 * @param timePoint
	 * @param solution
	 */
	public void timePointComputed(double timePoint, double[] solution) throws DifferentialAnalysisException;
}
