/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * Lightweight variant view of the LTS.
 * 
 * @author Giacomo Alzetta
 *
 */
public class VariantView<S> implements LTS<S> {
	
	/**
	 * The underlying LTS.
	 */
	LTS<S> model;
	
	/**
	 * The tau self-loop rates. The value at index i corresponds to the rate
	 * of the self-loop for state i.
	 */
	HashMap<S, Double> selfLoops;
	
	public VariantView(LTS<S> model) {
		this.model = model;
		
		// We compute all self loops now.
		selfLoops = new HashMap<S, Double>(model.numberOfStates());
		for (S state: model) {
			selfLoops.put(state, computeSelfLoopRate(state));
		}
	}
	
	/**
	 * Get the rate of the tau self loop for state i.
	 * 
	 * @param i
	 * @return
	 */
	private double computeSelfLoopRate(S state) {
		double rate = 0.0d;
		for (S target: model.getImage(state)) {
			if (!state.equals(target)) {
				rate += model.getApparentRate(state, target, ISymbolGenerator.TAU_ACTION);
			}
		}
		
		return -rate;
	}

	/**
	 * Iterate over all the states of the underlying LTS.
	 * @return
	 */
	@Override
	public Iterator<S> iterator() {
		return model.iterator();
	}

	/**
	 * The number of states of the underlying LTS.
	 */
	@Override
	public int numberOfStates() {
		return model.numberOfStates();
	}

	/**
	 * The number of transitions in the underlying LTS.
	 */
	@Override
	public int numberOfTransitions() {
		// FIXME: add self loops? 
		return model.numberOfTransitions();
	}

	/**
	 * The number of action types in the underlying LTS.
	 */
	@Override
	public int numberOfActionTypes() {
		return model.numberOfActionTypes();
	}

	/**
	 * Get the actions that label transitions between <code>source</code>
	 * and <code>target</code>.
	 * 
	 * Note that if <code>source.equals(target)</code> then tau is always
	 * included.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	@Override
	public Iterable<Short> getActions(S source, S target) {
		ArrayList<Short> acts = (ArrayList<Short>) model.getActions(source, target);
		
		if (source.equals(target) && !acts.contains(ISymbolGenerator.TAU_ACTION)) {
			acts.add(ISymbolGenerator.TAU_ACTION);
		}
		return acts;
	}

	/**
	 * Get the actions that label transitions between <code>source</code>
	 * and <code>target</code>.
	 * 
	 * Note that if <code>source.equals(target)</code> then tau is always
	 * included.
	 * 
	 * @param source
	 * @param target
	 * @param level
	 * @return
	 */
	@Override
	public Iterable<Short> getActions(S source, S target, ActionLevel level) {
		ArrayList<Short> acts = (ArrayList<Short>) model.getActions(source, target, level);
		
		if (source.equals(target) && !acts.contains(ISymbolGenerator.TAU_ACTION)) {
			acts.add(ISymbolGenerator.TAU_ACTION);
		}
		return acts;
	}

	/**
	 * Get the apparent rate of the transitions from source to target
	 * with the given actionId in the underlying LTS.
	 * 
	 * If <code>source.equals(target)</code> and the <code>actionId</code>
	 * refers to the tau transition then the self-loop rate is returned.
	 * 
	 * @param source
	 * @param target
	 * @param actionId
	 * @return
	 */
	@Override
	public double getApparentRate(S source, S target, short actionId) {
		if (actionId == ISymbolGenerator.TAU_ACTION && source.equals(target)) {
			return selfLoops.get(source);
		}
		
		return model.getApparentRate(source, target, actionId);
	}

	/**
	 * Get the states that are reachable via transitions from <code>source</code>.
	 * 
	 * Note that <code>source</code> itself is always included since
	 * it has a tau self-loop.
	 * 
	 * @param source
	 * @return
	 */
	@Override
	public Iterable<S> getImage(S source) {
		HashSet<S> targets = (HashSet<S>) model.getImage(source);
		targets.add(source);
		
		return targets;
	}

	/**
	 * Get the states that can reach via transitions the given
	 * state <code>target</code>.
	 * 
	 * Note that <code>target</code> itself is always included since
	 * it has a tau self-loop.
	 * 
	 * @param target
	 * @return
	 */
	@Override
	public Iterable<S> getPreImage(S target) {
		ArrayList<S> sources = (ArrayList<S>) model.getPreImage(target);
		
		if (!sources.contains(target)) sources.add(target);
		
		return sources;
	}

	/**
	 * Get the states that are reachable via transitions from <code>source</code>.
	 * 
	 * Note that <code>source</code> itself is always included since
	 * it has a tau self-loop.
	 * 
	 * @param source
	 * @param level
	 * @return
	 */
	@Override
	public Iterable<S> getImage(S source, ActionLevel level) {
		HashSet<S> targets = (HashSet<S>) model.getImage(source, level);
		targets.add(source);
		
		return targets;
	}

	/**
	 * Get the states that can reach via transitions the given
	 * state <code>target</code>.
	 * 
	 * Note that <code>target</code> itself is always included since
	 * it has a tau self-loop.
	 * 
	 * @param target
	 * @param level
	 * @return
	 */
	@Override
	public Iterable<S> getPreImage(S target, ActionLevel level) {
		ArrayList<S> sources = (ArrayList<S>) model.getPreImage(target, level);
		
		if (!sources.contains(target)) {
			sources.add(target);
		}
		
		return sources;
	}

	@Override
	public ActionLevel getActionLevel(short actionid) {
		return model.getActionLevel(actionid);
	}

	/**
	 * Returns itself.
	 */
	@Override
	public LTS<S> variantView() {
		return this;
	}
}