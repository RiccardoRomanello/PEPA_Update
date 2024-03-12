/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;

public class AbstractCTMCTransition {

	private AbstractCTMCState fromState;
	private AbstractCTMCState toState;
	
	private double minProb;
	private double maxProb;
	
	private short actionID;
	
	public AbstractCTMCTransition(AbstractCTMCState fromState, AbstractCTMCState toState, short actionID, double minProb, double maxProb) {
		this.fromState = fromState;
		this.toState = toState;
		this.actionID = actionID;
		this.minProb = minProb;
		this.maxProb = maxProb;
	}
	
	public AbstractCTMCTransition(AbstractCTMCState fromState, AbstractCTMCState toState, double minProb, double maxProb) {
		this(fromState, toState, (short)-1, minProb, maxProb);
	}
	
	public AbstractCTMCState getFromState() {
		return fromState;
	}
	
	public AbstractCTMCState getToState() {
		return toState;
	}
	
	public short getActionID() {
		return actionID;
	}
	
	public void setToState(AbstractCTMCState state) {
		toState = state;
	}
	
	public double getMinProb() {
		return minProb;
	}
	
	public void setMinProb(double prob) {
		minProb = prob;
	}
	
	public double getMaxProb() {
		return maxProb;
	}
	
	public void setMaxProb(double prob) {
		maxProb = prob;
	}
	
	public void addProbability(double minProb, double maxProb) {
		this.minProb += minProb;
		this.maxProb += maxProb;
	}
	
	public void delimit(ArrayList<AbstractCTMCTransition> transitions) {
		double probLower = 0;
		double probUpper = 0;
		for (AbstractCTMCTransition t : transitions) {
			if (t == this) continue;
			probLower += t.minProb;
			probUpper += t.maxProb;
		}
		minProb = Math.max(minProb, 1 - probUpper);
		maxProb = Math.min(maxProb, 1 - probLower);
		if (maxProb < minProb) {
			// This can happen due to rounding errors
			double temp = minProb;
			minProb = maxProb;
			maxProb = temp;
		}
	}
	
	/**
	 * Adjust the probabilities to account for not including the self loop
	 * and then delimit to remove unreachable probabilities.
	 */
	public static ArrayList<AbstractCTMCTransition> noLoopsDelimit(ArrayList<AbstractCTMCTransition> transitions) {
		// Make a copy of the transitions
		ArrayList<AbstractCTMCTransition> newTransitions = new ArrayList<AbstractCTMCTransition>(transitions);
		// Find the self loop
		AbstractCTMCTransition self_loop = null;
		for (AbstractCTMCTransition t : newTransitions) {
			if (t.getToState().equals(t.getFromState())) {
				self_loop = t;
			} 
		}
		if (self_loop != null) {
			newTransitions.remove(self_loop);
			double minProbabilityFactor = self_loop.getMaxProb();
			double maxProbabilityFactor = self_loop.getMinProb();
			// Adjust the probabilities
			for (AbstractCTMCTransition t : newTransitions) {
				if (minProbabilityFactor < 1) {
					t.minProb += (t.minProb * minProbabilityFactor) / (1 - minProbabilityFactor);
				} if (maxProbabilityFactor < 1) {
					t.maxProb += (t.maxProb * maxProbabilityFactor) / (1 - maxProbabilityFactor);
				}
			}
			// No need to delimit the transitions again
		}
		return newTransitions;
	}
	
	public boolean equals(Object o) {
		if (o instanceof AbstractCTMCTransition) {
			AbstractCTMCTransition t = (AbstractCTMCTransition)o;
			return fromState.equals(t.fromState) && toState.equals(t.toState) &&
			       maxProb == t.maxProb && minProb == t.minProb;
		}
		return false;
	}
	
	public boolean isNonDeterministic() {
		return minProb != maxProb;
	}
	
	public String toString() {
		return fromState + "==(" + minProb + "," + maxProb + ")==>" + toState;
	}
	
	public String toStringPartial() {
		return "==(" + minProb + "," + maxProb + ")==>" + toState;
	}
	
}
