/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.ApparentRateCalculator;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerActionManager;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.AbstractCTMCState;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.AbstractCTMCTransition;

public class AbstractKroneckerModel {

	private int numComponents;
	
	private KroneckerActionManager actionManager;
	
	private AbstractKroneckerComponent[] model;
	
	private double uniformisationConstant;
	
	private double maximumExitRate;
	
	public AbstractKroneckerModel(int numComponents, KroneckerActionManager actionManager, AbstractKroneckerComponent[] model, double uniformisationConstant) {
		this.actionManager = actionManager;
		this.numComponents = numComponents;
		this.model         = model;
		this.uniformisationConstant = uniformisationConstant;
		this.maximumExitRate = 0;
	}
	
	public double getUniformisationConstant() {
		return uniformisationConstant;
	}
	
	/**
	 * This method should only be called after the entire state space of the
	 * Abstract Kronecker model has been explored - it allows us to contract
	 * the uniformisation constant to the smallest possible value.
	 */
	public void optimiseUniformisationConstant() {
		uniformisationConstant = maximumExitRate;
		//uniformisationConstant = Math.round(maximumExitRate * AbstractCTMC.DECIMAL_PRECISION)
		//							/ AbstractCTMC.DECIMAL_PRECISION;
	}
	
	public ArrayList<AbstractCTMCTransition> getTransitionsFrom(AbstractCTMCState state) throws DerivationException {
		short[] stateID = state.getState();
		assert stateID.length == numComponents;
		ArrayList<AbstractCTMCTransition> transitions = new ArrayList<AbstractCTMCTransition>(10);
		
		// Synchronising activities
		for (int action = 0; action < numSyncActions(); action++) {
			ApparentRateCalculator calculator = actionManager.getApparentRateCalculator((short)action);
			
			// Look at the reachability for every synchronising action
			// If the apparent rate is zero for some state, totalStates will be zero.
			double[] lowerRates = getRates(stateID, (short)action, false);
			double[] upperRates = getRates(stateID, (short)action, true);
			if (calculator.compute(upperRates) == 0) continue;
			
			int totalStates = 1;
			int[] numStates = new int[numComponents];
			short[][] nextStates = new short[numComponents][];
			double[][] nextLowerProb = new double[numComponents][];
			double[][] nextUpperProb = new double[numComponents][];
			// build the list of reachable states for each component
			for (int i = 0; i < numComponents; i++) {
				short currentState = stateID[i];
				AbstractKroneckerComponent component = model[i];
				NextStateInformation nextInfo = component.nextSyncStates(action, currentState);
				nextStates[i] = nextInfo.getNextStates();
				nextLowerProb[i] = nextInfo.getNextLowerProbabilities();
				nextUpperProb[i] = nextInfo.getNextUpperProbabilities();
				numStates[i] = nextStates[i].length;
				totalStates *= nextStates[i].length;
				//System.out.println("Component " + i + " action " + action + " has " + nextStates[i].length + " states");
			}
			
			boolean[][] choices = calculator.getChoices(upperRates);
			for (int n = 0; n < choices.length; n++) {
				// Consider each combination of enabled activities - this handles the internal choices
				boolean[] enabled = choices[n];
				double upperRate = calculator.compute(upperRates, enabled) / uniformisationConstant;
				double lowerRate = calculator.compute(lowerRates, enabled) / uniformisationConstant;
				
				// fix numStates so we only consider the enabled components
				int totalStates_ = 1;
				int[] numStates_ = new int[numComponents];
				for (int i = 0; i < numComponents; i++) {
					int num = enabled[i] ? numStates[i] : 1;
					numStates_[i] = num;
					totalStates *= num;
				}
				                           
				// iterate over the states that each component can reach, to generate
				// the reachable state space of the composed system
				int[] currentState = new int[numComponents];
				for (int j = 0; j < totalStates_; j++) {
					short[] nextState = new short[numComponents];
					double lowerProb = lowerRate;
					double upperProb = upperRate;
					for (int k = 0; k < numComponents; k++) {
						if (enabled[k]) {
							// lookup the actual next state
							nextState[k] = nextStates[k][currentState[k]];
							// compute the probability of the transition
							lowerProb *= nextLowerProb[k][currentState[k]];
							upperProb *= nextUpperProb[k][currentState[k]];
						} else {
							nextState[k] = stateID[k];
						}
					}
					AbstractCTMCState nextCTMCState = new AbstractCTMCState(nextState);
					AbstractCTMCTransition transition = new AbstractCTMCTransition(state, nextCTMCState, (short)action, lowerProb, upperProb);
					transitions.add(transition);
					KroneckerUtilities.incrementArray(currentState,numStates_);
				}                    	
			}
		}
		
		// Keep track of the total exit probability from the state.
		// Need this to create the correct self loop.
		double lowerExitProb = 0;
		double upperExitProb = 0;
		for (AbstractCTMCTransition t : transitions) {
			lowerExitProb += t.getMinProb();
			upperExitProb += t.getMaxProb();
		}
		
		// Local activities
		for (int i = 0; i < numComponents; i++) {
			short currentState = stateID[i];
			// For each component, look at the reachable next states
			AbstractKroneckerComponent component = model[i];
			// Only one component can transition at a time, because these are local transitions
			NextStateInformation nextInfo = component.nextLocalStates(currentState);
			short[] nextStates = nextInfo.getNextStates();
			double[] nextLowerProb = nextInfo.getNextLowerProbabilities();
			double[] nextUpperProb = nextInfo.getNextUpperProbabilities();
			// Uniformise the rates
			double lowerRate = component.getLocalLowerRate(currentState) / uniformisationConstant;
			double upperRate = component.getLocalUpperRate(currentState) / uniformisationConstant;
			if (upperRate == 0) continue;
			// Add a transition for every possible next state
			for (int j = 0; j < nextStates.length; j++) {
				// Ignore self loops
				if (nextStates[j] != currentState) {
					short[] nextState = new short[numComponents];
					for (int k = 0; k < nextState.length; k++) {
						// Copy the current state - only change the state of the current component
						nextState[k] = (k == i) ? nextStates[j] : stateID[k];
					}
					assert !Arrays.equals(nextState, stateID);
					AbstractCTMCState nextCTMCState = new AbstractCTMCState(nextState);
					if (upperRate * nextUpperProb[j] > 0) {
						double lowerProb = lowerRate * nextLowerProb[j];
						double upperProb = upperRate * nextUpperProb[j];
						AbstractCTMCTransition transition = new AbstractCTMCTransition(state, nextCTMCState, lowerProb, upperProb);
						lowerExitProb += lowerProb;
						upperExitProb += upperProb;
						//System.out.println("[L]" + transition);
						transitions.add(transition);
					}
				}
			}			
		}
		
		if (upperExitProb > 1.0000001) System.out.println("Prob = " + upperExitProb);
		assert upperExitProb <= 1.0000001;
		upperExitProb = Math.min(upperExitProb, 1);
		maximumExitRate = Math.max(maximumExitRate, upperExitProb * uniformisationConstant);
		
		// Add the self loop that we need to introduce due to uniformisation
		if (lowerExitProb < 1) {
			AbstractCTMCTransition self_loop = new AbstractCTMCTransition(state, state, 1 - upperExitProb, 1 - lowerExitProb);
			transitions.add(self_loop);
		}
		
		return transitions;
	}
	
	private double[] getRates(short[] startState, short actionIndex, boolean isUpper) throws DerivationException {
		double[] rates = new double[numComponents];
		for (int i = 0; i < numComponents; i++) {
			if (isUpper) {
				rates[i] = model[i].getSyncUpperRate(actionIndex, startState[i]);
			} else {
				rates[i] = model[i].getSyncLowerRate(actionIndex, startState[i]);
			}
		}
		return rates;		
	}
	
	public SequentialAbstraction[] getAbstractStateSpace() {
		SequentialAbstraction[] abstractStateSpace = new SequentialAbstraction[numComponents];
		for (int i = 0; i < numComponents; i++) {
			abstractStateSpace[i] = model[i].getAbstraction();
		}
		return abstractStateSpace;
	}
	
	/**
	 * Returns the number of synchronising action types.
	 * We store local action types internally in the same matrix.
	 */
	public int numSyncActions() {
		return actionManager.getNumSyncActions();
	}
	
}
