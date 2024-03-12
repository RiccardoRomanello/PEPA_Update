/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.HashMap;
import java.util.Map.Entry;


public class RateUsageManager {

	/* Holds information on rate declaration usage */
	private HashMap<String, RateCounter> rateUsage = new HashMap<String, RateCounter>();

	private ProblemManager problemManager;

	public RateUsageManager(ProblemManager problemManager) {
		this.problemManager = problemManager;
	}

	/* Register an lhs entry for a rate name */
	public void lhs(String name) {
		RateCounter counter = getCounter(name);
		counter.lhs = counter.lhs + 1;
	}

	/* Register a rhs usage for the rate */
	public void rhs(String name) {
		/* name must have been registered first */
		checkLhs(name).rhs++;
	}

	/* Register a rate usage by a process */
	public void process(String name) {
		checkLhs(name).process++;
	}

	private RateCounter checkLhs(String name) {
		RateCounter counter = getCounter(name);
		if (counter.lhs == 0)
			problemManager.rateNotDefinedProblem(name);
		return counter;
	}

	/*
	 * Reports warnings to the model. This method is called after the visitor
	 * object has finished to visit the model
	 */
	public void warn() {

		for (Entry<String, RateCounter> mapEntry : rateUsage.entrySet()) {
			RateCounter counter = mapEntry.getValue();
			String name = mapEntry.getKey();
			if (counter.lhs > 1)
				problemManager.rateMultipleDeclaration(name);
			if (counter.rhs + counter.process == 0)
				problemManager.rateNotUsedProblem(name);
		}

	}

	private RateCounter getCounter(String name) {
		if (rateUsage.containsKey(name))
			return rateUsage.get(name);
		else {
			RateCounter counter = new RateCounter();
			rateUsage.put(name, counter);
			return counter;
		}

	}

}
