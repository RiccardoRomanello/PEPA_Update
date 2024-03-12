/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.AbstractKroneckerModel;

public class AbstractCTMC implements Iterable<AbstractCTMCState> {

	//public static final int SIG_FIGS = 12;
	public static final double EPSILON = 1e-15;
	
	private AbstractKroneckerModel model;
	
	private ArrayList<AbstractCTMCState> states;
	private HashMap<AbstractCTMCState, AbstractCTMCState> stateSpace;
	private boolean isGenerated = false;
	private DerivationException generationError = null;
	private int maxNondeterministicChoices = 0;
	private int numTransitions = 0;
	
	private AbstractCTMCState initialState;
	
	private CSLPropertyManager propertyManager;
	
	private ModelCheckingLog eventLog;

	/**
	 * ACTMC must be uniformised, so we store the uniformisation constant here
	 */
	private double uniformisationConstant;
	
	public AbstractCTMC(short[] initialState, AbstractKroneckerModel model, PropertyBank propertyBank, ModelCheckingLog eventLog) {
		this.model = model;
		this.initialState = new AbstractCTMCState(initialState);
		this.propertyManager = new CSLPropertyManager(propertyBank, model.getAbstractStateSpace());
		this.uniformisationConstant = model.getUniformisationConstant();
		this.eventLog = eventLog;
	}
	
	public CSLPropertyManager getPropertyManager() {
		return propertyManager;
	}
	
	public double getUniformisationConstant() {
		return uniformisationConstant;
	}
	
	public int getMaxNondeterministicChoices() {
		return maxNondeterministicChoices;
	}
	
	public int getNumTransitions() {
		return numTransitions;
	}
	
	private void optimiseUniformisationConstant() {
		model.optimiseUniformisationConstant();
		uniformisationConstant = model.getUniformisationConstant();
	}
	
	/**
	 * Adds the state to the stateSpace (a HashMap) if it is a new state, in which
	 * case it adds the state to the states (an ArrayList of the state space)
	 */
	private AbstractCTMCState addState(AbstractCTMCState state) {
		if (!stateSpace.containsKey(state)) {
			int index = states.size();
			state.setIndex(index);
			stateSpace.put(state, state);
			states.add(state);
			return null;
		} else {
			return stateSpace.get(state);
		}
	}
	
	private void initStateSpace() {
		states = new ArrayList<AbstractCTMCState>(1000);
		stateSpace = new HashMap<AbstractCTMCState, AbstractCTMCState>(1000);
		initialState = new AbstractCTMCState(initialState);
		isGenerated = false;
		maxNondeterministicChoices = 0;
		numTransitions = 0;
	}
	
	/**
	 * State space is generated lazily, when first needed.
	 * @throws InterruptedException 
	 */
	private void generateStateSpace() throws InterruptedException {
		try {
			eventLog.addEntry("Generating abstract CTMC... ");
			internalGenerateStateSpace();
			optimiseUniformisationConstant();
			eventLog.addEntry("Optimising uniformisation constant to " + getUniformisationConstant() + "...");
			internalGenerateStateSpace();
			eventLog.addEntry("Generated abstract CTMC with " + states.size() + " states and " + numTransitions + " transitions.");
			// cleanup, to reclaim memory
			stateSpace = null;
		} catch (DerivationException e) {
			eventLog.addEntry("Error while generating abstract CTMC: " + e.getMessage());
			isGenerated = false;
			generationError = e;
		}
	}
	
	/**
	 * Generates the reachable state space
	 * @throws InterruptedException 
	 */
	private void internalGenerateStateSpace() throws DerivationException, InterruptedException {
		// We build the state space of abstract CTMC here
		// First reset the state space
		initStateSpace();
		
		// Start with the initial state
		AbstractCTMCState state = initialState;
		addState(state);
		
		// Stack of unexplored states
		Queue<AbstractCTMCState> queue = new LinkedList<AbstractCTMCState>();
		queue.add(state);
		
		// Explore the states in the queue
		int counter = 0;
		while (!queue.isEmpty()) {
			if (counter % 1000 == 0 && Thread.interrupted()) {
				throw new InterruptedException();
			}
			counter++;
			
			state = queue.remove();
			propertyManager.labelAtomicProperties(state);
			ArrayList<AbstractCTMCTransition> found = model.getTransitionsFrom(state);
			
			// First of all, we combine transitions that lead to the same state
			HashMap<AbstractCTMCState, AbstractCTMCTransition> toStates = new HashMap<AbstractCTMCState, AbstractCTMCTransition>(10);
			for (AbstractCTMCTransition t : found) {
				AbstractCTMCState toState = t.getToState();
				if (toStates.containsKey(toState)) {
					AbstractCTMCTransition existing_t = toStates.get(toState);
					existing_t.addProbability(t.getMinProb(), t.getMaxProb());
				} else {
					toStates.put(toState, t);
				}
			}
			found = new ArrayList<AbstractCTMCTransition>(toStates.values());
			
			// Now delimit the transitions (remove probability combinations that are not possible)
			for (AbstractCTMCTransition t : found) {
				t.delimit(found);
			}
			
			// Now add transitions to the state, and new states that have been discovered
			// Also keep track of how many transitions are non-deterministic (have differing
			//   min and max rates), because this will help us for exporting to MRMC.
			int numChoices = 1;
			for (AbstractCTMCTransition t : found) {
				// Sort out some numerical issues
				if (t.getMaxProb() < EPSILON) {
					continue;
				} else if (Math.abs(t.getMaxProb() - t.getMinProb()) < EPSILON) {
					double newValue = (t.getMaxProb() + t.getMinProb()) / 2.0;
					t.setMinProb(newValue);
					t.setMaxProb(newValue);
				} else if (t.getMinProb() < EPSILON) {
					t.setMinProb(0);
				}
				
				// Now add the transition
				AbstractCTMCState foundState = t.getToState();
				AbstractCTMCState existingState = addState(foundState);
				if (existingState == null) {
					// We've found a new state
					queue.add(foundState);
				} else {
					t.setToState(existingState);
				}
				state.addTransition(t);
				numTransitions++;
				if (t.isNonDeterministic()) {
					//System.out.println("[" + t.getMinProb() + ", " + t.getMaxProb() + "]");
					numChoices *= 2;
				}
			}
			maxNondeterministicChoices = Math.max(maxNondeterministicChoices, numChoices);
			
		}
		
		isGenerated = true;
	}

	// Iterator
	public Iterator<AbstractCTMCState> iterator() {
		if (!isGenerated && generationError == null) {
			try {
				generateStateSpace();
			} catch (InterruptedException e) {
				generationError = new DerivationException("Interrupted while generating state space.");
			}
		}
		if (generationError != null) return new ArrayList<AbstractCTMCState>().iterator();
		return states.iterator();
	}
	
	public AbstractCTMCState getInitialState() {
		return initialState;
	}
		
	public int size() {
		if (!isGenerated && generationError == null) {
			try {
				generateStateSpace();
			} catch (InterruptedException e) {
				generationError = new DerivationException("Interrupted while generating state space.");
			}
		}
		if (generationError != null) return 0;
		return states.size();
	}
	
	public String toString() {
		if (!isGenerated && generationError == null) {
			try {
				generateStateSpace();
			} catch (InterruptedException e) {
				generationError = new DerivationException("Interrupted while generating state space.");
			}
		}
		if (generationError != null) return "";
		String actmcString = "";
		for (AbstractCTMCState state : states) {
			actmcString += state.toStringFull() + "\n";
		}
		return actmcString;
	}
	
	public DerivationException getGenerationError() {
		return generationError;
	}
	
}
