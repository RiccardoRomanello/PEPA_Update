/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Buffer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Component;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Operator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.SequentialComponentData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;

/**
 * Explores a state.
 * 
 * @author mtribast
 * 
 */
public class StateExplorer implements IStateExplorer {

	/**
	 * All the sequential components, as they appear in the system equation
	 */
	Component[] sequentialComponents;

	/**
	 * The operators, as they appear in the system equation's tree
	 */
	Operator[] operators;

	/**
	 * Information for sequential components, computed once.
	 * <p>
	 * The array is indexed by process id
	 */
	SequentialComponentData[] sequentialComponentInfo;

	short[] initialVector;

	private Buffer buf;

	StateExplorer() {
	}

	public void init() {

		buf = new Buffer(initialVector.length);

		for (Component c : sequentialComponents)
			c.init(this);
		for (Operator o : operators)
			o.init(this);
		
	
	}

	public void dispose() {

		for (int i = operators.length - 1; i >= 0; i--)
			operators[i].dumpMeasurement();

		/*System.err.println("Buffer requests: " + buf.totalRequests);
		System.err.println("Buffer missed requests: " + buf.missedRequests);
		System.err.println("Buffer max requests per state: "
				+ buf.maxRequestsPerState);*/

		buf = null;
	}

	public void debug() {
		System.out.println("List of components:");
		for (Component c : sequentialComponents) {
			System.out.println(c.toString());
		}
		System.out.println("List of operators:");
		for (Operator o : operators) {
			System.out.println(o.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.IStateExplorer#exploreState(short[])
	 */
	public Transition[] exploreState(short[] state)
			throws DerivationException {

		buf.clear();
		/*
		 * Notify sequential components, i.e. sets their apparent rates and the
		 * first step derivatives
		 */
		for (Component c : sequentialComponents) {
			c.update(state);
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

	public final Buffer getBuffer() {
		return this.buf;
	}

}
