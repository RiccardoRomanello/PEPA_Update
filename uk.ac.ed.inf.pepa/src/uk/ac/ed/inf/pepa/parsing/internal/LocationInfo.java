/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing.internal;

import uk.ac.ed.inf.pepa.parsing.ILocationInfo;

public class LocationInfo implements ILocationInfo {
	
	private int line;
	private int column;
	private int startChar;
	
	public LocationInfo(int line, int column, int startChar) {
		this.line = line;
		this.column = column;
		this.startChar = startChar;
	}
	public int getChar() {
		return startChar;
	}

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}

}
