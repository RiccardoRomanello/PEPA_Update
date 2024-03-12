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

public class CSLPathPropertyNode extends CSLAbstractStateProperty {

	private CSLAbstractPathProperty property;
	private CSLAbstractProbability comparator;

	public CSLAbstractPathProperty getProperty() {
		return property;
	}
	
	public CSLAbstractProbability getComparator() {
		return comparator;
	}
	
	public CSLPathPropertyNode(CSLAbstractPathProperty property, CSLAbstractProbability comparator) {
		this.property = property;
		this.comparator = comparator;
	}
	
	public boolean isProbabilityTest() {
		return comparator.isProbabilityTest();
	}
	
	public String toString() {
		return "P" + comparator.toString() + " [ " + property.toString() + " ]";  
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
		return new CSLPathPropertyNode(property.copy(), comparator.copy());
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
		if (o instanceof CSLPathPropertyNode) {
			CSLPathPropertyNode node = (CSLPathPropertyNode)o;
			return property.equals(node.property) && comparator.equals(node.comparator);
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode() + comparator.hashCode() + 6;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		isCompositional = false;
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		CSLAbstractPathProperty newPath = property.normalise();
		CSLAbstractProbability newComparator = comparator;
		if (newPath instanceof CSLEventuallyNode) {
			// F phi = true U phi
			CSLEventuallyNode path = (CSLEventuallyNode) newPath;
			newPath = new CSLUntilNode(new CSLBooleanNode(true),
					path.getProperty(), path.getTimeInterval());
		} else if (newPath instanceof CSLGloballyNode) {
			// G phi = ~ F ~ phi = ~ (true U ~ phi)
			// P<=p [G phi] = P>=1-p [true U ~ phi]
			CSLGloballyNode path = (CSLGloballyNode) newPath;
			newPath = new CSLUntilNode(new CSLBooleanNode(true),
					new CSLNotNode(path.getProperty()), path.getTimeInterval());
			newComparator = newComparator.invert();
		}
		return new CSLPathPropertyNode(newPath, newComparator);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		return property.getAtomicProperties();
	}
	
}
