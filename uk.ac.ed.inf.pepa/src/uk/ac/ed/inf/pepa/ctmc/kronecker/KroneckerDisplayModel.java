/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.IPropertyChangedListener;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.PropertyBank;

public class KroneckerDisplayModel {
	
	private HashMap<Short, KroneckerDisplayState> stateMap;
	
	private PropertyBank propertyBank;
	
	private KroneckerDisplayComponent[] components;
	private int numComponents;
	private ISymbolGenerator symbolGenerator;
	
	public KroneckerDisplayModel(int numComponents, ISymbolGenerator symbolGenerator, PropertyBank propertyBank) {
		this.symbolGenerator = symbolGenerator;
		this.numComponents = numComponents;
		this.components = new KroneckerDisplayComponent[numComponents];
		this.stateMap = new HashMap<Short, KroneckerDisplayState>();
		this.propertyBank = propertyBank;
	}
	
	public KroneckerDisplayComponent getComponent(int componentID) {
		return components[componentID];
	}
	
	public KroneckerDisplayState getState(short state) {
		KroneckerDisplayState s = stateMap.get(state);
		if (s == null) {
			s = new KroneckerDisplayState(state, this);
			stateMap.put(state, s);
		}
		return s;
	}
	
	public KroneckerDisplayState getState(String name, int number) {
		int numToFind = number;
		for (KroneckerDisplayState s : stateMap.values()) {
			String sName = getStateName(s.getID(), true);
			if (name.equals(sName)) {
				if (numToFind == 0) {
					return s;
				} else {
					numToFind--;
				}
			}
		}
		return null;
	}
	
	public int getStateNameInstance(KroneckerDisplayState state) {
		String name = getStateName(state.getID(), true);
		int instance = 0;
		for (KroneckerDisplayState s : stateMap.values()) {
			if (s == state) break;
			String sName = getStateName(s.getID(), true);
			if (name.equals(sName)) instance++;
		}
		return instance;
	}
	
	public void initialiseComponent(int componentID, short initialState, SequentialAbstraction abstraction) {		
		components[componentID] = new KroneckerDisplayComponent(componentID, getState(initialState), this, propertyBank, abstraction);
	}
	
	public String getStateName(short state, boolean isShort) {
		String name = symbolGenerator.getProcessLabel(state);
		return isShort ? contract(name) : name;
	}
	
	private String contract(String name) {
		String newString = "";
		boolean inActivity = false;
		boolean inString = false;
		boolean inRate = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '"') {
				inString = !inString;
				newString += c;
			} else if (inString) {
				newString += c;
			} else if (c == '(' && !inActivity) {
				inActivity = true;
			} else if (!inActivity) {
				newString += c;
			} else { // we're not in a string, and in an activity
				if (c == ',') {
					inRate = true;
					continue;
				} else if (c == ')') {
					inActivity = false;
					inRate = false;
					continue;
				}
				if (!inRate) newString += c;
			}
		}
		return newString;
	}
	
	public String getActionName(short actionID) {
		return symbolGenerator.getActionLabel(actionID);
	}
	
	public int getNumComponents() {
		return numComponents;
	}
	
	public String[] getAtomicProperties() {
		return propertyBank.getAtomicPropertyNames();
	}
	
	public boolean containsCSLPropertyName(String name) {
		return propertyBank.containsCSLPropertyName(name);
	}
	
	public boolean addCSLProperty(String name, CSLAbstractStateProperty property) {
		return propertyBank.addCSLProperty(name, property);
	}
	
	public void removeCSLProperty(String name) {
		propertyBank.removeCSLProperty(name);
	}
	
	public boolean changeCSLProperty(String oldName, CSLAbstractStateProperty oldProperty,
			                         String newName, CSLAbstractStateProperty newProperty) {
		return propertyBank.changeCSLProperty(oldName, oldProperty, newName, newProperty);
	}
	
	public String[] getCSLPropertyNames() {
		return propertyBank.getCSLPropertyNames();
	}
	
	public CSLAbstractStateProperty getCSLProperty(String name) {
		return propertyBank.getCSLProperty(name);
	}
	
	public String getCSLShortPropertyValue(String name) {
		return propertyBank.getCSLShortPropertyValue(name);
	}
	
	public String getCSLLongPropertyValue(String name) {
		return propertyBank.getCSLLongPropertyValue(name);
	}
	
	public void setCSLPropertyValue(String name, String valueShort, String valueLong) {
		propertyBank.setCSLPropertyValue(name, valueShort, valueLong);
	}
	
	public void addPropertyChangedListener(IPropertyChangedListener propertyChangedListener) {
		propertyBank.addPropertyChangedListener(propertyChangedListener);
	}
	
}
