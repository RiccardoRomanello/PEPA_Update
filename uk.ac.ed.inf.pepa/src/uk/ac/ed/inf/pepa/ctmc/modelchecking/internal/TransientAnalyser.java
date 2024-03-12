/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLTimeInterval;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ModelCheckingException;

public class TransientAnalyser {

	private AbstractCTMC abstractCTMC;
	private CSLPropertyManager propertyManager;
	
	private double boundAccuracy;
	
	public TransientAnalyser(AbstractCTMC abstractCTMC, CSLPropertyManager propertyManager, double boundAccuracy) {
		this.abstractCTMC = abstractCTMC;
		this.propertyManager = propertyManager;
		this.boundAccuracy = boundAccuracy;
	}
	
	public void checkUntil(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2, CSLTimeInterval timeInterval) throws ModelCheckingException {
		if (timeInterval.isStartBounded() && timeInterval.isEndBounded()) {
			boundedUntil(property1, property2, timeInterval.getStartTime().getValue(), timeInterval.getEndTime().getValue());	
		} else if (timeInterval.isStartBounded() && !timeInterval.isEndBounded()){
			startBoundedUntil(property1, property2, timeInterval.getStartTime().getValue());
		} else if (!timeInterval.isStartBounded() && timeInterval.isEndBounded()) {
			endBoundedUntil(property1, property2, timeInterval.getEndTime().getValue());
		} else {
			unboundedUntil(property1, property2);
		}
	}
	
	public void checkNext(CSLAbstractStateProperty property, CSLTimeInterval timeInterval) throws ModelCheckingException {
		if (timeInterval.isStartBounded() || timeInterval.isEndBounded()) {
			throw new ModelCheckingException("Unable to model check the timed Next operator on an abstract model.");
		} else {
			unboundedNext(property);
		}
	}
	
	public void checkLongRun(CSLAbstractStateProperty property) throws ModelCheckingException {
		longRun(property);
	}
	
