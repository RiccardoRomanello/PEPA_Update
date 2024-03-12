/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing.internal;

import uk.ac.ed.inf.pepa.parsing.ILocationInfo;
import java_cup.runtime.Symbol;

public class PepaSymbol extends Symbol {

	private ILocationInfo leftLocation;

	private ILocationInfo rightLocation;

	private String name;

	public PepaSymbol(int arg0, String name, ILocationInfo left,
			ILocationInfo right, Object value) {
		super(arg0, value);
		this.name = name;
		this.leftLocation = left;
		this.rightLocation = right;
	}
	
	public PepaSymbol(String name, int id, int state, ILocationInfo left,
			ILocationInfo right) {
		super(id, null);
		this.parse_state = state;
		this.name = null;
		this.leftLocation = left;
		this.leftLocation = right;
	}

	public ILocationInfo getLeftLocation() {
		return leftLocation;
	}

	public ILocationInfo getRightLocation() {
		return rightLocation;
	}

	public String getName() {
		return name;
	}

}
