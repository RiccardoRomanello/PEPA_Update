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

public class CSLAtomicNode extends CSLAbstractStateProperty {

	private String property;
	
	public CSLAtomicNode(String property) {
		this.property = property;
	}
	
	public String toString() {
		return "\"" + property + "\"";
	}
	
	public boolean containsPlaceHolder() {
		return false;
	}
	
	public CSLAbstractStateProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)object2;
		} else {
			return this;
		}
	}
	
	public String getName() {
		return property;
	}
	
	public void rename(String oldName, String newName) {
		if (property.equals(oldName)) {
			property = newName;
		}
	}
	
	public CSLAbstractStateProperty copy() {
		return new CSLAtomicNode(property);
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLAtomicNode) {
			CSLAtomicNode node = (CSLAtomicNode)o;
			return property.equals(node.property);
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode() + 12;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		isCompositional = withinSteadyStateOperator;
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		return this;
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		ArrayList<CSLAtomicNode> properties = new ArrayList<CSLAtomicNode>();
		properties.add(this);
		return properties;
	}
	
}
