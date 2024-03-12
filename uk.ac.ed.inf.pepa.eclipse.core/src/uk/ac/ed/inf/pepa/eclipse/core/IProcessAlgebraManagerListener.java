/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

/**
 * A PEPA manager listener is notified when a PEPA model
 * is added or removed in the manager.
 *  
 * @author mtribast
 *
 */
public interface IProcessAlgebraManagerListener {

	public void modelAdded(IProcessAlgebraModel model);
	
	public void modelRemoved(IProcessAlgebraModel model);
	
}
