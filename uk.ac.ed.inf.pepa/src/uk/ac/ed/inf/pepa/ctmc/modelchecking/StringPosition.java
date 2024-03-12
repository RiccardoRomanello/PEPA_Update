/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public class StringPosition {

	private int startIndex;
	private int endIndex;
	private CSLAbstractProperty object;
	
	public StringPosition(int startIndex, int endIndex, CSLAbstractProperty object) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.object = object;
	}
	
	public int getStart() {
		return startIndex;
	}
	
	public int getEnd() {
		return endIndex;
	}
	
	public void incrementEnd(int offset) {
		endIndex = Math.max(startIndex, endIndex + offset);
	}
	
	public StringPosition addOffset(int offset) {
		startIndex += offset;
		endIndex += offset;
		return this;
	}
	
	public CSLAbstractProperty getObject() {
		return object;
	}
	
}
