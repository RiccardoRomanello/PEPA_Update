/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 * Abstract PEPA Component
 * 
 * @author msmith
 */
public class AbstractRateMatrix {

	private SparseVector lowerRates;
	private SparseVector upperRates;

	private FlexCompRowMatrix lowerProbMatrix;
	private FlexCompRowMatrix upperProbMatrix;

	private boolean isEmpty;
	
	private SequentialAbstraction abstraction;
	
	public AbstractRateMatrix(SequentialAbstraction abstraction, SparseVector lowerRates, SparseVector upperRates,
			                  FlexCompRowMatrix lowerProbMatrix, FlexCompRowMatrix upperProbMatrix) {
		int size = abstraction.size();
		assert size == lowerRates.size();
		assert size == upperRates.size();
		assert lowerProbMatrix.isSquare() && size == lowerProbMatrix.numColumns();
		assert upperProbMatrix.isSquare() && size == upperProbMatrix.numColumns();
		this.abstraction = abstraction;
		this.lowerRates = lowerRates;
		this.upperRates = upperRates;
		this.lowerProbMatrix = lowerProbMatrix;
		this.upperProbMatrix = upperProbMatrix;
		this.isEmpty = false;
	}
	
	public AbstractRateMatrix(SequentialAbstraction abstraction) {
		this.abstraction = abstraction;
		this.isEmpty = true;
	}

	public double getLowerRate(short state) {
		if (isEmpty) return 0;
		// TODO
		//if (isEmpty) return -1;
		return lowerRates.get(state);
	}
	
	public double getUpperRate(short state) {
		if (isEmpty) return 0;
		// TODO
		//if (isEmpty) return -1;
		return upperRates.get(state);
	}
	
	/**
	 * Computes the set of states reachable from the given state for the component
	 */
	public NextStateInformation nextStates(short state) {
		NextStateInformation nextStates = new NextStateInformation();
		if (isEmpty) {
			// TODO
			nextStates.addState(state, 1.0, 1.0);
		} else {
			SparseVector probDist = upperProbMatrix.getRow(state);
			for (VectorEntry v : probDist) {
				int index = v.index();
				short nextState = (short)index;
				double lowerProb = lowerProbMatrix.get(state, index);
				double upperProb = v.get();
				if (upperProb > 0) {
					nextStates.addState(nextState, lowerProb, upperProb);
				}
			}
		}
		return nextStates;
	}
	
	public int size() {
		return abstraction.size();
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
}
