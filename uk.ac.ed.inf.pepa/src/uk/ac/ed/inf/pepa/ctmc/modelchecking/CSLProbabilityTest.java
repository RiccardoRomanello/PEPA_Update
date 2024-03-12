/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public class CSLProbabilityTest extends CSLAbstractProbability {

	// if isInverse, we compute 1 - p, rather than p.
	private boolean isInverse;
	
	public CSLProbabilityTest() {
		this.isInverse = false;
	}
	
	public CSLProbabilityTest(boolean isInverse) {
		this.isInverse = isInverse;
	}
	
	public String toString() {
		return "=?";
	}
	
	public boolean isProbabilityTest() {
		return true;
	}
	
	public boolean isInverse() {
		return isInverse;
	}
	
	public CSLAbstractProbability invert() {
		return new CSLProbabilityTest(!isInverse);
	}
	
	public CSLAbstractProbability replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractProbability) {
			return (CSLAbstractProbability)object2;
		} else {
			return this;
		}
	}
	
	public boolean containsPlaceHolder() {
		return false;
	}
	
	public CSLAbstractProbability copy() {
		return new CSLProbabilityTest();
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLPathPropertyNode) {
			return true;
		}
		return false;
	}
	
	public int hashCode() {
		return 10;
	}
	
}
