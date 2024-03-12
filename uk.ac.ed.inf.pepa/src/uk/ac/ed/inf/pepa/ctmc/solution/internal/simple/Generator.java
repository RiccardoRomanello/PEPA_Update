/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution.internal.simple;

/**
 * A simple generator matrix.
 * 
 * @author mtribast
 * 
 */
public class Generator {

	public int[] row;

	public int[] column;

	public double[] value;

	public double[] diagonal;

	public Generator(int[] row, int[] column, double[] value, double[] diagonal) {
		if (row == null || column == null || value == null || diagonal == null)
			throw new NullPointerException();
		if (diagonal.length != row.length)
			throw new IllegalArgumentException();
		if (value.length != column.length)
			throw new IllegalArgumentException();
		this.row = row;
		this.column = column;
		this.value = value;
		this.diagonal = diagonal;

	}

}
