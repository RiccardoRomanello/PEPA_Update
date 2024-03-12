/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import uk.ac.ed.inf.pepa.ctmc.abstraction.AbstractState;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialOrder;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds.ComponentRateContext;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds.RateContext;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds.StochasticBoundsRateWise;

import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 * This class implements the component of a generator matrix for
 * a particular sequential component and action type. Here, we have
 * a vector representing the rate of leaving each state due to the
 * given action, and a probability matrix describing the probability
 * of moving to a given state
 * 
 * @author msmith
 * 
 */

public class RateMatrix {
	
	/**
	 * The rate vector, r_{i,\alpha}
	 * The identity Matrix I_{i,\alpha} is inferred from the rate vector.
	 * if the rate is zero, so is the identity.
	 */
	private SparseVector rateVector;

	/**
	 * The probability transition matrix P_{i,\alpha}
	 */
	private FlexCompRowMatrix probMatrix;
	
	/**
	 * The transpose of probMatrix - stored separately for efficiency
	 */
	private FlexCompRowMatrix revProbMatrix;
	
	/**
	 * Keeps a record of which action type is associated with the matrix
	 */
	private short actionID = -1;
	
	/**
	 * The state space of the sequential component
	 */
	private SequentialStateSpace stateSpace; 
		
	private boolean isAbstracted;
	
	/**
	 * True if we don't perform any actions of the given type
	 * (i.e. we introduce passive self-loops)
	 */
	private boolean isEmpty = true;
	private int emptyRate = 0;
	
	private int componentID;
	
	public RateMatrix(int componentID, SequentialStateSpace states) {
		this(componentID, states, false);
	}
	
	public RateMatrix(int componentID, SequentialStateSpace states,
	          short actionID, SparseVector rateVector,
	          FlexCompRowMatrix probMatrix) {
		this(componentID, states, actionID, rateVector, probMatrix, false);
	}
	
	public RateMatrix(int componentID, SequentialStateSpace states, boolean isAbstracted) {
		if (!isAbstracted) states.register(this);
		this.componentID = componentID;
		this.stateSpace = states;
		this.isAbstracted = isAbstracted;
	}
	
	public RateMatrix(int componentID, SequentialStateSpace stateSpace,
			          short actionID, SparseVector rateVector,
			          FlexCompRowMatrix probMatrix, boolean isAbstracted) {
		if (!isAbstracted) stateSpace.register(this);
		this.componentID = componentID;
		this.stateSpace = stateSpace;
		this.rateVector = rateVector;
		this.probMatrix = probMatrix;
		if (!isAbstracted) this.revProbMatrix = (FlexCompRowMatrix)probMatrix.copy().transpose();
		this.actionID = actionID;
		this.isAbstracted = isAbstracted;
	}
	
	/**
	 * Copy constructor. Note that we do _not_ make a deep copy of the state space, since
	 * we want any changes to the state space to be reflected in the rate matrix.
	 */
	public RateMatrix(RateMatrix copy) {
		componentID = copy.componentID;
		stateSpace = copy.stateSpace;
		if (copy.isAbstracted) stateSpace.register(this);
		isEmpty = copy.isEmpty;
		emptyRate = copy.emptyRate;
		rateVector = copy.rateVector.copy();
		probMatrix = (FlexCompRowMatrix) copy.probMatrix.copy();
		revProbMatrix = copy.revProbMatrix == null ? null : (FlexCompRowMatrix) copy.revProbMatrix.copy();
		actionID = copy.actionID;
		isAbstracted = copy.isAbstracted;
	}
	
	public void setEmptyRatePassive() {
		emptyRate = -1;
	}
	
	/**
	 * Set the rate matrix to zero, so that all transitions are disabled (the
	 * exit rate from every state is zero).
	 */
	public void disableTransitions() {
		init();
	}
	
	private void init() {
		isEmpty = false;
		emptyRate = 0;
		rateVector = new SparseVector(size());
		probMatrix = new FlexCompRowMatrix(size(), size());
		revProbMatrix = new FlexCompRowMatrix(size(), size());
	}
	
