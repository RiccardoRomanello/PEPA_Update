/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.OptimisedHashMap;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.OptimisedHashMap.InsertionResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace;
import uk.ac.ed.inf.pepa.ctmc.kronecker.IKroneckerStateSpace;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.AbstractCTMC;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.CSLModelChecker;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.CompositionalProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.ModelCheckingLog;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.PropertyBank;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.Generator;
import uk.ac.ed.inf.pepa.model.NamedAction;

/**
 * This implements the Kronecker state space for PEPA models. The
 * field kronecker_representation is indexed by action type, and then
 * sequential component
 * 
 * @author msmith
 *
 */
public class KroneckerStateSpace extends AbstractStateSpace implements IKroneckerStateSpace {

	private KroneckerModel kroneckerModel; 
	
	private OptimisedHashMap stateSpace;
	
	private DerivationException generationError = null;
	
	private boolean generatedStateSpace = false;
	
	private short[] initialState;
	
	protected KroneckerStateSpace(ISymbolGenerator generator, KroneckerModel kroneckerModel) {
		super(generator, null, false, generator.getInitialState().length);
		assert kroneckerModel != null;
		this.kroneckerModel = kroneckerModel;
		this.initialState = generator.getInitialState();
	}
	
	/**
	 * Provide an explicit initial state - used when we abstract the state space (stochastic bounds)
	 */
	private KroneckerStateSpace(short[] initialState, ISymbolGenerator generator, KroneckerModel kroneckerModel) {
		super(generator, null, false, generator.getInitialState().length);
		assert kroneckerModel != null;
		this.kroneckerModel = kroneckerModel;
		this.initialState = initialState;
	}
	
	private short[] getAbstractState(short[] state) {
		assert state.length == kroneckerModel.numComponents();
		short[] abstractState = new short[state.length];
		for (int i = 0; i < state.length; i++) {
			SequentialAbstraction abstraction = kroneckerModel.getAbstraction(i);
			abstractState[i] = abstraction.getAbstractState(state[i]);
		}
		return abstractState;
	}
	
	public KroneckerDisplayModel getDisplayModel() {
		return kroneckerModel.getDisplayModel();
	}
	
	public CSLModelChecker getModelChecker(OptionMap optionMap, IProgressMonitor monitor, double boundAccuracy) {
		ModelCheckingLog eventLog = new ModelCheckingLog();
		PropertyBank propertyBank = kroneckerModel.getPropertyBank();
		AbstractCTMC abstractCTMC = getAbstractCTMC(propertyBank, eventLog);
		SequentialAbstraction[] abstraction = kroneckerModel.getAbstraction();
		CSLModelChecker modelChecker = new CSLModelChecker(this, abstractCTMC, abstraction, optionMap, monitor, boundAccuracy, eventLog);
		return modelChecker;
	}
	
	private AbstractCTMC getAbstractCTMC(PropertyBank propertyBank, ModelCheckingLog eventLog) {
		AbstractKroneckerModel abstractModel = kroneckerModel.getAbstractModel();
		short[] abstractInitialState = getAbstractState(initialState);
		return new AbstractCTMC(abstractInitialState, abstractModel, propertyBank, eventLog);
	}
	
	private KroneckerStateSpace makeBound(KroneckerModel boundingModel, short[] newInitialState) {
		if (boundingModel == kroneckerModel) {
			return this;
		}
		if (newInitialState == null) {
			newInitialState = getAbstractState(initialState);
		}
		return new KroneckerStateSpace(newInitialState, symbolGenerator, boundingModel);
	}
	
	public KroneckerStateSpace getUpperBoundingStateSpace(CompositionalProperty property) {
		KroneckerModel boundingModel = kroneckerModel.getUpperBoundingModel(property);
		// We should make sure that the initial state is a state for which
		// the property is true, so that we can be sure to get an upper bound
		return makeBound(boundingModel, property.anyTrueState());
	}
	
