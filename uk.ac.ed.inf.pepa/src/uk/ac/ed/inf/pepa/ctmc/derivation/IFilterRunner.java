/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation;

public interface IFilterRunner {
	
	/**
	 * Applies this filter to <code>state</code>. It the state makes it through the
	 * filter the method returns <code>true</code>.
	 * @param state the state being filtered
	 * @return <code>true</code> if the state makes it through the
	 * filter
	 */
	public boolean select(int stateIndex);
}
