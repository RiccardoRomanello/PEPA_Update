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
import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Visitor;
import uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess;

/**
 * Implementation of the state space derivation using the Visitor pattern.
 * 
 * @author mtribast
 * 
 */
public class CopyOfRecursiveBuilder implements IStateSpaceBuilder {

	private static Logger logger = Logger.getLogger(CopyOfRecursiveBuilder.class);

	private Stack<Process> stack = new Stack<Process>();

	private Model derivable = null;

	private IProgressMonitor monitor = new DoNothingMonitor();

	//private IDynamicAnalyser analyser = IDynamicAnalyser.LAZY_ANALYSER;
	
	public CopyOfRecursiveBuilder(Model derivablePepaModel) {

		derivable = derivablePepaModel;

		/*logger.debug("System equation: "
				+ derivable.getSystemEquation().prettyPrint());*/

		Expander expander = new Expander();
		derivable.getSystemEquation().accept(expander);

		// stack.push(derivable.getSystemEquation());
		stack.push(expander.expanded);
		//logger.debug("Expanded : " + expander.expanded.prettyPrint());

	}

	/**
	 * @return
	 * @throws DerivationException
	 */
	public IStateSpace derive(boolean allowPassiveTransitions, IProgressMonitor progressMonitor)
			throws DerivationException {

		if (progressMonitor != null) {
			this.monitor = progressMonitor;
		}
		TransitionList transitions;

		StateSpaceMap stateSpace = new StateSpaceMap();

		int log_state = 1;
		int log_maxSize = -1;

		monitor.beginTask(IProgressMonitor.UNKNOWN);
		
		long tic;
		long totalExploration = 0;
		int totalTransitions = 0;
		int numStates = 0;
		while (!stack.isEmpty()) {

			/* Exit condition for the monitor */
			if (monitor.isCanceled()) {
				return null;
			}

			monitor.worked(1);

			if (log_maxSize < stack.size())
				log_maxSize = stack.size();
			/*
			 * Compute next process.
			 */
			Process process = stack.pop();

			if (stateSpace.containsState(process)) {
				//logger.debug("Already traversed: " + process.prettyPrint());
				continue;
			}

			/*logger
					.debug("State " + (log_state++) + " "
							+ process.prettyPrint());*/
			
			
			tic = System.nanoTime();
			/* Compute outgoing transitions for this state */
			ActivityMultisetVisitor visitor = new ActivityMultisetVisitor();
			process.accept(visitor);
			if (!visitor.isSuccess()) {
				throw visitor.getCause();
			}
			totalExploration += System.nanoTime() - tic;

			transitions = visitor.getTransitions();
			totalTransitions += transitions.size();
			numStates++;
			
			/*
			 * Security test: a state must have at least an outgoing transition!
			 */
			if (transitions.size() == 0) {
				String message = "Absorption state found for state "
						+ process.prettyPrint();
				//logger.error(message);
				throw new DerivationException(message);
			}

			/*
			 * New! Remove transitions with passive rates, if any
			 */
			if (transitions.hasPassiveRates() &&
					!allowPassiveTransitions)
				throw new DerivationException("State " + process.prettyPrint()
						+ " has passive rates in its transition set.");

			//log(process, transitions);

			/* Add the new process to the state space */
			stateSpace.put(process, transitions);
			
			

			/*
			 * Push processes reached by this state. Check first it they are in
			 * the stack already
			 */
			Iterator<TransitionEntry> iter = transitions.iterator();
			Process target;
			while (iter.hasNext()) {
				target = iter.next().target;
				//logger.debug("Enqueuing " + target.prettyPrint());
				if (!stack.contains(target)) {
					stack.push(target);
					//logger.debug("...added");
				}/* else
					logger.debug("...already in the queue");*/

			}
		}

		//printStateSpace(stateSpace.getMap());

		//logger.debug("Max stack size: " + log_maxSize);

		//System.err.println("Total exploration (recursion): " + totalExploration / 1e9);
		//System.err.println("States:" + numStates);
		//System.err.println("Total transitions (recursion):" + totalTransitions);
		
		IStateSpace iStateSpace = createStateSpace(stateSpace.getMap());

		/* Callback for state space derived */
		//analyser.stateSpaceDerived(iStateSpace);
		
		monitor.done();
		
		return iStateSpace;

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

			public String[] getAction(State source, State target) {
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

			public String[] getAction(int source, int target) {
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
		// TODO Auto-generated method stub
		return null;
	}

}

/**
 * Expand the system equation, i.e. resolves sub-systems which are defined as
 * constants.
 * <p>
 * For example, if the model is defined as
 * 
 * <pre>
 *    P = (a,1).P1;
 *    P1 = (b,1).P;
 *    S = P &lt;&gt; P &lt;&gt; P;
 *    
 *    S
 * </pre>
 * 
 * Then the system equation is
 * 
 * <pre>
 *    P &lt;&gt; P &lt;&gt; P
 * </pre>
 * 
 * The system equation must show the maximum number of top-level components of
 * the model.
 * 
 * @author mtribast
 * 
 */
class Expander implements Visitor {

	private uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess factory = DoMakePepaProcess
			.getInstance();

	public Process expanded;

	public Expander() {

	}

	public void visitPrefix(Prefix prefix) {
		expanded = prefix;
	}

	public void visitChoice(Choice choice) {
		expanded = choice;
	}

	public void visitHiding(Hiding hiding) {
		expanded = hiding;
	}

	public void visitCooperation(Cooperation cooperation) {
		Expander lhs = new Expander();
		cooperation.getLeftHandSide().accept(lhs);
		Expander rhs = new Expander();
		cooperation.getRightHandSide().accept(rhs);

		Cooperation coop = factory.createCooperation(lhs.expanded,
				rhs.expanded, cooperation.getActionSet());

		expanded = coop;
	}

	public void visitConstant(Constant constant) {
		Expander expander = new Expander();
		if (constant.getBinding() instanceof Hiding
				|| constant.getBinding() instanceof Cooperation
				|| constant.getBinding() instanceof Constant) {
			constant.getBinding().accept(expander);
			expanded = expander.expanded;
		} else
			expanded = constant;

	}

	public void visitAggregation(Aggregation aggregation) {
		expanded = aggregation;
	}

}