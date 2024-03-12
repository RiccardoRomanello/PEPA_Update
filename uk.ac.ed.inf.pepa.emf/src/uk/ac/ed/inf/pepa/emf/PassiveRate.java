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
 * Represents a 'T' rate, i.e. a unspecified activity rate.
 *  
 * @author mtribast
 * @model
 */
public interface PassiveRate extends Rate {
    
    /**
     * @return the weight of the passive activity
     * @model
     */
    public int getWeight();
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.PassiveRate#getWeight <em>Weight</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Weight</em>' attribute.
	 * @see #getWeight()
	 * @generated
	 */
	void setWeight(int value);

}