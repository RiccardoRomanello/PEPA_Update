/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.largescale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.largescale.internal.GeneratingFunction;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricComponent;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricStateExplorer;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricStateExplorerBuilder;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricTransition;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricTransitionTriple;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class ParametricDerivationGraphBuilder {
	
	private double[] initialState;

	private ParametricStateExplorer explorer;

	/*
	 * Collects all of the transitions recorded during the exploration of the state space
	 * Some of them contain duplicate information - for instance the rate of an independent
	 * action is recorded for all possible states of the non-cooperating components.
	 * The generating functions will filter out those transitions 
	 */
	private ArrayList<ParametricTransitionTriple> parametricDerivationGraph = new ArrayList<ParametricTransitionTriple>(20);
	
	/*
	 * This is probably not the fastest representation, but in general we don't expect
	 * too many generating functions for a PEPA model
	 */
	private ArrayList<GeneratingFunction> generatingFunctions = new ArrayList<GeneratingFunction>();

	private ISymbolGenerator generator;
	
	private static final int REFRESH_MONITOR = 20;
	
	public static IParametricDerivationGraph createDerivationGraph(
			ModelNode model, IProgressMonitor monitor)
			throws DifferentialAnalysisException, InterruptedException {
		final ParametricDerivationGraphBuilder builder = new ParametricDerivationGraphBuilder(model);
		builder.derive(monitor);
		final ParametricTransitionTriple[] triples = new ParametricTransitionTriple[builder.getDerivationGraph().size()];
		builder.getDerivationGraph().toArray(triples);
		final GeneratingFunction[] functions = new GeneratingFunction[builder.getGeneratingFunctions().size()];
		builder.getGeneratingFunctions().toArray(functions);
		final short[] processMappings = builder.explorer.getProcessMappings(); 
		return new IParametricDerivationGraph() {

			public IGeneratingFunction[] getGeneratingFunctions() {
				return functions;
			}

			public ISymbolGenerator getSymbolGenerator() {
				return builder.getSymbolGenerator();
			}

			public IParametricTransitionTriple[] getTransitionTriples() {
				return triples;
			}
			
			public double[] getInitialState() {
				return builder.getInitialState();
			}

			public short[] getProcessMappings() {
				return processMappings;
			}

			public ISequentialComponent[] getSequentialComponents() {
				return builder.explorer.getSequentialComponents();
			}
			
		};
	}
	
	private ParametricDerivationGraphBuilder(ModelNode model) throws DifferentialAnalysisException {
		Model cModel = new Compiler(true, model).getModel();
		ParametricStateExplorerBuilder seb = new ParametricStateExplorerBuilder(
				cModel);
		this.explorer = seb.getExplorer();
		this.generator = seb.getSymbolGenerator();
		this.initialState = new double[explorer.getProblemSize()];
		Arrays.fill(initialState, 0);
		short[] firstComponent = generator.getInitialState();
		for (int i = 0; i < firstComponent.length; i++) {
			this.initialState[explorer.getSequentialComponents()[i].getCoordinate(firstComponent[i])] = 
				explorer.getSequentialComponents()[i].getInitialPopulationLevel();
		}
		
	}
	
	public ISymbolGenerator getSymbolGenerator() {
		return generator;
	}
	
	public double[] getInitialState() {
		return initialState;
	}
	
	public ArrayList<ParametricTransitionTriple> getDerivationGraph() {
		return parametricDerivationGraph;
	}
	
	public ArrayList<GeneratingFunction> getGeneratingFunctions() {
		return generatingFunctions;
	}
	
	public void derive(
			IProgressMonitor monitor)
			throws DifferentialAnalysisException, InterruptedException {
		
		if (monitor == null)
			monitor = new DoNothingMonitor();

		monitor.beginTask(IProgressMonitor.UNKNOWN);
		
		
		HashSet<State> exploredStates = new HashSet<State>();
		
		// stack of unexplored states
		Queue<State> queue = new LinkedList<State>();

		// prepare initial state;
		short[] initialState = generator.getInitialState();
		
		int hashCode = Arrays.hashCode(initialState);
		State initState = new State(initialState, hashCode);
		exploredStates.add(initState);
		queue.add(initState);

		int transitions = 0;
		ParametricTransition[] found = null;
		int stateNumber = 0;
		while (!queue.isEmpty()) {
			
			if (monitor.isCanceled()) {
				monitor.done();
				throw new InterruptedException("ODE generation was interrupted");
			}

			State s = queue.remove();
			if (stateNumber++ % REFRESH_MONITOR == 0) {
				monitor.worked(REFRESH_MONITOR);
			}
	
			try {
				found = explorer.exploreState(s.fState);

			} catch (DifferentialAnalysisException e) {
				throw createException(s, e.getMessage());
			}
			if (found.length == 0) {
				monitor.done();
				throw createException(s, "Deadlock found.");
			}

			transitions += found.length;
			for (int i = 0; i < found.length; i++) {

				ParametricTransition aT = found[i];

				hashCode = Arrays.hashCode(aT.getTarget());
				State target = new State(aT.getTarget(), hashCode);
				if (!exploredStates.contains(target)) {
					exploredStates.add(target);
					queue.add(target);
				}
				ParametricTransitionTriple triple = new ParametricTransitionTriple();
				triple.setSource(s.fState);
				triple.setTarget(aT.getTarget());
				triple.setActionId(aT.getActionId());
				triple.setRate(aT.getParametricRate());
				parametricDerivationGraph.add(triple);
			}
		}
		
		explorer.dispose();

		parametricDerivationGraph.trimToSize();
		
		buildGeneratingFunctions();
		
		monitor.done();
	}
	
	/*
	 * Generating functions are built after the derivation graph to
	 * be able to keep derivation graphs and generating functions 
	 * as two separate representations for debugging and educational
	 * purposes.
	 */
	private void buildGeneratingFunctions() {
		for (IParametricTransitionTriple transition : parametricDerivationGraph) {
			short[] jump = buildJump(transition.getSource(), transition.getTarget());
			short actionId = transition.getActionId();
			boolean foundSameJump = false;
			for (IGeneratingFunction f : generatingFunctions) {
				if (Arrays.equals(jump, f.getJump())) {
					if (actionId == f.getActionId()) {
						if (!Arrays.equals(transition.getSource(), f.getRepresentativeSource()) &&
								!Arrays.equals(transition.getTarget(), f.getRepresentativeTarget())) {
							foundSameJump = true;
							break;
						}
					}
				}
			}
			if (foundSameJump)
				continue;
			GeneratingFunction f = new GeneratingFunction();
			f.setActionId(actionId);
			f.setJump(jump);
			f.setRepresentativeSource(transition.getSource());
			f.setRepresentativeTarget(transition.getTarget());
			f.setRate(transition.getRate());
			generatingFunctions.add(f);
		}
	}
	

	
	/**
	 * Source and target just give the local state for each sequential component
	 * @param source
	 * @param target
	 * @return
	 */
	private short[] buildJump(short[] source, short[] target) {
		short[] jump = new short[explorer.getProblemSize()];
		Arrays.fill(jump, (short) 0);
		for (int i = 0; i < source.length; i++) {
			ParametricComponent c = explorer.getSequentialComponents()[i];
			jump[c.getCoordinate(source[i])] -= 1;
			jump[c.getCoordinate(target[i])] += 1;
		}
		return jump;
	}
	
	private DifferentialAnalysisException createException(State state, String message) {
		StringBuffer buf = new StringBuffer();
		buf.append(message + " State number: ");
		buf.append(state.stateNumber + ". ");
		buf.append("State: ");
		for (int i = 0; i < state.fState.length; i++) {
			buf.append(generator.getProcessLabel(state.fState[i]));
			if (i != state.fState.length - 1)
				buf.append(",");
		}
		return new DifferentialAnalysisException(buf.toString());
	}

}
