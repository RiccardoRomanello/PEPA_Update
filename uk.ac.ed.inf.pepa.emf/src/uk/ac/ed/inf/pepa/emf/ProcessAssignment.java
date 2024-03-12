/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf;

import org.eclipse.emf.ecore.EObject;

/**
 * A process assignment
 * @author mtribast
 * @model
 */
public interface ProcessAssignment extends EObject {
	
	/**
	 * Get the identifier for this process assignment
	 * 
	 * @return the identifier of the process assignment
	 * @model required="true" containment="true"
	 * 
	 *
	 */
	ProcessIdentifier getProcessIdentifier();
	
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.ProcessAssignment#getProcessIdentifier <em>Process Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Process Identifier</em>' containment reference.
	 * @see #getProcessIdentifier()
	 * @generated
	 */
	void setProcessIdentifier(ProcessIdentifier value);

	/**
	 * Get the assigned process in this assignment
	 * @return the process which is bound to the {@link #getProcessIdentifier()}
	 * @model required="true" containment="true"
	 */
	uk.ac.ed.inf.pepa.emf.Process getProcess();

	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.ProcessAssignment#getProcess <em>Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Process</em>' containment reference.
	 * @see #getProcess()
	 * @generated
	 */
	void setProcess(uk.ac.ed.inf.pepa.emf.Process value);

}