	/**
	 * The maximum exit rate of any state. Used to construct a
	 * uniformisation constant - this is always non-negative,
	 * even for passive components.
	 */
	public double getMaximumRate() {
		if (isEmpty) return 0;
		double maxRate = 0;
		for (VectorEntry r : rateVector) {
			maxRate = Math.max(r.get(), maxRate);
		}
		return maxRate;
	}
	
	public void addTransition(short state1, short state2, short actionID, double rate) throws DerivationException {
		//System.out.println("  Adding " + state1 + " --(" + actionID + "," + rate + ")--> " + state2);
		if (isEmpty) init();
		this.actionID = actionID;
		int i1 = getIndex(state1);
		int i2 = getIndex(state2);
		assert rate != 0;
		if (rate < 0) {
			// We are adding a passive transition
			if (rateVector.get(i1) < 0) {
				// We already have other passive transitions
				probMatrix.set(i1, i2, -rate);
			} else if (rateVector.get(i1) == 0) {
				// The first transition we've seen from this state
				rateVector.set(i1, -1);
				probMatrix.set(i1, i2, -rate);
			} else {
				// There's an active transition already
				throw new DerivationException("Active and passive transitions from the same state.");	
			}
		} else {
			// We are adding an active transition
			if (rateVector.get(i1) > 0) {
				// We already have other active transitions
				probMatrix.set(i1, i2, rate);
			} else if (rateVector.get(i1) == 0) {
				// The first transition we've seen from this state
				rateVector.set(i1, 1);
				probMatrix.set(i1, i2, rate);
			} else {
				// There's a passive transition already
				throw new DerivationException("Active and passive transitions from the same state.");	
			}
		}
	}

	/**
	 *  This is called when we've finished adding transitions, to normalise the probability transition matrix.
	 */
	public void normalise() {
		if (isEmpty) return;
		for (int i = 0; i < rateVector.size(); i++) {
			double rate = rateVector.get(i);
			// Look for states that have transitions
			SparseVector nextState = probMatrix.getRow(i);
			double totalRate = 0;
			for (VectorEntry s : nextState) {
				totalRate += s.get();
			}
			if (totalRate != 0) {
				assert rate != 0;
				for (VectorEntry s : nextState) {
					double prob = s.get() / totalRate;
					s.set(prob);
					revProbMatrix.set(s.index(), i, prob);
				}
				rateVector.set(i, rate * totalRate);
			}
		}
	}
	
	/**
	 * Adds diagonal elements to states with zero exit rate, so that the
	 * probMatrix is stochastic. This is needed for the stochastic bounding
	 * algorithm.
	 */
	private FlexCompRowMatrix getStochasticProbMatrix() {
		assert !isEmpty;
		FlexCompRowMatrix stochasticProbMatrix = (FlexCompRowMatrix) probMatrix.copy();
		for (int i = 0; i < rateVector.size(); i++) {
			double rate = rateVector.get(i);
			SparseVector nextState = stochasticProbMatrix.getRow(i);
			if (rate == 0) {
				nextState.zero();
				nextState.set(i, 1);
			}
		}
		return stochasticProbMatrix;
	}
	
	/**
	 * Computes the set of states reachable from the given state for the component
	 * and their respective probabilities
	 */
	public StateDistribution nextStates(short state) {
		if (isEmpty) {
			return StateDistribution.getLoopDistribution(state);
		}
		SparseVector probDist = probMatrix.getRow(getIndex(state));
		if (getRate(state) == 0) {
			return new StateDistribution(0);
		} else {
			int numStates = probDist.getUsed();
			StateDistribution nextStates = new StateDistribution(numStates);
			for (VectorEntry v : probDist) {
				if (v.get() > 0) {
					nextStates.addEntry(getState(v.index()), v.get());
				}
			}
			return nextStates;
		}
	}
	
	/**
	 * Computes the possible previous states for the component in the given state
	 */
	public StateDistribution prevStates(short state) {
		if (isEmpty) {
			return StateDistribution.getLoopDistribution(state);
		}
		SparseVector probDist = revProbMatrix.getRow(getIndex(state));
		int numStates = probDist.getUsed();
		StateDistribution prevStates = new StateDistribution(numStates);
		for (VectorEntry v : probDist) {
			if (v.get() > 0) {
				prevStates.addEntry(getState(v.index()), v.get());
			}
		}
		return prevStates;
	}
	
