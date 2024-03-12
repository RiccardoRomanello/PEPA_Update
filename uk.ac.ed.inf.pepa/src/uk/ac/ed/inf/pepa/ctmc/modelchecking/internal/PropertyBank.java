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
import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayPropertyMap;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAtomicNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.IPropertyChangedListener;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.PropertyDependencyException;

public class PropertyBank {

	private int numComponents;
	
	private ArrayList<KroneckerDisplayPropertyMap> registeredMaps;
	private ArrayList<IPropertyChangedListener> listeners;
	
	private HashMap<String, AtomicProperty> atomicProperties;
	
	private HashMap<String, CSLAbstractStateProperty> CSLProperties;
	private HashMap<String, String> CSLShortValues;
	private HashMap<String, String> CSLLongValues;
	
	public PropertyBank(int numComponents) {
		this.numComponents = numComponents;
		this.registeredMaps = new ArrayList<KroneckerDisplayPropertyMap>(10);
		this.listeners = new ArrayList<IPropertyChangedListener>(10);
		this.atomicProperties = new HashMap<String, AtomicProperty>(10);
		this.CSLProperties = new HashMap<String, CSLAbstractStateProperty>();
		this.CSLShortValues = new HashMap<String, String>();
		this.CSLLongValues = new HashMap<String, String>();
	}
	
	public void register(KroneckerDisplayPropertyMap propertyMap) {
		registeredMaps.add(propertyMap);
	}
	
	public void addPropertyChangedListener(IPropertyChangedListener propertyChangedListener) {
		if (listeners.contains(propertyChangedListener)) return;
		listeners.add(propertyChangedListener);
	}
	
	private void notifyPropertyChanged() {
		for (IPropertyChangedListener listener : listeners) {
			listener.handlePropertiesChanged();
		}
	}
	
	private String getNewPropertyName() {
		String newName = "New Property ";
		int i = 1;
		while (atomicProperties.containsKey(newName + i)) i++;
		return newName + i;
	}
	
	public String addAtomicProperty(String name) {
		if (atomicProperties.containsKey(name)) return null;
		AtomicProperty property = new AtomicProperty(numComponents);
		atomicProperties.put(name, property);
		for (KroneckerDisplayPropertyMap propertyMap : registeredMaps) {
			propertyMap.notifyAddProperty(name, property);
		}
		return name;
	}
	
	public String addAtomicProperty() {
		return addAtomicProperty(getNewPropertyName());
	}
	
	public String renameAtomicProperty(String oldName, String newName) {
		if (newName.length() == 0 || atomicProperties.containsKey(newName)) return oldName;
		AtomicProperty atom = atomicProperties.remove(oldName);
		atomicProperties.put(newName, atom);
		ArrayList<CSLAtomicNode> atomicNodes = new ArrayList<CSLAtomicNode>(10);
		for (CSLAbstractStateProperty property : CSLProperties.values()) {
			property.getAtomicNodes(atomicNodes);
		}
		for (CSLAtomicNode atomic : atomicNodes) {
			atomic.rename(oldName, newName);
		}
		for (KroneckerDisplayPropertyMap propertyMap : registeredMaps) {
			propertyMap.notifyRenameProperty(oldName, newName);
		}
		notifyPropertyChanged();
		return newName;
	}
	
	/**
	 * Removes the given property - throwing a PropertyDependencyException if it
	 * is currently being used in a CSL property.
	 */
	public void removeAtomicProperty(String name) throws PropertyDependencyException {
		boolean canRemove = true;
		ArrayList<CSLAtomicNode> atomicNodes = new ArrayList<CSLAtomicNode>(10);
		for (CSLAbstractStateProperty property : CSLProperties.values()) {
			property.getAtomicNodes(atomicNodes);
		}
		for (CSLAtomicNode atomic : atomicNodes) {
			if (name.equals(atomic.toString())) {
				canRemove = false;
			}
		}
		if (canRemove) {
			atomicProperties.remove(name);
			for (KroneckerDisplayPropertyMap propertyMap : registeredMaps) {
				propertyMap.notifyRemoveProperty(name);
			}
		} else {
			throw new PropertyDependencyException();
		}
	}
	
