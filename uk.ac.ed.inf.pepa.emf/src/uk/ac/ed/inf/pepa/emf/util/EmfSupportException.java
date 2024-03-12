/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.util;

/**
 * This exception is thrown when <code>EmfTools</code> calls
 * throw errors. Those errors are simply wrapped by this exception.
 * 
 * @author mtribast
 * 
 */
public class EmfSupportException extends Exception {
	/**
	 * Create a new exception
	 * 
	 * @param message
	 *            human readable message describing the exception
	 * @param t
	 *            error causing this exception to be thrown
	 */
	public EmfSupportException(String message, Throwable t) {
		super(message, t);
	}
}
