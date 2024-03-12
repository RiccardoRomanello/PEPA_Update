/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer.legacy;


/**
 * Object model for a state of the underlying CTMC of a PEPA model. This
 * actually wrappers the already existing IStateSpace interface in order to
 * provide nicer functionalities that are used in the state explorer view.
 * 
 * @author mtribast
 * 
 */
public interface IStateModel {

	public static final int SOLUTION_NOT_AVAILABLE = -1;

	/**
	 * Zero-base position in the state space model for this process
	 * 
	 * @return the state number
	 */
	public int getStateNumber();

	/**
	 * The number of the top-level sequential components of the system equation.
	 * <p>
	 * If the equation contains aggregation, each aggregation contributes as
	 * many components as the number of copies defined in the system equation.
	 * 
	 * @return
	 */
	public int getSequentialComponentCount();

	/**
	 * Request the top level component at the given (zero-based) index
	 * 
	 * @param index
	 * @return the Process at the given index of this state
	 */
	public String getSequentialComponentAt(int index);
	
	public boolean isUnnamed(int index);
	
	/**
	 * The state-state probability of this state
	 * 
	 * @return the steady-state probability or the array, or
	 *         <code>{@link #SOLUTION_NOT_AVAILABLE}</code> if the solution is
	 *         not available
	 */
	public double getSolution();

	/**
	 * Return the indices of the outgoing states of this state.
	 * Two states are the same if the index returned by this method
	 * is equal to the <code>getStateNumber()</code>.
	 * <p>
	 * The target can be revealed in the table view by calling
	 * <code>TableViewer.reveal(Object)</code>, the object being
	 * <code>(StateModel) tableViwer.getInput()[index]</code> 
	 * 
	 * @return the array of indices outgoing states. <code>null</code> should be never
	 *         returned.
	 *         
	 */
	public int[] getOutgoingStatesIndices();
	
	/**
	 * A string representing problems which this state may have.
	 * 
	 * @return the problem, or <code>null</code> if none
	 */
	public String getProblem();
	

}
