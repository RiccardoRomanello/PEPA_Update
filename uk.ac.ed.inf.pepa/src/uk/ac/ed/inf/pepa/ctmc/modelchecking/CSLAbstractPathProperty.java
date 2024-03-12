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

public abstract class CSLAbstractPathProperty extends CSLAbstractProperty {

	public abstract CSLAbstractPathProperty copy();
	
	public abstract CSLAbstractPathProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2);
	
	public abstract CSLAbstractPathProperty normalise();
	
	public abstract ArrayList<CSLAtomicNode> getAtomicProperties();
	
	public abstract void accept(ICSLVisitor visitor) throws ModelCheckingException;
	
}
