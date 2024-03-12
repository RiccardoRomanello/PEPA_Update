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

import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Visitor;

/**
 * Implementation of Constant
 * 
 * @author mtribast
 * @see Constant
 *  
 */
public class ConstantImpl implements Constant {

    private Process resolved;

    private String name;

    /**
     * @see Constant
     */
    public void resolve(Process process) {
        if (process == null)
            throw new NullPointerException();
        this.resolved = process;
    }

    /**
     * @see Constant
     */
    public void setName(String name) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("A name must be specified");
        this.name = name;

    }

    /**
     * @see Constant
     */
    public String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#accept(uk.ac.ed.inf.pepa.Visitor)
     */
    public void accept(Visitor v) {
        v.visitConstant(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Constant#getResolvedProcess()
     */
    public Process getBinding() {

        return this.resolved;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#prettyPrint()
     */
    public String prettyPrint() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        /*else 
        	return false;*/
        /* Old Implementation */
        if (!(o instanceof Constant))
            return false;
        return this.name.equals(((Constant) o).getName());
        
    }

    public int hashCode() {
        return this.name.hashCode();
    }

}