/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public abstract class CSLAbstractProbability extends CSLAbstractProperty {

	public abstract CSLAbstractProbability copy();
	
	public abstract CSLAbstractProbability replace(CSLAbstractProperty object1, CSLAbstractProperty object2);
	
	public abstract CSLAbstractProbability invert();
	
}
