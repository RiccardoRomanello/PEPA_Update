/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.HashMap;

/**
 * 
 * @author ajduguid
 * 
 */
public class Mapping {
	public HashMap<String, String> labelled, unlabelled;
	public Mapping next, previous;
	public String originalRepresentation, cooperation;

	Mapping() {
		labelled = new HashMap<String, String>();
		unlabelled = new HashMap<String, String>();
		originalRepresentation = cooperation = null;
		next = previous = null;
	}

	public int length() {
		Mapping m = this;
		while (m.previous != null)
			m = m.previous;
		int count = 1;
		while (m.next != null) {
			count++;
			m = m.next;
		}
		return count;
	}

}
