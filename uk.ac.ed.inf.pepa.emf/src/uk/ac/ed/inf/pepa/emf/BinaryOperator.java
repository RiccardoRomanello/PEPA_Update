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
 * Provides common operations for PEPA binary operators (i.e.,
 * <code>Choice</code> and <code>Cooperation</code>
 * 
 * @author mtribast
 * @model abstract="true"
 *  
 */
public interface BinaryOperator extends uk.ac.ed.inf.pepa.emf.Process {
    /**
     * @model required="true" containment="true"
     * @return
     */
    public uk.ac.ed.inf.pepa.emf.Process getRightHandSide();
    
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.BinaryOperator#getRightHandSide <em>Right Hand Side</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right Hand Side</em>' containment reference.
	 * @see #getRightHandSide()
	 * @generated
	 */
	void setRightHandSide(uk.ac.ed.inf.pepa.emf.Process value);

    /**
     * @model required="true" containment="true"
     * @return
     */
    public uk.ac.ed.inf.pepa.emf.Process getLeftHandSide();
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.BinaryOperator#getLeftHandSide <em>Left Hand Side</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left Hand Side</em>' containment reference.
	 * @see #getLeftHandSide()
	 * @generated
	 */
	void setLeftHandSide(uk.ac.ed.inf.pepa.emf.Process value);

}