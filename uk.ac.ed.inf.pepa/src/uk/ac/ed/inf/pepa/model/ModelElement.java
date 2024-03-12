/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.model;

/**
 * Abstract interface for objects representing operators in the PEPA
 * language.
 * 
 * @author mtribast
 *
 */
public abstract interface ModelElement {
	
	/**
	 * Get a human-readable string representation of the model
	 * @return the string representation of the model
	 */
	String prettyPrint();

}
