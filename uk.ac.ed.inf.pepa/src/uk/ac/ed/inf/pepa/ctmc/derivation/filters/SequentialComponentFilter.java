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

public class SequentialComponentFilter implements IStateSpaceFilter {

	private Operator fOperator;

	private int fNumCopies;
	
	private String componentName;
	
	public SequentialComponentFilter(String componentName, Operator operator, int numCopies) {
		if (operator == null)
			throw new NullPointerException();
		this.componentName = componentName;
		this.fOperator = operator;
		this.fNumCopies = numCopies;
	}
	
	public IFilterRunner getRunner(IStateSpace stateSpace) {
		return new SequentialFilterRunner(stateSpace);
		
	}
	
	private class SequentialFilterRunner implements IFilterRunner {

		private short fProcessId;
		private IStateSpace stateSpace;
		
		public SequentialFilterRunner(IStateSpace ss) {
			fProcessId = ss.getProcessId(componentName);
			this.stateSpace = ss;
			
		}
		
		public boolean select(int state) {
			int copies = stateSpace.getNumberOfCopies(state, fProcessId);
			if (fOperator == Operator.EQ)
				return copies == fNumCopies;
			if (fOperator == Operator.GET)
				return copies >= fNumCopies;
			if (fOperator == Operator.GT)
				return copies > fNumCopies;
			if (fOperator == Operator.LET)
				return copies <= fNumCopies;
			if (fOperator == Operator.LT)
				return copies < fNumCopies;
			if (fOperator == Operator.NEQ)
				return copies != fNumCopies;
			throw new IllegalStateException();
		}
		
	}

}
