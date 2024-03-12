/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.filters;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;

public class PatternMatchingFilter implements IStateSpaceFilter {
	
	private String pattern;
	
	public PatternMatchingFilter(String pattern) {
		this.pattern = pattern;
	}
	
	private class PatternData {
		
		int position;
		short processId;
	}
	
	public IFilterRunner getRunner(IStateSpace stateSpace) {
		
		return new FilterRunner(stateSpace);
	}
	
	private class FilterRunner implements IFilterRunner {
		
		private ArrayList<PatternData> fList = new ArrayList<PatternData>();
		
		private IStateSpace ss;
		
		public FilterRunner(IStateSpace ss) {
			this.ss = ss;
			String[] individualComponents = pattern.split(FilterFactory.VERTICAL_BAR);
			for (int i = 0; i < individualComponents.length; i++) {
				String comp = individualComponents[i].trim();
				if (!comp.equals(FilterFactory.WILDCARD)) {
					PatternData d = new PatternData();
					d.position = i;
					d.processId = ss.getProcessId(comp);
					if (d.processId == -1)
						throw new IllegalArgumentException("Component " + comp + " does not exist");
					fList.add(d);
				}
			}
		}
		
		public boolean select(int state) {
			for (PatternData d : fList) {
				if (ss.getProcessId(state, d.position)!= d.processId)
					return false;
			}
			return true;
		}
		
	}

}
