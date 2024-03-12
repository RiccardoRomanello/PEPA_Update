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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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

public class NewParallelBuilder implements IStateSpaceBuilder {

	private static final boolean TIME = false;

	private static final String ERROR_MESSAGE = "Thread terminated unexpectedly";

	private static final State EOF_STATE = new State(new short[0], 0);

	private final BlockingQueue<State> unexploredQueue;

	private final BlockingQueue<Result> writesQueue;

	private final OptimisedHashMap map;

	private final AtomicInteger waitingStates = new AtomicInteger(1);

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

	private class ExplorerThread extends Thread {

		private IStateExplorer explorer;

		public ExplorerThread(int vectorSize, IStateExplorer explorer) {
			super("Explorer");
			this.explorer = explorer;
		}

		// long tic = System.nanoTime();
		// long ticMap = 0, tocAdded = 0, tocLookedUp = 0;

		public void run() {
			int hashCode;
			uk.ac.ed.inf.pepa.ctmc.derivation.common.State successor = null;

			Result result = null;
			while (true) {
				
				try {
					successor = unexploredQueue.take();
				} catch (InterruptedException e1) {
					break;
				}

				if (successor == EOF_STATE) {
					break;
				}

				try {
					result = new Result(successor, explorer
							.exploreState(successor.fState));
				} catch (DerivationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Transition[] transitions = result.derivatives;
				for (int i = 0; i < transitions.length; i++) {

					Transition aT = transitions[i];

					if (aT.fRate <= 0) {

						// TODO Throw Exception
						// throw createException(
						// result.state,
						// "Incomplete model with respect to action: "
						// + generator
						// .getActionLabel(aT.fActionId)
						// + ". ");
					}

					hashCode = Arrays.hashCode(aT.fTargetProcess);

					// ticMap = System.nanoTime();
					InsertionResult insertion = map.putIfNotPresentSync(
							aT.fTargetProcess, hashCode);
					aT.fState = insertion.state;
					if (!insertion.wasPresent) {
						// tocAdded += System.nanoTime() - ticMap;
						// the new state was found
						try {
							// state.stateNumber = rowNumber.getAndIncrement();
							waitingStates.incrementAndGet();

							unexploredQueue.put(insertion.state);

						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					} else {
						// tocLookedUp += System.nanoTime() - ticMap;
					}

				}
				try {
					writesQueue.put(result);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			// double elapsed = (System.nanoTime() - tic);
			// double fractionAdded = tocAdded / elapsed;
			// double fractionLooked = tocLookedUp / elapsed;
			// System.out.println("Elapsed: " + elapsed);
			// System.out.println("Percentage look-up: " + fractionLooked);
			// System.out.println("Percentage added: " + fractionAdded);

		}
	}

	private class WrapperWriterThread extends Thread {

		private final ICallbackListener listener;

		private final ArrayList<uk.ac.ed.inf.pepa.ctmc.derivation.common.State> states;

		private final TreeSet<Result> buffer = new TreeSet<Result>();

		public WrapperWriterThread(ICallbackListener listener) {
			super("Writer");
			this.listener = listener;
			states = new ArrayList<uk.ac.ed.inf.pepa.ctmc.derivation.common.State>(
					1000);
		}

		public IStateSpace done(ISymbolGenerator generator)
				throws DerivationException {
			// System.err.println("Max buf size: " + maxSize);
			return listener.done(generator, states);
		}

		// private int maxSize = 0;

		public void run() {
			// The first state has been initialised
			int currentState = 0;
			do {
				Result nextResult = null;
				try {
					nextResult = writesQueue.take();
				} catch (InterruptedException e) {
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
			} while (waitingStates.decrementAndGet() > 0);

			for (int i = 0; i < explorers.length; i++) {
				try {
					unexploredQueue.put(EOF_STATE);
				} catch (InterruptedException e) {
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

	private static final int REFRESH_MONITOR = 20000;

	private int id;

	public NewParallelBuilder(IStateExplorer[] explorers,
			ISymbolGenerator generator, int productId, IResourceManager manager) {
		this.unexploredQueue = new LinkedBlockingQueue<State>();
		// this.derivativesQueue = new LinkedBlockingQueue<Result>();
		this.writesQueue = new LinkedBlockingQueue<Result>();
		this.map = new OptimisedHashMap();
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

		// map of found states
		// prepare initial state;
		short[] initialState = generator.getInitialState();
		int hashCode = Arrays.hashCode(initialState);

		InsertionResult firstInsertion = map.putIfNotPresentSync(initialState,
				hashCode);
		unexploredQueue.add(firstInsertion.state);

		ICallbackListener callBack = null;
		if (this.id == OptionMap.DERIVATION_MEMORY_STORAGE) {
			callBack = new MemoryCallback();
		} else if (this.id == OptionMap.DERIVATION_DISK_STORAGE) {
			callBack = new DiskCallback(manager);
		}

		final WrapperWriterThread writerThread = new WrapperWriterThread(
				callBack);
		writerThread.start();
		ExplorerThread[] explorerThreads = new ExplorerThread[explorers.length];

		Thread timer = null;
		if (TIME) {
			timer = new Thread() {

				public void run() {
					PrintWriter os = null;
					final String comma = ", ";
					try {
						os = new PrintWriter(new BufferedWriter(new FileWriter(
								"c:/tmp/hbf/measure.csv")));
						os.println("#Obs, " + "Unexplored, " + "State Space, "
								+ "Writes");
						long start = System.nanoTime();
						do {
							Thread.sleep(25);
							os.println((System.nanoTime() - start) / 1e06
									+ comma + unexploredQueue.size() + comma
									+ map.size() + comma + writesQueue.size());
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
		}

		for (int i = 0; i < explorers.length; i++) {
			explorerThreads[i] = new ExplorerThread(initialState.length,
					explorers[i]);
			explorerThreads[i].start();
		}

		try {
			// joins threads
			// WRITER
			writerThread.join();
			for (Thread explorer : explorerThreads)
				explorer.join();
		} catch (InterruptedException e) {
			throw new DerivationException(ERROR_MESSAGE, e);

		}

		if (TIME) {
			timer.interrupt();
			try {
				timer.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		monitor.done();
		// System.err.println("Quit");

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
