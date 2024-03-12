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

import uk.ac.ed.inf.pepa.model.PassiveRate;

/**
 * Implementation of a passive activity rate.
 * 
 * @see PassiveRate
 * @author mtribast
 *  
 */
public class PassiveRateImpl implements PassiveRate {

    private double weight;

    /**
     * @see PassiveRate
     */
    public void setWeight(double weight) {
        if (weight <= 0)
            throw new IllegalArgumentException(
                    "Weight of a passive rate must be positive");
        this.weight = weight;

    }

    /**
     * @see PassiveRate
     */
    public double getWeight() {
        return this.weight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Rate#prettyPrint()
     */
    public String prettyPrint() {

        return (weight == 1) ? "inf" : "" + weight + "*inf";
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof PassiveRate))
            return false;
        return weight == ((PassiveRate)o).getWeight();
    }
    
    public int hashCode() {
        return (weight+"").hashCode();
    }
}