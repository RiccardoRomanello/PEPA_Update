/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

import java.awt.Point;

public abstract class SequentialOrder {

	protected SequentialStateSpace stateSpace;
	
	protected SequentialOrder(SequentialStateSpace stateSpace) {
		this.stateSpace = stateSpace;
	}
	
	public abstract boolean isComparableIndex(int index);
	
	public abstract boolean isAnythingComparable();
	
	public abstract Point getPrevious(int index);
	
	public abstract Point getCurrent(int index);
	
	public abstract Point getNext(int index);
	
}
