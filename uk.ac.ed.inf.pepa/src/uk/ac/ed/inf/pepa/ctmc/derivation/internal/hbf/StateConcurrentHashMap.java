/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;

@SuppressWarnings("serial")
public class StateConcurrentHashMap extends ConcurrentHashMap<State,State> {
	
	private AtomicInteger stateNumber = new AtomicInteger(0);
	
	public StateConcurrentHashMap(int i, float f, int length) {
		super(i,f,length);
	}
	
	public final State putIfAbsent(State key, State value) {
		State result = super.putIfAbsent(key, value);
		if (result == null)
			key.stateNumber = stateNumber.getAndIncrement();
		return result;
	}
	

}
