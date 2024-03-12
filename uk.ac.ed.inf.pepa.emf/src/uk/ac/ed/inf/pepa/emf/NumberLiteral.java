/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf;

/**
 * Number literal finite rate
 * @author mtribast
 * @model
 */
public interface NumberLiteral extends FiniteRate {
	
	/**
	 * Get the double value of this rate
	 * @return the value of this rate
	 * @model
	 */
	public double getValue();
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.NumberLiteral#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(double value);

}