	/**
	 * Bounded until operator (time interval [t,t']). Need to:
	 * 1) Compute the probability of staying in property1 states until time t.
	 * 2) Compute Prob(property1 U[0,t'-t] property2)
	 * @throws ModelCheckingException 
	 */
	private void boundedUntil(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2, double startTime, double endTime) throws ModelCheckingException {
		if (startTime == 0) {
			endBoundedUntil(property1, property2, endTime);
		} else {
			// First Compute Prob(property1 U[0,t'-t] property2)
			endBoundedUntil(property1, property2, endTime - startTime);
			// Now compute reachability - stay within states that satisfy property1 until the startTime
			reachabilitySatisfying(property1, startTime);
		}
	}
	
	
	/**
	 * End bounded until operator (time interval [t,-]). Need to:
	 * 1) Compute the probability of staying in phi_1 states until time t.
	 * 2) Compute Prob(property1 U property2)
	 * @throws ModelCheckingException 
	 */
	private void startBoundedUntil(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2, double startTime) throws ModelCheckingException {
		if (startTime == 0) {
			unboundedUntil(property1, property2);
		} else {
			// First Compute Prob(property1 U property2)
			unboundedUntil(property1, property2);
			// Now compute reachability - stay within states that satisfy property1 until the startTime
			reachabilitySatisfying(property1, startTime);
		}
	}
	
	
	/**
	 * Computes the transient probabilities in time interval [0,t] of being in a state while
	 * only passing through states that satisfy the given property.
	 * @throws ModelCheckingException 
	 */
	private void reachabilitySatisfying(CSLAbstractStateProperty property, double time) throws ModelCheckingException {
		FoxGlynn poisson = new FoxGlynn(abstractCTMC.getUniformisationConstant() * time, boundAccuracy);
		if (!poisson.getFlag()) {
			throw new ModelCheckingException("Error when computing Poisson probabilities.");
		}
		// Initialise states: Absorbing states are those that don't satisfy property1
		AbstractCTMCProbabilities probabilities = new AbstractCTMCProbabilities(abstractCTMC);
		AbstractCTMCProperty min_false_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty max_false_states = new AbstractCTMCProperty(abstractCTMC);
		for (AbstractCTMCState state : abstractCTMC) {
			AbstractBoolean v1 = propertyManager.test(property, state);
			if (v1 == AbstractBoolean.FALSE) {
				min_false_states.addState(state);
				max_false_states.addState(state);
			} else if (v1 == AbstractBoolean.MAYBE) {
				min_false_states.addState(state);
				probabilities.setProbability(state, 0, state.getMaxProbability());
			} else {
				probabilities.setProbability(state, state.getMinProbability(), state.getMaxProbability());
			}
			state.setMinProbability(0);
			state.setMaxProbability(0);
			state.setCache(0);
		}
		
		boundedReachabilityMin(min_false_states, probabilities, poisson);
		boundedReachabilityMax(max_false_states, probabilities, poisson);
		
		for (AbstractCTMCState state : abstractCTMC) {
			// Ensure that the maximum probability is actually an upper bound
			double newMinProb = state.getMinProbability() + poisson.psi(0) * probabilities.getProbability(state, true);
			double newMaxProb = state.getMaxProbability() + poisson.psi(0) * probabilities.getProbability(state, false);
			newMinProb = Math.max(0, newMinProb);
			newMaxProb = Math.min(1, newMaxProb);
			if (newMinProb < newMaxProb) {
				state.setMinProbability(newMinProb);
				state.setMaxProbability(newMaxProb);
			} else {
				state.setMinProbability(newMaxProb);
				state.setMaxProbability(newMinProb);
			}
		}
	
	}
	
	
	/**
	 * End bounded until operator (time interval [0,t]). Compute the states that definitely
	 * satisfy, or do not satisfy the property. Then compute time bounded reachability
	 * probabilities for all other states.
	 * @throws ModelCheckingException 
	 */
	private void endBoundedUntil(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2, double endTime) throws ModelCheckingException {
		// 1) Label those states for which property2 is true (definitely satisfies)
		//    or else neither property1 nor property2 is true (definitely doesn't satisfy)
		AbstractCTMCProperty min_true_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty min_false_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty max_true_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty max_false_states = new AbstractCTMCProperty(abstractCTMC);
		for (AbstractCTMCState state : abstractCTMC) {
			AbstractBoolean v1 = propertyManager.test(property1, state);
			AbstractBoolean v2 = propertyManager.test(property2, state);
			AbstractBoolean isOK = v2;
			AbstractBoolean isPossiblyOK = AbstractBoolean.or(v1, v2);
			if (isOK == AbstractBoolean.TRUE) {
				min_true_states.addState(state);
				max_true_states.addState(state);
			} else if (isOK == AbstractBoolean.MAYBE) {
				max_true_states.addState(state);
			}
			if (isPossiblyOK == AbstractBoolean.FALSE) {
				min_false_states.addState(state);
				max_false_states.addState(state);
			} else if (isPossiblyOK == AbstractBoolean.MAYBE) {
				min_false_states.addState(state);
			}
			state.setMinProbability(0);
			state.setMaxProbability(0);
			state.setCache(0);
		}
		
		// 2) Time bounded reachability of states satisfying property2 within t time-steps - minimise the probabilities.
		FoxGlynn poisson = new FoxGlynn(abstractCTMC.getUniformisationConstant() * endTime, boundAccuracy);
		if (!poisson.getFlag()) {
			throw new ModelCheckingException("Error when computing Poisson probabilities.");
		}
		boundedReachabilityMin(min_false_states, min_true_states, poisson);
		boundedReachabilityMax(max_false_states, max_true_states, poisson);
		
		for (AbstractCTMCState state : abstractCTMC) {
			if (min_true_states.containsState(state)) {
				state.setMinProbability(1);
				state.setMaxProbability(1);
			} else {
				// Ensure that the maximum probability is actually an upper bound
				double newMinProb = Math.max(0, state.getMinProbability());
				double newMaxProb = Math.min(1, state.getMaxProbability());
				if (newMinProb < newMaxProb) {
					state.setMinProbability(newMinProb);
					state.setMaxProbability(newMaxProb);
				} else {
					state.setMinProbability(newMaxProb);
					state.setMaxProbability(newMinProb);
				}
			}
		}
	}
	
