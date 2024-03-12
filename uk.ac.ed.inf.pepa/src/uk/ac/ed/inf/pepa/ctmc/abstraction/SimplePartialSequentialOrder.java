/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

import java.awt.Point;

public class SimplePartialSequentialOrder extends SequentialOrder {

	private Point upper;
	private Point lower;
	
	private boolean isAnythingComparable;
	
	public SimplePartialSequentialOrder(SequentialAbstraction abstraction, int propertySize) {
		super(abstraction.getConcreteStateSpace());
		int upperIndex = stateSpace.size();
		AbstractState[] abstractStates = abstraction.getAbstractStateSpace();
		for (int i = abstractStates.length - 1; i > abstractStates.length - 1 - propertySize; i--) {
			upperIndex -= abstractStates[i].size();
		}
		this.upper = new Point(upperIndex, stateSpace.size() - 1);
		this.lower = new Point(0, Math.max(0, upperIndex - 1));
		this.isAnythingComparable = upperIndex > 0;
	}
	
	public SimplePartialSequentialOrder(SequentialAbstraction abstraction) {
		super(abstraction.getConcreteStateSpace());
		this.upper = new Point(0, stateSpace.size() - 1);
		this.lower = new Point(-1, -1);
		this.isAnythingComparable = false;
	}

	@Override
	public boolean isComparableIndex(int index) {
		return index == upper.x || index == 0;
	}

	@Override
	public Point getNext(int index) {
		if (index >= lower.x && index <= lower.y) {
			return upper;
		} else {
			return null;
		}
	}

	@Override
	public Point getPrevious(int index) {
		if (index >= lower.x && index <= lower.y) {
			return null;
		} else {
			return lower;
		}
	}

	@Override
	public Point getCurrent(int index) {
		if (index >= lower.x && index <= lower.y) {
			return lower;
		} else if (index >= upper.x && index <= upper.y) {
			return upper;
		} else {
			return null;
		}
	}

	@Override
	public boolean isAnythingComparable() {
		return isAnythingComparable;
	}

}
