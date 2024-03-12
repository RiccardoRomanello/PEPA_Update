/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.largescale.internal;

import java.util.Map;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.SequentialComponentData;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

/**
 * Explores a state.
 * 
 * @author mtribast
 * 
 */
public class ParametricStateExplorer {

	/**
	 * All the sequential components, as they appear in the system equation
	 */
	ParametricComponent[] sequentialComponents;

	/**
	 * The operators, as they appear in the system equation's tree
	 */
	ParametricOperator[] operators;

	/**
	 * Information for sequential components, computed once.
	 * <p>
	 * The array is indexed by process id
	 */
	SequentialComponentData[] sequentialComponentInfo;
	
	/** Initial state vector for the transition system, i.e.
	 *  NOT in the numerical vector form 
	 */
	short[] initialVector;
	
	/** 
	 * Process mappings for the numerical vector form representation
	 */
	short[] processMappings;
	
	
	int problemSize;
	
	ParametricStateExplorer() {
	}
	
	public int getProblemSize() {
		return problemSize;
	}
	
	public short[] getProcessMappings() {
		return processMappings;
	}
	
	public ParametricComponent[] getSequentialComponents() {
		return sequentialComponents;
	}

	public void init() {
		int coordinateIndex = 0;
		for (ParametricComponent c : sequentialComponents) {
			c.init(this);
			coordinateIndex = c.setupCoordinateIndexes(coordinateIndex);
			
		}
		for (ParametricOperator o : operators)
			o.init(this);
		problemSize = coordinateIndex;
		processMappings = new short[problemSize];
		for (ParametricComponent c: sequentialComponents)
			for (Map.Entry<Short, Coordinate> entry : c.getComponentMapping())
				processMappings[entry.getValue().getCoordinate()] = entry.getKey();
	}

	public void dispose() {
	}

	public void debug() {
		System.out.println("List of components:");
		for (ParametricComponent c : sequentialComponents) {
			System.out.println(c.toString());
		}
		System.out.println("List of operators:");
		for (ParametricOperator o : operators) {
			System.out.println(o.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.IStateExplorer#exploreState(short[])
	 */
	public ParametricTransition[] exploreState(short[] state)
			throws DifferentialAnalysisException {
		
		/*
		 * Notify sequential components, i.e. sets their apparent rates and the
		 * first step derivatives
		 */
		for (ParametricComponent c : sequentialComponents) {
			c.compose(state);
		}

		for (int i = operators.length - 1; i >= 0; i--) {
			operators[i].compose(state);
		}
		/*
		 * Should check for passive rates here, but iteration over the
		 * transition will be done anyway later (state space builder), so skip
		 * now.
		 */
		if (operators.length == 0) {
			return sequentialComponents[0].getDerivatives();
		} else {
			return operators[0].getDerivatives();
		}

	}

	public SequentialComponentData getData(short processId) {
		return sequentialComponentInfo[processId];
	}

}
