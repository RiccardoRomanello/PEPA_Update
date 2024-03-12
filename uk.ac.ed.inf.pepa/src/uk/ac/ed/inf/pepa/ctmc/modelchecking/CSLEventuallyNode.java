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

public class CSLEventuallyNode extends CSLAbstractPathProperty {

	private CSLAbstractStateProperty property;
	private CSLTimeInterval timeBound;
	
	public CSLEventuallyNode(CSLAbstractStateProperty property, CSLTimeInterval timeBound) {
		this.property = property;
		this.timeBound = timeBound;
	}
	
	public CSLAbstractStateProperty getProperty() {
		return property;
	}
	
	public CSLTimeInterval getTimeInterval() {
		return timeBound;
	}
	
	public String toString() {
		String s = property.isSimple() ? property.toString()
                                       : "(" + property.toString() + ")";
		return "F" + timeBound.toString() + " " + s;
	}
	
	public boolean containsPlaceHolder() {
		return property.containsPlaceHolder();
	}
	
	public CSLAbstractPathProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractPathProperty) {
			return (CSLAbstractPathProperty)object2;
		} else {
			property = property.replace(object1, object2);
			timeBound = timeBound.replace(object1, object2);
			return this;
		}
	}
	
	public CSLAbstractPathProperty copy() {
		return new CSLEventuallyNode(property.copy(), timeBound.copy());
	}
	
	public StringPosition[] getChildren() {
		int start1 = 1;
		StringPosition[] intervalChildren = timeBound.getChildren();
		int end1 = start1 + timeBound.toString().length();
		int start2 = end1 + 1 + (property.isSimple() ? 0 : 1);
		int end2 = start2 + property.toString().length();
		StringPosition position2 = new StringPosition(start2, end2, property);
		StringPosition[] children = new StringPosition[1 + intervalChildren.length]; 
		for (int i = 0; i < intervalChildren.length; i++) {
			children[i] = intervalChildren[i].addOffset(start1);
		}
		children[intervalChildren.length] = position2;
		return children;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLEventuallyNode) {
			CSLEventuallyNode node = (CSLEventuallyNode)o;
			return property.equals(node.property) && timeBound.equals(node.timeBound);
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode() + timeBound.hashCode() + 15;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property.accept(visitor);
		visitor.visit(this);
	}

	@Override
	public CSLAbstractPathProperty normalise() {
		CSLAbstractStateProperty normal = property.normalise();
		return new CSLEventuallyNode(normal, timeBound);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		return property.getAtomicProperties();
	}
	
}
