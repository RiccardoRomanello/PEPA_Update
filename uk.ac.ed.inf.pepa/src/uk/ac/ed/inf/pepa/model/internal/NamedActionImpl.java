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

import uk.ac.ed.inf.pepa.model.NamedAction;

/**
 * Implementation of a named action.
 * 
 * @author mtribast
 * @see NamedAction
 */
public class NamedActionImpl implements NamedAction {

    private String type;

    /**
     * @see NamedAction
     */
    public String getName() {
        return this.type;
    }

    /**
     * @see NamedAction
     */
    public void setName(String name) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("Name must be specified");
        this.type = name;
    }

    public boolean equals(Object o) {
        if (!(o instanceof NamedAction))
            return false;
        return ((NamedAction) o).getName().equals(type);
    }

    public int hashCode() {
        return type.hashCode();
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.Action#prettyPrint()
     */
    public String prettyPrint() {
        return this.type;
    }
}