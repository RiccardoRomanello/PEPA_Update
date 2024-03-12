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

import uk.ac.ed.inf.pepa.model.Activity;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Visitor;

/**
 * Implementation of the Prefix process
 * 
 * @author mtribast
 * @see Prefix
 */
public class PrefixImpl implements Prefix {

    private Process target;

    private Activity activity;

    public PrefixImpl() {
        this.target = null;
        this.activity = null;
    }

    /**
     * @see Prefix
     */
    public void setTargetProcess(Process target) {
        if (target == null)
            throw new NullPointerException("Target must be not null");
        this.target = target;
    }

    /**
     * @see Prefix
     */
    public void setActivity(Activity activity) {
        if (activity == null)
            throw new NullPointerException("Activity must not be null");
        this.activity = activity;

    }

    /**
     * @return
     */
    public Process getTargetProcess() {
        return target;
    }

    /**
     * @return
     */
    public Activity getActivity() {
        return activity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#accept(uk.ac.ed.inf.pepa.Visitor)
     */
    public void accept(Visitor v) {
        v.visitPrefix(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#prettyPrint()
     */
    public String prettyPrint() {
        return this.activity.prettyPrint() + "." + this.target.prettyPrint();
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Prefix))
            return false;
        Prefix prefix = (Prefix) o;
        return this.activity.equals(prefix.getActivity())
                && this.target.equals(prefix.getTargetProcess());

    }
    
    public int hashCode() {
        return this.activity.hashCode() + this.target.hashCode();
    }
}