/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

import java.awt.Point;

public class TotalSequentialOrder extends SequentialOrder {

	public TotalSequentialOrder(SequentialStateSpace stateSpace) {
		super(stateSpace);
	}

	@Override
	public boolean isComparableIndex(int index) {
		return true;
	}

	@Override
	public Point getCurrent(int index) {
		return new Point(index,index);
	}

	@Override
	public Point getNext(int index) {
		if (index >= stateSpace.size()) {
			return null;
		} else {
			return new Point(index + 1, index + 1);
		} 
	}

	@Override
	public Point getPrevious(int index) {
		if (index <= 0) {
			return null;
		} else {
			return new Point(index - 1, index - 1);
		} 
	}
	
	@Override
	public boolean isAnythingComparable() {
		return true;
	}

}
