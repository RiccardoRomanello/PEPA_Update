/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;

public class CompositionalPropertyList implements Iterable<CompositionalProperty> {

	private ArrayList<CompositionalProperty> properties;
	
	private SequentialAbstraction[] abstraction;
	
	public CompositionalPropertyList(CompositionalProperty property) {
		this.properties = new ArrayList<CompositionalProperty>(10);
		this.properties.add(property);
		this.abstraction = property.getAbstraction();
	}
	
	public CompositionalPropertyList(ArrayList<CompositionalProperty> properties, SequentialAbstraction[] abstraction) {
		this.properties = properties;
		this.abstraction = abstraction;
	}

	public CompositionalPropertyList complement() {
		if (properties.size() == 0) {
			// Need to return a true property
			return new CompositionalPropertyList(new CompositionalProperty(abstraction, true));
		}
		ArrayList<CompositionalProperty> oldProperties = split().properties;
		ArrayList<CompositionalProperty> newProperties = new ArrayList<CompositionalProperty>(10);
		for (CompositionalProperty property : properties) {
			CompositionalPropertyList complement = property.complement().split();
			for (CompositionalProperty newProperty : complement) {
				if (!oldProperties.contains(newProperty) && !newProperties.contains(newProperty)) {
					newProperties.add(newProperty);
				}
			}
		}
		return new CompositionalPropertyList(newProperties, abstraction);
	}
	
	public CompositionalPropertyList intersection(CompositionalPropertyList propertyList) {
		ArrayList<CompositionalProperty> newProperties = new ArrayList<CompositionalProperty>(10);
		for (CompositionalProperty property1 : properties) {
			for (CompositionalProperty property2 : propertyList) {
				CompositionalProperty newProperty = property1.intersection(property2);
				if (newProperty.isTrue()) {
					return new CompositionalPropertyList(new CompositionalProperty(property1.getAbstraction(), true));
				} else if (!newProperty.isFalse()) {
					if (!newProperties.contains(newProperty)) {
						newProperties.add(newProperty);
					}
				}
			}
		}
		return new CompositionalPropertyList(newProperties, abstraction);
	}
	
	/**
	 * Computes the union using A \/ B = ¬(¬A /\ ¬B)
	 */
	public CompositionalPropertyList union(CompositionalPropertyList propertyList) {
		// We don't need to do anything if the properties are disjoint
		boolean isDisjoint = true;
		for (CompositionalProperty property1 : properties) {
			for (CompositionalProperty property2 : propertyList) {
				if (!property1.equals(property2)) {
					CompositionalProperty intersection = property1.intersection(property2);
					if (!intersection.isFalse()) {
						isDisjoint = false;
						break;
					}
				}
			}
		}
		if (isDisjoint) {
			ArrayList<CompositionalProperty> newProperties = new ArrayList<CompositionalProperty>(10); 
			newProperties.addAll(properties);
			for (CompositionalProperty property : propertyList) {
				if (!newProperties.contains(property)) {
					newProperties.add(property);
				}
			}
			return new CompositionalPropertyList(newProperties, abstraction);
		} else {
			// This is horribly inefficient
			CompositionalPropertyList notProperty1 = complement();
			CompositionalPropertyList notProperty2 = propertyList.complement();
//			System.out.println("NOT1");
//			System.out.println(notProperty1);
//			System.out.println("\n\nNOT2");
//			System.out.println(notProperty2);
//			System.out.println("\n\nINTERSECT");
//			System.out.println(notProperty1.intersection(notProperty2));
//			System.out.println("\n\nFINAL");
//			System.out.println(notProperty1.intersection(notProperty2).complement());
			return notProperty1.intersection(notProperty2).complement();
		}
	}
	
	public boolean isFalse() {
		boolean isFalse = true;
		for (CompositionalProperty property : properties) {
			isFalse = isFalse && property.isFalse();
		}
		return isFalse;
	}
	
	public boolean isTrue() {
		if (properties.size() == 0) return false;
		boolean isTrue = true;
		for (CompositionalProperty property : properties) {
			isTrue = isTrue && property.isTrue();
		}
		return isTrue;
	}
	
	public CompositionalPropertyList split() {
		ArrayList<CompositionalProperty> newProperties = new ArrayList<CompositionalProperty>(10);
		for (CompositionalProperty property : properties) {
			newProperties.addAll(property.split().properties);
		}
		return new CompositionalPropertyList(newProperties, abstraction);
	}
	
	public boolean isSingleComponent() {
		if (properties.size() == 1) {
			return properties.get(0).isSingleComponent();
		} else {
			// We wouldn't have more than one property unless
			// multiple components are involved
			return false;
		}
	}
	
	public void unregister() {
		for (CompositionalProperty property : properties) {
			property.unregister();
		}
	}
	
	public Iterator<CompositionalProperty> iterator() {
		return properties.iterator();
	}
	
	public String toString() {
		String s = "";
		int i = 0;
		for (CompositionalProperty property : properties) {
			s += "Property " + i + ":\n" + property + "\n";
			i++;
		}
		return s;
	}

}
