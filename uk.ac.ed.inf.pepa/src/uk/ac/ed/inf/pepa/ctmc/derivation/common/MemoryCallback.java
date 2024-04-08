/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.ICallbackListener;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.MemoryStateSpace;
import uk.ac.ed.inf.pepa.model.ActionLevel;

public class MemoryCallback implements ICallbackListener {

	private static final int INITIAL_CAPACITY = 1000;

	private IntegerArray row;

	private IntegerArray column;

	private DoubleArray value;

	private ShortArray action;

	private ArrayList<ActionLevel> action_level;

	private int t;

	private int currentColumn;

	private double sum;

	private short currentAction;

	private ActionLevel currentLevel;

	private int t_count = 0;
	
	private int maximumLength = 0;
	
	private boolean hasVariableLength = false;
	
	private boolean isDone = false;

	public MemoryCallback() {
		row = new IntegerArray(INITIAL_CAPACITY);
		column = new IntegerArray(INITIAL_CAPACITY);
		value = new DoubleArray(INITIAL_CAPACITY * 2);
		action = new ShortArray(INITIAL_CAPACITY * 2);
		action_level = new ArrayList<ActionLevel>(INITIAL_CAPACITY * 2);
	}

	public IStateSpace done(ISymbolGenerator generator, ArrayList<State> states)
			throws DerivationException {
		row.trimToSize();
		column.trimToSize();
		value.trimToSize();
		action.trimToSize();
		action_level.trimToSize();
		isDone = true;
		/*
		System.err.println("Row: " + row);
		System.err.println("Col: " + column);
		System.err.println("Rates: " + value);
		System.err.println("Actions: " + action);
		*/
		return new MemoryStateSpace(generator, states, row, column, action,
									value, hasVariableLength, maximumLength);
	}
	
	// FIXME: this is a bit of a hack to retrieve these values...
	public IntegerArray getRow() throws RuntimeException {
		if (!isDone)
			throw new RuntimeException("Cannot retrieve row before completion!");
		return row;
	}
	
	public IntegerArray getColumn() throws RuntimeException {
		if (!isDone)
			throw new RuntimeException("Cannot retrieve column before completion!");
		return column;
	}

	public DoubleArray getRates() throws RuntimeException {
		if (!isDone)
			throw new RuntimeException("Cannot retrieve value before completion!");
		return value;
	}
	
	public ShortArray getActions() throws RuntimeException {
		if (!isDone)
			throw new RuntimeException("Cannot retrieve actions before completion!");
		return action;
	}

	public ArrayList<ActionLevel> getActionLevels() throws RuntimeException {
		if (!isDone)
			throw new RuntimeException("Cannot retrieve action levels before completion!");
		return action_level;
	}

	public void foundDerivatives(State state, Transition[] transitions) {
		this.exploringState(state.stateNumber, transitions.length);
		if (state.stateNumber == 0)
			maximumLength = state.fState.length;
		int oldLength = maximumLength;
		maximumLength = Math.max(state.fState.length, maximumLength);
		//System.err.println(state.stateNumber);
		//System.err.println("Maximum length:" + maximumLength);
		//System.err.println("Old length: " + oldLength);
		if (oldLength != maximumLength)
			hasVariableLength = true;
		t_count += transitions.length;
		Arrays.sort(transitions);
		for (Transition t: transitions) {

			/*
			 * System.err.println("--\nFound:"); for (short s :
			 * t.fTargetProcess) System.err.print(s + " ");
			 * System.err.println("\nAction: " + t.fActionId);
			 * System.err.println("Rate: " + t.fRate);
			 * System.err.println("Column: " + t.columnNumber + "\n--");
			 */

			foundTransition(state.stateNumber, t.fState.stateNumber, t.fRate,
					t.fActionId, t.fLevel);
		}
	}

	private void exploringState(int state, int numTransitions) {
		t = numTransitions;
		currentColumn = -1;
		row.add(column.size());
	}

	private void foundTransition(int i, int j, double rate, short action, ActionLevel action_level) {
		if (currentColumn != j) {
			if (currentColumn != -1)
				writeAction();

			column.add(j);
			column.add(value.size());
			currentColumn = j;
			currentAction = action;
			currentLevel = action_level;
			sum = 0;
		}
		
		if (currentAction != action) {
			writeAction();
			currentAction = action;
			currentLevel = action_level;
			sum = rate;
		} else {
			sum += rate;
		}
		t = t - 1;
		if (t == 0)
			writeAction();

	}

	private void writeAction() {
		action.add(currentAction);
		action_level.add(currentLevel);
		value.add(sum);
	}

}
