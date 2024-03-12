/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.filters;

import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory.Operator;

public class SteadyStateThresholdFilter implements IStateSpaceFilter {
	
	private Operator fOperator;
	
	private double fThreshold;
	
	public SteadyStateThresholdFilter(Operator operator, double threshold) {
		if (operator == null)
			throw new NullPointerException();
		this.fOperator = operator;
		this.fThreshold = threshold;
	}

	public IFilterRunner getRunner(final IStateSpace ss) {
		return new IFilterRunner() {

			public boolean select(int state) {
				double value = ss.getSolution(state);
				if (Double.isNaN(value))
					return true; // no-effect rule
				if (fOperator == Operator.EQ)
					return value == fThreshold;
				if (fOperator == Operator.GET)
					return value >= fThreshold;
				if (fOperator == Operator.GT)
					return value > fThreshold;
				if (fOperator == Operator.LET)
					return value <= fThreshold;
				if (fOperator == Operator.LT)
					return value < fThreshold;
				if (fOperator == Operator.NEQ)
					return value != fThreshold;
				throw new IllegalStateException();
			}
			
		};
	}

}
