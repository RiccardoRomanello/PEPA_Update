/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import org.eclipse.core.runtime.Assert;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class FilterFactory {

	public static final String TAG_SEQUENTIAL_COMPONENT = "sequentialComponent";
	
	public static final String TAG_STEADY_STATE = "steadyStateProbability";
	
	public static final String TAG_PATTERN_MATCHING = "patternMatching";
	
	public static final String TAG_UNNAMED_PROCESSES = "unnamedProcess";
	
	public static final String TAG_INCOMING_ACTION = "incomingActionFilter";
	
	public static final String TAG_OUTGOING_ACTION = "outgoingActionFilter";
	
	public static AbstractConfigurableStateSpaceFilter createFilter(IProcessAlgebraModel model, String description) {
		Assert.isNotNull(description);

		if (description.equals(TAG_SEQUENTIAL_COMPONENT))
			return new SequentialComponentFilter(model);
		if (description.equals(TAG_STEADY_STATE))
			return new SteadyStateProbabilityFilter(model);
		if (description.equals(TAG_PATTERN_MATCHING))
			return new PatternMatchingFilter(model);
		if (description.equals(TAG_UNNAMED_PROCESSES))
			return new UnnamedProcessesFilter(model);
		if (description.equals(TAG_INCOMING_ACTION))
			return new IncomingActionFilter(model);
		if (description.equals(TAG_OUTGOING_ACTION))
			return new OutgoingActionFilter(model);
		return null;
	}

	public static String getTagForClass(
			AbstractConfigurableStateSpaceFilter filterClass) {
		if (filterClass instanceof SequentialComponentFilter)
			return TAG_SEQUENTIAL_COMPONENT;
		if (filterClass instanceof SteadyStateProbabilityFilter)
			return TAG_STEADY_STATE;
		if (filterClass instanceof PatternMatchingFilter) 
			return TAG_PATTERN_MATCHING;
		if (filterClass instanceof UnnamedProcessesFilter)
			return TAG_UNNAMED_PROCESSES;
		if (filterClass instanceof IncomingActionFilter)
			return TAG_INCOMING_ACTION;
		if (filterClass instanceof OutgoingActionFilter)
			return TAG_OUTGOING_ACTION;
		Assert.isTrue(false, "No Tag found for " + filterClass);
		return null;

	}
}
