/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.LocalState;
import uk.ac.ed.inf.pepa.ctmc.PopulationLevelResult;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.Generator;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Process;

public abstract class AbstractStateSpace implements IStateSpace {
	
	
	protected static final ThroughputResult[] EMPTY_THROUGHPUT = new ThroughputResult[0];

	protected static final SequentialComponent[] EMPTY_UTILISATION = new SequentialComponent[0];

	protected static final PopulationLevelResult[] EMPTY_POPULATION = new PopulationLevelResult[0];

	protected double[] solution = null;

	protected ThroughputResult[] throughput = EMPTY_THROUGHPUT;

	protected SequentialComponent[] utilisation = EMPTY_UTILISATION;

	protected PopulationLevelResult[] populations = EMPTY_POPULATION;

	protected ISymbolGenerator symbolGenerator;

	private String[] orderedComponentNames;
	
	protected ArrayList<State> states;
	
	private boolean hasVariableLengthStates;
	
	private int maximumLength;
	
	protected AbstractStateSpace(ISymbolGenerator symbolGenerator, ArrayList<State> states,
			boolean hasVariableLengthStates, int maximumLength) {
		this.symbolGenerator = symbolGenerator;
		this.states = states;
		Collection<String> names = symbolGenerator
				.getSequentialComponentNames();
		this.orderedComponentNames = names.toArray(new String[names.size()]);
		this.hasVariableLengthStates = hasVariableLengthStates;
		this.maximumLength = maximumLength;

	}
	
	public int getNumberOfSequentialComponents(int stateIndex) {
		if (!hasVariableLengthStates)
			return maximumLength;
		else 
			return states.get(stateIndex).fState.length;
	}
	
	public int getMaximumNumberOfSequentialComponents() {
		return maximumLength;
	}

	protected abstract FlexCompRowMatrix createGeneratorMatrix();
	
	protected abstract Generator createSimpleGenerator();

	private void internalSetSolution(double[] pdf) {
		this.solution = pdf;
		doUtilisationAndPopulationLevels(null);
		doThroughput(null);
	}
	
	protected abstract void doThroughput(IProgressMonitor monitor);

	private final void doUtilisationAndPopulationLevels(IProgressMonitor monitor) {
		if (monitor == null)
			monitor = new DoNothingMonitor();

		monitor.beginTask(2*size());

		populations = EMPTY_POPULATION;
		utilisation = EMPTY_UTILISATION;
		
		if (solution == null) {
			monitor.done();
			return;
		}
		
		
		boolean doPopulation = true;
		boolean doUtilisation = !hasVariableLengthStates;
		
		if (doPopulation)
			doPopulation(monitor);
		if (doUtilisation)
			doUtilisation(monitor);
		monitor.done();
	}
	
	private void doUtilisation(IProgressMonitor monitor) {
		// Data structures for utilisation
		HashMap[] utilisationMaps = new HashMap[getMaximumNumberOfSequentialComponents()];
		for (int i = 0; i < utilisationMaps.length; i++)
			utilisationMaps[i] = new HashMap<Short, Double>();
		int stateNumber = 0;
		for (State state : states) {
			// monitor stuff
			if (monitor.isCanceled()) {
				monitor.done();
				return;
			}
			if ((stateNumber + 1) % 10000 == 0) {
				System.out.println("States: " + stateNumber);
				monitor.worked(10000);
			}

			for (int seqComp = 0; seqComp < utilisationMaps.length; seqComp++) {
				
				// update population levels
				// ************************
				double steadyStateSolution = solution[state.stateNumber];
				short processId = state.fState[seqComp];
				// update utilisation
				// *****************
				Double current = (Double) utilisationMaps[seqComp].get(processId);
				if (current == null)
					current = 0d;
				current += steadyStateSolution;
				utilisationMaps[seqComp].put(processId, current);

			}
		}

		// preparation of data model for results
		// Utilisation
		this.utilisation = new SequentialComponent[utilisationMaps.length];
		for (int i = 0; i < utilisationMaps.length; i++) {
			LocalState[] states = new LocalState[utilisationMaps[i].size()];
			int j = 0;
			Map<Short, Double> currentMap = utilisationMaps[i];
			for (Map.Entry<Short, Double> entry : currentMap.entrySet()) {
				states[j++] = new LocalState(symbolGenerator
						.getProcessLabel(entry.getKey()), entry.getValue());
			}
			utilisation[i] = new SequentialComponent(getLabel(0, i), states);
		}
		
	}
	
