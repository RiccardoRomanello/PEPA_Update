/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

public class Bound<T> {

	private T lowerBound;
	private T upperBound;
	
	public Bound(T lowerBound, T upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public T getLowerBound() {
		return lowerBound;
	}
	
	public T getUpperBound() {
		return upperBound;
	}
	
}
