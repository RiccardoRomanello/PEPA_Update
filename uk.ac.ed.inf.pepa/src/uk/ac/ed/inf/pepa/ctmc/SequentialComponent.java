/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc;

/**
 * Results from utilisation analysis about a sequential component. The component
 * has a name, which is typically the name that appears in the system equation,
 * and a number of local states ({@link #getLocalStates()}
 * 
 * @author mtribast
 * 
 */
public class SequentialComponent extends UtilisationResult {

	protected LocalState[] fStates;

	public SequentialComponent(String name, LocalState[] states) {
		super(name);
		this.fStates = states;
		for (LocalState state : fStates) {
			state.setParent(this);
		}
	}

	public LocalState[] getLocalStates() {
		return fStates;
	}

}
