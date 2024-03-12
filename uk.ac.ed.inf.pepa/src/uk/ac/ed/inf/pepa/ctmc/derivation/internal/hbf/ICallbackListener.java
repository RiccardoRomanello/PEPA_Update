/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;

public interface ICallbackListener {

	/**
	 * Notifies listener that derivatives have been found for that state
	 * @param state
	 * @param transitions
	 * @throws DerivationException
	 */
	public void foundDerivatives(State state, Transition[] stream) throws DerivationException;
	
	/**
	 * Exploration finished.
	 */
	public IStateSpace done(ISymbolGenerator generator, ArrayList<State> states) throws DerivationException;

}