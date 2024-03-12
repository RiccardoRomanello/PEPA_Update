/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.pepa.PEPAThroughputPerformanceMetric;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.pepa.UtilisationPerformanceMetric;


public class ConcretePerformanceMetricFactory extends
		AbstractPerformanceMetricFactory {

	private static final String OVERALL_STEADY_STATE = "Steady-state Probability";

	private static final String THROUGHPUT = "Throughput";

	private static final String UTILISATION = "Utilisation";

	private static final String[] DESCRIPTIONS = new String[] {
			OVERALL_STEADY_STATE, THROUGHPUT, UTILISATION };

	@Override
	public IPerformanceMetric createPerformanceMetric(String description,
			IEvaluator evaluator) {
		if (THROUGHPUT.equals(description))
			return new PEPAThroughputPerformanceMetric(description, evaluator);
		if (UTILISATION.equals(description))
			return new UtilisationPerformanceMetric(description, evaluator);
		if (OVERALL_STEADY_STATE.equals(description))
			return new FilteredStateSpaceOverallProbability(description,
					evaluator);
		return null;
	}

	@Override
	public String[] getDescriptions() {
		return DESCRIPTIONS;
	}

}
