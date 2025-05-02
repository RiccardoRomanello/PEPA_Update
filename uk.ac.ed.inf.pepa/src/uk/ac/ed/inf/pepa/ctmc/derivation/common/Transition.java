/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * A lightweight data structure representing a transition.
 * 
 * @author mtribast
 *
 */
public final class Transition implements Comparable<Transition> {
	
	//public static final int COLUMN_NOT_INITIALISED = -1;
	/**
	 * The target process, represented as an array of <code>short</code>
	 */
	public short[] fTargetProcess;
	
	/**
	 * Identifier of the action type of this transition
	 */
	public short fActionId;

	/**
	 * Level of the transition
	 */
	public ActionLevel fLevel = ActionLevel.UNDEFINED;
	
	/**
	 * Is action reversible
	 */
	public Boolean fReversible = false;
	
	/**
	 * The rate at which this transition occurs
	 */
	public double fRate;
	
	/**
	 * Target state: it will be used instead of 
	 * {@link #columnNumber}
	 */
	public State fState = null;
	
	public Transition() {};
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Action id: " + fActionId + " Rate: " + fRate + 
				" Target Process: [");
		for (int i = 0; i < fTargetProcess.length; i++) {
			buf.append(fTargetProcess[i]);
			if (i < fTargetProcess.length - 1)
				buf.append(",");
		}
		return  buf.toString() + "] Location:" + super.toString();
	}

	public int compareTo(Transition o) {
		//int v = this.columnNumber - o.columnNumber;
		assert fState.stateNumber != State.NOT_INITIALISED;
		assert o.fState.stateNumber != State.NOT_INITIALISED;
		int v = this.fState.stateNumber - o.fState.stateNumber;
		return v != 0 ? v : fActionId - o.fActionId;
	}
}
