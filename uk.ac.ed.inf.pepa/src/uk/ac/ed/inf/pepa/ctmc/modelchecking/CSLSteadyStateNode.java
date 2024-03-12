/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.ICSLVisitor;

public class CSLSteadyStateNode extends CSLAbstractStateProperty {

	private CSLAbstractStateProperty property;
	private CSLAbstractProbability comparator;

	public CSLAbstractStateProperty getProperty() {
		return property;
	}
	
	public CSLAbstractProbability getComparator() {
		return comparator;
	}
	
	public CSLSteadyStateNode(CSLAbstractStateProperty property, CSLAbstractProbability comparator) {
		this.property = property;
		this.comparator = comparator;
	}
	
	public boolean isProbabilityTest() {
		return comparator.isProbabilityTest();
	}
	
	public String toString() {
		return "S" + comparator.toString() + " [ " + property.toString() + " ]";  
	}
	
	public boolean containsPlaceHolder() {
		return property.containsPlaceHolder();
	}
	
	public CSLAbstractStateProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)object2;
		} else {
			property = property.replace(object1, object2);
			comparator = comparator.replace(object1, object2);
			return this;
		}
	}
	
	public CSLAbstractStateProperty copy() {
		return new CSLSteadyStateNode(property.copy(), comparator.copy());
	}
	
	public StringPosition[] getChildren() {
		int start1 = 1;
		StringPosition[] comparatorChildren = comparator.getChildren();
		int end1 = start1 + comparator.toString().length();
		int start2 = end1 + 3;
		int end2 = start2 + property.toString().length();
		StringPosition position2 = new StringPosition(start2, end2, property);
		
		StringPosition[] children = new StringPosition[1 + comparatorChildren.length]; 
		for (int i = 0; i < comparatorChildren.length; i++) {
			children[i] = comparatorChildren[i].addOffset(start1);
		}
		children[comparatorChildren.length] = position2;
		return children;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLSteadyStateNode) {
			CSLSteadyStateNode node = (CSLSteadyStateNode)o;
			return property.equals(node.property) && comparator.equals(node.comparator);
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode() + comparator.hashCode() + 7;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		property.setCompositionality(true);
		isCompositional = true;
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		CSLAbstractStateProperty normal = property.normalise();
		return new CSLSteadyStateNode(normal, comparator);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		return property.getAtomicProperties();
	}
	
}
