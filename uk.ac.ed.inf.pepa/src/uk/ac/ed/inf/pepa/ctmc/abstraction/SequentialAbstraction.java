/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.CompositionalProperty;

/**
 * Base class for abstracting the state space of a sequential
 * PEPA component. The basic 
 * 
 * @author msmith
 *
 */
public class SequentialAbstraction implements Abstraction {
	
	private int numConcreteStates;
	private int numAbstractStates;
	private int numAggregatedStates = 0;
	
	/**
	 * 
	 */
	private SequentialStateSpace concreteStateSpace;
	
	/**
	 * contains the mapping from concrete state indices to abstract state indices
	 */
	private	short[] abstractStateIDs;
	
	/**
	 * contains the actual abstract state corresponding to each concrete state
	 */
	private AbstractState[] abstractStates;
	
	/**
	 * Array of abstract states, indexed by abstract state identifier
	 */
	private AbstractState[] abstractStateSpace = null;
		
	private boolean normalised = false;

	private ArrayList<CompositionalProperty> registeredProperties;
	
	/**
	 * Constructs a new SequentialAbstraction based on the given number of
	 * concrete states, initially mapping every concrete state to an abstract
	 * state.
	 */
	public SequentialAbstraction(SequentialStateSpace concreteStateSpace) {
		concreteStateSpace.register(this);
		this.concreteStateSpace = concreteStateSpace;
		this.numConcreteStates = concreteStateSpace.size();
		this.numAbstractStates = numConcreteStates;
		this.abstractStateIDs = new short[numConcreteStates];
		this.abstractStates = new AbstractState[numConcreteStates];
		for (int i = 0; i < numConcreteStates; i++) {
			abstractStateIDs[i] = (short)i;
		}
		this.registeredProperties = new ArrayList<CompositionalProperty>(10);
	}
	
	/**
	 * Sets the abstract state number that each concrete state corresponds to,
	 * and also the state numbers [0..numAbstractStates-1] of the aggregated states 
	 */
	private void normalise() {
		short abstractStateCounter = 0;
		short stateCounter = (short)numAggregatedStates;
		resetAbstractStates();
		for (int i = 0; i < numConcreteStates; i++) {
			if (abstractStates[i] == null) {
				abstractStateIDs[i] = stateCounter++;
			} else if (abstractStates[i].getID() >= 0) {
				abstractStateIDs[i] = abstractStates[i].getID();
			} else {
				abstractStateIDs[i] = abstractStateCounter;
				abstractStates[i].setID(abstractStateCounter++);
			}
		}
		normalised = true;
	}
	
	private void resetAbstractStates() {
		for (int i = 0; i < numConcreteStates; i++) {
			if (abstractStates[i] != null) {
				abstractStates[i].setID((short)-1);
			}
		}
	}
	
	private void changed() {
		abstractStateSpace = null;
	}
	
	private int[] getConcreteIndices(short[] states) {
		int[] concreteIndices = new int[states.length];
		for (int i = 0; i < states.length; i++) {
			concreteIndices[i] = concreteStateSpace.getIndex(states[i]); 
		}
		return concreteIndices;
	}
	
	private void addAbstractState(short[] states) throws AggregationException {
		if (states.length <= 1) return;
		int[] indices = getConcreteIndices(states);
		if (!canAggregate(indices)) throw new AggregationException();
		AbstractState s = new AbstractState(states);
		for (int i = 0; i < indices.length; i++) {
			abstractStates[indices[i]] = s;
		}
		numAbstractStates -= indices.length - 1;
		numAggregatedStates++;
		normalise();
		changed();
	}
	
	private void removeAbstractState(AbstractState state) {
		int[] indices = getConcreteIndices(state.getConcrete());
		if (indices.length > 1) {
			for (int i = 0; i < indices.length; i++) {
				abstractStates[indices[i]] = null;
			}
			numAbstractStates += indices.length - 1;
			numAggregatedStates--;
			normalise();
			changed();
		}
	}
	
	private void generateAbstractStateSpace() {
		abstractStateSpace = new AbstractState[numAbstractStates];
		for (int i = 0; i < numConcreteStates; i++) {
			AbstractState currentState = abstractStates[i];
			if (currentState == null) {
				currentState = new AbstractState(concreteStateSpace.getState(i));
				currentState.setID(abstractStateIDs[i]);
			}
			abstractStateSpace[abstractStateIDs[i]] = currentState;
		}
	}
	
	public AbstractState[] getAbstractStateSpace() {
		if (!normalised) normalise();
		if (abstractStateSpace == null) {
			generateAbstractStateSpace();
		}
		return abstractStateSpace;
	}
	
