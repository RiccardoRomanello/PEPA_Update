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

public class CSLUntilNode extends CSLAbstractPathProperty {

	private CSLAbstractStateProperty property1;
	private CSLAbstractStateProperty property2;
	private CSLTimeInterval timeBound;
	
	public CSLUntilNode(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2, CSLTimeInterval timeBound) {
		this.property1 = property1;
		this.property2 = property2;
		this.timeBound = timeBound;
	}
	
	public CSLAbstractStateProperty getProperty1() {
		return property1;
	}
	
	public CSLAbstractStateProperty getProperty2() {
		return property2;
	}
	
	public CSLTimeInterval getTimeInterval() {
		return timeBound;
	}
	
	public String toString() {
		String s1 = property1.isSimple() ? property1.toString()
                                         : "(" + property1.toString() + ")";
		String s2 = property2.isSimple() ? property2.toString()
                                         : "(" + property2.toString() + ")";
		return s1 + " U" + timeBound.toString() + " " + s2;
	}
	
	public boolean containsPlaceHolder() {
		return property1.containsPlaceHolder() || property2.containsPlaceHolder();
	}
	
	public CSLAbstractPathProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractPathProperty) {
			return (CSLAbstractPathProperty)object2;
		} else {
			property1 = property1.replace(object1, object2);
			property2 = property2.replace(object1, object2);
			timeBound = timeBound.replace(object1, object2);
			return this;
		}
	}
	
	public CSLAbstractPathProperty copy() {
		return new CSLUntilNode(property1.copy(), property2.copy(), timeBound.copy());
	}
	
	public StringPosition[] getChildren() {
		int start1 = property1.isSimple() ? 0 : 1;
		int end1 = start1 + property1.toString().length();
		StringPosition position1 = new StringPosition(start1, end1, property1);
		
		int start2 = end1 + 2 + (property1.isSimple() ? 0 : 1);
		StringPosition[] intervalChildren = timeBound.getChildren();
		int end2 = start2 + timeBound.toString().length();		
		int start3 = end2 + 1 + (property2.isSimple() ? 0 : 1);
		int end3 = start3 + property2.toString().length();
		StringPosition position3 = new StringPosition(start3, end3, property2);
		
		StringPosition[] children = new StringPosition[2 + intervalChildren.length];
		children[0] = position1;
		for (int i = 0; i < intervalChildren.length; i++) {
			children[i+1] = intervalChildren[i].addOffset(start2);
		}
		children[intervalChildren.length+1] = position3;
		return children;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLUntilNode) {
			CSLUntilNode node = (CSLUntilNode)o;
			return property1.equals(node.property1) && property2.equals(node.property2) &&
			       timeBound.equals(node.timeBound);
		}
		return false;
	}
	
	public int hashCode() {
		return property1.hashCode() + property2.hashCode() + timeBound.hashCode() + 11;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property1.accept(visitor);
		property2.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	public CSLAbstractPathProperty normalise() {
		CSLAbstractStateProperty normal1 = property1.normalise();
		CSLAbstractStateProperty normal2 = property2.normalise();
		return new CSLUntilNode(normal1, normal2, timeBound);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		ArrayList<CSLAtomicNode> properties = property1.getAtomicProperties();
		properties.addAll(property2.getAtomicProperties());
		return properties;
	}
	
}
