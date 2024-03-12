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

import java.util.Collection;

import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * This interface represents a PEPA model.
 * <p>
 * Examples on how to use the API for creating a new model are available in the
 * class <code>uk.ac.ed.inf.pepa.test.Test</code>
 * 
 * @author mtribast
 * 
 */
public interface Model {
	
	/**
	 * Return the Abstract Syntax Tree of the Model
	 * @return the AST of this model
	 */
	public ModelNode getASTModel();
	
	/**
	 * Retrieves the process definitions for this model
	 * 
	 * @return the collection of process definitions
	 */
	public Collection<Constant> getProcessDefinitions();

	/**
	 * Retrieves the rate definitions of this model
	 * 
	 * @return the collection of rate definitions
	 */
	public Collection<NamedRate> getRateDefinitions();

	/**
	 * Retrieves the system equation.
	 * 
	 * @return the system equation
	 */
	public Process getSystemEquation();

	/**
	 * Sets the system equation.
	 * 
	 * @param equation
	 *            the system equation
	 */
	public void setSystemEquation(Process equation);

}