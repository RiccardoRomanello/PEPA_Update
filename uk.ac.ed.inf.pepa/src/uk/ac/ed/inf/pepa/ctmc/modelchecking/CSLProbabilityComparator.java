/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public class CSLProbabilityComparator extends CSLAbstractProbability {

	private boolean isLessThan;
	private CSLDouble probability;
	
	public CSLProbabilityComparator(boolean isLessThan, CSLDouble probability) {
		this.isLessThan = isLessThan;
		this.probability = probability;
	}
	
	public CSLAbstractProbability invert() {
		CSLDouble prob = new CSLDouble(true, 1 - probability.getValue());
		return new CSLProbabilityComparator(!isLessThan, prob);
	}
	
	public boolean checkProbability(double testProbability) {
		if (isLessThan) {
			return Math.min(testProbability, 1.0) <= probability.getValue();
		} else {
			return Math.max(testProbability, 0.0) >= probability.getValue();
		}
	}
	
	public AbstractBoolean checkProbability(ProbabilityInterval testProbability) {
		return checkProbability(testProbability.getLower(), testProbability.getUpper());
	}
	
	public AbstractBoolean checkProbability(double lowerProbability, double upperProbability) {
		//System.out.println("Prob: [" + lowerProbability + "," + upperProbability + "]");
		boolean lowerOK = checkProbability(lowerProbability);
		boolean upperOK = checkProbability(upperProbability);
		if (lowerOK && upperOK) {
			return AbstractBoolean.TRUE;
		} else if (!lowerOK && !upperOK) {
			return AbstractBoolean.FALSE;
		} else {
			return AbstractBoolean.MAYBE;
		}
	}
	
	public String toString() {
		if (isLessThan) {
			return "<=" + probability.toString();
		} else {
			return ">=" + probability.toString();
		}
	}
	
	public boolean containsPlaceHolder() {
		return false;
	}
	
	public StringPosition[] getChildren() {
		int start = 2;
		int end = start + probability.toString().length();
		StringPosition position = new StringPosition(start, end, probability);
		return new StringPosition[] {position};
	}
	
	public CSLAbstractProbability replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractProbability) {
			return (CSLAbstractProbability)object2;
		} else {
			return this;
		}
	}
	
	public CSLAbstractProbability copy() {
		return new CSLProbabilityComparator(isLessThan, probability);
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLProbabilityComparator) {
			CSLProbabilityComparator node = (CSLProbabilityComparator)o;
			return isLessThan == node.isLessThan && probability.equals(node.probability);
		}
		return false;
	}
	
	public int hashCode() {
		return (isLessThan ? 0 : 1) + probability.hashCode() + 8;
	}
	
}
