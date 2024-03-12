/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.Constant;

public class DeadCode {
	
	public Constant process;
	
	public List<Action> actions = new ArrayList<Action>();

}