	/**
	 * Returns the possible action types for a transition between state1 and state2
	 */
	public short getActionID() {
		return actionID;
	}
	
	public double getRate(short state) {
		if (isEmpty) return emptyRate;
		return rateVector.get(getIndex(state));
	}
	
	/**
	 * Swaps index1 for index2 in the rate vector and probability transition matrices
	 */
	public void notifySwap(int index1, int index2) {
		if (isEmpty) return;
		
		// Swap in the rate vector
		double rate1 = rateVector.get(index1);
		double rate2 = rateVector.get(index2);
		rateVector.set(index1,rate2);
		rateVector.set(index2,rate1);
		
		// Swap in the probability transition matrix
		SparseVector row1 = probMatrix.getRow(index1);
		SparseVector row2 = probMatrix.getRow(index2);
		probMatrix.setRow(index1, row2);
		probMatrix.setRow(index2, row1);
		for (int i = 0; i < size(); i++) {
			double prob1 = probMatrix.get(i, index1);
			double prob2 = probMatrix.get(i, index2);
			probMatrix.set(i, index1, prob2);
			probMatrix.set(i, index2, prob1);
		}
		
		// Swap in the reverse probability transition matrix
		SparseVector rev_row1 = revProbMatrix.getRow(index1);
		SparseVector rev_row2 = revProbMatrix.getRow(index2);
		revProbMatrix.setRow(index1, rev_row2);
		revProbMatrix.setRow(index2, rev_row1);
		for (int i = 0; i < size(); i++) {
			double prob1 = revProbMatrix.get(i, index1);
			double prob2 = revProbMatrix.get(i, index2);
			revProbMatrix.set(i, index1, prob2);
			revProbMatrix.set(i, index2, prob1);
		}
	}
	
	private static abstract class LumperCallBack { 
		public abstract void notifyRate(int index, double rate);
		public abstract void notifyProbability(int index1, int index2, double probability);
		public boolean isLumpable() { return false; }
	};
	
	/**
	 * Performs a "lumping" of the rate matrix. This encompasses creation of an abstract rate matrix.
	 */
	private void doLumping(SequentialAbstraction abstraction, LumperCallBack callBack) {
		AbstractState[] abstractStates = abstraction.getAbstractStateSpace();
		for (int k = 0; k < abstractStates.length; k++) {
			AbstractState abstractState1 = abstractStates[k];
			short[] states1 = abstractState1.getConcrete();
			for (int i = 0; i < states1.length; i++) {
				int index = getConcreteIndex(states1[i]);
				callBack.notifyRate(abstractState1.getID(), rateVector.get(index));
			}
			for (int l = 0; l < abstractStates.length; l++) {
				AbstractState abstractState2 = abstractStates[l];
				short[] states2 = abstractState2.getConcrete();
				for (int i = 0; i < states1.length; i++) {
					double prob = 0;
					for (int j = 0; j < states2.length; j++) {
						prob += probMatrix.get(getConcreteIndex(states1[i]), getConcreteIndex(states2[j]));
					}
					callBack.notifyProbability(abstractState1.getID(), abstractState2.getID(), prob);
				}
			}
		}
	}
	
	/**
	 * Creates a lumped rate matrix - this assumes that the rate matrix is lumpable 
	 * 
	 * @param abstraction
	 * @return
	 * @throws NotLumpableException
	 */
	public RateMatrix getLumpedMatrix(SequentialAbstraction abstraction) {
		if (isEmpty) {
			// TODO - need to change the state space
			RateMatrix lumpedMatrix = new RateMatrix(componentID, stateSpace, true);
			if (emptyRate < 0) lumpedMatrix.setEmptyRatePassive();
			return lumpedMatrix;
		}
		int size = abstraction.size();
		final SparseVector lumpedRates = new SparseVector(size);
		final FlexCompRowMatrix lumpedProbMatrix = new FlexCompRowMatrix(size,size);
		LumperCallBack callBack = new LumperCallBack() {
			public void notifyProbability(int index1, int index2, double probability) {
				//System.out.println("Found: " + index1 + " ==(" + probability + ")==> " + index2);				
				if (lumpedProbMatrix.get(index1, index2) != 0) assert lumpedProbMatrix.get(index1, index2) == probability;
				lumpedProbMatrix.set(index1, index2, probability);
			}
			public void notifyRate(int index, double rate) {
				//System.out.println("Found: " + index + " @ " + rate);	
				if (lumpedRates.get(index) != 0) assert lumpedRates.get(index) == rate;
				lumpedRates.set(index,rate);
			}
		};
		doLumping(abstraction, callBack);
		// TODO - need to change the state space
		// Don't really care about the actions, so what to do here?
		RateMatrix lumpedMatrix = new RateMatrix(componentID, stateSpace, actionID, lumpedRates, lumpedProbMatrix, true);
		lumpedMatrix.isEmpty = false;
		return lumpedMatrix;
	}
	
