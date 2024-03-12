/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.abstraction.AggregationException;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.PropertyBank;

public class KroneckerDisplayComponent {

	private int componentID;
	private KroneckerDisplayState componentState;
	private KroneckerDisplayModel model;
	private ArrayList<KroneckerDisplayTransition> transitions;
	private SequentialAbstraction abstraction;
	private ShortArray actions;
	private KroneckerDisplayPropertyMap propertyMap;
	
	private ShortArray selectedStates;

	KroneckerDisplayComponent(int componentID, KroneckerDisplayState componentState, KroneckerDisplayModel model, PropertyBank propertyBank, SequentialAbstraction abstraction) {
		this.componentID    = componentID;
		this.componentState = componentState;
		this.model          = model;
		this.transitions    = new ArrayList<KroneckerDisplayTransition>(20);
		this.abstraction    = abstraction;
		this.actions        = new ShortArray(10);
		this.propertyMap    = new KroneckerDisplayPropertyMap(propertyBank, this, abstraction);
		this.selectedStates = new ShortArray(10);
	}
	
	public String getName() {
		return componentState.getLabel(false);
	}
	
	public int getComponentID() {
		return componentID;
	}
	
	public KroneckerDisplayPropertyMap getPropertyMap() {
		return propertyMap;
	}
	
	public void addTransition(short actionID, short state1, short state2, double rate) {
		KroneckerDisplayState s1 = model.getState(state1);
		KroneckerDisplayState s2 = model.getState(state2);
		KroneckerDisplayAction action = new KroneckerDisplayAction(actionID, rate, model);
		KroneckerDisplayTransition transition = new KroneckerDisplayTransition(s1, s2, action);
		if (!transitions.contains(transition)) {
			transitions.add(transition);
			actions.addNew(actionID);
		}
	}
	
	public short getAbstractState(KroneckerDisplayState state) {
		return abstraction.getAbstractState(state.getID());
	}
	
	/**
	 * Returns the set of states in the same aggregate as the given state
	 */
	public KroneckerDisplayState[] getAggregate(KroneckerDisplayState state) {
		short abstractState = getAbstractState(state);
		short[] concreteStates = abstraction.getConcreteStates(abstractState);
		KroneckerDisplayState[] aggregates = new KroneckerDisplayState[concreteStates.length];
		for (int i = 0; i < concreteStates.length; i++) {
			aggregates[i] = model.getState(concreteStates[i]);
		}
		return aggregates;
	}
	
	public KroneckerDisplayTransition[] getTransitions() {
		KroneckerDisplayTransition[] transitionArray = new KroneckerDisplayTransition[transitions.size()];
		transitions.toArray(transitionArray);
		return transitionArray;
	}
	
	public KroneckerDisplayModel getModel() {
		return model;
	}
	
	public void selectState(KroneckerDisplayState state) {
		selectedStates.add(state.getID());
	}
	
	public void deselectState(KroneckerDisplayState state) {
		selectedStates.remove(state.getID());
	}
	
	public void clearSelection() {
		selectedStates.clear();
	}
	
	public boolean isSelected(KroneckerDisplayState state) {
		return selectedStates.contains(state.getID());
	}
	
	public void aggregateSelected() {
		try {
			disaggregateSelected();
			abstraction.aggregate(selectedStates.toArray());
		} catch (AggregationException e) {
			assert false;
		}
	}
	
	public void disaggregateSelected() {
		short[] selected = selectedStates.toArray();
		for (int i = 0; i < selected.length; i++) {
			abstraction.disaggregate(abstraction.getAbstractState(selected[i]));
		}
	}
	
	public int getNumActions() {
		return actions.size();
	}
	
}
