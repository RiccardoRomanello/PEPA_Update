/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

/* Utility data type for counting definitions */
public abstract class Counter {
	/*
	 * How many definitions have been encountered. This value should be 1 to
	 * avoid multiple declarations
	 */
	int lhs = 0;

	/*
	 * How many times this object name as been used in rhs rate productions
	 */
	int rhs = 0;

}
