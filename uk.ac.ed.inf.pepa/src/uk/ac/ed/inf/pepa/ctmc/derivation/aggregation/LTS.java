package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.ed.inf.pepa.model.ActionLevel;


/**
 * Interface to represent a PEPA's LTS model.
 * 
 * @author Giacomo Alzetta
 *
 */
public interface LTS<S> extends Iterable<S> {
	
	/**
	 * The number of states in the transition system.
	 * @return the number of states in the transition system.
	 */
	public int numberOfStates();
	
	/**
	 * A transition is a triplet: <source-state, target-state, label>
	 * 
	 * @return the number of transitions in the system.
	 */
	public int numberOfTransitions();
	
	/**
	 * @return the number of action types that appear in LTS transitions.
	 */
	public int numberOfActionTypes();
	
	/**
	 * Return all the action types that appear in transitions from source
	 * to target.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public Iterable<Short> getActions(S source, S target);
	
	/**
	 * Return all the action types that appear in transitions from source
	 * to target.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public Iterable<Short> getActions(S source, S target, ActionLevel level);

	/**
	 * Get the apparent rate of the transitions from source to target
	 * with the given action type.
	 * 
	 * @param source
	 * @param target
	 * @param actionId
	 * @return
	 */
	public double getApparentRate(S source, S target, short actionId);


	/**
	 * Get the level of the transitions from source to target
	 * with the given action type.
	 * 
	 * @param source
	 * @param target
	 * @param actionId
	 * @return
	 */
	public ActionLevel getActionLevel(short actionId);

	/**
	 * Get all the states reachable by transitions from source.
	 * @param source
	 * @return
	 */
	public Iterable<S> getImage(S source);
	
	/**
	 * Get all states that have transition to target.
	 * 
	 * @param target
	 * @return
	 */
	public Iterable<S> getPreImage(S target);
	

	/**
	 * Get all the states reachable by transitions from source.
	 * @param source
	 * @param level
	 * @return
	 */
	public Iterable<S> getImage(S source, ActionLevel level);
	
	/**
	 * Get all states that have transition to target.
	 * 
	 * @param target
	 * @param level
	 * @return
	 */
	public Iterable<S> getPreImage(S target, ActionLevel level);

	/**
	 * Obtain a variant view of the PEPA LTS.
	 * 
	 * The variant view should be exactly the same LTS, the only change
	 * is that it introduces self-loops for every state with label tau.
	 * The rate of such self-loops is equal to *minus* the sum of all outgoing
	 * tau transitions from the state.
	 * 
	 * Changes to the view may reflect on the original LTS.
	 * 
	 * @return The variant view of this LTS.
	 */
	public LTS<S> variantView();
}