	public boolean isLumpable(SequentialAbstraction abstraction) {
		if (isEmpty) return true;
		// Correctness relies on us iterating through partitions contiguously
		LumperCallBack callBack = new LumperCallBack() {
			private int current_index   = -1;
			private int current_index1  = -1;
			private int current_index2  = -1;
			private double current_prob = 0;
			private double current_rate = 0;
			private boolean isLumpable  = true;
			public void notifyProbability(int index1, int index2, double probability) {
				if (current_index1 == index1 && current_index2 == index2) {
					if (current_prob != probability) {
						isLumpable = false;
					}
				} else {
					current_prob = probability;
				}
			}
			public void notifyRate(int index, double rate) {
				if (current_index == index) {
					if (current_rate != rate) {
						isLumpable = false;
					}
				} else {
					current_rate = rate;
				}
			}
			public boolean isLumpable() {
				return isLumpable;
			}
		};
		doLumping(abstraction, callBack);
		return callBack.isLumpable();
	}
	
	public AbstractRateMatrix getAbstractMatrix(SequentialAbstraction abstraction) {
		if (isEmpty) {
			return new AbstractRateMatrix(abstraction);
		}
		int size = abstraction.size();
		final SparseVector lowerRates = new SparseVector(size);
		final SparseVector upperRates = new SparseVector(size);
		final SparseVector enabledRates = new SparseVector(size);
		final FlexCompRowMatrix lowerProbMatrix = new FlexCompRowMatrix(size,size);
		final FlexCompRowMatrix upperProbMatrix = new FlexCompRowMatrix(size,size);
		final FlexCompRowMatrix enabledTransitions = new FlexCompRowMatrix(size,size);
		LumperCallBack callBack = new LumperCallBack() {
			public void notifyProbability(int index1, int index2, double probability) {
				if (enabledTransitions.get(index1, index2) == 0) {
					lowerProbMatrix.set(index1, index2, probability);
					upperProbMatrix.set(index1, index2, probability);
					enabledTransitions.set(index1, index2, 1);
				} else {
					double old_lower = lowerProbMatrix.get(index1, index2);
					lowerProbMatrix.set(index1, index2, Math.min(old_lower, probability));
					double old_upper = upperProbMatrix.get(index1, index2);
					upperProbMatrix.set(index1, index2, Math.max(old_upper, probability));
				}
			}
			public void notifyRate(int index, double rate) {
				if (enabledRates.get(index) == 0) {
					lowerRates.set(index, rate);
					upperRates.set(index, rate);
					enabledRates.set(index, 1);
				} else {
					double old_lower = lowerRates.get(index);
					if (old_lower < 0 || rate < 0) {
						lowerRates.set(index, Math.max(old_lower, rate));
					} else {
						lowerRates.set(index, Math.min(old_lower, rate));
					}
					double old_upper = upperRates.get(index);
					if (old_upper < 0 || rate < 0) {
						upperRates.set(index, Math.min(old_upper, rate));
					} else {
						upperRates.set(index, Math.max(old_upper, rate));
					}
				}
			}
		};
		doLumping(abstraction, callBack);
		return new AbstractRateMatrix(abstraction, lowerRates, upperRates, lowerProbMatrix, upperProbMatrix);
	}
	
	public void addRateContext(SequentialAbstraction abstraction, SequentialOrder order, RateContext context) {
		if (isEmpty) {
			context.addEmptyComponent(componentID);	
		} else {
			context.addComponent(componentID, rateVector, abstraction, order);
		}
	}
	
	public void addEmptyRateContext(RateContext context) {
		context.addEmptyComponent(componentID);
	}
	
