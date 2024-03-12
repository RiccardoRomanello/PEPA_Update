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
package uk.ac.ed.inf.pepa.model;

/**
 * Indicates a rate for a PEPA process activity.
 * 
 * @author mtribast
 */
public interface FiniteRate extends Rate {
	
    /**
     * @param value the rate. It must be non-negative
     */
//    public void setValue(double value);

    /**
     * @return the rate
     * @model
     */
    public double getValue();
}