	private void boundedReachabilityMin(AbstractCTMCProperty false_states, AbstractCTMCProperty true_states, FoxGlynn poisson) {
		boundedReachability(false_states, true_states, poisson, true, null);
	}
	
	private void boundedReachabilityMax(AbstractCTMCProperty false_states, AbstractCTMCProperty true_states, FoxGlynn poisson) {
		boundedReachability(false_states, true_states, poisson, false, null);
	}
	
	private void boundedReachabilityMin(AbstractCTMCProperty false_states, AbstractCTMCProbabilities probabilities, FoxGlynn poisson) {
		AbstractCTMCProperty true_states = new AbstractCTMCProperty(abstractCTMC);
		boundedReachability(false_states, true_states, poisson, true, probabilities);
	}
	
	private void boundedReachabilityMax(AbstractCTMCProperty false_states, AbstractCTMCProbabilities probabilities, FoxGlynn poisson) {
		AbstractCTMCProperty true_states = new AbstractCTMCProperty(abstractCTMC);
		boundedReachability(false_states, true_states, poisson, false, probabilities);
	}
	
	private void boundedReachability(AbstractCTMCProperty false_states, AbstractCTMCProperty true_states,
			                         FoxGlynn poisson, boolean is_minimum,
			                         AbstractCTMCProbabilities probabilities) {
		int R = poisson.getRightTruncation();
		
		// Make sure that we iterate an even number of times so that we write to the minProb/maxProb on the final iteration
		if (R % 2 == 1) R++;
		
		boolean write_cache = false;
		for (int i = R; i > 0; i--) {
			// switch between the cache and the minProbability field for the old value;
			write_cache = !write_cache;
			double psi = poisson.psi(i);
			//System.out.println("FG(" + i + ") = " + psi);
			for (AbstractCTMCState state : abstractCTMC) {
				if (false_states.containsState(state)) continue;
				double newProb = 0;
				if (true_states.containsState(state)) {
					newProb = psi + getProbability(state, is_minimum, !write_cache);
				} else {
					ArrayList<AbstractCTMCTransition> transitions = state.getSortedTransitions(is_minimum, !write_cache);
					double probabilityRemaining = 1;
					// Keep track of the minimum possible probability we have left to assign
					double minimumRemaining = 0;
					for (AbstractCTMCTransition t : transitions) minimumRemaining += t.getMinProb();
					for (AbstractCTMCTransition t : transitions) {
						AbstractCTMCState toState = t.getToState();
						minimumRemaining -= t.getMinProb();
						double transitionProbability = Math.min(t.getMaxProb(), probabilityRemaining - minimumRemaining);
						probabilityRemaining -= transitionProbability;
						// q_i = psi*P_i*i_B + P_i*q_{i+1}
						if (probabilities != null) {
							newProb += psi * transitionProbability * probabilities.getProbability(toState, is_minimum);
						} else if (true_states.containsState(toState)) {
							// i_B = 1
							newProb += psi * transitionProbability;
						}
						newProb += transitionProbability * getProbability(toState, is_minimum, !write_cache);
					}
				}
				setProbability(state, newProb, is_minimum, write_cache);
			}
		}
		assert write_cache == false;
	}
	
	
	/**
	 * Unbounded until operator. Model check on the embedded DTMC, except that we can
	 * leave the self loops from uniformisation since they do not affect until properties.
	 */
	private void unboundedUntil(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2) {
		// Gather those states that have probability 0 or 1
		AbstractCTMCProperty min_true_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty min_false_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty max_true_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty max_false_states = new AbstractCTMCProperty(abstractCTMC);
		for (AbstractCTMCState state : abstractCTMC) {
			state.resetProbabilities();
			AbstractBoolean v1 = propertyManager.test(property1, state);
			AbstractBoolean v2 = propertyManager.test(property2, state);
			AbstractBoolean isOK = v2;
			AbstractBoolean isPossiblyOK = AbstractBoolean.or(v1, v2);
			if (isOK == AbstractBoolean.TRUE) {
				min_true_states.addState(state);
				max_true_states.addState(state);
			} else if (isOK == AbstractBoolean.MAYBE) {
				max_true_states.addState(state);
			}
			if (isPossiblyOK == AbstractBoolean.FALSE) {
				min_false_states.addState(state);
				max_false_states.addState(state);
			} else if (isPossiblyOK == AbstractBoolean.MAYBE) {
				min_false_states.addState(state);
			}
		}
		
		// Compute lower bound
		unboundedReachability(min_true_states, min_false_states, property1, true);
		unboundedUntil(min_true_states, min_false_states, true);
		
		// Compute upper bound
		unboundedReachability(max_true_states, max_false_states, property1, false);
		unboundedUntil(max_true_states, max_false_states, false);
	}
	