	public KroneckerStateSpace getLowerBoundingStateSpace(CompositionalProperty property) {
		KroneckerModel boundingModel = kroneckerModel.getLowerBoundingModel(property);
		return makeBound(boundingModel, property.anyFalseState());
	}
	
	// Adds the state to the stateSpace (a HashMap) if it is a new state, in which
	// case it adds the state to the states (an ArrayList of the state space)
	private boolean addState(short[] state) {
		int hashCode = Arrays.hashCode(state);
		InsertionResult result = stateSpace.putIfNotPresentUnsync(state, hashCode);
		if (!result.wasPresent) {
			states.add(result.state);
		}
		return result.wasPresent;
	}
	
	private State getState(short[] state) {
		int hashCode = Arrays.hashCode(state);
		return stateSpace.putIfNotPresentUnsync(state, hashCode).state;
	}
	
	public short[] getSystemState(int index) {
		return states.get(index).fState;
	}
	
	private void initStateSpace() {
		states = new ArrayList<State>(1000);
		stateSpace = new OptimisedHashMap(1000);
		generatedStateSpace = false;
	}
	
	private void generateStateSpace() {
		// We build the state space of the system here
		// First reset the state space
		initStateSpace();
		
		// Start with the initial state
		short[] state = initialState;
		addState(state);
		
		// Stack of unexplored states
		Queue<short[]> queue = new LinkedList<short[]>();
		queue.add(state);
		
		// Explore the states in the queue
		try {
			while (!queue.isEmpty()) {
				state = queue.remove();
				ArrayList<Transition> found = kroneckerModel.getTransitionsFrom(state);
				for (Transition t : found) {
					short[] s = t.fTargetProcess;
					if (!addState(s)) {
						queue.add(s);
					}
				}
			}
			generatedStateSpace = true;
			generationError = null;
		} catch (DerivationException e) {
			generatedStateSpace = false;
			generationError = e;
		}
	}
	
	public DerivationException getDerivationException() {
		return generationError;
	}
	
	protected FlexCompRowMatrix createGeneratorMatrix() {
		// Pass 1 - generate the state space
		if (!generatedStateSpace && generationError == null) generateStateSpace();
		if (generationError != null) return null;
		
		// Pass 2 - generate the generator matrix Q
		FlexCompRowMatrix Q = new FlexCompRowMatrix(size(), size());
		for (int i = 0; i < size(); i++) {
			State s = states.get(i);
			ArrayList<Transition> transitions = null;
			try {
				transitions = kroneckerModel.getTransitionsFrom(s.fState);
			} catch (DerivationException e) {
				assert false;
			}
			
			for (Transition t : transitions) {
				// Find out the index of the state to insert entry into matrix
				short[] target = t.fTargetProcess;
				int target_index = getState(target).stateNumber;
				
				// Add rate to matrix, and subtract from diagonal
				double rate = t.fRate;
				//if (rate < 0) System.out.println("Found a passive rate: " + rate);
				
				Q.add(i, target_index, rate);
				Q.add(i,i,-rate);
			}
		}
		//System.out.println(Q + "\n==");
		return Q;
	}

	@Override
	// TODO We don't implement this for now - need to generate a MemoryStateSpace at some
	// point, for a more efficient representation of the transition system once we generate
	// the state space. Currently, we generate transitions on the fly.
	protected Generator createSimpleGenerator() {
		return null;
	}
	
