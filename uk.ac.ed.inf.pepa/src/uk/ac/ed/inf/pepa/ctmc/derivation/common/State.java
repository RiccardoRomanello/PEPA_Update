/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.Arrays;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;

/**
 * Lightweight wrapper around a state description vector to be used in hash maps
 * and alike.
 * 
 * @author mtribast
 * 
 */
public class State {

	private static final String COMMA = ",";

	private static final String OPEN = "[";

	private static final String CLOSE = "]";

	/**
	 * Default value for a state that is not initialised
	 */
	public static final int NOT_INITIALISED = -1;

	/**
	 * State description vector. Each element represents a sequential
	 * component. If aggregation of process arrays is turned on, an element may
	 * represent an aggregated array. To retrieve human-readable descriptions of
	 * the state, clients should use services from {@link IStateSpace}. <br>
	 * For performance, this is a public method. Clients must not change this
	 * array.
	 * 
	 */
	public final short[] fState;

	/**
	 *  Position of the state in the generator matrix.
	 *  <br>
	 *  Clients must not change this value.
	 *  
	 */
	public int stateNumber = NOT_INITIALISED;

	private int hashCode;

	public State(short[] stateVector, int hashCode) {
		this.fState = stateVector;
		//this.fState = new short[stateVector.length];
		//System.arraycopy(stateVector, 0, fState, 0, stateVector.length);
		this.hashCode = hashCode;
	}

	public int hashCode() {
		return hashCode;
	}

	/**
	 * Two states are equal if their descriptors are.
	 * 
	 */
	public boolean equals(Object o) {
		if (!(o instanceof State))
			return false;
		return Arrays.equals(fState, ((State) o).fState);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(OPEN);
		for (int i = 0; i < fState.length; i++)
			buf.append(fState[i] + ((i < fState.length - 1) ? COMMA : ""));
		buf.append(CLOSE);
		return buf.toString();
	}

}
