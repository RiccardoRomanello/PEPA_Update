/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 15-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.recursive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.PopulationLevelResult;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.MeasurementData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Process;

/**
 * Implementation of the state space derivation using the Visitor pattern.
 * 
 * @author mtribast
 * 
 */
public class RecursiveBuilder implements IStateSpaceBuilder {

	private static Logger logger = Logger.getLogger(RecursiveBuilder.class);

	private Stack<Process> stack = new Stack<Process>();

	private HashSet<Process> stateSpace = new HashSet<Process>();
	
	private Model derivable = null;

	private IProgressMonitor monitor = new DoNothingMonitor();
	
	private MeasurementData measurement = new MeasurementData();
	
	public RecursiveBuilder(Model derivablePepaModel) {
		long ticSetup = System.nanoTime();
		derivable = derivablePepaModel;

		/*logger.debug("System equation: "
				+ derivable.getSystemEquation().prettyPrint());*/

		Expander expander = new Expander();
		derivable.getSystemEquation().accept(expander);

		// stack.push(derivable.getSystemEquation());
		stack.push(expander.expanded);
		//logger.debug("Expanded : " + expander.expanded.prettyPrint());
		stateSpace.add(expander.expanded);
		measurement.setupTime = System.nanoTime() - ticSetup;

	}

	/**
	 * @return
	 * @throws DerivationException
	 */
	public IStateSpace derive(boolean allowPassiveTransitions, IProgressMonitor progressMonitor)
			throws DerivationException {

		long ticWc = System.nanoTime();
		
		ActivityMultisetVisitor.init();
		
		TransitionList transitions;
		
		long ticSuccessor;
		long ticLookup = 0;
		int totalTransitions = 0;
		int numStates = 0;
		while (!stack.isEmpty()) {

			Process process = stack.pop();
			
			ticSuccessor = System.nanoTime();
			/* Compute outgoing transitions for this state */
			ActivityMultisetVisitor visitor = new ActivityMultisetVisitor();
			process.accept(visitor);
			measurement.successorTime += System.nanoTime() - ticSuccessor;

			transitions = visitor.getTransitions();
			totalTransitions += transitions.size();
			numStates++;
			
			ticLookup = System.nanoTime();
			Iterator<TransitionEntry> iter = transitions.iterator();
			Process target;
			while (iter.hasNext()) {
				target = iter.next().target;
				if (!stateSpace.contains(target)) {
					stack.push(target);
					stateSpace.add(target);
				}
			}
			measurement.lookupTime += System.nanoTime() - ticLookup;
					
		}

		measurement.wallClockDerivationTime = System.nanoTime() - ticWc;
		
		measurement.states = numStates;
		measurement.transitions= totalTransitions;
		//System.err.println("H: " + ActivityMultisetVisitor.hits);
		//System.err.println("M: " + ActivityMultisetVisitor.misses);
				
		return null;

	}

