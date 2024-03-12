/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.RateMatrix;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.AtomicProperty;

public class SequentialStateSpace {

	private ShortArray states;
	
	private ArrayList<RateMatrix> registeredMatrices       = new ArrayList<RateMatrix>(10);
	private ArrayList<Abstraction> registeredAbstractions  = new ArrayList<Abstraction>(10);
	private ArrayList<AtomicProperty> registeredProperties = new ArrayList<AtomicProperty>(10);
	
	/**
	 * We optimise reverse lookup of the state space the first time getState() is called
	 */
	private boolean optimiseFind = false;
	
	public SequentialStateSpace() {
		states = new ShortArray(10);
	}
	
	public void addState(short state) {
		states.addNew(state);
		optimiseFind = false;
	}
	
	public boolean containsState(short state) {
		return states.contains(state);
	}
	
	/**
	 * Returns the index of a given state identifier
	 */
	public int getIndex(short state) {
		return states.findPosition(state);
	}
	
	/**
	 * Returns the state identifier corresponding to the given index
	 */
	public short getState(int index) {
		if (!optimiseFind) states.optimiseFind();
		return states.get(index);
	}

	/**
	 * Change the indices of the given states, so that they are in sequential order, according to
	 * the ordering of the abstract states as given
	 */
	public void reorderStates(AbstractState[] aggregates) {
		int indexCount = 0;
		for (int i = 0; i < aggregates.length; i++) {
			short[] concreteStates = aggregates[i].getConcrete();
			for (int j = 0; j < concreteStates.length; j++) {
				int currentIndex = getIndex(concreteStates[j]);
				if (currentIndex != indexCount) {
					states.swap(currentIndex, indexCount);
					notifySwap(currentIndex, indexCount);
				}
				indexCount++;
			}
		}
	}
	
	/**
	 * Notifies any registered rate matrices of a swapping of states in the state space
	 */
	private void notifySwap(int index1, int index2) {
		for (RateMatrix rateMatrix : registeredMatrices) {
			rateMatrix.notifySwap(index1, index2);
		}
		for (Abstraction abstraction : registeredAbstractions) {
			abstraction.notifySwap(index1, index2);
		}
		for (AtomicProperty property : registeredProperties) {
			property.notifySwap(this, index1, index2);
		}
	}
	
	/**
	 * Called by a rate matrix using this state space, in order to notify it of
	 * any changes to the state space.
	 */
	public void register(RateMatrix rateMatrix) {
		registeredMatrices.add(rateMatrix);
	}

	/**
	 * Called by an abstraction built on this state space, in order to notify it of
	 * any changes to the state space.
	 */
	public void register(Abstraction abstraction) {
		registeredAbstractions.add(abstraction);
	}
	
	/**
	 * Called by an atomic property built on this state space, in order to notify it of
	 * any changes to the state space.
	 */
	public void register(AtomicProperty property) {
		registeredProperties.add(property);
	}
	
	/**
	 * Returns the size of the state space
	 */
	public int size() {
		return states.size();
	}
	
}
