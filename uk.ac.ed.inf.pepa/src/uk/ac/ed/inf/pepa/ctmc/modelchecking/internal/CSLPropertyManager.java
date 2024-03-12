/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractPathProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAtomicNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLBooleanNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ModelCheckingException;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.StringPosition;

/**
 * Keep track of the labelling of abstract CTMC states. We label each
 * state with a maximum of 32 properties.
 * 
 * @author msmith
 * 
 */
public class CSLPropertyManager {

	private static final int MAX_ENABLED = 32;
	
	private ArrayList<CSLAbstractStateProperty> properties;
	private CSLAbstractStateProperty[] propertiesAssigned;
	
	private HashMap<CSLAbstractStateProperty, AbstractBoolean> constantAnswers;
	
	private CSLAbstractStateProperty currentProperty;
	private int currentIndex;
	
	private int numAtomicProperties;
	
	private PropertyBank propertyBank;
	private SequentialAbstraction[] abstractStateSpace;
	
	public CSLPropertyManager(PropertyBank propertyBank, SequentialAbstraction[] abstractStateSpace) {
		this.propertyBank = propertyBank;
		this.abstractStateSpace = abstractStateSpace;
		this.properties = new ArrayList<CSLAbstractStateProperty>(MAX_ENABLED);
		this.propertiesAssigned = new CSLAbstractStateProperty[MAX_ENABLED];
		this.constantAnswers = new HashMap<CSLAbstractStateProperty, AbstractBoolean>(10);
		// Add the boolean constants
		constantAnswers.put(new CSLBooleanNode(true), AbstractBoolean.TRUE);
		constantAnswers.put(new CSLBooleanNode(false), AbstractBoolean.FALSE);
		addAtomicProperties();
	}
	
	private void addNewProperty(CSLAbstractStateProperty property) {
		if (!properties.contains(property)) {
			//System.out.println("Adding property: " + property.toString());
			properties.add(property);
		}
	}
	
	private void addAtomicProperties() {
		String[] names = propertyBank.getAtomicPropertyNames();
		numAtomicProperties = names.length;
		for (int i = 0; i < names.length; i++) {
			addNewProperty(new CSLAtomicNode(names[i]));
		}
	}
	
	private void addProperty(CSLAbstractPathProperty property) {
		StringPosition[] children = property.getChildren();
		for (int i = 0; i < children.length; i++) {
			CSLAbstractProperty child = children[i].getObject();
			if (child instanceof CSLAbstractStateProperty) {
				addProperty((CSLAbstractStateProperty)child);
			} 
		}
	}
	
	public void addProperty(CSLAbstractStateProperty property) {
		if (property.isCompositional()) return;
		StringPosition[] children = property.getChildren();
		for (int i = 0; i < children.length; i++) {
			CSLAbstractProperty child = children[i].getObject();
			if (child instanceof CSLAbstractStateProperty) {
				addProperty((CSLAbstractStateProperty)child);
			} else if (child instanceof CSLAbstractPathProperty) {
				addProperty((CSLAbstractPathProperty)child);
			}
		}
		addNewProperty(property);
	}
	
	public void setConstantProperty(CSLAbstractStateProperty property, AbstractBoolean value) {
		constantAnswers.put(property, value);
	}
	
	public AbstractBoolean getConstantProperty(CSLAbstractStateProperty property) {
		return constantAnswers.get(property);
	}
	
	public void labelAtomicProperties(AbstractCTMCState state) {
		short[] stateID = state.getState();
		for (int i = 0; i < numAtomicProperties; i++) {
			CSLAtomicNode CSLAtom = (CSLAtomicNode)properties.get(i);
			propertiesAssigned[i] = CSLAtom;
			AtomicProperty property = propertyBank.getAtomicProperty(CSLAtom.getName());
			boolean isTrue = true;
			boolean isFalse = false;
			for (int j = 0; j < stateID.length; j++) {
				boolean isAbstractTrue = false;
				boolean isAbstractFalse = false;
				short abstractState = stateID[j];
				SequentialAbstraction abstraction = abstractStateSpace[j];
				SequentialStateSpace stateSpace = abstraction.getConcreteStateSpace();
				short[] concreteStates = abstraction.getConcreteStates(abstractState);
				// look at each of the concrete states in the abstraction
				for (int k = 0; k < concreteStates.length; k++) {
					short concreteState = concreteStates[k];
					int index = stateSpace.getIndex(concreteState);
					boolean isConcreteTrue = property.getProperty(j, index);
					isAbstractTrue = isAbstractTrue || isConcreteTrue;
					isAbstractFalse = isAbstractFalse || !isConcreteTrue;
				}
				isTrue = isTrue && isAbstractTrue;
				isFalse = isFalse || isAbstractFalse;
			}
			state.setProperty(i, isTrue, isFalse);
		}
	}
	
	private void setProperty(CSLAbstractStateProperty property, AbstractCTMCState state, AbstractBoolean value) throws PropertyTagFullException {
		int index = getPropertyIndex(property);
		//System.out.println("Set Property " + index + " (" + property + ") := " + value);
		state.setProperty(index, value);
	}
	
	private AbstractBoolean getProperty(CSLAbstractStateProperty property, AbstractCTMCState state) {
		try {
			int index = getPropertyIndex(property);
			return state.getProperty(index);
		}
		catch (PropertyTagFullException e) {
			return AbstractBoolean.NOT_SET;
		}
	}
	
	public AbstractBoolean test(CSLAbstractStateProperty property, AbstractCTMCState state) {
		AbstractBoolean value = constantAnswers.get(property);
		if (value != null) {
			return value;
		} else {
			return getProperty(property, state);
		}
	}
	
	public void set(CSLAbstractStateProperty property, AbstractCTMCState state, AbstractBoolean value) throws ModelCheckingException {
		try {
			setProperty(property, state, value);
		}  catch (PropertyTagFullException e) {
			throw new ModelCheckingException("The property is too large to check.");
		}
	}
	
	private int getPropertyIndex(CSLAbstractStateProperty property) throws PropertyTagFullException {
		if (currentProperty != null && (currentProperty == property || currentProperty.equals(property))) {
			return currentIndex;
		} else {
			int index = properties.lastIndexOf(property);
			if (index < 0 || index >= 32) throw new PropertyTagFullException();
			currentProperty = property;
			currentIndex = index;
			return index;
		}
	}
	
	public PropertyBank getPropertyBank() {
		return propertyBank;
	}
	
}
