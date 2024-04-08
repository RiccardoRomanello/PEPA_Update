/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * @author Giacomo Alzetta
 *
 */
public interface LTSBuilder<S> {
	/**
	 * Adds a state to the LTS model.
	 * 
	 * @param state The state to be added.
	 */
	public void addState(S state);
	
	/**
	 * Add a transition to the LTS model.
	 * 
	 * @param source 	The starting state
	 * @param target 	The ending state
	 * @param rate   	The rate of the transition
	 * @param actionId 	The action type of the transition
	 * @param level		The level of the action
	 */
	public void addTransition(S source, S target, double rate,
							  short actionId, ActionLevel level);
	
	/**
	 * Obtain the built LTS model.
	 */
	public LTS<S> getLts();
}
