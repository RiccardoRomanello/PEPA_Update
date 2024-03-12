/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

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
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.NewParallelBuilder.Result;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class ParallelBuilder implements IStateSpaceBuilder {

	private static final String ERROR_MESSAGE = "Thread terminated unexpectedly";

	private static final State EOF_STATE = new State(new short[0], 0);

	private static final Result EOF_RESULT = new Result(EOF_STATE, null);

	public static class Result implements Comparable<Result> {

		public State state;

		public Transition[] derivatives;

		public Result(State state, Transition[] derivatives) {
			this.state = state;
			this.derivatives = derivatives;
		}

		public int compareTo(Result o) {
			return state.stateNumber - o.state.stateNumber;
		}

	}

	private static class ExplorerThread extends Thread {

		private BlockingQueue<uk.ac.ed.inf.pepa.ctmc.derivation.common.State> unexploredQueue;

		private BlockingQueue<Result> derivativesQueue;

		private IStateExplorer explorer;

		public ExplorerThread(
				int vectorSize,
				IStateExplorer explorer,
				BlockingQueue<uk.ac.ed.inf.pepa.ctmc.derivation.common.State> unexploredQueue,
				BlockingQueue<Result> derivativesQueue) {
			super("Explorer");
			this.unexploredQueue = unexploredQueue;
			this.derivativesQueue = derivativesQueue;
			this.explorer = explorer;
		}

		public void run() {
			while (true) {
				try {
					uk.ac.ed.inf.pepa.ctmc.derivation.common.State next = unexploredQueue
							.take();
					if (next == EOF_STATE) {
						return;
					}
					Result result = new Result(next, explorer
							.exploreState(next.fState));
					derivativesQueue.add(result);
				} catch (DerivationException e) {

				} catch (InterruptedException e) {

				}
			}
		}
	}

	private static class WrapperWriterThread extends Thread {

		private final ICallbackListener listener;

		private final BlockingQueue<Result> writesQueue;

		final ArrayList<Result> buffer = new ArrayList<Result>();

		private final ArrayList<uk.ac.ed.inf.pepa.ctmc.derivation.common.State> states;

		public WrapperWriterThread(ICallbackListener listener,
				BlockingQueue<Result> writesQueue) {
			super("Writer");
			this.listener = listener;
			this.writesQueue = writesQueue;
			states = new ArrayList<uk.ac.ed.inf.pepa.ctmc.derivation.common.State>(
					1000);
		}

		public IStateSpace done(ISymbolGenerator generator)
				throws DerivationException {
			return listener.done(generator, states);
		}

		public void run() {
			// The first state has been initialised
			int currentState = 0;
			while (true) {
				Result nextResult = null;
				try {
					nextResult = writesQueue.take();
				} catch (InterruptedException e) {
				}
				if (nextResult == EOF_RESULT) {
					return;
				}

				// maxSize = Math.max(maxSize, buffer.size());

				try {

					if (nextResult.state.stateNumber == currentState) {
						addState(nextResult);
						currentState++;
						for (Iterator<Result> i = buffer.iterator(); i
								.hasNext();) {
							Result result = i.next();
							if (result.state.stateNumber == currentState) {
								addState(result);
								currentState++;
								i.remove();
							} else {
								break;
							}
						}
					} else {
						buffer.add(nextResult);
					}
				} catch (DerivationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		private final void addState(Result nextResult)
				throws DerivationException {
			states.add(nextResult.state);
			listener.foundDerivatives(nextResult.state, nextResult.derivatives);

		}

	}

	/**
	 * A copy of the original model
	 */
	private final ISymbolGenerator generator;

	private IStateExplorer[] explorers;

	private IResourceManager manager;

	private static final int REFRESH_MONITOR = 50000;

	private int id;

	public ParallelBuilder(IStateExplorer[] explorers,
			ISymbolGenerator generator, int productId, IResourceManager manager) {
		this.explorers = explorers;
		this.generator = generator;
		this.id = productId;
		this.manager = manager;
	}

	public IStateSpace derive(boolean allowPassiveRates,
			IProgressMonitor monitor) throws DerivationException {

		if (monitor == null)
			monitor = new DoNothingMonitor();

		monitor.beginTask(IProgressMonitor.UNKNOWN);

		final OptimisedHashMap map = new OptimisedHashMap();
		final BlockingQueue<State> unexploredQueue = new LinkedBlockingQueue<State>();
		final BlockingQueue<Result> derivativesQueue = new LinkedBlockingQueue<Result>();
		final BlockingQueue<Result> writesQueue = new LinkedBlockingQueue<Result>();
		ICallbackListener callBack = null;
		if (this.id == OptionMap.DERIVATION_MEMORY_STORAGE) {
			callBack = new MemoryCallback();
		} else if (this.id == OptionMap.DERIVATION_DISK_STORAGE) {
			callBack = new DiskCallback(manager);
		}
		final WrapperWriterThread writerThread = new WrapperWriterThread(
				callBack, writesQueue);

		// MEASUREMENT SECTION
		// long dqStart = 0, dqStop = 0, uqStart = 0, uqStop = 0, wqStart = 0,
		// wqStop = 0;
		// long mngStart = System.nanoTime();

		Thread timer = new Thread() {

			public void run() {
				PrintWriter os = null;
				final String comma = ", ";
				try {
					os = new PrintWriter(new BufferedWriter(new FileWriter(
							"c:/tmp/hbf/measure.csv")));
					os.println("#Obs, " + "Unexplored, " + "Derivatives, "
							+ "State Space, " + "Writes, " + "Buf Size");
					long start = System.nanoTime();
					do {
						Thread.sleep(25);
						os.println((System.nanoTime() - start) / 1e06 + comma
								+ unexploredQueue.size() + comma
								+ derivativesQueue.size() + comma + map.size()
								+ comma + writesQueue.size() + comma
								+ writerThread.buffer.size());
					} while (!interrupted());

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (os != null)
						os.close();
				}

			}
		};
		timer.start();

		// prepare initial state;
		short[] initialState = generator.getInitialState();
		int hashCode = Arrays.hashCode(initialState);
		State initState = map.putIfNotPresentSync(initialState, hashCode).state;
		unexploredQueue.add(initState);

		// determines the end of the derivation cicle
		int waitingStates = 1;

		writerThread.start();

		// WriterElement writer = new WriterElement(callBack);

		ExplorerThread[] explorerThreads = new ExplorerThread[explorers.length];

		for (int i = 0; i < explorers.length; i++) {
			explorerThreads[i] = new ExplorerThread(initialState.length,
					explorers[i], unexploredQueue, derivativesQueue);
			explorerThreads[i].start();
		}

		while (waitingStates != 0) {

			// extract from transitions
			Result result;
			try {
				result = derivativesQueue.take();

			} catch (InterruptedException e) {
				throw new DerivationException(ERROR_MESSAGE, e);
			}

			waitingStates--;
			Transition[] transitions = result.derivatives;
			for (int i = 0; i < transitions.length; i++) {

				Transition aT = transitions[i];

				if (aT.fRate <= 0) {
					throw createException(result.state,
							"Incomplete model with respect to action: "
									+ generator.getActionLabel(aT.fActionId)
									+ ". ");
				}

				hashCode = Arrays.hashCode(aT.fTargetProcess);
				// IMPORTANT hashCode is calculated externally and then
				// passed in to avoid calculating twice when a new state is
				// added
				InsertionResult insertion = map.putIfNotPresentSync(
						aT.fTargetProcess, hashCode);

				if (!insertion.wasPresent) {
					try {
						// uqStart = System.nanoTime();
						unexploredQueue.put(insertion.state);
						// uqStop += System.nanoTime() - uqStart;
					} catch (InterruptedException e) {
						throw new DerivationException(ERROR_MESSAGE, e);
					}
					waitingStates++;
				}
				aT.fState = insertion.state;

			}

			// writer.writeResult(result);
			try {
				writesQueue.put(result);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// long elapsed = (System.nanoTime() - mngStart);
		// System.err.println("[" + Thread.currentThread().getId() + "] Mng
		// time:"
		// + elapsed);
		// System.err.println("[" + Thread.currentThread().getId()
		// + "] Derivative queue: " + dqStop / (double) elapsed);
		// System.err.println("[" + Thread.currentThread().getId()
		// + "] Unexplored queue: " + uqStop / (double) elapsed);
		// System.err.println("[" + Thread.currentThread().getId()
		// + "] Write queue: " + wqStop / (double) elapsed);
		try {
			writesQueue.put(EOF_RESULT);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		try {
			for (int i = 0; i < explorerThreads.length; i++)
				unexploredQueue.put(EOF_STATE);

			// joins threads
			writerThread.join();

			for (Thread explorer : explorerThreads)
				explorer.join();
		} catch (InterruptedException e) {
			throw new DerivationException(ERROR_MESSAGE, e);

		}
		timer.interrupt();
		try {
			timer.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		monitor.done();

		// Generator ends here.
		return writerThread.done(this.generator);
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
