/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf;

/**
 * Expression for a finite rate
 * @author mtribast
 * @model 
 *
 */
public interface RateExpression extends FiniteRate {
	
	
	/**
	 * Get the left hand side of this expression, which must be
	 * a finite rate
	 * 
	 * @return the left hand side
	 * @model required="true" containment="true"
	 */
	FiniteRate getLeftHandSide();
	
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.RateExpression#getLeftHandSide <em>Left Hand Side</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left Hand Side</em>' containment reference.
	 * @see #getLeftHandSide()
	 * @generated
	 */
	void setLeftHandSide(FiniteRate value);

	/**
	 * Get the right hand side of this expression, which must be
	 * a finite rate
	 * 
	 * @return the left hand side
	 * @model required="true" containment="true"
	 */
	FiniteRate getRightHandSide();
	
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.RateExpression#getRightHandSide <em>Right Hand Side</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right Hand Side</em>' containment reference.
	 * @see #getRightHandSide()
	 * @generated
	 */
	void setRightHandSide(FiniteRate value);

	/**
	 * @model
	 */
	RateOperator getOperator();

	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.RateExpression#getOperator <em>Operator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Operator</em>' attribute.
	 * @see uk.ac.ed.inf.pepa.emf.RateOperator
	 * @see #getOperator()
	 * @generated
	 */
	void setOperator(RateOperator value);

}
