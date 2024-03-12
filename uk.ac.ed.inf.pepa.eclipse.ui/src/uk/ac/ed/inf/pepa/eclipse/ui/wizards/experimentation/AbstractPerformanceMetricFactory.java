/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;



public abstract class AbstractPerformanceMetricFactory {
	
	/**
	 * Return the list of available performance metrics which can be created by
	 * this factory.
	 * <p>
	 * In this project these are used to lazily create the menu for the
	 * performance metric list in the experimentation wizard
	 * 
	 * @return the available experiments
	 */
	public abstract String[] getDescriptions();

	/**
	 * Create a new instance of the performance experiment with the given
	 * description
	 * 
	 * @param description
	 *            the experiment name
	 * @return the experiment.
	 * @see #getDescriptions()
	 */
	public abstract IPerformanceMetric createPerformanceMetric(
			String description, IEvaluator evaluator);

}
