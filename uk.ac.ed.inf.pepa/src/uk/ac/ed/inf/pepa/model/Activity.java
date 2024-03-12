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
 * Representation of a PEPA activity, i.e. an action type and its associated
 * rate.
 * 
 * @author mtribast
 * 
 */

public interface Activity extends ModelElement {

	/**
	 * Get the rate at which this activity happens
	 * @return the rate
	 */
	public Rate getRate();

	/**
	 * Get the action type of this activity
	 * @return the action type
	 */
	public Action getAction();

}