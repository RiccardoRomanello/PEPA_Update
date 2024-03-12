/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 16-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.emf;

/**
 * Implements an aggregation
 * 
 * @author mtribast
 * @model
 * 
 */
public interface Aggregation extends uk.ac.ed.inf.pepa.emf.Process {

	/**
	 * Gets an instance of the process
	 * 
	 * @return the process
	 * @model required="true" containment="true"
	 */
	public uk.ac.ed.inf.pepa.emf.Process getProcess();

	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Aggregation#getProcess <em>Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Process</em>' containment reference.
	 * @see #getProcess()
	 * @generated
	 */
	void setProcess(uk.ac.ed.inf.pepa.emf.Process value);

	/**
	 * Gets the number of copies
	 * 
	 * @return
	 * @model required="true"
	 */
	public FiniteRate getCopies();

	

	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Aggregation#getCopies <em>Copies</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Copies</em>' containment reference.
	 * @see #getCopies()
	 * @generated
	 */
	void setCopies(FiniteRate value);

}