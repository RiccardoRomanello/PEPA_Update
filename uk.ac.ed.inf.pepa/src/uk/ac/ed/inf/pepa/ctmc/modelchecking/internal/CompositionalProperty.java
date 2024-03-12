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

import uk.ac.ed.inf.pepa.ctmc.abstraction.AbstractState;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.KroneckerUtilities;

public class CompositionalProperty {

	private int numComponents;
	
	private SequentialAbstraction[] abstraction;
	private short[][] upperConcreteProperty;
	private short[][] upperAbstractProperty;
	private short[][] lowerConcreteProperty;
	private short[][] lowerAbstractProperty;
	
	private boolean isTrue;
	private boolean isFalse;
	
	/**
	 * Use when the property is a tautology or contradiction
	 */
	public CompositionalProperty(SequentialAbstraction[] abstraction, boolean isTrue) {
		this.numComponents = abstraction.length;
		this.abstraction = abstraction;
		this.upperAbstractProperty = null;
		this.upperConcreteProperty = null;
		this.lowerAbstractProperty = null;
		this.lowerConcreteProperty = null;
		this.isTrue = isTrue;
		this.isFalse = !isTrue;
	}
	
	public CompositionalProperty(SequentialAbstraction[] abstraction, short[][] property) {
		this.numComponents = abstraction.length;
		this.abstraction = abstraction;
		this.upperAbstractProperty = property;
		this.upperConcreteProperty = new short[numComponents][];
		this.lowerAbstractProperty = null;
		this.lowerConcreteProperty = null;
		this.isTrue = false;
		this.isFalse = false;
		setConcreteProperty();
		for (int i = 0; i < numComponents; i++) {
			abstraction[i].registerProperty(this);
		}
	}
	
	/**
	 * The returned compositional property is an upper bound on the original atomic
	 * property (which holds over the concrete state space).
	 */
	public CompositionalProperty(SequentialAbstraction[] abstraction, AtomicProperty property) {
		this.numComponents = abstraction.length;
		this.abstraction = abstraction;
		this.upperAbstractProperty = new short[numComponents][];
		this.upperConcreteProperty = new short[numComponents][];
		this.lowerAbstractProperty = new short[numComponents][];
		this.lowerConcreteProperty = new short[numComponents][];
		this.isTrue = false;
		this.isFalse = false;
		for (int component = 0; component < numComponents; component++) {
			SequentialStateSpace concreteStateSpace = abstraction[component].getConcreteStateSpace();
			AbstractState[] abstractStateSpace = abstraction[component].getAbstractStateSpace();
			ShortArray upperAbstractStateArray = new ShortArray(abstraction[component].size());
			ShortArray upperConcreteStateArray = new ShortArray(abstraction[component].getConcreteStateSpace().size());
			ShortArray lowerAbstractStateArray = new ShortArray(abstraction[component].size());
			ShortArray lowerConcreteStateArray = new ShortArray(abstraction[component].getConcreteStateSpace().size());
			for (int i = 0; i < abstractStateSpace.length; i++) {
				AbstractState state = abstractStateSpace[i];
				short[] concreteStates = state.getConcrete();
				boolean allTrue = true;
				boolean allFalse = true;
				for (int j = 0; j < concreteStates.length; j++) {
					int index = concreteStateSpace.getIndex(concreteStates[j]);
					if (property.getProperty(component, index)) {
						allFalse = false;
					} else {
						allTrue = false;
					}
				}
				if (allTrue) {
					lowerAbstractStateArray.addNew(state.getID());
					lowerConcreteStateArray.add(concreteStates);
				} 
				if (!allFalse) {
					upperAbstractStateArray.addNew(state.getID());
					upperConcreteStateArray.add(concreteStates);
				}
			}
			upperAbstractProperty[component] = upperAbstractStateArray.toArray();
			upperConcreteProperty[component] = upperConcreteStateArray.toArray();
			lowerAbstractProperty[component] = lowerAbstractStateArray.toArray();
			lowerConcreteProperty[component] = lowerConcreteStateArray.toArray();
		}
		for (int i = 0; i < numComponents; i++) {
			abstraction[i].registerProperty(this);
		}
	}
	
