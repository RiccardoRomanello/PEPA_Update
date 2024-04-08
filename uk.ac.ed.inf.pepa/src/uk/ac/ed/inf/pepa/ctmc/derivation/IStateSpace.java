/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.PopulationLevelResult;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.model.NamedAction;

/**
 * Represents the underlying Continuous Time Markov Chain (CTMC) of a PEPA
 * model. The labels of the states are PEPA processes.
 * <p>
 * This interface represents mutable objects. The state of such
 * object changes when {@link #computeSteadyStateDistribution(OptionMap, IProgressMonitor)}
 * is invoked. Subsequent calls to {@link #getSolution(State)},
 * {@link #getThroughput()}, {@link #getUtilisation()}, {@link #getPopulationLevels()}
 * will return steady-state results if the computation of the steady-state
 * probability distribution is successful. 
 * 
 * @see #isSolutionAvailable()
 * 
 * @author mtribast
 * 
 */
public interface IStateSpace {

	/**
	 * Size of the state space
	 * 
	 * @return the size of the state space
	 */
	int size();
	
	/**
	 * Returns the number of sequential components in state <code>stateIndex</code>
	 * @param stateIndex
	 * @return
	 */
	public int getNumberOfSequentialComponents(int stateIndex);
	
	/**
	 * Returns the maximum number of sequential components
	 * @return
	 */
	public int getMaximumNumberOfSequentialComponents();
	
	/**
	 * Returns an alphabetically ordered array of component
	 * names as found during state space exploration.
	 * 
	 * @return
	 */
	public String[] getComponentNames();
	
	/**
	 * Get the process id corresponding to the sequential component
	 * @param process
	 * @return -1 if no association is found
	 */
	public short getProcessId(String process);
	
	/**
	 * Get the number of copies of the process identified by
	 * processId in state.
	 * 
	 * @param state
	 * @param processId
	 * @return
	 */
	public int getNumberOfCopies(int stateIndex, short processId);
	
	/**
	 * Returns the name of the sequential component of <code>state</code> at
	 * <code>position</code>
	 * 
	 * @param state
	 *            the state
	 * @param position
	 *            the position in the flattened representation of the state.
	 * @return
	 */
	public String getLabel(int stateIndex, int position);
	
	public short getProcessId(int stateIndex, int position);

	/**
	 * Determines if the sequential component is unnamed
	 * 
	 * @param state
	 *            the state
	 * @param position
	 *            the position of the sequential component
	 * @return <code>true</code> if the sequential component is unnamed
	 */
	public boolean isUnnamed(int stateIndex, int position);

	/**
	 * Returns the indices of state's outgoing transitions.
	 * @param state
	 * @return
	 * @throws DerivationException 
	 */
	public int[] getOutgoingStateIndices(int stateIndex);
	
	/**
	 * Returns the indices of the states for which <code>state</code>
	 * is an outgoing transition.
	 * @param state
	 * @return
	 * @throws DerivationException 
	 */
	public int[] getIncomingStateIndices(int stateIndex);
	
	/**
	 * Returns the action type of the transition from 
	 * <code>source</code> to <code>target</code>. If
	 * no transition is possible between the two states
	 * returns <code>null<code>.
	 * @param source
	 * @param target
	 * @return the action type of the transition from source to 
	 * target, or <code>null</code>
	 * @throws DerivationException 
	 */
	public NamedAction[] getAction(int source, int target);

	/**
	 * Returns the transition rate.
	 * @param source
	 * @param target
	 * @return 0 if none
	 * @throws DerivationException 
	 */
	public double getRate(int source, int target);
	
	/**
	 * Gets the generator matrix for the state space.
	 * It uses the adaptable pattern to allow for various
	 * kinds of formats. Delegating the construction of the 
	 * generator matrix to the state space also allows for 
	 * efficiency as implementors can make better use of
	 * the underlying storage mechanism. 
	 * 
	 * @param clazz class of the generator matrix
	 * @return the generator matrix.
	 */
	public Object getGeneratorMatrix(Class<?> clazz);
	
	public void setSolution(double[] solution);
	

	/**
	 * Tests the current state of the object. 
	 * @return <code>true</code> if solution is available. 
	 * 
	 */
	public boolean isSolutionAvailable();
	
	/**
	 * Returns the steady-state probability of <code>state</code>.
	 * 
	 * @param state
	 *            the state
	 * @return the steady-state probability of <code>state</code>,
	 *         <code>NaN</code> if solution is not available.
	 */
	public double getSolution(int index);

	/**
	 * Returns throughput results associated to this model. The array will be
	 * empty if a steady-state solution is not available for the model.
	 * 
	 * @return an array of throughput results, one for each model's sequential
	 *         component. If solution is not available an empty array is returned.
	 *         
	 */
	public ThroughputResult[] getThroughput();
	
	/**
	 * Returns utilisation of all the sequential components
	 * in the system equation.
	 * @return the utilisation, or an empty array is solution
	 * is not available
	 */
	public SequentialComponent[] getUtilisation();
	
	/**
	 * Returns populations levels of the sequential components 
	 * in the steady state.
	 * @return the population levels, or an empty array
	 * if solution is not available yet.
	 */
	public PopulationLevelResult[] getPopulationLevels();
	
	/**
	 * Asks the state space to release any resources that it 
	 * may have taken to perform its internal tasks.
	 */
	public void dispose();
	
}
