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
 * Implementation of a process constant.
 * 
 * @author mtribast
 */
public interface Constant extends Process {
   
    /**
     * Get the name of the constant
     * @return the name of the constant
     */
    public String getName();
    
    /**
     *  
     * @return the resolved Process 
     */
    public Process getBinding();
}