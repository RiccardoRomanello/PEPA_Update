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

import uk.ac.ed.inf.pepa.model.BinaryOperator;
import uk.ac.ed.inf.pepa.model.Process;

/**
 * Implementation for binary operators.
 * 
 * @author mtribast
 * @see BinaryOperator
 */
abstract class BinaryOperatorImpl implements BinaryOperator {

    private Process leftHandSide;

    private Process rightHandSide;

    public void setRightHandSide(Process process) {
        if (process == null)
            throw new NullPointerException();
        this.rightHandSide = process;
    }

    public void setLeftHandSide(Process process) {
        if (process == null)
            throw new NullPointerException();
        this.leftHandSide = process;
    }

    public Process getRightHandSide() {
        return this.rightHandSide;
    }

    public Process getLeftHandSide() {
        return this.leftHandSide;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof BinaryOperator))
            return false;
        BinaryOperator other = (BinaryOperator) o;
        return this.getLeftHandSide().equals(other.getLeftHandSide()) &&
        	this.getRightHandSide().equals(other.getRightHandSide());
    }
    
    public int hashCode() {
        return this.getLeftHandSide().hashCode() + 
        	this.getRightHandSide().hashCode();
    }
    
    
    
    
    
    
}