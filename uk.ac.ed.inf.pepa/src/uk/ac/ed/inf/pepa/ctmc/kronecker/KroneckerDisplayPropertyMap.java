/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.PropertyDependencyException;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.AtomicProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.PropertyBank;

/**
 * Contains a mapping of properties onto the main PEPA model.
 * 
 * @author msmith
 * 
 */
public class KroneckerDisplayPropertyMap {

	private int componentID;
	private PropertyBank propertyBank;
	private HashMap<String, AtomicProperty> propertyMap;
	private KroneckerDisplayComponent component;
	private SequentialAbstraction abstractStateSpace;
	private SequentialStateSpace concreteStateSpace;

	public KroneckerDisplayPropertyMap(PropertyBank propertyBank, KroneckerDisplayComponent component, SequentialAbstraction stateSpace) {
		this.componentID = component.getComponentID();
		this.propertyBank = propertyBank;
		this.component = component;
		this.abstractStateSpace = stateSpace;
		this.concreteStateSpace = stateSpace.getConcreteStateSpace();
		this.propertyMap = new HashMap<String, AtomicProperty>();
		propertyBank.register(this);
	}

	public SequentialStateSpace getSequentialStateSpace() {
		return concreteStateSpace;
	}

	public void notifyAddProperty(String name, AtomicProperty property) {
		property.addPropertyMap(componentID, concreteStateSpace, true);
		propertyMap.put(name, property);
	}

	public HashMap<String, AtomicProperty> getPropertyMap() {
		return propertyMap;
	}

	public String addProperty(String name) {
		return propertyBank.addAtomicProperty(name);
	}

	public String addProperty() {
		return propertyBank.addAtomicProperty();
	}

	public void notifyRemoveProperty(String name) {
		propertyMap.remove(name);
	}

	public void removeProperty(String name) throws PropertyDependencyException {
		propertyBank.removeAtomicProperty(name);
	}
	
	public void removeAllProperties() {
		propertyBank.removeAllProperties();
	}

	public void notifyRenameProperty(String oldName, String newName) {
		AtomicProperty property = propertyMap.remove(oldName);
		propertyMap.put(newName, property);
	}

	public String renameProperty(String oldName, String newName) {
		return propertyBank.renameAtomicProperty(oldName, newName);
	}

	public void setProperty(String name, ArrayList<KroneckerDisplayState> states, boolean isTrue) {
		AtomicProperty property = propertyMap.get(name);
		if (property == null)
			return;
		for (KroneckerDisplayState state : states) {
			int index = concreteStateSpace.getIndex(state.getID());
			if (index >= 0) {
				property.setProperty(componentID, index, isTrue);
			}
		}
		propertyBank.notifyCSLPropertyValueChanged(name);
	}

	public void setProperty(String name, KroneckerDisplayState states, boolean isTrue) {
		AtomicProperty property = propertyMap.get(name);
		if (property == null)
			return;

		int index = concreteStateSpace.getIndex(states.getID());
		if (index >= 0) {
			property.setProperty(componentID, index, isTrue);
			propertyBank.notifyCSLPropertyValueChanged(name);
		}
	}

	public void setPropertyAll(String name, boolean isTrue) {
		AtomicProperty property = propertyMap.get(name);
		if (property == null)
			return;
		property.setPropertyAll(componentID, isTrue);
		propertyBank.notifyCSLPropertyValueChanged(name);
	}

	public ArrayList<KroneckerDisplayState> getStates(String name, boolean isTrue) {
		int numStates = concreteStateSpace.size();
		AtomicProperty property = propertyMap.get(name);
		ArrayList<KroneckerDisplayState> states = new ArrayList<KroneckerDisplayState>(numStates);
		if (property == null)
			return states;
		for (int i = 0; i < numStates; i++) {
			if (property.getProperty(componentID, i) == isTrue) {
				states.add(component.getModel().getState(concreteStateSpace.getState(i)));
			}
		}
		return states;
	}

	public ArrayList<KroneckerDisplayState> getAllStates() {
		ArrayList<KroneckerDisplayState> allStates = new ArrayList<KroneckerDisplayState>(concreteStateSpace.size());
		for (int i = 0; i < concreteStateSpace.size(); i++) {
			allStates.add(component.getModel().getState(concreteStateSpace.getState(i)));
		}
		return allStates;
	}

	private boolean testProperty(AtomicProperty property, KroneckerDisplayState state, boolean isTrue) {
		int index = concreteStateSpace.getIndex(state.getID());
		return property.getProperty(componentID, index) == isTrue;
	}
	
	public boolean testProperty(String name, KroneckerDisplayState state, boolean isTrue) {
		AtomicProperty property = propertyMap.get(name);
		return testProperty(property, state, isTrue);
	}
	
	public boolean testProperty(String name, ArrayList<KroneckerDisplayState> states, boolean isTrue) {
		boolean isValid = true;
		AtomicProperty property = propertyMap.get(name);
		if (property == null)
			return false;
		for (KroneckerDisplayState state : states) {
			isValid = isValid && testProperty(property, state, isTrue);
		}
		return isValid;
	}

	/**
	 * Returns true if the abstract state that the given state maps to is
	 * consistent in its satisfaction of properties.
	 */
	public boolean isConsistent(KroneckerDisplayState state) {
		short abstractState = abstractStateSpace.getAbstractState(state.getID());
		short[] concreteStates = abstractStateSpace.getConcreteStates(abstractState);
		if (concreteStates.length <= 1)
			return true;
		boolean isConsistent = true;
		for (Entry<String, AtomicProperty> property : propertyMap.entrySet()) {
			boolean isPropertyConsistent = true;
			boolean propertyTruth = property.getValue().getProperty(componentID, concreteStateSpace.getIndex(concreteStates[0]));
			for (int i = 1; i < concreteStates.length; i++) {
				if (property.getValue().getProperty(componentID, concreteStateSpace.getIndex(concreteStates[i])) != propertyTruth) {
					isPropertyConsistent = false;
				}
			}
			isConsistent = isConsistent && isPropertyConsistent;
		}
		return isConsistent;
	}

	public String[] getProperties() {
		return propertyBank.getAtomicPropertyNames();
	}

}