	public RateMatrix getAbstractCopy(boolean makePassive) {
		RateMatrix abstractCopy = new RateMatrix(componentID, stateSpace, true);
		if ((isEmpty && emptyRate < 0) || (!isEmpty && makePassive)) abstractCopy.setEmptyRatePassive();
		return abstractCopy;
	}
	
	public RateMatrix upperBound(SequentialAbstraction abstraction, ComponentRateContext context, SequentialOrder order) {
		if (isEmpty) {
			RateMatrix bound = new RateMatrix(componentID, stateSpace, true);
			if (emptyRate < 0) bound.setEmptyRatePassive();
			return bound;
		} else {
			FlexCompRowMatrix upperProb = StochasticBoundsRateWise.upperBoundMatrix(getStochasticProbMatrix(), abstraction, context, order);
			RateMatrix upperBound = new RateMatrix(componentID, stateSpace, actionID, context.getUpperRateVector(), upperProb, true);
			upperBound.isEmpty = false;
			return upperBound;
		}
	}
	
	public RateMatrix lowerBound(SequentialAbstraction abstraction, ComponentRateContext context, SequentialOrder order) {
		if (isEmpty) {
			RateMatrix bound = new RateMatrix(componentID, stateSpace, true);
			if (emptyRate < 0) bound.setEmptyRatePassive();
			return bound;
		} else {
			FlexCompRowMatrix lowerProb = StochasticBoundsRateWise.lowerBoundMatrix(getStochasticProbMatrix(), abstraction, context, order);
			RateMatrix lowerBound = new RateMatrix(componentID, stateSpace, actionID, context.getLowerRateVector(), lowerProb, true);
			lowerBound.isEmpty = false;
			return lowerBound;
		}
	}
	
	private boolean containsPassive() {
		if (isEmpty) {
			return emptyRate < 0;
		} else {
			for (int i = 0; i < size(); i++) {
				if (rateVector.get(i) < 0) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Uniformise rates for local activities, so that a tighter stochastic bound
	 * can be applied. Note that by definition, no passive rates are allowed.
	 */
	public RateMatrix uniformiseRates() {
		if (isEmpty) {
			RateMatrix unif = new RateMatrix(componentID, stateSpace, true);
			if (emptyRate < 0) unif.setEmptyRatePassive();
			return unif;
		} else {
			double max_rate = getMaximumRate();
			assert !containsPassive();
			if (max_rate > 0) {
				FlexCompRowMatrix newProbMatrix = new FlexCompRowMatrix(size(),size()); 
				SparseVector newRateVector = new SparseVector(size());
				for (int i = 0; i < size(); i++) {
					double old_rate = rateVector.get(i);
					assert old_rate >= 0;
					newRateVector.set(i, max_rate);
					double rate_sum = 0;
					for (int j = 0; j < size(); j++) {
						if (j == i) continue;
						double old_prob = probMatrix.get(i, j);
						if (old_prob > 0) {
							double rate = old_prob * old_rate;
							rate_sum += rate;
							newProbMatrix.set(i, j, rate / max_rate);
						}
					}
					// self loop
					newProbMatrix.set(i, i, 1 - (rate_sum / max_rate));
				}	
				RateMatrix newMatrix = new RateMatrix(componentID, stateSpace, actionID, newRateVector, newProbMatrix, false);
				newMatrix.isEmpty = false;
				return newMatrix;
			} else if (max_rate == 0) {
				// all actions are disabled - treat as an empty matrix
				return new RateMatrix(componentID, stateSpace, false);
			} else {
				// should never have a local rate matrix where all rates are passive 
				assert false;
				return null;
			}
		}
	}
	
	public int size() {
		return stateSpace.size();
	}
	
	private int getConcreteIndex(short state) {
		return stateSpace.getIndex(state);
	}
	
	private int getIndex(short state) {
		if (isAbstracted) {
			return state;
		} else {
			return stateSpace.getIndex(state);
		}
	}
	
	private short getState(int index) {
		if (isAbstracted) {
			return (short)index;
		} else {
			return stateSpace.getState(index);
		}
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
	public String toString() {
		String s = "================================\n";
		s += "Rates:\n" + rateVector;
		s += "Probability Matrix:\n" + probMatrix + "\n";
		s += "================================\n";
		return s;
	}
	
}
