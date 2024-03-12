/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui;

import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public interface IFilterManager {
	
	/**
	 * Returns the PEPA model whose filters are managed by this class.
	 * 
	 * @return the PEPA model.
	 */
	public IProcessAlgebraModel getProcessAlgebraModel();
	
	/**
	 * Gets the filter sets for this model.
	 * 
	 * @return the filter sets for this model.
	 */
	public IFilterModel[] getFilterModels();

}