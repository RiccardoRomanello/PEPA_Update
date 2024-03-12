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
package uk.ac.ed.inf.pepa.emf;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * This interface represents a PEPA model.
 * <p>
 * Examples on how to use the API for creating a new model are available in the
 * class <code>uk.ac.ed.inf.pepa.test.Test</code>
 * 
 * @author mtribast
 * @model
 * 
 */
public interface Model extends EObject {
	
	/**
	 * Retrieves the process definitions for this model
	 * 
	 * @return the collection of process definitions
	 * @model type="ProcessAssignment" containment="true"
	 */
	public EList getProcessAssignments();

	/**
	 * Retrieves the rate definitions of this model
	 * 
	 * @return the collection of rate definitions
	 * @model type="RateAssignment" containment="true"
	 */
	public EList getRateAssignments();

	/**
	 * Retrieves the system equation.
	 * 
	 * @return the system equation
	 * @model containment="true" required="true"
	 */
	public uk.ac.ed.inf.pepa.emf.Process getSystemEquation();
	
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Model#getSystemEquation <em>System Equation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>System Equation</em>' containment reference.
	 * @see #getSystemEquation()
	 * @generated
	 */
	void setSystemEquation(uk.ac.ed.inf.pepa.emf.Process value);

}