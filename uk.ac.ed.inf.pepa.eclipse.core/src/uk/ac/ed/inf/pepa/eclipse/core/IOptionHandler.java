/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import org.eclipse.core.resources.IResource;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public interface IOptionHandler {

	public IResource getResource();
	
	/**
	 * Gets the current option map for this model.
	 * 
	 * @return
	 */
	public OptionMap getOptionMap();
	
	/**
	 * Sets the current option map of this model and forgets the previous
	 * settings. If map is <code>null</code>, the model is initialised to its
	 * default settings
	 * 
	 * @param map
	 */
	public void setOptionMap(OptionMap map);

	public Object getOption(String key);

}