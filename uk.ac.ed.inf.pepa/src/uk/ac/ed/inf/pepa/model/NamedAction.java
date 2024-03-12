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
package uk.ac.ed.inf.pepa.model;

/**
 * Represents a fully specified action type. Implementations shall also override
 * the <code>equals</code> and <code>hashCode</code> methods. More
 * precisely, two named actions shall be equal when the type is the same.
 * <p>
 * For example, a straightforward implementation may be based on
 * <code>String</code> equality as follows:
 * 
 * <pre>
 * 
 *  public boolean equals(Object o) {
 *     if (!(o instanceof NamedAction))
 *         return false
 *     return ((NamedAction)o).getName().equals(getName()); 
 *  }
 *  
 * </pre>
 * 
 * @author mtribast
 *  
 */
public interface NamedAction extends Action {
   
	/**
     * @return the name of the action
     */
    public String getName();


}