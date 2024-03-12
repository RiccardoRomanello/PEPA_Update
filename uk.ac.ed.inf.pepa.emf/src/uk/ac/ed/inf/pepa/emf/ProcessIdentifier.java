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
 * Implementation of a process constant
 * 
 * @author mtribast
 * @model
 */
public interface ProcessIdentifier extends uk.ac.ed.inf.pepa.emf.Process {

    /**
     * @model required="true"
     * @return the name of the constant
     */
    public String getName();
    
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.ProcessIdentifier#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

}