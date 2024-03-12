/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 08-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model;

/**
 * Representation of the PEPA Hiding combinator
 * 
 * @author mtribast
 * 
 */
public interface Hiding extends ProcessWithSet {

	// public void setHiddenProcess(Process p);

	/**
	 * Gets the process which is hiding the
	 * {@link ProcessWithSet#getActionSet()}.
	 * 
	 * @return the process hiding the action set
	 */
	public Process getHiddenProcess();
}