	private IStateSpace createStateSpace(
			final Map<Process, TransitionList> stateSpace) {

		return new IStateSpace() {

			private ArrayList<Process> processes = new ArrayList<Process>(
					stateSpace.keySet());

			private Map<Process, TransitionList> map = stateSpace;

			public int size() {
				return stateSpace.size();
			}

			public Process get(int index) {
				return processes.get(index);
			}

			public int indexOf(Process process) {
				/*
				 * TODO foundAsConstant piece of code has been commented out
				 * because the system equation gets expanded before processing.
				 */
				int index = processes.indexOf(process);

				return index;

				// if (index != -1) {
				// return index;
				// } else {
				// return foundAsConstant(process);
				// }

			}

			// private int foundAsConstant(Process process) {
			//
			// int index = 0;
			// for (Process state : processes) {
			// if (state instanceof Constant
			// && process.equals(((Constant) state)
			// .getResolvedProcess()))
			// return index;
			// index++;
			// }
			// return -1;
			// }

			public Iterator<TransitionEntry> getTransitionListIterator(
					Process process) {
				return map.get(process).iterator();
			}

			public Iterator<Process> getProcessIterator() {
				return map.keySet().iterator();
			}

			public State getInitialState() {
				// TODO Auto-generated method stub
				return null;
			}

			public int getNumberOfSequentialComponents() {
				// TODO Auto-generated method stub
				return 0;
			}

			public String getLabel(State state, int position) {
				// TODO Auto-generated method stub
				return null;
			}

			public Iterator<State> getStateIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isUnnamed(State state, int position) {
				// TODO Auto-generated method stub
				return false;
			}

			public State[] getTransitions(State state) {
				// TODO Auto-generated method stub
				return null;
			}

			public void computeSteadyStateDistribution(OptionMap options,
					IProgressMonitor monitor) throws SolverException {
				// TODO Auto-generated method stub
				
			}

			public double[] getSolution() {
				// TODO Auto-generated method stub
				return null;
			}

			public double getSolution(State state) {
				// TODO Auto-generated method stub
				return 0;
			}

			public ThroughputResult[] getThroughput() {
				// TODO Auto-generated method stub
				return null;
			}

			public SequentialComponent[] getUtilisation() {
				// TODO Auto-generated method stub
				return null;
			}

			public State getState(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			public int[] getIncomingStateIndices(State state) {
				// TODO Auto-generated method stub
				return null;
			}

			public int[] getOutgoingStateIndices(State state) {
				// TODO Auto-generated method stub
				return null;
			}

			public NamedAction[] getAction(State source, State target) {
				// TODO Auto-generated method stub
				return null;
			}

			public short getProcessId(String process) {
				// TODO Auto-generated method stub
				return 0;
			}

			public int getNumberOfCopies(State state, short processId) {
				// TODO Auto-generated method stub
				return 0;
			}

			public String[] getComponentNames() {
				// TODO Auto-generated method stub
				return null;
			}

			public PopulationLevelResult[] getPopulationLevels() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isSolutionAvailable() {
				// TODO Auto-generated method stub
				return false;
			}

			public double getRate(State source, State target) {
				// TODO Auto-generated method stub
				return 0;
			}

			public void setSolution(double[] solution) {
				// TODO Auto-generated method stub
				
			}

			public NamedAction[] getAction(int source, int target) {
				// TODO Auto-generated method stub
				return null;
			}

			public int[] getIncomingStateIndices(int stateIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getLabel(int stateIndex, int position) {
				// TODO Auto-generated method stub
				return null;
			}

			public int getNumberOfCopies(int stateIndex, short processId) {
				// TODO Auto-generated method stub
				return 0;
			}

			public int[] getOutgoingStateIndices(int stateIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public short getProcessId(int stateIndex, int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			public double getRate(int source, int target) {
				// TODO Auto-generated method stub
				return 0;
			}

			public double getSolution(int index) {
				// TODO Auto-generated method stub
				return 0;
			}

			public boolean isUnnamed(int stateIndex, int position) {
				// TODO Auto-generated method stub
				return false;
			}

			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			public Object getGeneratorMatrix(Class clazz) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public int getMaximumNumberOfSequentialComponents() {
				// TODO Auto-generated method stub
				return 0;
			}

			public int getNumberOfSequentialComponents(int stateIndex) {
				// TODO Auto-generated method stub
				return 0;
			}

		};
	}

	/* Debug Function */
	private void printStateSpace(Map<Process, TransitionList> stateSpace) {
		logger.debug("State Space:");
		ArrayList<Process> list = new ArrayList<Process>(stateSpace.keySet());

		for (Process state : list) {
			TransitionList transitions = stateSpace.get(state);
			StringBuffer dest = new StringBuffer();
			dest.append((list.indexOf(state) + 1) + " " + state.prettyPrint()
					+ " -> { ");
			Iterator<TransitionEntry> trans = transitions.iterator();
			while (trans.hasNext()) {
				Process destinationProcess = trans.next().target;
				dest.append("" + (list.indexOf(destinationProcess) + 1) + " ");

			}
			dest.append("}");
			logger.debug(dest.toString());
		}
	}

	/* Debug function */
	private static void log(Process p, TransitionList transitions) {
		if (logger.isDebugEnabled()) {

			Iterator<TransitionEntry> iter = transitions.iterator();
			while (iter.hasNext() == true) {
				TransitionEntry entry = iter.next();
				if (entry.activity == null) {
					logger.debug("Null activity?!?!?");
				} else {
					logger.debug(p.prettyPrint() + " -> "
							+ entry.activity.prettyPrint() + " -> "
							+ entry.target.prettyPrint());
				}

			}

		}

	}

	public MeasurementData getMeasurementData() {
		return measurement;
	}

}