	private void doPopulation(IProgressMonitor monitor) {
		// Data structures for population levels
		HashMap<Short, Double> populationLevels = new HashMap<Short, Double>(
				this.getComponentNames().length);
		int stateNumber = 0;
		for (State state : states) {
			if (monitor.isCanceled()) {
				monitor.done();
				return;
			}
			if ((stateNumber + 1) % 10000 == 0) {
				System.out.println("States: " + stateNumber);
				monitor.worked(10000);
			}
			double steadyStateSolution = solution[state.stateNumber];
			for (short processId : state.fState)
				add(processId, steadyStateSolution, populationLevels);
		}
		this.populations = new PopulationLevelResult[populationLevels.size()];
		int i = 0;
		for (Map.Entry<Short, Double> entry : populationLevels.entrySet()) {
			this.populations[i++] = new PopulationLevelResult(symbolGenerator
					.getProcessLabel(entry.getKey()), entry.getValue());
		}
		
	}
	
	private void add(short seqComponentId, double value,
			HashMap<Short, Double> map) {
		Double c = map.get(seqComponentId);
		if (c == null)
			c = 0d;
		c += value;
		map.put(seqComponentId, c);
	}


	public abstract NamedAction[] getAction(int source, int target);

	public String[] getComponentNames() {

		return orderedComponentNames;
	}

	public abstract int[] getIncomingStateIndices(int stateIndex);
	
	public final String getLabel(int stateIndex, int position) {
		return symbolGenerator
				.getProcessLabel(states.get(stateIndex).fState[position]);
	}

	public final int getNumberOfCopies(int stateIndex, short processId) {
		return this.symbolGenerator.getNumOfCopies(processId, states
				.get(stateIndex).fState);
	}

	public abstract int[] getOutgoingStateIndices(int stateIndex);

	public PopulationLevelResult[] getPopulationLevels() {
		return this.populations;
	}

	public final short getProcessId(int stateIndex, int position) {
		return states.get(stateIndex).fState[position];
	}

	public final short getProcessId(String process) {
		return this.symbolGenerator.getProcessId(process);
	}

	public abstract double getRate(int source, int target);

	public double getSolution(int index) {
		if (this.solution == null)
			return Double.NaN;
		else
			return solution[index];
	}

	public ThroughputResult[] getThroughput() {
		return this.throughput;
	}

	public SequentialComponent[] getUtilisation() {
		return this.utilisation;
	}

	public final boolean isSolutionAvailable() {
		return solution != null;
	}

	public final boolean isUnnamed(int stateIndex, int position) {
		Process process = this.symbolGenerator.getSequentialComponentMap().get(
				this.states.get(stateIndex).fState[position]);
		if (process == null) {
			return false; // it is an aggregation
		} else {
			return !(process instanceof Constant);
		}
	}
	
	public void setSolution(double[] solution) {
		if (solution != null)
			if (size() != solution.length)
				throw new IllegalArgumentException();
		internalSetSolution(solution);
	}

	public int size() {
		return states.size();
	}
	
	public Object getGeneratorMatrix(Class clazz) {
		if (clazz == null)
			return null;
		if (clazz == FlexCompRowMatrix.class)
			return createGeneratorMatrix();
		if (clazz == uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.Generator.class) {
			return createSimpleGenerator();
		}
		return null;
	}


}
