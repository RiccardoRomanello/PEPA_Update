/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;


public abstract class AbstractPerformanceMetric implements IPerformanceMetric {

	protected String description;

	protected IExperiment experiment;
	
	protected IDynamicParent parent;
	
	protected IEvaluator evaluator;
	
	protected AbstractPerformanceMetric(String description, IEvaluator evaluator) {
		this.description = description;
		this.evaluator = evaluator;
	}

	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Label used for graphs
	 * @return the label used for graphs
	 */
	public abstract String getLabel();
	
	public IExperiment getExperiment() {
		return this.experiment;
	}

	public void setExperiment(IExperiment experiment) {
		this.experiment = experiment;
	}
	
	public IDynamicParent getDynamicParent() {
		return this.parent;
	}

	public void setDynamicParent(IDynamicParent parent) {
		this.parent = parent;
	}
	
	protected IProcessAlgebraModel getModel() {
		return ((ExperimentationWizard) this.getExperiment()
				.getExperimentPage().getWizard())
				.getProcessAlgebraModel();
	}

}
