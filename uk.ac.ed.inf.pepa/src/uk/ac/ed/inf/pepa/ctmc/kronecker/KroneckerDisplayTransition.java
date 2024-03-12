/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

public class KroneckerDisplayTransition {
	
	private KroneckerDisplayState startState;
	private KroneckerDisplayState endState;
	private KroneckerDisplayAction action;
	
	KroneckerDisplayTransition(KroneckerDisplayState startState,
			                   KroneckerDisplayState endState,
			                   KroneckerDisplayAction action) {
		this.startState = startState;
		this.endState   = endState;
		this.action     = action;
	}
	
	public KroneckerDisplayAction getAction() {
		return action;
	}
	
	public KroneckerDisplayState getStartState() {
		return startState;
	}
	
	public KroneckerDisplayState getEndState() {
		return endState;
	}
	
	public boolean equals(Object o) {
		if (o instanceof KroneckerDisplayTransition) {
			KroneckerDisplayTransition transition = (KroneckerDisplayTransition)o;
			return startState.equals(transition.startState) && endState.equals(transition.endState) && action.equals(transition.action);
		}
		return false;
	}
	
	// returns true if the given transition is symmetric
	public boolean isReverse(KroneckerDisplayTransition transition) {
		return startState.equals(transition.endState) && endState.equals(transition.startState);
	}
	
}
