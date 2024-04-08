/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import uk.ac.ed.inf.pepa.model.ActionLevel;

public class Buffer {

	private static final int SIZE = 500;

	private Transition[] buf;

	private int counter;

	public int maxRequestsPerState;

	public int missedRequests;

	public int totalRequests;

	private int requests;

	public Buffer(int stateVectorSize) {
		buf = new Transition[SIZE];
		for (int i = 0; i < SIZE; i++) {
			Transition t = new Transition();
			t.fTargetProcess = new short[stateVectorSize];
			buf[i] = t;
			
		}
		counter = 0;
		maxRequestsPerState = 0;
		requests = 0;
		missedRequests = 0;
		totalRequests = 0;
	}

	public void clear() {
		maxRequestsPerState = Math.max(requests, maxRequestsPerState);
		totalRequests += requests;
		requests = counter = 0;
	}

	public Transition getTransition(short[] state, int offset, int length,
			short actionId, ActionLevel action_level, double rate) {
		requests++;
		Transition newTransition = null;
		if (counter == buf.length) {
			// just create a new state
			missedRequests++;
			newTransition = new Transition();
			newTransition.fTargetProcess = new short[state.length];
		} else {
			newTransition = buf[counter++];
		}
		newTransition.fActionId = actionId;
		newTransition.fLevel = action_level;
		newTransition.fRate = rate;
		for (int i = 0; i < length; i++) {
			newTransition.fTargetProcess[offset + i] = state[offset + i];
		}
		return newTransition;
	}
}