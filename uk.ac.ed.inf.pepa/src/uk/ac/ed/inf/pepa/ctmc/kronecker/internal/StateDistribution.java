/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

public class StateDistribution {
	
	private short[] states;
	private double[] probabilities;
	
	private int numUsed;
	
	public StateDistribution(int numStates) {
		states = new short[numStates];
		probabilities = new double[numStates];
		numUsed = 0;
	}
	
	/**
	 * Creates a self loop
	 */
	public static StateDistribution getLoopDistribution(short loopState) {
		StateDistribution loop = new StateDistribution(1);
		loop.addEntry(loopState, 1.0);
		return loop;
	}
	
	
	public void addEntry(short state, double probability) {
		int index = numUsed++;
		states[index] = state;
		probabilities[index] = probability;
	}
	
	public short[] getStates() {
		if (states.length > numUsed) {
			short[] newStates = new short[numUsed];
			System.arraycopy(states, 0, newStates, 0, numUsed);
			states = newStates;
		}
		return states;
	}
	
	public double[] getProbabilities() {
		if (probabilities.length > numUsed) {
			double[] newProbabilities = new double[numUsed];
			System.arraycopy(probabilities, 0, newProbabilities, 0, numUsed);
			probabilities = newProbabilities;
		}
		return probabilities;
	}
	
	public int size() {
		return states.length;
	}
	
}
