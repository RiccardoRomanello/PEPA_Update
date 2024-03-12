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

public class CSLNotNode extends CSLAbstractStateProperty {

	private CSLAbstractStateProperty property;

	public CSLAbstractStateProperty getProperty() {
		return property;
	}
	
	public CSLNotNode(CSLAbstractStateProperty property) {
		this.property = property;
	}
	
	public String toString() {
		String s = property.isSimple() ? property.toString()
				                       : "(" + property.toString() + ")";
		return "!" + s;
	}
	
	public boolean containsPlaceHolder() {
		return property.containsPlaceHolder();
	}
	
	public CSLAbstractStateProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)object2;
		} else {
			property = property.replace(object1, object2);
			return this;
		}
	}
	
	public CSLAbstractStateProperty copy() {
		return new CSLNotNode(property.copy());
	}
	
	public StringPosition[] getChildren() {
		int start = 1 + (property.isSimple() ? 0 : 1);
		int end = start + property.toString().length();
		StringPosition position = new StringPosition(start, end, property);
		
		StringPosition[] children = {position};
		return children;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLNotNode) {
			CSLNotNode node = (CSLNotNode)o;
			return property.equals(node.property);
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode() + 2;
	}

	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		property.setCompositionality(withinSteadyStateOperator);
		isCompositional = property.isCompositional();
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		CSLAbstractStateProperty normal = property.normalise();
		return new CSLNotNode(normal);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		return property.getAtomicProperties();
	}
	
}
