package uk.ac.ed.inf.pepa.ode;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public interface IODESolver {

	/**
	 * Solves the ODE. The solution is returned via the callback. Useful for
	 * obtaining different implementations for storage. I.e., memory storage or
	 * disk-based solutions.
	 * 
	 * @param callback
	 * @throws InterruptedException 
	 */
	public void solve(OptionMap map, ISolutionRoutineCallback callback, IProgressMonitor monitor)
			throws DifferentialAnalysisException, InterruptedException;
	/**
	 * Solves the system overriding the initial state of the derivation graph
	 * @param map
	 * @param callback
	 * @param monitor
	 * @param initialState
	 * @throws InterruptedException 
	 * @throws DifferentialAnalysisException 
	 */
	public void solve(OptionMap map, final ISolutionRoutineCallback callback,
			IProgressMonitor monitor, double[] initialState) throws DifferentialAnalysisException, InterruptedException;
	/**
	 * Evaluates the vector field of the differential equation
	 * @param t time point
	 * @param state current state
	 * @return
	 * @throws DifferentialAnalysisException
	 */
	public double[] evaluateVectorField(double t, double[] state) throws DifferentialAnalysisException;

}
