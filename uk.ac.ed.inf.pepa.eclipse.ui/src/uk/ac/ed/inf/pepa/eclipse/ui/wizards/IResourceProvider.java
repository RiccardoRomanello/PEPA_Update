/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards;

import org.eclipse.core.resources.IResource;

/**
 * Objects that can return an instance of a resource they
 * are working on
 * @author mtribast
 *
 */
public interface IResourceProvider {
	/**
	 * Return the underlying resource this object is working on.
	 * @return the underlying resource
	 */
	public IResource getResource();
	
}
