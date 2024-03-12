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
 * Top-level general representation of a PEPA component.
 * 
 * @author mtribast
 */
public interface Process extends ModelElement {
    
    /**
     * Accept a {@link Visitor}
     * @param v
     */
    public void accept(Visitor v);
    
}