	private CompositionalProperty(SequentialAbstraction[] abstraction) {
		this.numComponents = abstraction.length;
		this.abstraction = abstraction;
		this.isTrue = false;
		this.isFalse = false;
		for (int i = 0; i < numComponents; i++) {
			abstraction[i].registerProperty(this);
		}
	}
	
	public void unregister() {
		if (!isTrue && !isFalse) {
			for (int i = 0; i < numComponents; i++) {
				abstraction[i].unregisterProperty(this);
			}
		}
	}
	
	private void setConcreteProperty() {
		for (int component = 0; component < numComponents; component++) {
			ShortArray concreteStates = new ShortArray(abstraction[component].getConcreteStateSpace().size());
			short[] abstractStates = upperAbstractProperty[component];
			for (short j = 0; j < abstractStates.length; j++) {
				short[] states = abstraction[component].getConcreteStates(abstractStates[j]);
				for (int k = 0; k < states.length; k++) {
					concreteStates.add(states[k]);
				}				
			}
			upperConcreteProperty[component] = concreteStates.toArray();			
		}
	}
	
	public void notifyAbstractChange(SequentialAbstraction stateSpace, short[] IDMap) {
		for (int i = 0; i < numComponents; i++) {
			if (abstraction[i] == stateSpace) {
				//System.out.println("Component " + i);
				short[] upperStates = upperAbstractProperty[i];
				//System.out.println("Abstract property: " + Arrays.toString(upperStates));
				//System.out.println("IDMap: " + Arrays.toString(IDMap));
				for (int j = 0; j < upperStates.length; j++) {
					upperStates[j] = IDMap[upperStates[j]];
				}
				if (lowerAbstractProperty != null) {
					short[] lowerStates = lowerAbstractProperty[i];
					for (int j = 0; j < lowerStates.length; j++) {
						lowerStates[j] = IDMap[lowerStates[j]];
					}
				}
			}
		}
	}
	
	/**
	 * This is in general an over-approximation of the union of the two properties
	 * TODO - this doesn't update the lower properties.
	 */
	public CompositionalProperty union(CompositionalProperty property) {
		if (isFalse) {
			return property;
		} else if (property.isFalse) {
			return this;
		} else if (isTrue || property.isTrue) {
			return new CompositionalProperty(abstraction, true);
		} else {
			CompositionalProperty newProperty = new CompositionalProperty(abstraction);
			short[][] newConcreteProperty = new short[numComponents][];
			short[][] newAbstractProperty = new short[numComponents][];
			for (int component = 0; component < numComponents; component++) {
				ShortArray newConcrete = new ShortArray(abstraction[component].getConcreteStateSpace().size());
				ShortArray newAbstract = new ShortArray(abstraction[component].size());
				for (int i = 0; i < upperAbstractProperty[component].length; i++) {
					newAbstract.addNew(upperAbstractProperty[component][i]);
				}
				for (int i = 0; i < property.upperAbstractProperty[component].length; i++) {
					newAbstract.addNew(property.upperAbstractProperty[component][i]);
				}
				for (int i = 0; i < upperConcreteProperty[component].length; i++) {
					newConcrete.addNew(upperConcreteProperty[component][i]);
				}
				for (int i = 0; i < property.upperConcreteProperty[component].length; i++) {
					newConcrete.addNew(property.upperConcreteProperty[component][i]);
				}
				newConcreteProperty[component] = newConcrete.toArray();
				newAbstractProperty[component] = newAbstract.toArray();
			}
			newProperty.upperConcreteProperty = newConcreteProperty;
			newProperty.upperAbstractProperty = newAbstractProperty;
			return newProperty;
		}
	}
	
