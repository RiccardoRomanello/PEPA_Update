package uk.ac.ed.inf.pepa.largescale.simulation;

/**
 * Interface to define an object that calculate the 
 * convergence error of a simulation.
 * Currently the API does not let the user choose one,
 * but it uses the default {@link DefaultConvergenceChecker}
 * @author Mirco
 *
 */
interface IConvergenceChecker {
	
	public double computeConvergenceError(AbstractStochasticSimulation simulation);
	
}