	public short getAbstractState(short state) {
		return abstractStateIDs[concreteStateSpace.getIndex(state)];
	}
	
	private AbstractState findAbstract(short state) {
		if (!normalised) normalise();
		if (abstractStateSpace == null) {
			// We haven't indexed the abstract state space, so have to search
			for (int i = 0; i < abstractStateIDs.length; i++) {
				if (abstractStateIDs[i] == state) {
					AbstractState s = abstractStates[i];
					if (s == null) {
						s = new AbstractState(concreteStateSpace.getState(i));
						s.setID(state);
					}
					return s;
				}
			}
			return null;
		} else {
			return abstractStateSpace[state];
		}
	}
	
	public short[] getConcreteStates(short state) {
		if (state < 0 || state >= numAbstractStates) {
			// TODO check that state exists
			assert false;
		}
		return findAbstract(state).getConcrete();
	}
	
	private boolean canAggregate(int[] indices) {
		if (indices.length == 0) return false;
		for (int i = 1; i < indices.length; i++) {
			if (abstractStates[indices[i]] != null) return false;
		}
		return true;
	}
	
	public void disaggregate(short state) {
		AbstractState s = findAbstract(state);
		removeAbstractState(s);
	}
	
	public void aggregate(short[] states) throws AggregationException {
		addAbstractState(states);
	}
	
	private boolean propertyContains(short[] property, AbstractState state) {
		for (int i = 0; i < property.length; i++) {
			if (property[i] == state.getID()) return true;
		}
		return false;
	}
	
	/**
	 * Reorders the state space - concrete states are indexed so that aggregated states are adjacent,
	 * and those in abstract states in property are put at the top of the ordering on indices. 
	 * @param property A set of abstract states that should be at the top of the partial ordering of the state space
	 */
	public void reorderStateSpace(short[] property) {
		AbstractState[] stateSpace = getAbstractStateSpace();
		AbstractState[] newStateSpace = new AbstractState[stateSpace.length];
		short[] IDMap = new short[stateSpace.length];
		short startIndex = 0;
		short endIndex = (short)(stateSpace.length - 1);
		for (int i = 0; i < stateSpace.length; i++) {
			assert startIndex <= endIndex;
			AbstractState state = stateSpace[i];
			short newID;
			if (propertyContains(property,state)) {
				newID = endIndex;
				endIndex--;
			} else {
				newID = startIndex;
				startIndex++;
			}
			newStateSpace[newID] = state;
			state.setID(newID);
			IDMap[i] = newID;
			int[] indices = getConcreteIndices(state.getConcrete());
			boolean isSingleState = indices.length == 1;
			for (int j = 0; j < indices.length; j++) {
				int index = indices[j];
				abstractStateIDs[index] = newID;
				if (isSingleState) {
					abstractStates[index] = null;
				} else {
					abstractStates[index] = state;
				}
			}
		}
		for (CompositionalProperty registeredProperty : registeredProperties) {
			//System.out.println("=== NOTIFY ===");
			//System.out.println(registeredProperty);
			//System.out.println("=== >>> ===");
			registeredProperty.notifyAbstractChange(this, IDMap);
			//System.out.println(registeredProperty);
			//System.out.println("===");
		}
		abstractStateSpace = newStateSpace;
		concreteStateSpace.reorderStates(newStateSpace);
	}
	
	public void registerProperty(CompositionalProperty property) {
		//System.out.println("=== REGISTER ===");
		//System.out.println(property);
		//System.out.println("===");
		registeredProperties.add(property);
	}
	
	public void unregisterProperty(CompositionalProperty property) {
		registeredProperties.remove(property);
	}
	
	public void unregisterAllProperties() {
		registeredProperties.clear();
	}
	
	/**
	 * Called whenever two states in the concrete state space swap their indices
	 */
	public void notifySwap(int index1, int index2) {
		short stateID1 = abstractStateIDs[index1];
		short stateID2 = abstractStateIDs[index2];
		abstractStateIDs[index1] = stateID2;
		abstractStateIDs[index2] = stateID1;
		AbstractState abstractState1 = abstractStates[index1];
		AbstractState abstractState2 = abstractStates[index2];
		abstractStates[index1] = abstractState2;
		abstractStates[index2] = abstractState1;
	}
	
	public int size() {
		return numAbstractStates;
	}
	
	public SequentialStateSpace getConcreteStateSpace() {
		return concreteStateSpace;
	}
	
	public boolean isAbstracted() {
		return size() != concreteStateSpace.size();
	}
	
	public String toString() {
		String s = "";
		AbstractState[] stateSpace = getAbstractStateSpace();
		for (int i = 0; i < stateSpace.length; i++) {
			s += stateSpace[i] + "\n";
		}
		return s;
	}
	
}