	public CompositionalProperty intersection(CompositionalProperty property) {
		if (isTrue) {
			return property;
		} else if (property.isTrue) {
			return this;
		} else if (isFalse || property.isFalse) {
			return new CompositionalProperty(abstraction, false);
		} else {
			CompositionalProperty newProperty = new CompositionalProperty(abstraction);
			
			// First compute for the upper property
			short[][] newUpperConcreteProperty = new short[numComponents][];
			short[][] newUpperAbstractProperty = new short[numComponents][];
			for (int component = 0; component < numComponents; component++) {
				ShortArray newUpperConcrete = new ShortArray(abstraction[component].getConcreteStateSpace().size());
				ShortArray newUpperAbstract = new ShortArray(abstraction[component].size());
				for (int i = 0; i < upperAbstractProperty[component].length; i++) {
					short state = upperAbstractProperty[component][i];
					for (int j = 0; j < property.upperAbstractProperty[component].length; j++) {
						if (state == property.upperAbstractProperty[component][j]) {
							newUpperAbstract.add(state);
							//System.out.println("[C" + component + "] Adding Abstract: " + state);
						}
					}
				}
				for (int i = 0; i < upperConcreteProperty[component].length; i++) {
					short state = upperConcreteProperty[component][i];
					for (int j = 0; j < property.upperConcreteProperty[component].length; j++) {
						if (state == property.upperConcreteProperty[component][j]) {
							newUpperConcrete.add(state);
							//System.out.println("[C" + component + "] Adding Concrete: " + state);
						}
					}
				}
				newUpperConcreteProperty[component] = newUpperConcrete.toArray();
				newUpperAbstractProperty[component] = newUpperAbstract.toArray();
			}
			
			// Now compute for the lower property - but only if it is set for one of the properties
			// we are combining.
			short[][] newLowerConcreteProperty = null;
			short[][] newLowerAbstractProperty = null;
			if (lowerAbstractProperty != null || property.lowerAbstractProperty != null) {
				newLowerConcreteProperty = new short[numComponents][];
				newLowerAbstractProperty = new short[numComponents][];
				for (int component = 0; component < numComponents; component++) {
					short[][] lowerAbstract1 = (lowerAbstractProperty == null) ? upperAbstractProperty : lowerAbstractProperty; 
					short[][] lowerConcrete1 = (lowerConcreteProperty == null) ? upperConcreteProperty : lowerConcreteProperty;
					short[][] lowerAbstract2 = (property.lowerAbstractProperty == null) ? property.upperAbstractProperty : property.lowerAbstractProperty; 
					short[][] lowerConcrete2 = (property.lowerConcreteProperty == null) ? property.upperConcreteProperty : property.lowerConcreteProperty;
					ShortArray newLowerConcrete = new ShortArray(abstraction[component].getConcreteStateSpace().size());
					ShortArray newLowerAbstract = new ShortArray(abstraction[component].size());
					for (int i = 0; i < lowerAbstract1[component].length; i++) {
						short state = lowerAbstract1[component][i];
						for (int j = 0; j < lowerAbstract2[component].length; j++) {
							if (state == lowerAbstract2[component][j]) {
								newLowerAbstract.add(state);
							}
						}
					}
					for (int i = 0; i < lowerConcrete1[component].length; i++) {
						short state = lowerConcrete1[component][i];
						for (int j = 0; j < lowerConcrete2[component].length; j++) {
							if (state == lowerConcrete2[component][j]) {
								newLowerConcrete.add(state);
							}
						}
					}
					newLowerConcreteProperty[component] = newLowerConcrete.toArray();
					newLowerAbstractProperty[component] = newLowerAbstract.toArray();
				}
			}
			newProperty.upperConcreteProperty = newUpperConcreteProperty;
			newProperty.upperAbstractProperty = newUpperAbstractProperty;
			newProperty.lowerConcreteProperty = newLowerConcreteProperty;
			newProperty.lowerAbstractProperty = newLowerAbstractProperty;
			return newProperty;
		}
	}
	
