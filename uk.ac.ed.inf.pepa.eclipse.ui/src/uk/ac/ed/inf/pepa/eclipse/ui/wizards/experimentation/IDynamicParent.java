/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

/**
 * Interface for object which are dynamic parents of other objects.
 * <p>
 * It is used by controls which are required to be dynamically built in response
 * to selections that change in some control. Parents exposes interface for
 * updating the user interface components their children are not aware of
 * 
 * @author mtribast
 * 
 */
public interface IDynamicParent {

	/**
	 * Called by the children when something has to be updated by the parents.
	 * 
	 */
	public void updateParentState();

}
