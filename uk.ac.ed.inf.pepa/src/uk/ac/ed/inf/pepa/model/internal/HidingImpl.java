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

import uk.ac.ed.inf.pepa.model.ActionSet;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.ProcessWithSet;
import uk.ac.ed.inf.pepa.model.Visitor;

/**
 * Implementation of Hiding.
 * 
 * @author mtribast
 * @see Hiding
 */
public class HidingImpl implements Hiding {

    private ActionSet actionSet;

    private Process hiddenProcess;

    public HidingImpl() {
        this.actionSet = null;
        this.hiddenProcess = null;
    }

    /**
     * @see ProcessWithSet
     */
    public void setActionSet(ActionSet actionSet) {
        if (actionSet == null)
            throw new NullPointerException("Cannot accept null action set");
        this.actionSet = actionSet;
    }

    /**
     * @see ProcessWithSet
     */
    public ActionSet getActionSet() {
        return this.actionSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#accept(uk.ac.ed.inf.pepa.Visitor)
     */
    public void accept(Visitor v) {
        v.visitHiding(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#prettyPrint()
     */
    public String prettyPrint() {
       
        return hiddenProcess.prettyPrint() + "/" + actionSet.prettyPrint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Hiding#setHiddenProcess(uk.ac.ed.inf.pepa.Process)
     */
    public void setHiddenProcess(Process p) {
        if (p == null)
            throw new NullPointerException();
        this.hiddenProcess = p;

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Hiding#getHiddenProcess()
     */
    public Process getHiddenProcess() {
        return this.hiddenProcess;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Hiding))
            return false;
        else {
            Hiding other = (Hiding) o;
            return this.actionSet.equals(other.getActionSet())
                    && this.hiddenProcess.equals(other.getHiddenProcess());
        }
    }
    
    public int hashCode() {
        return this.actionSet.hashCode() + this.hiddenProcess.hashCode();
    }
}