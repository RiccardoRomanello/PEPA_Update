/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution.internal.simple;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;

public abstract class AbstractSolver implements ISolver {

	protected Generator generator;

	protected OptionMap options;

	protected int maxIteration;

	protected static final int REFRESH_RATE = 20;

	public AbstractSolver(Generator generator, OptionMap options) {
		this.generator = generator;
		this.options = options;
	}

	public final double[] solve(IProgressMonitor monitor)
			throws SolverException {
		if (monitor == null)
			monitor = new DoNothingMonitor();
		maxIteration = (Integer) options.get(OptionMap.SIMPLE_MAX_ITERATION);
		int ticks = maxIteration + generator.row.length;
		monitor.beginTask(ticks);
		double[] solution = transposeAndSolve(generator.row, generator.column,
				generator.value, generator.diagonal, monitor);
		monitor.done();
		return solution;
	}

	@SuppressWarnings(value = "unchecked")
	private final double[] transposeAndSolve(int[] row, int[] column,
			double[] value, double[] diagonal, IProgressMonitor monitor) throws SolverException {

		int[] tempRows = new int[row.length];
		for (int i = 0; i < tempRows.length; i++) {
			tempRows[i] = 0;
		}

		ArrayList<Integer>[] tempColumns = new ArrayList[row.length];
		for (int i = 0; i < row.length; i++)
			tempColumns[i] = new ArrayList<Integer>();

		ArrayList<Double>[] tempValues = new ArrayList[row.length];
		for (int i = 0; i < row.length; i++)
			tempValues[i] = new ArrayList<Double>();

		// set up last row of transpose matrix
		ArrayList<Double> lastValue = tempValues[row.length - 1];
		ArrayList<Integer> lastColumn = tempColumns[row.length - 1];
		for (int i = 0; i < row.length; i++) {
			lastValue.add(1.0);
			lastColumn.add(i);
		}
		tempRows[row.length - 1] = row.length;

		int nzc = 0, valuesPtr = 0;
		for (int i = 0; i < row.length; i++) {
			if (i % REFRESH_RATE == 0 && i != 0)
				monitor.worked(REFRESH_RATE);

			int start = row[i];
			int end = (i == row.length - 1) ? column.length : row[i + 1];
			for (int j = start; j < end; j++) {
				int index = column[j];
				if (index == row.length - 1) {
					valuesPtr++;
					continue; // last row, normalisation condition
				}

				tempRows[index]++;
				ArrayList<Integer> c = tempColumns[index];
				c.add(i);
				tempValues[index].add(value[valuesPtr]);
				nzc++;
				valuesPtr++;

			}
		}
		nzc += row.length;
		// transpose matrix row vector
		int[] trows = new int[row.length];
		// transpose matrix column vector
		int[] tcolumns = new int[nzc];
		// transpose matrix values
		double[] tvalues = new double[tcolumns.length];

		// create column vector
		for (int i = 0, c = 0; i < tempColumns.length; i++) {
			ArrayList<Integer> array = tempColumns[i];
			for (int v : array) {
				tcolumns[c++] = v;
			}
		}

		// create values vector
		for (int i = 0, c = 0; i < tempValues.length; i++) {
			ArrayList<Double> array = tempValues[i];
			for (double v : array) {
				tvalues[c++] = v;
			}
		}

		trows[0] = 0;
		for (int i = 1; i < tempRows.length; i++) {
			trows[i] = trows[i - 1] + tempRows[i - 1];
		}
		diagonal[diagonal.length - 1] = 1.0d; // norm
		/*
		
		  { System.err.println("trows"); for (int i : trows) {
		  System.err.println(i); }
		  
		  System.err.println("tcolumns"); for (int i : tcolumns) {
		  System.err.println(i); }
		  
		  System.err.println("tvalues"); for (double i : tvalues) {
		  System.err.println(i); } }
		
		System.err.println("diagonal"); 
		for (double i : diagonal) {
			  System.err.println(i); 
		}*/
		return doSolve(trows, tcolumns, tvalues, diagonal, monitor);
	}

	protected abstract double[] doSolve(int[] rows, int[] columns, double[] values,
			double[] diagonal, IProgressMonitor monitor) throws SolverException;
	
	protected static String createMaximumIterationsMessage(int iterations, double norm) {
		return "Maximum number of iterations (" + iterations + ") reached. Residual norm: " + norm;
	}
	
	protected static String createDivergenceDetected(int iterations, double norm) {
		return "Divergence detected at iteration " + iterations + ". Residual norm: " + norm;
	}

}
