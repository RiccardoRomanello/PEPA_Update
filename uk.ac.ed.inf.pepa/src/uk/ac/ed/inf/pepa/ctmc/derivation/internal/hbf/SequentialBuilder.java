/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.MeasurementData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.MemoryCallback;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.OptimisedHashMap;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.OptimisedHashMap.InsertionResult;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class SequentialBuilder implements IStateSpaceBuilder {

	/**
	 * A copy of the original model
	 */
	private final ISymbolGenerator generator;

	private IStateExplorer explorer;

	private IResourceManager manager;

	private static final int REFRESH_MONITOR = 20000;

	private int id;

	public SequentialBuilder(IStateExplorer explorer,
			ISymbolGenerator generator, int productId, IResourceManager manager) {
		this.explorer = explorer;
		this.generator = generator;
		this.id = productId;
		this.manager = manager;
	}

	public IStateSpace derive(boolean allowPassiveRates,
			IProgressMonitor monitor) throws DerivationException {

		if (monitor == null)
			monitor = new DoNothingMonitor();

		monitor.beginTask(IProgressMonitor.UNKNOWN);
		ICallbackListener ss = null;
		ArrayList<State> states = new ArrayList<State>(1000);
		switch (id) {
		case OptionMap.DERIVATION_MEMORY_STORAGE:
			// ss = new HBFStateSpace(generator, states);
			ss = new MemoryCallback();
			break;
		case OptionMap.DERIVATION_DISK_STORAGE:
			// ss = new DiskBasedStateSpace(generator, states, manager);
			ss = new DiskCallback(manager);
			break;
		default:
			throw new IllegalArgumentException();
		}

		OptimisedHashMap/* <State, Object> */map = new OptimisedHashMap();

		// stack of unexplored states
		Queue<State> queue = new LinkedList<State>();

		// prepare initial state;
		short[] initialState = generator.getInitialState();

		// TransitionStream stream = new TransitionStream(initialState.length,
		// 100);

		int hashCode = Arrays.hashCode(initialState);

		State initState = map.putIfNotPresentUnsync(initialState, hashCode).state;
		//initState.stateNumber = 0;
		//int rowNumber = 1;
		// ss.foundState(initState);
		queue.add(initState);

		int transitions = 0;
		Transition[] found = null;
		
		
		//long tic = System.nanoTime();
		//long ticMap = 0, tocAdded = 0;
		
		while (!queue.isEmpty()) {

			if (monitor.isCanceled()) {
				monitor.done();
				return null;
			}

			State s = queue.remove();
			// System.err.println("Visiting " + s.stateNumber);
			if (s.stateNumber % REFRESH_MONITOR == 0) {
				monitor.worked(REFRESH_MONITOR);

			}
			// stream.clear();

			try {
				found = explorer.exploreState(s.fState);

			} catch (DerivationException e) {
				throw createException(s, e.getMessage());
			}
			if (found.length == 0) {
				monitor.done();
				throw createException(s, "Deadlock found.");
			}

			// ss.exploringState(s.stateNumber, t.size());
			transitions += found.length;
			for (int i = 0; i < found.length; i++) {

				Transition aT = found[i];

				if (aT.fRate <= 0) {
					throw createException(s,
							"Incomplete model with respect to action: "
									+ generator.getActionLabel(aT.fActionId)
									+ ". ");
				}

				hashCode = Arrays.hashCode(aT.fTargetProcess);
				// IMPORTANT hashCode is calculated externally and then
				// passed in to avoid calculating twice when a new state is
				// added
				//ticMap = System.nanoTime();
				InsertionResult result = map.putIfNotPresentUnsync(aT.fTargetProcess,
						hashCode);
				//tocAdded += System.nanoTime() - ticMap;
				
				aT.fState = result.state;
				
				if (!result.wasPresent) {
					queue.add(result.state);
					//result.state.stateNumber = rowNumber++;
				}
	
			}

			// here we have: row number, column number and rate
			// for matrix generation
			ss.foundDerivatives(s, found);
			states.add(s);
			// ss.foundTransition(s.stateNumber, columnNumber, aT.fRate,
			// aT.fActionId);

		}
		
		//long elapsed = System.nanoTime() - tic;
		//double frac = tocAdded / (double) elapsed;
		//System.err.println("Fraction: " + frac);
		explorer.dispose();

		states.trimToSize();

		IStateSpace stateSpace = ss.done(this.generator, states);

		//System.err.println("Transitions per state: " + transitions
		//		/ stateSpace.size());
		monitor.done();

		// Generator ends here.
		return stateSpace;
	}

	private DerivationException createException(State state, String message) {
		StringBuffer buf = new StringBuffer();
		buf.append(message + " State number: ");
		buf.append(state.stateNumber + ". ");
		buf.append("State: ");
		for (int i = 0; i < state.fState.length; i++) {
			buf.append(generator.getProcessLabel(state.fState[i]));
			if (i != state.fState.length - 1)
				buf.append(",");
		}
		return new DerivationException(buf.toString());
	}

	public MeasurementData getMeasurementData() {
		return null;
	}

}
