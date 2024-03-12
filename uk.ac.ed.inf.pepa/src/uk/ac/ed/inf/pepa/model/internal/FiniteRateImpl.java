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

import uk.ac.ed.inf.pepa.model.FiniteRate;

/**
 * Implementation of an active rate.
 * 
 * @author mtribast
 *  
 */
public class FiniteRateImpl implements FiniteRate {

    private double value;

    public void setValue(double value) {
        /*
         * One case where it is common practice to throw a RuntimeException is
         * when the user calls a method incorrectly. For example, a method can
         * check if one of its arguments is incorrectly null. If an argument is
         * null, the method might throw a NullPointerException, which is an
         * unchecked exception
         */
        if (value < 0)
            throw new IllegalArgumentException("Rate must be non negative");
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.Rate#prettyPrint()
     */
    public String prettyPrint() {
        return ""+value;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof FiniteRate))
            return false;
        return value == ((FiniteRate)o).getValue();
    }
    
    public int hashCode() {
        return (""+value).hashCode();
    }
}