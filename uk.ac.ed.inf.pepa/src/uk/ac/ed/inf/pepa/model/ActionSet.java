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
package uk.ac.ed.inf.pepa.model;

import java.util.Iterator;

/**
 * Represent a set of actions. Instances are associated to the following PEPA
 * operators: {@link Cooperation}, {@link Hiding}, {@link Aggregation}.
 * 
 * @author mtribast
 * 
 */
public interface ActionSet extends ModelElement {

	/**
	 * Returns <code>true</code> if this set contains the specified action.
	 * 
	 * @param action
	 *            elements whose presence in the set has to be tested.
	 * @return <code>true</code> if this set contains the given action.
	 */
	public boolean contains(Action action);

	/**
	 * Returns the number of action types in this set.
	 * 
	 * @return the size of this set
	 */
	public int size();

	/**
	 * Gets an iterator for the action types in this set. Its
	 * <code>remove</code> method will thrown an
	 * <code>OperationNotSupportedException</code>
	 * 
	 * @return the iterator
	 */
	public Iterator<Action> iterator();
}