	/**
	 * Reachability - compute states that can only reach states that are definitely true or false,
	 * whilst satisfying the given property.
	 */
	private void unboundedReachability(AbstractCTMCProperty true_states, AbstractCTMCProperty false_states, CSLAbstractStateProperty property, boolean is_minimum) {
		while (true_states.hasChanged() || false_states.hasChanged()) {
			true_states.resetChanged();
			false_states.resetChanged();
			for (AbstractCTMCState state : abstractCTMC) {
				if (true_states.containsState(state) || false_states.containsState(state)) continue;
				ArrayList<AbstractCTMCTransition> transitions = state.getTransitions();
				AbstractBoolean satisfiesProperty = propertyManager.test(property, state);
				boolean definitelyOK = is_minimum ? satisfiesProperty == AbstractBoolean.TRUE : satisfiesProperty != AbstractBoolean.FALSE;
				boolean definitelyNotOK = true;
				// All transitions must lead to a definitelyOK state (and we satisfy property1)
				// Or, all transitions lead to a definitelyNotOK state
				for (AbstractCTMCTransition t : transitions) {
					AbstractCTMCState nextState = t.getToState();
					if (nextState.equals(state)) continue;
					if (!false_states.containsState(nextState)) {
						definitelyNotOK = false;
					} 
					if (!true_states.containsState(nextState)) {
						definitelyOK = false;
					}
				}
				if (definitelyNotOK) {
					false_states.addState(state);
				} else if (definitelyOK) {
					true_states.addState(state);
				}
			}
		}
	}
	
