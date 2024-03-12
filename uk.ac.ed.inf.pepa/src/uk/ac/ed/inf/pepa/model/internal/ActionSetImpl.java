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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.ActionSet;
import uk.ac.ed.inf.pepa.model.NamedAction;

/**
 * Implementation of the action set
 * 
 * @author mtribast
 * @see ActionSet
 */
public class ActionSetImpl implements ActionSet {

    private Set<Action> actions;

    public ActionSetImpl() {
        actions = new LinkedHashSet<Action>();
    }

    /**
     * @see ActionSet
     */
    public boolean add(Action action) {
        if (action == null)
            throw new NullPointerException();
        return actions.add(action);
    }

    /**
     * @see ActionSet
     */
    public boolean contains(Action action) {
        return actions.contains(action);
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.ActionSet#iterator()
     */
    public Iterator<Action> iterator() {
    	
    	final Iterator<Action> iterator = actions.iterator();
    	
        return new Iterator<Action>() {
        	
			public boolean hasNext() {
				return iterator.hasNext();
			}

			public Action next() {
				return iterator.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
        	
        };
        
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.inf.pepa.ActionSet#size()
     */
    public int size() {
        return actions.size();
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        
        if (o == this)
            return true;
        
        if (!(o instanceof ActionSet))
            return false;
        
        ActionSet actionSet = (ActionSet) o;
        boolean error = false;
        Iterator<Action> iter = actions.iterator();
        Action action = null;
        while (iter.hasNext() == true) {
            action = iter.next();
            if (actionSet.contains(action) == false) {
                error = true;
                break;
            }
        }
        return !error;
    }
    
    public int hashCode() {
        Iterator<Action> iter = actions.iterator();
        int hash = 0;
        while (iter.hasNext() == true) {
            hash += (iter.next()).hashCode();
        }
        return hash;
        
    }

	public String prettyPrint() {
		StringBuffer set = new StringBuffer();
		set.append("<");
        Iterator<Action> iter = actions.iterator();
        while (iter.hasNext()) {
            Action action = iter.next();
            set.append(((NamedAction) action).getName());
            set.append(",");
        }

        set.setCharAt(set.length() - 1, '>');
        return set.toString();
 	}
}