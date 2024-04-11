/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 09-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model.internal;

import uk.ac.ed.inf.pepa.model.ActionLevel;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.SilentAction;

/**
 * Implementation of unknown or unspecified action types.
 * 
 * @author mtribast
 * @see SilentAction
 */
public class SilentActionImpl implements SilentAction {

    
    private NamedAction oldAction = null;
    
    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.Action#prettyPrint()
     */
    public String prettyPrint() {
        
        return SilentAction.TAU + ((oldAction==null)?"":"["+oldAction.prettyPrint()+"]");
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof SilentAction))
            return false;
        else
            return true;
    }
    
    public int hashCode() {
        return SilentAction.TAU.hashCode();
    }

    public void setHiddenAction(NamedAction old) {
        /* null is allowed
         * 
         */
        oldAction = old;
    }

    public NamedAction getHiddenAction() {
        return oldAction;
    }
    
    public ActionLevel getLevel() {
    	return ActionLevel.UNDEFINED;
    }
}