	/**
	 * Removes all properties - regardless of whether they are in use.
	 */
	public void removeAllProperties() {
		for (String name : atomicProperties.keySet()) {
			for (KroneckerDisplayPropertyMap propertyMap : registeredMaps) {
				propertyMap.notifyRemoveProperty(name);
			}
		}
		atomicProperties.clear();
	}
	
	/**
	 * Returns a sorted array of the atomic properties that are present
	 */
	public String[] getAtomicPropertyNames() {
		String[] properties = new String[atomicProperties.size()];
		atomicProperties.keySet().toArray(properties);
		Arrays.sort(properties);
		return properties;
	}
	
	public boolean containsCSLPropertyName(String name) {
		return CSLProperties.containsKey(name);
	}
	
	public boolean addCSLProperty(String name, CSLAbstractStateProperty property) {
		if (CSLProperties.containsKey(name)) return false;
		CSLProperties.put(name, property);
		return true;
	}
	
	public void removeCSLProperty(String name) {
		CSLProperties.remove(name);
		removeCSLPropertyValue(name);
	}
	
	public boolean changeCSLProperty(String oldName, CSLAbstractStateProperty oldProperty,
			                         String newName, CSLAbstractStateProperty newProperty) {
		if (!oldName.equals(newName) && CSLProperties.containsKey(newName)) return false;
		CSLProperties.remove(oldName);
		CSLProperties.put(newName, newProperty);
		if (newProperty.equals(oldProperty)) {
			renameCSLPropertyValue(oldName, newName);
		} else {
			removeCSLPropertyValue(oldName);
		}
		return true;
	}
	
	public String[] getCSLPropertyNames() {
		String[] names = new String[CSLProperties.size()];
		CSLProperties.keySet().toArray(names);
		Arrays.sort(names);
		return names;
	}

	private void renameCSLPropertyValue(String oldName, String newName) {
		String shortValue = CSLShortValues.remove(oldName);
		String longValue = CSLShortValues.remove(oldName);
		if (shortValue != null) {
			CSLShortValues.put(newName, shortValue);
			CSLLongValues.put(newName, longValue);
		}
	}
	
	private void removeCSLPropertyValue(String name) {
		CSLShortValues.remove(name);
		CSLLongValues.remove(name);
	}
	
	public void clearCSLPropertyValues() {
		CSLShortValues.clear();
		CSLLongValues.clear();
	}
	
	public String getCSLShortPropertyValue(String name) {
		return CSLShortValues.get(name);
	}
	
	public String getCSLLongPropertyValue(String name) {
		return CSLLongValues.get(name);
	}
	
	public void setCSLPropertyValue(String name, String valueShort, String valueLong) {
		CSLShortValues.put(name, valueShort);
		CSLLongValues.put(name, valueLong);
	}
	
	/**
	 * Remove cached value for any CSL properties that contain the
	 * give atomic property.
	 */
	public void notifyCSLPropertyValueChanged(String name) {
		for (Entry<String,CSLAbstractStateProperty> entry : CSLProperties.entrySet()) {
			String propertyName = entry.getKey();
			CSLAbstractStateProperty property = entry.getValue();
			ArrayList<CSLAtomicNode> atomicNodes = new ArrayList<CSLAtomicNode>(10);
			property.getAtomicNodes(atomicNodes);
			for (CSLAtomicNode atomic : atomicNodes) {
				if (name.equals(atomic.toString())) {
					removeCSLPropertyValue(propertyName);
					break;
				}
			}
		}
		notifyPropertyChanged();
	}
	
	public CSLAbstractStateProperty getCSLProperty(String name) {
		if (CSLProperties.containsKey(name)) {
			return CSLProperties.get(name);
		} else {
			return null;
		}
	}

	public AtomicProperty getAtomicProperty(String name) {
		return atomicProperties.get(name);
	}
	
}
