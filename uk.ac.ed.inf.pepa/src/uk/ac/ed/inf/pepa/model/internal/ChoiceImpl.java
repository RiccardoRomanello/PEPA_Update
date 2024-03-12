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

import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Visitor;

/**
 * @author mtribast
 *
 */
public class ChoiceImpl extends BinaryOperatorImpl implements Choice {

    
    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.Process#accept(uk.ac.ed.inf.pepa.Visitor)
     */
    public void accept(Visitor v) {
        v.visitChoice(this);
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.Process#prettyPrint()
     */
    public String prettyPrint() {
        return "(" + this.getLeftHandSide().prettyPrint() + " + " + this.getRightHandSide().prettyPrint() + ")";
    }
}