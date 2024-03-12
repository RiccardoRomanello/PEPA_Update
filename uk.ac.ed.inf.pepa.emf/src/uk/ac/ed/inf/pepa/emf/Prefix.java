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
 * Representation of the PEPA Prefix process
 * 
 * @author mtribast
 * @model
 *  
 */
public interface Prefix extends uk.ac.ed.inf.pepa.emf.Process {
    
    /**
     * @model required="true" containment="true"
     * @return
     */
    public uk.ac.ed.inf.pepa.emf.Process getTargetProcess();

	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Prefix#getTargetProcess <em>Target Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Process</em>' containment reference.
	 * @see #getTargetProcess()
	 * @generated
	 */
	void setTargetProcess(uk.ac.ed.inf.pepa.emf.Process value);

    /**
     * @model required="true" containment="true"
     * @return
     */
    public Activity getActivity();
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.Prefix#getActivity <em>Activity</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Activity</em>' containment reference.
	 * @see #getActivity()
	 * @generated
	 */
	void setActivity(Activity value);

}