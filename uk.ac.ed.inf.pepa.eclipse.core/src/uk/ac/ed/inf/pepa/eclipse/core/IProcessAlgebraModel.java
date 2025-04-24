/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.inf.pepa.eclipse.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;

/**
 * Abstraction for methods which may be useful to any
 * process algebra model. It is mainly used to allow 
 * reuse of the functionality of the state space view
 * by other editors. 
 *  
 * @author mtribast
 *
 */
public interface IProcessAlgebraModel extends IOptionHandler {
	
	public void dispose();
	
	/**
	 * Add a listener to this model. It is notified of changes in the state of
	 * the model such as state space derivation, solution, etc.
	 * <p>
	 * This method has no effect if the listener is already registered.
	 * 
	 * @param listener
	 *            the listener to be added
	 * @see IProcessAlgebraModelChangedListener
	 */
	public void addModelChangedListener(IProcessAlgebraModelChangedListener listener);

	/**
	 * Remove the listener. It has no effect if the listener was not registered
	 * already.
	 * 
	 * @param listener
	 */
	public void removeModelChangedListener(IProcessAlgebraModelChangedListener listener);
	
	/**
	 * Return the underlying resource representing the PEPA model in the
	 * workbench
	 * 
	 * @return the Eclipse IResource
	 */
	public IResource getUnderlyingResource();
	
	/**
	 * Determines if the model is derivable. State space related operations on
	 * not derivable models have no effect
	 * 
	 * @return true if the model is derivable
	 */
	public boolean isDerivable();
	
	/**
	 * Derive the state space of the underlying CTMC.
	 * <p>
	 * This is typically a long-running operation scheduled in a separate
	 * thread.
	 * <p>
	 * Once the state space is created, listeners are notified with the
	 * STATE_SPACE_DERIVED event.
	 * <p>
	 * The method has no effect when it is called on models which are not have a
	 * correct AST yet. Information about success of this operation is found in
	 * the state space returned by <code>getStateSpace()</code>
	 * 
	 * @param monitor
	 *            a progress monitor for controlling this long-running activity,
	 *            or null
	 * @see #getStateSpace()
	 * @see #isDerivable()
	 * @throws DerivationException
	 *             if errors occur during state space derivation
	 * 
	 */
	public void derive(IProgressMonitor monitor)
			throws DerivationException;

	/**
	 * Verify the Persistent Stochastic Non-Interference (PSNI) of the underlying CTMC.
	 * <p>
	 * This is typically a long-running operation scheduled in a separate
	 * thread.
	 * <p>
	 * Once the state space is created, listeners are notified with the
	 * STATE_SPACE_DERIVED event.
	 * <p>
	 * The method has no effect when it is called on models which are not have a
	 * correct AST yet. Information about success of this operation is found in
	 * the state space returned by <code>getStateSpace()</code>
	 *
	 * @param monitor
	 *            a progress monitor for controlling this long-running activity,
	 *            or null
	 * @throws DerivationException
	 *             if errors occur during the PSNI verification
	 */
	public void PSNI_verify(IProgressMonitor monitor)
			throws DerivationException;

	public Boolean isPSNI();

	public void reversible_verify(IProgressMonitor monitor);

	public Boolean isReversible();
	
	/**
	 * Return the state space of the underlying CTMC.
	 * <p>
	 * State space is derived via the <code>derive()</code> method.
	 * 
	 * @return the state space or <code>null</code> if no state space has been
	 *         derived yet
	 * @see #derive()
	 */
	public IStateSpace getStateSpace();
	
	/**
	 * Solve the steady-state probability distribution function of the
	 * underlying CTMC of this PEPA model.
	 * 
	 * @param options
	 *            solver options
	 * @param monitor
	 *            a progress monitor for controlling this long-running activity,
	 *            or null
	 */
	public void solveCTMCSteadyState(IProgressMonitor monitor)
			throws SolverException;
	
	/**
	 * Set the solution of the Markov chain obtained from
	 * an external source. 
	 * <p>
	 * The PEPA Model must be solvable {@link #isSolvable()} and
	 * the solution must be not null and the same length as
	 * the spate space size. A runtime exception will be thrown
	 * if such conditions are not met.
	 * 
	 * @param solution the solution
	 * @throws DerivationException 
	 */
	public void setSolution(double[] solution) throws DerivationException;

	/**
	 * Determine if this model's CTMC can be solved
	 * 
	 * @return true if it can be solved
	 */
	public boolean isSolvable();
	
	
	public boolean isSolved();
}