	private CompositionalProperty computeSelectiveComplement(boolean[] components) {
		boolean allTrue = true;
		for (int i = 0; i < components.length; i++) allTrue = allTrue && components[i];
		assert !allTrue;
		CompositionalProperty newProperty = new CompositionalProperty(abstraction);
		short[][] abstractProperty = (lowerAbstractProperty == null) ? upperAbstractProperty : lowerAbstractProperty; 
		short[][] concreteProperty = (lowerConcreteProperty == null) ? upperConcreteProperty : lowerConcreteProperty;
		short[][] newAbstractProperty = new short[numComponents][];
		short[][] newConcreteProperty = new short[numComponents][];
		for (int component = 0; component < numComponents; component++) {
			if (components[component]) {
				newAbstractProperty[component] = new short[abstractProperty[component].length];
				System.arraycopy(abstractProperty[component], 0, newAbstractProperty[component], 0, abstractProperty[component].length);
				newConcreteProperty[component] = new short[concreteProperty[component].length];
				System.arraycopy(concreteProperty[component], 0, newConcreteProperty[component], 0, concreteProperty[component].length);
			} else {
				ShortArray newConcrete = new ShortArray(abstraction[component].getConcreteStateSpace().size());
				ShortArray newAbstract = new ShortArray(abstraction[component].size());
				AbstractState[] abstractStateSpace = abstraction[component].getAbstractStateSpace();
				for (int i = 0; i < abstractStateSpace.length; i++) {
					AbstractState state = abstractStateSpace[i];
					boolean is_in_complement = true;
					for (int j = 0; j < abstractProperty[component].length; j++) {
						if (state.getID() == abstractProperty[component][j]) {
							is_in_complement = false;
						}
					}
					if (is_in_complement) {
						newAbstract.add(state.getID());
						newConcrete.add(state.getConcrete());
					}
				}
				newConcreteProperty[component] = newConcrete.toArray();
				newAbstractProperty[component] = newAbstract.toArray();
			}
		}
		// TODO - need to update the lower concrete and abstract properties too...
		newProperty.upperConcreteProperty = newConcreteProperty;
		newProperty.upperAbstractProperty = newAbstractProperty;
		return newProperty;
	}
	
	public CompositionalPropertyList complement() {
		if (isTrue) {
			return new CompositionalPropertyList(new CompositionalProperty(abstraction, false));
		} else if (isFalse) {
			return new CompositionalPropertyList(new CompositionalProperty(abstraction, true));
		} else {
			ArrayList<CompositionalProperty> newProperty = new ArrayList<CompositionalProperty>(10);
			boolean[] components = new boolean[numComponents];
			for (int i = 0; i < components.length; i++) components[i] = false;
			int combinations = (1 << numComponents) - 1;
			for (int i = 0; i < combinations; i++) {
				CompositionalProperty property = computeSelectiveComplement(components);
				//System.out.println("=== FOUND PROPERTY === \n" + property + "===\n");
				if (property.isTrue()) {
					//System.out.println("TRUE");
					property.unregister();
					for (CompositionalProperty p : newProperty) p.unregister();
					return new CompositionalPropertyList(new CompositionalProperty(abstraction, true));
				} else if (!property.isFalse()) {
					//System.out.println("ADDING");
					newProperty.add(property);
				} else {
					//System.out.println("FALSE");
					property.unregister();
				}
				//System.out.println(i + " = " + Arrays.toString(components));
				KroneckerUtilities.incrementBooleanArray(components);
			}
			return new CompositionalPropertyList(newProperty, abstraction);
		}
		
	}
	
	public short[][] getConcreteProperty() {
		return upperConcreteProperty;
	}
	
	public short[][] getAbstractProperty() {
		return upperAbstractProperty;
	}
	
	public SequentialAbstraction[] getAbstraction() {
		return abstraction;
	}
	
	public boolean isTrue() {
		if (isTrue) {
			return true;
		} else if (isFalse) {
			return false;
		} else {
			short[][] abstractProperty = (lowerAbstractProperty == null) ? upperAbstractProperty : lowerAbstractProperty;
			boolean isOK = true;
			for (int component = 0; component < numComponents; component++) {
				boolean isComponentOK = abstractProperty[component].length == abstraction[component].size();
				isOK = isOK && isComponentOK;
			}
			return isOK;
		}
	}
	
	public boolean isFalse() {
		if (isTrue) {
			return false;
		} else if (isFalse) {
			return true;
		} else {
			boolean isOK = false;
			for (int component = 0; component < numComponents; component++) {
				boolean isComponentOK = upperAbstractProperty[component].length == 0;
				isOK = isOK || isComponentOK;
			}
			return isOK;
		}
	}
	