	@Override
	// Calculates the throughput of each action
	protected void doThroughput(IProgressMonitor monitor) {
		// Handling the monitors
		throughput = EMPTY_THROUGHPUT;
		if (monitor == null)
			monitor = new DoNothingMonitor();
		if (solution == null) {
			monitor.done();
			return;
		}
		
		// A map from action identifier to throughput
		HashMap<Short, Double> throughputMap = new HashMap<Short, Double>();
		
		// Iterate over the solution vector
		for (int i = 0, size = size(); i < size; i++) {
			double steadyStateProb = solution[i];
			State state = states.get(i);
			ArrayList<Transition> transitions = null;
			try {
				kroneckerModel.getTransitionsFrom(state.fState);
			} catch (DerivationException e) {
				assert false;
			}
			for (Transition t : transitions) {	
				Double throughput = throughputMap.get(t.fActionId);
				if (throughput == null) throughput = 0.0;
				throughput += steadyStateProb * t.fRate;
				throughputMap.put(t.fActionId, throughput);
			}
		}
		
		// Generate the throughput data array
		ThroughputResult[] result = new ThroughputResult[throughputMap.size()];
		int i = 0;
		for (Map.Entry<Short, Double> entry : throughputMap.entrySet()) {
			ThroughputResult r = new ThroughputResult(symbolGenerator.getActionLabel(entry.getKey()), entry.getValue());
			result[i++] = r;
		}
		this.throughput = result;
		monitor.done();
	}

	@Override
	public NamedAction[] getAction(int source, int target) {
		if (!generatedStateSpace && generationError == null) generateStateSpace();
		if (generationError != null) return null;
		
		short[] source_state = states.get(source).fState;
		ArrayList<NamedAction> actions = new ArrayList<NamedAction>(10);
		ArrayList<Transition> transitions = null;
		try {
			transitions = kroneckerModel.getTransitionsFrom(source_state);
		} catch (DerivationException e) {
			assert false;
		}
		// For each transition, check whether it leads to the target state, and
		// if it does, what the action label is.
		for (Transition t : transitions) {
			if (getState(t.fTargetProcess).stateNumber == target) {
				NamedAction action_type = symbolGenerator.getAction(t.fActionId);
				if (!actions.contains(action_type)) {
					actions.add(action_type);
				}
			}
		}
		return (actions.size() > 0) ? (NamedAction[]) actions.toArray() : null;
	}

	@Override
	public int[] getIncomingStateIndices(int stateIndex) {
		if (!generatedStateSpace) generateStateSpace();
		short[] state = states.get(stateIndex).fState;
		IntegerArray indices = new IntegerArray(10);
		ArrayList<Transition> transitions = kroneckerModel.getTransitionsTo(state);
		// Add the indices of all states that we could have come from
		for (Transition t : transitions) {
			short[] prevState = t.fTargetProcess;
			int prevStateIndex = getState(prevState).stateNumber;
			if (!indices.contains(prevStateIndex)) {
				indices.add(prevStateIndex);
			}
		}
		return indices.toArray();
	}

	@Override
	public int[] getOutgoingStateIndices(int stateIndex) {
		if (!generatedStateSpace && generationError == null) generateStateSpace();
		if (generationError != null) return null;
		
		short[] state = states.get(stateIndex).fState;
		IntegerArray indices = new IntegerArray(10);
		ArrayList<Transition> transitions = null;
		try {
			kroneckerModel.getTransitionsFrom(state);
		} catch (DerivationException e) {
			assert false;
		}
		// Add the indices of all states that we can transition to
		for (Transition t : transitions) {
			short[] nextState = t.fTargetProcess;
			int nextStateIndex = getState(nextState).stateNumber;
			if (!indices.contains(nextStateIndex)) {
				indices.add(nextStateIndex);
			}
		}
		return indices.toArray();
	}

	@Override
	public double getRate(int source, int target) {
		if (!generatedStateSpace && generationError == null) generateStateSpace();
		if (generationError != null) return -1;
		
		short[] source_state = states.get(source).fState;
		ArrayList<Transition> transitions = null;
		try {
			kroneckerModel.getTransitionsFrom(source_state);
		} catch (DerivationException e) {
			assert false;
		}
		double rate = 0;
		// For each transition, check whether it leads to the target state, and
		// if it does, include its rate
		for (Transition t : transitions) {
			if (getState(t.fTargetProcess).stateNumber == target) {
				rate += t.fRate;
			}
		}
		return rate;
	}

	public void dispose() {
		// Nothing to do here
	}

	@Override
	public int size() {
		return (states == null) ? 0 : states.size();
	}

	public String toString() {
		return kroneckerModel.toString();
	}
	
	
}
