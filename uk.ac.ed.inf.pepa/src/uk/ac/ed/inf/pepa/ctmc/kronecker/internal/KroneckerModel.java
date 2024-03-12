/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialOrder;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SimplePartialSequentialOrder;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.*;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.ApparentRateCalculator;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerActionManager;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds.RateContext;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.CompositionalProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.PropertyBank;

/**
 * This is the base class for construction of a Kronecker representation
 * of a PEPA model.
 * 
 * @author msmith
 *
 */

public class KroneckerModel {
	
	private int numComponents;
	
	private KroneckerActionManager actionManager;
	
	private KroneckerComponent[] model;

	private PropertyBank propertyBank;
	
	private KroneckerDisplayModel displayModel;
		
	public KroneckerModel(int numComponents, KroneckerActionManager actionManager, ISymbolGenerator generator) {
		this.actionManager = actionManager;
		this.numComponents = numComponents;
		this.model         = new KroneckerComponent[numComponents];
		this.propertyBank  = new PropertyBank(numComponents);
		this.displayModel  = new KroneckerDisplayModel(numComponents, generator, propertyBank);
	}
	
	private KroneckerModel(int numComponents, KroneckerActionManager actionManager, KroneckerComponent[] model,
			               PropertyBank propertyBank, KroneckerDisplayModel displayModel) {
		this.actionManager = actionManager;
		this.numComponents = numComponents;
		this.model         = model;
		this.propertyBank  = propertyBank;
		this.displayModel  = displayModel;
	}

	public void initialiseComponent(int component, short initialState, SequentialStateSpace states) {
		SequentialAbstraction abstraction = new SequentialAbstraction(states);
		model[component] = new KroneckerComponent(component, states, abstraction, actionManager);
		model[component].initRateMatrices();
		displayModel.initialiseComponent(component, initialState, abstraction);
	}
	
	public PropertyBank getPropertyBank() {
		return propertyBank;
	}
	
	public SequentialAbstraction getAbstraction(int component) {
		return model[component].getAbstraction();
	}
	
	public SequentialAbstraction[] getAbstraction() {
		SequentialAbstraction[] abstraction = new SequentialAbstraction[numComponents];
		for (int i = 0; i < numComponents; i++) {
			abstraction[i] = model[i].getAbstraction();
		}
		return abstraction;
	}
	
	public KroneckerModel getUpperBoundingModel(CompositionalProperty property) {
		KroneckerModel upperModel = getBound(property, true);
		return upperModel;
	}
	
	public KroneckerModel getLowerBoundingModel(CompositionalProperty property) {
		KroneckerModel lowerModel = getBound(property, false);
		return lowerModel;
	}
	
	private KroneckerModel getBound(CompositionalProperty property, boolean isUpper) {
		// First check if any abstraction is being applied
		boolean isAbstract = false;
		for (int i = 0; i < numComponents; i++) {
			SequentialAbstraction abstraction = model[i].getAbstraction();
			if (abstraction.size() != abstraction.getConcreteStateSpace().size()) {
				isAbstract = true;
			}
		}
		if (!isAbstract) return this;
		
		// Now perform the actual abstraction
		KroneckerComponent[] newModel = new KroneckerComponent[numComponents];
		RateContext[] context = RateContext.makeNewContext(numComponents, numSyncActions());
		SequentialOrder[] orders = new SequentialOrder[numComponents];
				
		// (1) Determine which components need to be bounded
		short[][] abstractProperty = property.getAbstractProperty();
		boolean[] boundedComponents = new boolean[numComponents];
		for (int i = 0; i < numComponents; i++) {
			SequentialAbstraction abstraction = model[i].getAbstraction();
			short[] abstractComponentProperty = abstractProperty[i];
			boolean noAbstraction = abstractComponentProperty.length == abstraction.size();
			boundedComponents[i] = !noAbstraction;
		}
		
		// (2) Compute the rate contexts
		for (int i = 0; i < numComponents; i++) {
			if (boundedComponents[i]) {
				SequentialAbstraction abstraction = model[i].getAbstraction();
				short[] abstractComponentProperty = abstractProperty[i];
				abstraction.reorderStateSpace(abstractComponentProperty);
				orders[i] = new SimplePartialSequentialOrder(abstraction, abstractComponentProperty.length);
				model[i].addRateContext(abstraction, orders[i], context);
			} else {
				// Don't have any ordering constraints on the model
				// We therefore don't need to worry about adding to the context.
				model[i].addEmptyRateContext(context);
			}
		}
			
		// (3) Compute the bounds
		for (int i = 0; i < numComponents; i++) {
			if (boundedComponents[i]) {
				if (isUpper) {
					newModel[i] = model[i].upperBound(context, orders[i]);
				} else {
					newModel[i] = model[i].lowerBound(context, orders[i]);	
				}
			} else {
				// We don't need to compute a bound for the component
				// since there is no abstraction and the property is independent
				// of the state of this component
				newModel[i] = model[i].getAbstractCopy(boundedComponents);
			}
		}
		return new KroneckerModel(numComponents, actionManager, newModel, propertyBank, displayModel);
	}
	
	
	public AbstractKroneckerModel getAbstractModel() {
		AbstractKroneckerComponent[] abstractModel = new AbstractKroneckerComponent[numComponents];
		double uniformisationConstant = 0;
		for (int i = 0; i < numComponents; i++) {
			abstractModel[i] = model[i].abstractComponent();
			uniformisationConstant += model[i].getMaximumRate();
		}
		return new AbstractKroneckerModel(numComponents, actionManager, abstractModel, uniformisationConstant);
	}
	
