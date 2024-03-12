/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;

public interface IStateExplorer {
	
	public void init();
	
	public SequentialComponentData getData(short processId);
	
	/**
	 * Explores the given state.
	 * 
	 * @param state
	 * @return the number of transitions found
	 * @throws DerivationException 
	 */
	public abstract Transition[] exploreState(short[] state)
			throws DerivationException;
	
	public void dispose();

	public Buffer getBuffer();

}