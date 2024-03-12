/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ICSLModelChecker;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public interface IKroneckerStateSpace extends IStateSpace {

	public ICSLModelChecker getModelChecker(OptionMap optionMap, IProgressMonitor monitor, double boundAccuracy);
	
	public KroneckerDisplayModel getDisplayModel();
	
}
