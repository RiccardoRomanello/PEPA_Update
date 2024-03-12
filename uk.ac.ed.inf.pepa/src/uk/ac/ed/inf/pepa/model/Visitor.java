/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 11-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model;

/**
 * Visitor description for implementations of {@link Process}.
 * 
 * @author mtribast
 *
 */
public interface Visitor {
    /**
     * @param prefix
     */
    public void visitPrefix(Prefix prefix);

    /**
     * @param choice
     */
    public void visitChoice(Choice choice);

    /**
     * @param hiding
     */
    public void visitHiding(Hiding hiding);

    /**
     * @param cooperation
     */
    public void visitCooperation(Cooperation cooperation);

    /**
     * @param constant
     */
    public void visitConstant(Constant constant);
    
    /**
     * 
     * @param aggregation
     */
    public void visitAggregation(Aggregation aggregation);
    
}