/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 09-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.emf;

/**
 * Represents a fully specified action type.
 * 
 * @author mtribast
 * @model
 *  
 */
public interface ActionIdentifier extends Action {
	
    /**
     * @return the name of the action
     * @model required="true"
     */
    public String getName();
    
	/**
	 * Sets the value of the '{@link uk.ac.ed.inf.pepa.emf.ActionIdentifier#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

}