/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;

public class AbstractCTMCState {

	// Cache of the hash code for efficiency, to avoid recomputing it each time
	private int hashCode;
	private int index;
	
	private short[] state;
	private int propertyTrue;
	private int propertyFalse;
	private double minProbability;
	private double maxProbability;
	private double cache; //clunky, but we need this for computing time bounded reachability
	private ArrayList<AbstractCTMCTransition> transitions;
	
	private AbstractCTMCState(int index, short[] state) {
		this.index = index;
		this.state = state;
		this.transitions = new ArrayList<AbstractCTMCTransition>(10);
		this.hashCode = Arrays.hashCode(state);
	}
	
	public AbstractCTMCState(short[] state) {
		this(0, state);
	}
	
	/**
	 * Create a new AbstractCTMCState based on the existing state, but not
	 * keeping its transitions.
	 */
	public AbstractCTMCState(AbstractCTMCState state) {
		this(state.index, state.state);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int newIndex) {
		index = newIndex;
	}
	
	public void addTransition(AbstractCTMCState nextState, double minRate, double maxRate) {
		if (minRate < 0 || maxRate <= 0) return;
		AbstractCTMCTransition transition = new AbstractCTMCTransition(this, nextState, minRate, maxRate);
		transitions.add(transition);
	}
	
	public void addTransition(AbstractCTMCTransition transition) {
		if (transition.getMinProb() < 0 || transition.getMaxProb() <= 0) return;
		transitions.add(transition);
	}
	
	public ArrayList<AbstractCTMCTransition> getTransitions() {
		return transitions;
	}
	
	public ArrayList<AbstractCTMCTransition> getSortedTransitions(boolean minimise, boolean use_cache) {
		if (minimise) {
			return getSortedTransitionsMin(use_cache);
		} else {
			return getSortedTransitionsMax(use_cache);
		}
	}
	
	private ArrayList<AbstractCTMCTransition> getSortedTransitionsMax(final boolean use_cache) {
		ArrayList<AbstractCTMCTransition> transitionsToSort = new ArrayList<AbstractCTMCTransition>(transitions);
		Collections.sort(transitionsToSort, new Comparator<AbstractCTMCTransition>() {
			public int compare(AbstractCTMCTransition t1, AbstractCTMCTransition t2) {
				if (use_cache) {
					return Double.compare(t2.getToState().cache, t1.getToState().cache);
				} else {
					return Double.compare(t2.getToState().maxProbability, t1.getToState().maxProbability);
				}
			}
		});
		return transitionsToSort;
	}
	
	private ArrayList<AbstractCTMCTransition> getSortedTransitionsMin(final boolean use_cache) {
		ArrayList<AbstractCTMCTransition> transitionsToSort = new ArrayList<AbstractCTMCTransition>(transitions);
		Collections.sort(transitionsToSort, new Comparator<AbstractCTMCTransition>() {
			public int compare(AbstractCTMCTransition t1, AbstractCTMCTransition t2) {
				if (use_cache) {
					return Double.compare(t1.getToState().cache, t2.getToState().cache);
				} else {
					return Double.compare(t1.getToState().minProbability, t2.getToState().minProbability);
				}
			}
		});
		return transitionsToSort;
	}
	
	public short[] getState() {
		return state;
	}
	
	public void setMinProbability(double probability) {
		minProbability = probability;
	}
	
	public void setMaxProbability(double probability) {
		maxProbability = probability;
	}
	
	public void setCache(double probability) {
		cache = probability;
	}
	
	public void resetProbabilities() {
		minProbability = 0;
		maxProbability = 1;
	}
	
	public double getMinProbability() {
		return minProbability;
	}
	
	public double getMaxProbability() {
		return maxProbability;
	}
	
	public double getCache() {
		return cache;
	}
	
	public AbstractBoolean getProperty(int i) {
		assert i >= 0 && i < 32;
		int mask = 1 << i;
		boolean isTrue = (mask & propertyTrue) == mask;
		boolean isFalse = (mask & propertyFalse) == mask;
		if (isTrue) {
			if (isFalse) {
				return AbstractBoolean.MAYBE;
			} else {
				return AbstractBoolean.TRUE;
			}
		} else {
			if (isFalse) {
				return AbstractBoolean.FALSE;
			} else {
				return AbstractBoolean.NOT_SET;
			}
		}
	}
	
	public void setProperty(int i, boolean isTrue, boolean isFalse) {
		assert i >= 0 && i < 32;
		int mask = 1 << i;
		if (isTrue) {
			if (isFalse) {
				propertyTrue |= mask;
				propertyFalse |= mask;
			} else {
				propertyTrue |= mask;
				propertyFalse &= ~mask;
			}
		} else {
			if (isFalse) {
				propertyTrue &= ~mask;
				propertyFalse |= mask;
			} else {
				propertyTrue &= ~mask;
				propertyFalse &= ~mask;
			}
		}
	}
	
	public void setProperty(int i, AbstractBoolean value) {
		assert i >= 0 && i < 32;
		int mask = 1 << i;
		switch (value) {
		case MAYBE:
			propertyTrue |= mask;
			propertyFalse |= mask;
			break;
		case TRUE:
			propertyTrue |= mask;
			propertyFalse &= ~mask;
			break;
		case FALSE:
			propertyTrue &= ~mask;
			propertyFalse |= mask;
			break;
		case NOT_SET:
			propertyTrue &= ~mask;
			propertyFalse &= ~mask;
			break;
		default:
			assert false;
		}
	}
	
	public String toString() {
		return Arrays.toString(state);
	}
	
	public String toStringFull() {
		String name = toString();
		char[] spaces = new char[name.length()];
		Arrays.fill(spaces, ' ');
		String space = new String(spaces);
		String stateString = toString() + " :: T=" + Integer.toBinaryString(propertyTrue) + "\n"
		                   + space + " :: F=" + Integer.toBinaryString(propertyFalse) + "\n"
		                   + space + " :: Probability = [" + minProbability + "," + maxProbability + "]";
		for (AbstractCTMCTransition t : transitions) {
			stateString += "\n" + space + " " + t.toStringPartial();
		}
		return stateString;
	}
	
	public boolean equals(Object o) {
		if (o instanceof AbstractCTMCState) {
			return Arrays.equals(state, ((AbstractCTMCState)o).state);
		}
		return false;
	}
	
	public int hashCode() {
		return hashCode;
	}
	
}
