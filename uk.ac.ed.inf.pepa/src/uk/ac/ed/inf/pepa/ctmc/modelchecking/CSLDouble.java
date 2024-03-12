/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public class CSLDouble extends CSLAbstractProperty {

	private boolean isProbability;
	private double value;
	
	public CSLDouble(boolean isProbability, double value) {
		this.isProbability = isProbability;
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double newValue) {
		if (newValue < 0 || isProbability && newValue > 1) return;
		value = newValue;
	}
	
	public String toString() {
		return String.valueOf(value);
	}
	
	@Override
	public boolean containsPlaceHolder() {
		return false;
	}

	@Override
	public CSLAbstractProperty copy() {
		return new CSLDouble(isProbability, value);
	}

	@Override
	public CSLAbstractProperty replace(CSLAbstractProperty object1,
			CSLAbstractProperty object2) {
		return this;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLDouble) {
			CSLDouble node = (CSLDouble)o;
			return isProbability == node.isProbability && value == node.value;
		}
		return false;
	}
	
	public int hashCode() {
		return (isProbability ? 0 : 1) + (int)(value * 10000);
	}

}
