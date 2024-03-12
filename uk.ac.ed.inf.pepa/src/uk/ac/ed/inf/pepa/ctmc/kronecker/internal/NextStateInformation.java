/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import java.util.ArrayList;

public class NextStateInformation {

	private ArrayList<Short> nextStates;
	private ArrayList<Double> nextLowerProbs;
	private ArrayList<Double> nextUpperProbs;
	
	public NextStateInformation() {
		this.nextStates = new ArrayList<Short>(10);
		this.nextLowerProbs = new ArrayList<Double>(10);
		this.nextUpperProbs = new ArrayList<Double>(10);
	}
	
	public void addState(short state, double lowerProb, double upperProb) {
		nextStates.add(state);
		nextLowerProbs.add(lowerProb);
		nextUpperProbs.add(upperProb);
	}
	
	public short[] getNextStates() {
		short[] next = new short[nextStates.size()];
		for (int i = 0; i < nextStates.size(); i++) {
			next[i] = nextStates.get(i);
		}
		return next;
	}
	
	public double[] getNextLowerProbabilities() {
		double[] lower = new double[nextLowerProbs.size()];
		for (int i = 0; i < nextLowerProbs.size(); i++) {
			lower[i] = nextLowerProbs.get(i);
		}
		return lower;
	}
	
	public double[] getNextUpperProbabilities() {
		double[] upper = new double[nextUpperProbs.size()];
		for (int i = 0; i < nextUpperProbs.size(); i++) {
			upper[i] = nextUpperProbs.get(i);
		}
		return upper;
	}
	
}
