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


public class ProcessUsageManager {

	/* Holds information on process declaration usage */
	private HashMap<String, ProcessCounter> processUsage = new HashMap<String, ProcessCounter>();

	private ProblemManager problemManager;

	public ProcessUsageManager(ProblemManager problemManager) {
		this.problemManager = problemManager;
	}

	/* Register an lhs entry for a process name */
	public void lhs(String name) {
		ProcessCounter counter = getCounter(name);
		counter.lhs = counter.lhs + 1;
	}

	/* Register a rhs usage for the process */
	public void rhs(String name) {
		/* name must have been registered first */
		checkLhs(name).rhs++;
	}

	private ProcessCounter checkLhs(String name) {
		return getCounter(name);
	}

	public ProcessCounter getCounter(String name) {
		if (processUsage.containsKey(name))
			return processUsage.get(name);
		else {
			ProcessCounter counter = new ProcessCounter();
			processUsage.put(name, counter);
			return counter;
		}

	}

	/*
	 * Reports warnings to the model. This method is called after the visitor
	 * object has finished to visit the model
	 */
	public void warn() {

		for (Entry<String, ProcessCounter> mapEntry : processUsage
				.entrySet()) {
			ProcessCounter counter = mapEntry.getValue();
			String name = mapEntry.getKey();
			if (counter.lhs == 0) {
				problemManager.processNotDefinedError(name);
			} else if (counter.lhs > 1) {
				problemManager.processMultipleDeclaration(name);
			} else if (counter.rhs == 0) {
				problemManager.transientStateProblem(name);
			}
		}
	}

}





