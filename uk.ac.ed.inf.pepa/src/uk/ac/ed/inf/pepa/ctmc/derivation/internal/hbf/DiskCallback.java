/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;

public class DiskCallback implements ICallbackListener {

	private static final String ROW_FILE = "row.dat";

	private static final String COLUMN_FILE = "column.dat";

	private static final String VALUE_FILE = "value.dat";

	private static final String ACTION_FILE = "action.dat";

	private static final int BUFFER_SIZE = 4096 * 4;

	private DataOutputStream outputRow;

	private DataOutputStream outputColumn;

	private DataOutputStream outputValue;

	private DataOutputStream outputAction;

	private String path;

	private int t;

	private int currentColumn;

	private double sum;

	private short currentAction;

	private int t_count = 0;

	private int non_zero_count = 0;

	private IResourceManager manager;
	
	private int maximumLength = 0;
	
	private boolean hasVariableLength = false;


	public DiskCallback(IResourceManager manager) throws DerivationException {
		if (manager == null)
			throw new NullPointerException();
		path = manager.acquirePath(this);
		this.manager = manager;

		try {
			outputRow = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(getPath(ROW_FILE)), BUFFER_SIZE));
			outputColumn = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(getPath(COLUMN_FILE)), BUFFER_SIZE));

			outputValue = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(getPath(VALUE_FILE)), BUFFER_SIZE));
			outputAction = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(getPath(ACTION_FILE)), BUFFER_SIZE));
		} catch (FileNotFoundException e) {
			throw new DerivationException(e);
		}

	}

	private String getPath(String prefix) {
		return path + prefix;
	}

	public IStateSpace done(ISymbolGenerator generator, ArrayList<State> states)
			throws DerivationException {

		try {
			outputRow.close();
			outputColumn.close();
			outputAction.close();
			outputValue.close();

			/*{
				// DEBUG
				DataInputStream rowFile = null;
				DataInputStream actionFile = null;
				DataInputStream valueFile = null;
				DataInputStream columnFile = null;
				// Row data is accessed sequentially
				rowFile = new DataInputStream(new BufferedInputStream(
						new FileInputStream(getPath(ROW_FILE)), BUFFER_SIZE));
				actionFile = new DataInputStream(new BufferedInputStream(
						new FileInputStream(getPath(ACTION_FILE)), BUFFER_SIZE));
				valueFile = new DataInputStream(new BufferedInputStream(
						new FileInputStream(getPath(VALUE_FILE)), BUFFER_SIZE));
				columnFile = new DataInputStream(new BufferedInputStream(
						new FileInputStream(getPath(COLUMN_FILE)), BUFFER_SIZE));

				System.out.println("Row");
				for (int i = 0; i < 4; i++)
					System.out.println(rowFile.readInt());
				System.out.println("Column");
				for (int i = 0; i < 16; i++)
					System.out.println(columnFile.readInt());
				System.out.println("Value");
				for (int i = 0; i < 8; i++)
					System.out.println(valueFile.readDouble());
				System.out.println("Action");
				for (int i = 0; i < 8; i++)
					System.out.println(actionFile.readShort());

			}*/

			return new DiskBasedStateSpace(generator, states, manager,
					getPath(ROW_FILE), getPath(COLUMN_FILE),
					getPath(ACTION_FILE), getPath(VALUE_FILE), t_count, hasVariableLength, maximumLength);
		} catch (IOException e) {
			throw new DerivationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.ICallbackListener#foundDerivatives(uk.ac.ed.inf.pepa.ctmc.derivation.internal.State,
	 *      uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.Transition[])
	 */
	public void foundDerivatives(State state, Transition[] transitions)
			throws DerivationException {
		if (state.stateNumber == 0)
			maximumLength = state.fState.length;
		int oldLength = maximumLength;
		maximumLength = Math.max(state.fState.length, maximumLength);
		if (oldLength != maximumLength)
			hasVariableLength = true;
		try {
			exploringState(state.stateNumber, transitions.length);
		} catch (IOException e) {
			throw new DerivationException(e);
		}
		t_count += transitions.length;
		Arrays.sort(transitions);
		for (int i = 0; i < transitions.length; i++) {
			Transition t = transitions[i];
			foundTransition(state.stateNumber, t.fState.stateNumber, t.fRate,
					t.fActionId);

		}

	}

	private void exploringState(int state, int numTransitions)
			throws IOException {
		t = numTransitions;
		currentColumn = -1;
		outputRow.writeInt(outputColumn.size() >> 2);
	}

	private void foundTransition(int i, int j, double rate, short action)
			throws DerivationException {
		if (currentColumn != j) {
			if (currentColumn != -1)
				writeAction();
			try {
				outputColumn.writeInt(j);
				outputColumn.writeInt(outputValue.size() >> 3);
				non_zero_count += 2;
			} catch (IOException e) {
				throw new DerivationException(e);
			}
			currentColumn = j;
			currentAction = action;
			sum = 0;
		}
		if (currentAction != action) {
			writeAction();
			currentAction = action;
			sum = rate;
		} else {
			sum += rate;
		}
		t = t - 1;
		if (t == 0)
			writeAction();

	}

	private void writeAction() throws DerivationException {
		try {
			outputAction.writeShort(currentAction);
			outputValue.writeDouble(sum);

		} catch (IOException e) {
			throw new DerivationException(e);
		}
	}

}
