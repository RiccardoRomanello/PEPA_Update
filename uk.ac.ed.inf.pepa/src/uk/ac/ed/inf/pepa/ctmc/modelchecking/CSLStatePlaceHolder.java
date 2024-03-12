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


public class CSLStatePlaceHolder extends CSLAbstractStateProperty {

	public CSLStatePlaceHolder() {
	}
	
	public CSLAbstractStateProperty copy() {
		return new CSLStatePlaceHolder();
	}

	public boolean isSimple() {
		return true;
	}

	public String toString() {
		return "<*>";
	}
	
	public boolean containsPlaceHolder() {
		return true;
	}
	
	public CSLAbstractStateProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)object2;
		} else {
			return this;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLStatePlaceHolder) {
			return true;
		}
		return false;
	}
	
	public int hashCode() {
		return 5;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		isCompositional = false;
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		return this;
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		return new ArrayList<CSLAtomicNode>();
	}

}