	/**
	 * Iterative algorithm for computing the unbounded until property of states that are not
	 * definitely true or false. We use value iteration.
	 */
	private void unboundedUntil(AbstractCTMCProperty true_states, AbstractCTMCProperty false_states, boolean is_minimum) {
		// Pre-processing
		if (is_minimum) {
			for (AbstractCTMCState state : abstractCTMC) {
				if (true_states.containsState(state)) {
					setProbability(state, 1, is_minimum, false);
					setProbability(state, 1, is_minimum, true);
				} else {
					setProbability(state, 0, is_minimum, false);
					setProbability(state, 0, is_minimum, true);
				}
			}
		} else {
			for (AbstractCTMCState state : abstractCTMC) {
				if (false_states.containsState(state)) {
					setProbability(state, 0, is_minimum, false);
					setProbability(state, 0, is_minimum, true);
				} else {
					setProbability(state, 1, is_minimum, false);
					setProbability(state, 1, is_minimum, true);
				}
			}
		}
		
		// Main algorithm - value iteration
		boolean probabilities_changed = true;
		boolean write_cache = false;
		while (probabilities_changed || write_cache) {
			probabilities_changed = false;
			write_cache = !write_cache;	
			for (AbstractCTMCState state : abstractCTMC) {
				if (true_states.containsState(state) || false_states.containsState(state)) continue;
				double prob = 0;
				double probabilityRemaining = 1;
				ArrayList<AbstractCTMCTransition> transitions = state.getSortedTransitions(is_minimum, !write_cache);
				// Keep track of the minimum possible probability we have left to assign
				double minimumRemaining = 0;
				for (AbstractCTMCTransition t : transitions) minimumRemaining += t.getMinProb();
				for (AbstractCTMCTransition t : transitions) {
					AbstractCTMCState nextState = t.getToState();
					minimumRemaining -= t.getMinProb();
					double nextProb = getProbability(nextState, is_minimum, !write_cache);
					double transitionProbability = Math.min(t.getMaxProb(), probabilityRemaining - minimumRemaining);
					probabilityRemaining -= transitionProbability;
					prob += transitionProbability * nextProb;
				}
				double currentProb = getProbability(state, is_minimum, !write_cache);
				//assert is_minimum ? prob >= currentProb : prob <= currentProb;
				// Must converge...
				if (Math.abs(currentProb - prob) > boundAccuracy) probabilities_changed = true;
				setProbability(state, prob, is_minimum, write_cache);
			}
		}
		assert write_cache == false;
	}
	
	
	/**
	 * Long-run operator. Model check on the embedded MDP of the uniformised CTMDP
	 */
	private void longRun(CSLAbstractStateProperty property) {
		// Gather those states that satisfy the property
		AbstractCTMCProperty min_true_states = new AbstractCTMCProperty(abstractCTMC);
		AbstractCTMCProperty max_true_states = new AbstractCTMCProperty(abstractCTMC);
		for (AbstractCTMCState state : abstractCTMC) {
			state.resetProbabilities();
			AbstractBoolean isOK = propertyManager.test(property, state);
			if (isOK == AbstractBoolean.TRUE) {
				min_true_states.addState(state);
				max_true_states.addState(state);
			} else if (isOK == AbstractBoolean.MAYBE) {
				max_true_states.addState(state);
			}
		}
		
		// Compute lower bound
		longRunIteration(min_true_states, true);
		
		// Compute upper bound
		longRunIteration(max_true_states, false);
	}
	
	/**
	 * Iterative algorithm for computing the long-run averages. We use value iteration.
	 */
	private void longRunIteration(AbstractCTMCProperty true_states, boolean is_minimum) {
		// Pre-processing
		for (AbstractCTMCState state : abstractCTMC) {
			if (true_states.containsState(state)) {
				setProbability(state, 1, is_minimum, false);
				setProbability(state, 1, is_minimum, true);
			} else {
				setProbability(state, 0, is_minimum, false);
				setProbability(state, 0, is_minimum, true);
			}
		}
		
		// Main algorithm - value iteration
		boolean probabilities_changed = true;
		boolean write_cache = false;
		double iteration_count = 1;
		while (probabilities_changed || write_cache) {
			probabilities_changed = false;
			write_cache = !write_cache;
			for (AbstractCTMCState state : abstractCTMC) {
				double add_value = true_states.containsState(state) ? 1 : 0;
				double prob = 0;
				double probabilityRemaining = 1;
				ArrayList<AbstractCTMCTransition> transitions = state.getSortedTransitions(is_minimum, !write_cache);
				// Keep track of the minimum possible probability we have left to assign
				double minimumRemaining = 0;
				for (AbstractCTMCTransition t : transitions) minimumRemaining += t.getMinProb();
				for (AbstractCTMCTransition t : transitions) {
					AbstractCTMCState nextState = t.getToState();
					minimumRemaining -= t.getMinProb();
					double nextProb = getProbability(nextState, is_minimum, !write_cache);
					double transitionProbability = Math.min(t.getMaxProb(), probabilityRemaining - minimumRemaining);
					probabilityRemaining -= transitionProbability;
					prob += transitionProbability * nextProb;
				}
				prob = (iteration_count * prob + add_value) / (iteration_count + 1);
				double currentProb = getProbability(state, is_minimum, !write_cache);
				//assert is_minimum ? prob >= currentProb : prob <= currentProb;
				// Must converge...
				if (Math.abs(prob - currentProb) > boundAccuracy) probabilities_changed = true;
				setProbability(state, prob, is_minimum, write_cache);
			}
			//System.out.println(getProbability(abstractCTMC.getInitialState(), is_minimum, !write_cache));
			iteration_count++;
		}
		System.out.println(iteration_count);
		assert write_cache == false;
	}
	