	/**
	 * Returns an arbitrary abstract state that satisfies the property, if
	 * there is one, and returns null otherwise.
	 */
	public short[] anyTrueState() {
		short[] state = new short[numComponents];
		for (int component = 0; component < numComponents; component++) {
			if (upperAbstractProperty[component].length == 0) return null;
			state[component] = upperAbstractProperty[component][0];
		}
		return state;
	}
	
	/**
	 * Returns an arbitrary abstract state that fails to satisfy the property, if
	 * there is one, and returns null otherwise.
	 */
	public short[] anyFalseState() {
		short[] state = new short[numComponents];
		for (int component = 0; component < numComponents; component++) {
			if (lowerAbstractProperty[component].length == abstraction[component].size()) return null;
			for (short i = 0; i < abstraction[component].size(); i++) {
				boolean found = false;
				for (int j = 0; j < lowerAbstractProperty[component].length; j++) {
					if (lowerAbstractProperty[component][j] == i) {
						found = true;
						break;
					}
				}
				if (!found) {
					state[component] = i;
				}
			}
		}
		return state;
	}
	
	/**
	 * This separates out the property into distinct abstract states corresponding to all combinations
	 */
	public CompositionalPropertyList split() {
		boolean[] allTrue = new boolean[numComponents];
		int[] maximum = new int[numComponents];
		int size = 1;
		for (int component = 0; component < numComponents; component++) {
			maximum[component] = upperAbstractProperty[component].length;
			allTrue[component] = maximum[component] == abstraction[component].size();
			if (allTrue[component]) maximum[component] = 1;
			size *= maximum[component];
		}
		int[] current = new int[numComponents];
		
		ArrayList<CompositionalProperty> properties = new ArrayList<CompositionalProperty>(size);
		for (int i = 0; i < size; i++) {
			short[][] splitProperty = new short[numComponents][];
			for (int component = 0; component < numComponents; component++) {
				if (!allTrue[component]) {
					splitProperty[component] = new short[1];
					splitProperty[component][0] = upperAbstractProperty[component][current[component]];
				} else {
					splitProperty[component] = upperAbstractProperty[component];
				}
			}
			properties.add(new CompositionalProperty(abstraction, splitProperty));
			KroneckerUtilities.incrementArray(current, maximum);
		}
		
		return new CompositionalPropertyList(properties, abstraction);
	}
	
	public boolean isSingleComponent() {
		int numPropertyComponents = 0;
		for (int component = 0; component < numComponents; component++) {
			if (upperAbstractProperty[component].length < abstraction[component].size()) {
				numPropertyComponents++;
			}
		}
		return numPropertyComponents == 1;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof CompositionalProperty)) return false;
		CompositionalProperty property = (CompositionalProperty)o;
		if (abstraction != property.abstraction) return false;
		boolean lowerAbstract = false;
		if (lowerAbstractProperty == null && property.lowerAbstractProperty == null) {
			lowerAbstract = true;
		} else if (lowerAbstractProperty != null && property.lowerAbstractProperty != null) {
			lowerAbstract = Arrays.deepEquals(lowerAbstractProperty, property.lowerAbstractProperty);
		}
		boolean upperAbstract = false;
		if (upperAbstractProperty == null && property.upperAbstractProperty == null) {
			upperAbstract = true;
		} else if (upperAbstractProperty != null && property.upperAbstractProperty != null) {
			upperAbstract = Arrays.deepEquals(upperAbstractProperty, property.upperAbstractProperty);
		}
		return lowerAbstract && upperAbstract;
	}
	
	public String toString() {
		if (isTrue) {
			return "Property: TRUE";
		} else if (isFalse) {
			return "Property: FALSE";
		} else {
			String upperConcrete = Arrays.deepToString(upperConcreteProperty);
			String upperAbstract = Arrays.deepToString(upperAbstractProperty);
			String lowerConcrete = lowerConcreteProperty == null ? "[]" : Arrays.deepToString(lowerConcreteProperty);
			String lowerAbstract = lowerAbstractProperty == null ? "[]" : Arrays.deepToString(lowerAbstractProperty);
			return "Upper Concrete: " + upperConcrete + "\nUpper Abstract: " + upperAbstract +
			       "\nLower Concrete: " + lowerConcrete + "\nLower Abstract: " + lowerAbstract;
		}
	}
	
}
