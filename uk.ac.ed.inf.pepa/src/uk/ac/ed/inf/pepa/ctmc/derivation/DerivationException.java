/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 14-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.ctmc.derivation;

/**
 * Exception that is thrown when something goes wrong during state space
 * exploration.
 * 
 * @author mtribast
 * 
 */
public class DerivationException extends Exception {

	/*
	 * Automatically generated. 
	 */
	private static final long serialVersionUID = -2200822544317024932L;
	
	public DerivationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DerivationException(String message) {
		super(message);
	}
	
	public DerivationException(Throwable t) {
		super(t);
	}
}