	/**
	 * Model check the given unbounded next property.
	 */
	private void unboundedNext(CSLAbstractStateProperty property) {
		for (AbstractCTMCState state : abstractCTMC) {
			state.resetProbabilities();
			// Remove the self loop and adjust probabilities
			ArrayList<AbstractCTMCTransition> transitions = AbstractCTMCTransition.noLoopsDelimit(state.getTransitions());
			// Work out the maximum and minimum transition probabilities
			double minProbability = 0;
			double maxProbability = 0;
			double minProbabilityRemaining = 1;
			double maxProbabilityRemaining = 1;
			double minimumRemaining = 0;
			double maximumRemaining = 0;
			for (AbstractCTMCTransition t : transitions) {
				minimumRemaining += t.getMinProb();
				maximumRemaining += t.getMaxProb();
			}
			for (AbstractCTMCTransition t : transitions) {
				minimumRemaining -= t.getMinProb();
				maximumRemaining -= t.getMaxProb();
				AbstractCTMCState nextState = t.getToState();
				AbstractBoolean isOK = propertyManager.test(property, nextState);
				// minimum case - just pick the states that definitely satisfy the property
				if (isOK == AbstractBoolean.TRUE) {
					double transitionProbability = Math.max(t.getMinProb(), minProbabilityRemaining - maximumRemaining);
					minProbabilityRemaining -= transitionProbability;
					minProbability += transitionProbability;
				} else {
					minProbabilityRemaining -= Math.min(t.getMaxProb(), minProbabilityRemaining - minimumRemaining);
				}
				// maximum case - maximise making the property true (definitely true or maybe true)
				if (isOK != AbstractBoolean.FALSE) {
					double transitionProbability = Math.min(t.getMaxProb(), maxProbabilityRemaining - minimumRemaining);
					maxProbabilityRemaining -= transitionProbability;
					maxProbability += transitionProbability;
				} else {
					maxProbabilityRemaining -= Math.max(t.getMinProb(), maxProbabilityRemaining - maximumRemaining);
				}
			}
			state.setMinProbability(minProbability);
			state.setMaxProbability(maxProbability);
		}
	}
	
	private double getProbability(AbstractCTMCState state, boolean is_minimum, boolean read_cache) {
		if (read_cache) {
			return state.getCache();
		} else if (is_minimum) {
			return state.getMinProbability();
		} else {
			return state.getMaxProbability();
		}
	}
	
	private void setProbability(AbstractCTMCState state, double probability, boolean is_minimum, boolean write_cache) {
		// Need to take care of rounding errors
		if (probability < 0) probability = 0;
		if (probability > 1) probability = 1;
		if (write_cache) {
			state.setCache(probability);
		} else if (is_minimum) {
			state.setMinProbability(probability);
		} else {
			state.setMaxProbability(probability);
		}
	}
	
}
