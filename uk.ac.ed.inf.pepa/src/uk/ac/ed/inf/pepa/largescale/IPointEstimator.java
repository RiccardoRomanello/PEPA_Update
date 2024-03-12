package uk.ac.ed.inf.pepa.largescale;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public interface IPointEstimator {
	
	/**
	 * Computes an estimate on the model solution
	 * @param timePoint current time point
	 * @param solution current solution vector
	 * @return
	 * @throws DifferentialAnalysisException
	 */
	public double computeEstimate(double timePoint,
			double[] solution) throws DifferentialAnalysisException;

}
