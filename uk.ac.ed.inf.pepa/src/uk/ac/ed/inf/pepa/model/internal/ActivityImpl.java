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

import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.Activity;
import uk.ac.ed.inf.pepa.model.Rate;

/**
 * Implementation of an activity.
 * 
 * @see Activity
 * @author mtribast
 * 
 */
public class ActivityImpl implements Activity {

    private Action action;

    private Rate rate;

    /**
     * @see Activity
     */
    public void setAction(Action action) {
        if (action == null)
            throw new NullPointerException();
        this.action = action;
    }

    /**
     * @see Activity
     */
    public void setRate(Rate rate) {
        if (rate == null)
            throw new NullPointerException();
        this.rate = rate;
    }

    /**
     * @see Activity
     */
    public Rate getRate() {
        return this.rate;
    }

    /**
     * @see Activity
     */
    public Action getAction() {
        return this.action;
    }

    public String prettyPrint() {
        return "(" + action.prettyPrint() + ", " + rate.prettyPrint() + ")";
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Activity))
            return false;
        Activity act = (Activity) o;
        return this.rate.equals(act.getRate())
                && this.action.equals(act.getAction());
    }
    
    public int hashCode() {
        return rate.hashCode() + action.hashCode();
    }
}