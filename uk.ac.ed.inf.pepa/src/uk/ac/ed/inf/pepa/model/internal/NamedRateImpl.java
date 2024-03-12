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
package uk.ac.ed.inf.pepa.model.internal;

import uk.ac.ed.inf.pepa.model.NamedRate;

/**
 * Implementation of a NamedRate
 * 
 * @see NamedRate
 * @author mtribast
 */
public class NamedRateImpl extends FiniteRateImpl implements NamedRate {
    
    private String name;
    
    /**
     * @see NamedRate
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see NamedRate
     */
    public void setName(String name) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("A name must be specified");
        this.name = name;
    }
    
    /*
     * 02/11/2007 - Equality so as to allow common subexpression
     * elimination: the value is checked rather than the symbol. 
     */
    public boolean equals(Object o) {
        if (!(o instanceof NamedRate))
            return false;
        return super.equals(o);// && name.equals(((NamedRate)o).getName());
    }
    
    public int hashCode() {
        return super.hashCode();// + name.hashCode();
    }
    
    public String prettyPrint() {
    	return super.prettyPrint();
    }
}