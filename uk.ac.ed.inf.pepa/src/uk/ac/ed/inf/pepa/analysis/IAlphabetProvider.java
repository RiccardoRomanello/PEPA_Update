/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis;

import java.util.HashMap;
import java.util.HashSet;

public interface IAlphabetProvider {

	public HashMap<String, HashSet<String>> getProcessAlphabets();

	public HashMap<String, HashSet<String>> getActionAlphabets();
	
	public HashMap<String, HashSet<String>> getViewableActionAlphabets();
	
	public HashSet<String> getModelAlphabet();

}