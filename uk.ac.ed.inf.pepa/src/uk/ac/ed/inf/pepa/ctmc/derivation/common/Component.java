/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.ArrayList;
import java.util.BitSet;

import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * Representation of a component for the iterative state space explorer.
 * 
 * @author mtribast
 * 
 */
public class Component {

	/**
	 * Position of this component in the state vector
	 */
	public int fOffset;

	/**
	 * Number of positions taken up by the component
	 */
	public int fLength;

	/**
	 * First step derivatives of this component
	 */
	protected ArrayList<Transition> fFirstStepDerivatives;

	/**
	 * Apparent rates, indexed by action id.
	 */
	//Note, many elements may be 0, this contains all the
	//possible action types of the system. But this is OK,
	//because it is usually not that many. With this 
	// implementation, the access to rates is very fast.
	public double[] fApparentRates;
	
	public IStateExplorer fExplorer;

	/**
	 * Name of this component, for debug
	 */
	private String fName = null;

	/**
	 * Hiding set
	 */
	public BitSet fHidingSet;
	
	protected Buffer buf;

	/**
	 * Creates a component of a state explorer.
	 * 
	 * @param stateExplorer
	 *            the state explorer of this component
	 */
	public Component() {
		fFirstStepDerivatives = new ArrayList<Transition>();
		this.fHidingSet = new BitSet();
	}

	public Component(String name) {
		this();
		this.fName = name;
	}
	
	public String getName() {
		return fName;
	}

	public void dumpMeasurement() {
	}

	public void init(IStateExplorer stateExplorer) {
		fExplorer = stateExplorer;
		buf = fExplorer.getBuffer();
	}

	public Transition[] getDerivatives() {
		Transition[] transition = new Transition[fFirstStepDerivatives.size()];
		int i = 0;
		for (Transition result : fFirstStepDerivatives) {
			Transition t = new Transition();
			t.fTargetProcess = new short[result.fTargetProcess.length];
			System.arraycopy(result.fTargetProcess, 0, t.fTargetProcess, 0, result.fTargetProcess.length);
			t.fActionId =result.fActionId;
			t.fLevel = result.fLevel;
			t.fRate = result.fRate;
			transition[i++] = t;
		}
		return transition;
	}

	public void update(short[] currentState) {
		short processId = currentState[fOffset];
		SequentialComponentData data = fExplorer.getData(processId);
		if (fHidingSet.isEmpty()) {
			// they won't be modified
			fApparentRates = data.fArrayApparentRates;
			fFirstStepDerivatives = data.fFirstStepDerivative;
		} else {
			fApparentRates = applyHidingRule(data.fArrayApparentRates, fHidingSet);
			applyHidingRule(data.fFirstStepDerivative);
		}
	}

	private final void applyHidingRule(ArrayList<Transition> firstStepDerivative) {
		fFirstStepDerivatives.clear();
		for (Transition t : firstStepDerivative) {
			Transition newT = buf.getTransition(
							t.fTargetProcess,
							this.fOffset,
							this.fLength,
							fHidingSet.get(t.fActionId)
									? ISymbolGenerator.TAU_ACTION
									: t.fActionId,
							fHidingSet.get(t.fActionId)
									? ActionLevel.UNDEFINED
									: t.fLevel,
							t.fRate
			);
			fFirstStepDerivatives.add(newT);
		}
	}

	private static final double[] applyHidingRule(
			double[] original, BitSet hidingSet) {
		double[] copy = new double[original.length];
		for (int i = 0; i < copy.length; i++) {
			// copy[i] = (!hidingSet.get(i)) ? original[i] : 0.0d;
			if (!hidingSet.get(i)) {
				copy[i] = original[i];
			} else {
				copy[i] = 0.0d;
				copy[0] += original[i];
			}
		}
		return copy;
	}

	public void setHidingSet(BitSet hidingSet) {
		if (hidingSet == null)
			throw new NullPointerException();
		this.fHidingSet = hidingSet;
	}

	/**
	 * Prints position, offset and memory location
	 */
	public String toString() {
		return ((fName != null) ? fName : "") + " Component at position: "
				+ fOffset + ", length: " + fLength;
	}

}
