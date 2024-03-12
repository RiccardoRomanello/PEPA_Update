/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.HashSet;
import java.util.Iterator;

public class AbstractCTMCProperty implements Iterable<AbstractCTMCState> {
	
	private HashSet<AbstractCTMCState> states;
	
	private boolean hasChanged;
	
	public AbstractCTMCProperty(AbstractCTMC abstractCTMC) {
		this.states = new HashSet<AbstractCTMCState>();
		this.hasChanged = false;
	}
	
	public void addState(AbstractCTMCState state) {
		if (!states.contains(state)) {
			states.add(state);
			hasChanged = true;
		}
	}
	
	public boolean containsState(AbstractCTMCState state) {
		return states.contains(state);
	}
	
	public void resetChanged() {
		hasChanged = false;
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}
	
	// Iterator
	public Iterator<AbstractCTMCState> iterator() {
		return states.iterator();
	}
	
}
