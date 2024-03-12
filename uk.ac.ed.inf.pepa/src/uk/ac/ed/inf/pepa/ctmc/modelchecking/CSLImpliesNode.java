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

public class CSLImpliesNode extends CSLAbstractStateProperty {
	
	private CSLAbstractStateProperty property1;
	private CSLAbstractStateProperty property2;

	public CSLAbstractStateProperty getProperty1() {
		return property1;
	}
	
	public CSLAbstractStateProperty getProperty2() {
		return property2;
	}
	
	public CSLImpliesNode(CSLAbstractStateProperty property1, CSLAbstractStateProperty property2) {
		this.property1 = property1;
		this.property2 = property2;
	}
	
	public boolean isSimple() {
		return false;
	}
	
	public String toString() {
		String s1 = property1.isSimple() ? property1.toString()
				                         : "(" + property1.toString() + ")";
		String s2 = property2.isSimple() ? property2.toString()
                                         : "(" + property2.toString() + ")";
		return s1 + " => " + s2;
		//return s1 + " \u21D2 " + s2;
	}
	
	public boolean containsPlaceHolder() {
		return property1.containsPlaceHolder() || property2.containsPlaceHolder();
	}
	
	public CSLAbstractStateProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)object2;
		} else {
			property1 = property1.replace(object1, object2);
			property2 = property2.replace(object1, object2);
			return this;
		}
	}
	
	public CSLAbstractStateProperty copy() {
		return new CSLImpliesNode(property1.copy(), property2.copy());
	}
	
	public StringPosition[] getChildren() {
		int start1 = property1.isSimple() ? 0 : 1;
		int end1 = start1 + property1.toString().length();
		StringPosition position1 = new StringPosition(start1, end1, property1);
		
		int start2 = end1 + 4 + (property1.isSimple() ? 0 : 1)
		                      + (property2.isSimple() ? 0 : 1);
		int end2 = start2 + property2.toString().length();
		StringPosition position2 = new StringPosition(start2, end2, property2);
		
		StringPosition[] children = {position1, position2};
		return children;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLImpliesNode) {
			CSLImpliesNode node = (CSLImpliesNode)o;
			return property1.equals(node.property1) && property2.equals(node.property2);
		}
		return false;
	}
	
	public int hashCode() {
		return property1.hashCode() + property2.hashCode() + 3;
	}

	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property1.accept(visitor);
		property2.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		property1.setCompositionality(withinSteadyStateOperator);
		property2.setCompositionality(withinSteadyStateOperator);
		isCompositional = property1.isCompositional() && property2.isCompositional();
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		CSLAbstractStateProperty normal1 = property1.normalise();
		CSLAbstractStateProperty normal2 = property2.normalise();
		return new CSLImpliesNode(normal1, normal2);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		ArrayList<CSLAtomicNode> properties = property1.getAtomicProperties();
		properties.addAll(property2.getAtomicProperties());
		return properties;
	}
	
}
