/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;

public interface IFilterModel {

	/**
	 * Gets the name of this filter set.
	 * 
	 * @return the name of this filter set.
	 */
	public String getName();

	/**
	 * Get the viewer filters for this model.
	 * 
	 * @return the filters of this model.
	 */
	public IStateSpaceFilter[] getFilters();

}