	public void normaliseRateMatrices() {
		for (int i = 0; i < numComponents; i++) {
			model[i].normaliseRateMatrices();
		}
	}
	
	public void addTransition(short actionID, int component, short state1, short state2, double rate) throws DerivationException {
		short actionIndex = actionManager.getActionIndex(actionID);
		model[component].addTransition(actionIndex, state1, state2, rate);
		displayModel.getComponent(component).addTransition(actionID, state1, state2, rate);
	}
	
	public KroneckerDisplayModel getDisplayModel() {
		return displayModel;
	}
	
	/**
	 * Returns the number of synchronising action types.
	 * We store local action types internally in the same matrix.
	 */
	public int numSyncActions() {
		return actionManager.getNumSyncActions();
	}
	
	public int numComponents() {
		return numComponents;
	}
	
	public ArrayList<Transition> getTransitionsFrom(short[] state) throws DerivationException {
		//System.out.println(Arrays.toString(state));
		assert state.length == numComponents;
		ArrayList<Transition> transitions = new ArrayList<Transition>(10);
		for (int action = 0; action < numSyncActions(); action++) {
			ApparentRateCalculator calculator = actionManager.getApparentRateCalculator((short)action);

			// Look at the reachability for every synchronising action
			// If the apparent rate is zero for some state, totalStates will be zero.
			double[] rates = getRates(state, (short)action);
			double apparentRate = calculator.compute(rates);
			if (apparentRate <= 0) continue;
			
			int totalStates = 1;
			int[] numStates = new int[numComponents];
			short[][] nextStates = new short[numComponents][];
			double[][] nextProb = new double[numComponents][];
			// build the list of reachable states for each component
			for (int i = 0; i < numComponents; i++) {
				//System.out.println("Analysing Component " + j + ", Action type " + generator.getActionLabel(syncActionMap.get(i)));
				short currentState = state[i];
				// For each component, look at the reachable next states
				KroneckerComponent component = model[i];
				StateDistribution next = component.nextSyncStates(action, currentState);
				nextStates[i] = next.getStates();
				nextProb[i]   = next.getProbabilities();
				numStates[i]  = nextStates[i].length;
				totalStates  *= nextStates[i].length;
			}
			
			boolean[][] choices = calculator.getChoices(rates);
			for (int n = 0; n < choices.length; n++) {
				// Consider each combination of enabled activities - this handles the internal choices
				boolean[] enabled = choices[n];
				double rate = calculator.compute(rates, enabled);
				
				// fix numStates so we only consider the enabled components
				int totalStates_ = 1;
				int[] numStates_ = new int[numComponents];
				for (int i = 0; i < numComponents; i++) {
					int num = enabled[i] ? numStates[i] : 1;
					numStates_[i] = num;
					totalStates_ *= num;
				}
				                           
				// iterate over the states that each component can reach, to generate
				// the reachable state space of the composed system
				int[] currentState = new int[numComponents];
				for (int j = 0; j < totalStates_; j++) {
					short[] nextState = new short[numComponents];
					double prob = 1;
					for (int k = 0; k < numComponents; k++) {
						if (enabled[k]) {
							// lookup the actual next state
							nextState[k] = nextStates[k][currentState[k]];
							// compute the probability of the transition
							prob *= nextProb[k][currentState[k]];
						} else {
							nextState[k] = state[k];
						}
					}
					Transition t = new Transition();
					t.fActionId = (short)action;
					t.fRate = rate * prob;
					t.fTargetProcess = nextState;
					transitions.add(t);
					//System.out.println(Arrays.toString(state) + "==(" + actionManager.getActionName((short)action) + ", " + t.fRate + ")==>" + Arrays.toString(nextState));
					KroneckerUtilities.incrementArray(currentState,numStates_);
				}                    	
			}
		}
		
		// We now generate the local transitions
		for (int i = 0; i < numComponents; i++) {
			short currentState = state[i];
			// For each component, look at the reachable next states
			KroneckerComponent component = model[i];
			// Only one component can transition at a time, because these are local transitions
			StateDistribution next = component.nextLocalStates(currentState);
			short[] nextStates = next.getStates();
			double[] nextProb  = next.getProbabilities();
			double rate = component.getLocalRate(currentState);
			// We might have a passive rate in the case of stochastic bounds,
			// since unbounded components are passive on all actions (including local ones).
			if (rate <= 0) continue;
			// Add a transition for every possible next state
			for (int j = 0; j < nextStates.length; j++) {
				// Ignore self loops
				if (nextStates[j] != currentState) {
					short[] nextState = new short[numComponents];
					for (int k = 0; k < nextState.length; k++) {
						// Copy the current state - only change the state of the current component
						nextState[k] = (k == i) ? nextStates[j] : state[k];
					}
					Transition t = new Transition();
					t.fActionId      = -1;
					t.fTargetProcess = nextState;
					t.fRate          = rate * nextProb[j];
					transitions.add(t);
					//System.out.println("[L] " + Arrays.toString(state) + "==(" + t.fRate + ")==>" + Arrays.toString(nextState));
				}
			}			
		}
		return transitions;
	}
	
