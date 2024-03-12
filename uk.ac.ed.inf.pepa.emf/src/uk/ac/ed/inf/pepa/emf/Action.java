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
 * High level representation of an action type. Implementations of this
 * interface shall provide sound implementations of <code>equals</code> and
 * <code>hashCode</code>. In particular, equality shall be based on logical
 * rather then physical notion. See interface specialisation for more detail.
 * 
 * @see ActionIdentifier
 * @see SilentAction
 * 
 * @author mtribast
 * @model abstract="true"
 *  
 */
public interface Action extends EObject {
    
}