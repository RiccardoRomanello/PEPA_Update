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

public class NegationFilter implements IStateSpaceFilter {
	
	private IStateSpaceFilter fFilter;
	
	public NegationFilter(IStateSpaceFilter filter) {
		this.fFilter = filter;
	}
	
	public IFilterRunner getRunner(final IStateSpace stateSpace) {
		
		return new NotRunner(stateSpace);	
	
	}
	
	private class NotRunner implements IFilterRunner {
		
		private IFilterRunner fRunner;
		
		public NotRunner(IStateSpace stateSpace) {
			fRunner = fFilter.getRunner(stateSpace);
		}
		
		public boolean select(int stateIndex) {
			
			return !fRunner.select(stateIndex);
		}
		
	}

}
