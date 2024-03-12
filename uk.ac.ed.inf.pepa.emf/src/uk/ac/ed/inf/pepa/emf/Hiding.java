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

/**
 * Representation of the PEPA Hiding combinator.
 * E/L
 * 
 * @author mtribast
 * @model
 */
public interface Hiding extends ProcessWithSet {
    
    /**
     * Gets E
     * @return
     * @model containment="true" required="true"
     */
    public uk.ac.ed.inf.pepa.emf.Process getHiddenProcess();
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Hiding#getHiddenProcess <em>Hidden Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hidden Process</em>' containment reference.
	 * @see #getHiddenProcess()
	 * @generated
	 */
	void setHiddenProcess(uk.ac.ed.inf.pepa.emf.Process value);

}