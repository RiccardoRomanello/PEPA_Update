/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

public class KroneckerDisplayState implements Comparable<KroneckerDisplayState> {

	private short stateID;
	
	private KroneckerDisplayModel model;
	
	public KroneckerDisplayState(short stateID, KroneckerDisplayModel model) {
		this.stateID = stateID;
		this.model = model;
	}
	
	public short getID() {
		return stateID;
	}
	
	public String getLabel(boolean isShort) {
		return model.getStateName(stateID, isShort);
	}
	
	public String toString() {
		return String.valueOf(stateID);
	}
	
	public boolean equals(Object o) {
		if (o instanceof KroneckerDisplayState) {
			KroneckerDisplayState state = (KroneckerDisplayState)o;
			return stateID == state.stateID;
		}
		return false;
	}

	public int compareTo(KroneckerDisplayState state) {
		return getLabel(false).compareTo(state.getLabel(false));
	}
	
}
