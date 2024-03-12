/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution;

/**
 * This class represents exception thrown by solvers.
 * <p>
 * In most cases it acts as a wrapper to exceptions thrown by third-party
 * solvers used.
 * 
 * @author mtribast
 * 
 */
@SuppressWarnings("serial")
public class SolverException extends Exception {

	/**
	 * This type is for iterative solvers which have not converged.
	 */
	public static final int NOT_CONVERGED = 0;

	/**
	 * This type is for solvers which have not started running because of some
	 * problems in their configuration.
	 * <p>
	 * This is a critical error which should never occur if the option passing
	 * mechanism is implemented correctly according to the PEPAto API
	 * specification.
	 */
	public static final int CONFIGURATION_ERROR = 1;
	
	/**
	 * Generic type for unknown errors.
	 */
	public static final int UNKNOWN = -1;
	
	private int type;

	/**
	 * Create an exception by wrapping a previously thrown exception from the
	 * actual solver
	 * 
	 * @param t
	 */
	public SolverException(String message, int type) {
		super(message);
		this.type = type;

	}
	
	public SolverException(Throwable t) {
		super(t);
	}

	public int getType() {
		return this.type;
	}

}
