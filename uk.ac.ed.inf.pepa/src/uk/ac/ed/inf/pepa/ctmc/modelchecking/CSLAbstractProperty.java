/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

import java.util.ArrayList;

public abstract class CSLAbstractProperty {
	
	/**
	 * Is the structure of the property simple (i.e. not infix binary)?
	 * @return true if we should put parentheses around the property when
	 *         pretty printing
	 */
	public boolean isSimple() {
		return true;
	}
	
	/**
	 * Returns true if the property is asking a question about a probability
	 * (path or steady state).
	 */
	public boolean isProbabilityTest() {
		return false;
	}
	
	public StringPosition objectAt(int index) {
		StringPosition[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			StringPosition child = children[i];
			if (child.getStart() <= index && index <= child.getEnd()) {
				int start = child.getStart();
				return child.getObject().objectAt(index - start).addOffset(start);
			}			
		}
		return new StringPosition(0, toString().length(), this);
	}
	
	public StringPosition indexOf(CSLAbstractProperty property) {
		if (this == property) {
			return new StringPosition(0, toString().length(), this);
		} else {
			StringPosition[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				StringPosition position = children[i].getObject().indexOf(property);
				if (position != null) {
					return position.addOffset(children[i].getStart());
				}
			}
			return null;
		}
	}

	// TODO - re-implement this properly as a visitor...
	public void getAtomicNodes(ArrayList<CSLAtomicNode> atomicNodes) {
		if (this instanceof CSLAtomicNode) {
			atomicNodes.add((CSLAtomicNode)this);
		} else {
			StringPosition[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i].getObject().getAtomicNodes(atomicNodes);
			}
		}
	}
	
	public abstract boolean containsPlaceHolder();
	
	public abstract CSLAbstractProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2);
	
	public abstract CSLAbstractProperty copy();
	
	public StringPosition[] getChildren() {
		StringPosition[] children = { };
		return children;
	}
	
}
