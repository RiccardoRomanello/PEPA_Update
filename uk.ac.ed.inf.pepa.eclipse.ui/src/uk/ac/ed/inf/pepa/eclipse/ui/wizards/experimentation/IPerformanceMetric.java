/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.swt.widgets.Composite;

/**
 * Represent a configurable performance metric for experimentation
 * 
 * @author mtribast
 * 
 */
public interface IPerformanceMetric extends IDynamicChildren {

	public IExperiment getExperiment();
	
	public void setExperiment(IExperiment experiment);
	
	/**
	 * Evaluate the performance of the given model according to this metric.
	 * <p>
	 * The object must be in a correct state before calling this method.
	 * 
	 * @see #isCanEvaluate()
	 * @return the result of the evaluation
	 */
	public double evaluate(ISetting[] settings, int[] currentIndex)
		throws EvaluationException;

	/**
	 * Determine if the settings are correct
	 * 
	 * @return <code>true</code> if the settings are correct
	 */
	public boolean isCanEvaluate();

	/**
	 * Human readable description of this performance metric.
	 * <p>
	 * This will be used by combo boxes to list the available performance
	 * metrics in an experimentation context
	 * 
	 * @return the description of this metric
	 */
	public String getDescription();
	
	/**
	 * Label that will be used in graphs with showing this performance metric.
	 * This usually includes the description ({@link #getDescription()} as well
	 * as the settings of the particular instance.
	 * 
	 * @return the label used in graphs
	 */
	public String getLabel();

	/**
	 * Open/create the control for setting the parameters of this performance
	 * setting.
	 * <p>
	 * May be called multiple times during the life cycle of this object
	 * 
	 * @param parent
	 *            the parent of this control
	 */
	public void createControl(Composite parent);


}
