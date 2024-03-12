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

import org.eclipse.emf.ecore.EObject;

/**
 * Representation of an activity, i.e. an action type and its associated rate.
 * 
 * @author mtribast
 * @model
 */

public interface Activity extends EObject {
    
    /**
     * @model required="true" containment="true"
     * @return the rate
     */
    public Rate getRate();

	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Activity#getRate <em>Rate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rate</em>' containment reference.
	 * @see #getRate()
	 * @generated
	 */
	void setRate(Rate value);

    /**
     * @model required="true" containment="true"
     * @return the action type
     */
    public Action getAction();
    
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Activity#getAction <em>Action</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' containment reference.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(Action value);

}