/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

import java.util.Arrays;

public class AbstractState {

	private short[] concreteStates;
	
	private short stateNumber = -1;

	public AbstractState(short[] states) {
		concreteStates = new short[states.length];
		System.arraycopy(states, 0, concreteStates, 0, states.length); 
		Arrays.sort(concreteStates);
	}
	
	/**
	 * Creates an AbstractState with only a single state.
	 */
	public AbstractState(short state) {
		short[] singleState = { state };
		concreteStates = singleState;
	}
	
	public short[] getConcrete() {
		return concreteStates;
	}

	public void setID(short stateNumber) {
		this.stateNumber = stateNumber;
	}
	
	public short getID() {
		return stateNumber;
	}
	
	public int size() {
		return concreteStates.length;
	}
	
	public String toString() {
		return stateNumber + " = " + Arrays.toString(concreteStates);	
	}
	
}
