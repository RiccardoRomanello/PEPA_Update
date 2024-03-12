/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.recursive;

import uk.ac.ed.inf.pepa.model.Activity;
import uk.ac.ed.inf.pepa.model.Process;


/**
 * Represents an entry in the transition set of a state. 
 * Uses the same pattern as <code>java.util.Map.Entry</code>
 * 
 * @author mtribast
 *
 */
public class TransitionEntry {
	
	/** The activity */
	public Activity activity;
	/** The target process */
	public Process target;

	public TransitionEntry(Activity activity, Process target) {
		this.activity = activity;
		this.target = target;
	}

}
