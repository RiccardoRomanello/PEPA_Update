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
 * A rate assignment
 * @author mtribast
 * @model
 */
public interface RateAssignment extends EObject {
	
	/**
	 * Get the identifier of this assignment
	 * 
	 * @model required="true" containment="true"
	 * @return the identifier of this assignment
	 */
	RateIdentifier getRateIdentifier();
	
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.RateAssignment#getRateIdentifier <em>Rate Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rate Identifier</em>' containment reference.
	 * @see #getRateIdentifier()
	 * @generated
	 */
	void setRateIdentifier(RateIdentifier value);

	/**
	 * Get the rate which is bound to {@link #getRateIdentifier()}.
	 * According to the specifications, an assigned rate must be only
	 * of finite type.
	 * 
	 * @model required="true" containment="true"
	 * @return the bound rate
	 */
	FiniteRate getRate();
	
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.RateAssignment#getRate <em>Rate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rate</em>' containment reference.
	 * @see #getRate()
	 * @generated
	 */
	void setRate(FiniteRate value);

}