	private double[] getRates(short[] startState, short actionIndex) throws DerivationException {
		double[] rates = new double[numComponents];
		for (int i = 0; i < numComponents; i++) {
			rates[i] = model[i].getSyncRate(actionIndex, startState[i]);
		}
		return rates;	
	}
	
	/**
	 * Returns the transitions leading to the given state. The transitions point in
	 * the opposite direction (so the previous states are the target state of the
	 * transition).
	 */
	public ArrayList<Transition> getTransitionsTo(short[] state) {
		assert state.length == numComponents;
		ArrayList<Transition> transitions = new ArrayList<Transition>(10);
		for (int action = 0; action < numSyncActions(); action++) {
			// Look at the reachability for every synchronising action
			int totalStates = 1;
			int[] numStates = new int[numComponents];
			short[][] prevStates = new short[numComponents][];
			double[][] prevProb = new double[numComponents][];
			// build the list of possible previous states for each component
			for (int i = 0; i < numComponents; i++) {
				short currentState = state[i];
				// For each component, look at the possible previous states
				KroneckerComponent component = model[i];
				StateDistribution prev = component.prevSyncStates(action, currentState);
				prevStates[i] = prev.getStates();
				prevProb[i]   = prev.getProbabilities();
				numStates[i]  = prevStates[i].length;
				totalStates  *= prevStates[i].length;
			}
			// Iterate over the locally previous states for each component
			// Each state will in general have a different apparent rate, so
			// we need to check whether each transition is possible before returning it
			int[] currentState = new int[numComponents];
			for (int i = 0; i < totalStates; i++) {
				short[] prevState = new short[numComponents];
				double apparent = model[i].getLocalRate(prevStates[0][currentState[0]]);
				double prob = 1;
				for (int k = 0; k < numComponents; k++) {
					// lookup the actual previous state
					prevState[k] = prevStates[k][currentState[k]];
					// compute the apparent rate
					apparent = KroneckerUtilities.rateMin(apparent, model[i].getSyncRate(action, prevState[k]));
					// compute the probability of the transition
					prob *= prevProb[k][currentState[k]];
				}
				if (apparent > 0) {
					Transition t = new Transition();
					t.fActionId      = (short)i;
					t.fRate          = apparent * prob;
					t.fTargetProcess = prevState;
					transitions.add(t);
				}	
				KroneckerUtilities.incrementArray(currentState,numStates);
			}
		}
		
		// We now generate the local transitions (actionIndex = numActions)
		for (int i = 0; i < numComponents; i++) {
			short currentState = state[i];
			// For each component, look at the reachable next states
			KroneckerComponent component = model[i];
			// Only one component can transition at a time, because these are local transitions
			StateDistribution prev = component.prevLocalStates(currentState);
			short[] prevStates = prev.getStates();
			double[] prevProb  = prev.getProbabilities();
			// Add a transition for every possible previous state
			for (int j = 0; j < prevStates.length; j++) {
				short[] prevState = new short[numComponents];
				for (int k = 0; k < prevState.length; k++) {
					// Copy the current state - only change the state of the current component
					prevState[k] = (k == i) ? prevStates[j] : state[k];
				}
				double rate = component.getLocalRate(prevStates[j]);
				if (rate > 0) {
					Transition t = new Transition();
					t.fActionId = -1;
					t.fTargetProcess = prevState;
					t.fRate = rate * prevProb[j];
					transitions.add(t);
				}
			}			
		}
		
		return transitions;
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < numComponents; i++) {
			s += "Component " + i + ":\n";
			s += model[i].toString() + "\n";
		}
		return s;
	}
	
}
