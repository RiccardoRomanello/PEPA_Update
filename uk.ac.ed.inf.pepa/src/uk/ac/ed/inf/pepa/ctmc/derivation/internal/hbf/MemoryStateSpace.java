/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DoubleArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.Generator;
import uk.ac.ed.inf.pepa.model.ActionLevel;
import uk.ac.ed.inf.pepa.model.NamedAction;

/**
 * @author mtribast
 * 
 */
public class MemoryStateSpace extends AbstractStateSpace {
	
	private IntegerArray row;

	private IntegerArray column;

	private DoubleArray value;

	private ShortArray action;

	public MemoryStateSpace(ISymbolGenerator symbolGenerator,
			ArrayList<State> states, IntegerArray row, IntegerArray column,
			ShortArray action, DoubleArray value,
			boolean hasVariableLengthStates, int maximumLength) {
		super(symbolGenerator, states,hasVariableLengthStates, maximumLength);
		assert row != null;
		assert column != null;
		assert value != null;
		assert action != null;
		assert value.size() == action.size();
		this.row = row;
		this.column = column;
		this.value = value;
		this.action = action;
	}
	
	protected FlexCompRowMatrix createGeneratorMatrix() {
		FlexCompRowMatrix genMatrix = null;
		int rowSize = row.size();
		//System.err.println("Row size of the matrix: " + rowSize);
		//System.err.println("col size of the matrix: " + column.size());
		try {
			genMatrix = new FlexCompRowMatrix(rowSize, rowSize);
			for (int i = 0; i < rowSize; i++) {
				//System.err.println("i: " + i);
				int rangeStart = getColumnRangeStart(i);// row.get(i);
				int rangeEnd = getColumnRangeEnd(i);// (i < rowSize - 1) ? row.get(i
				// + 1) : column.size();
				//System.err.println("Range start " + rangeStart);
				//System.err.println("Range end " + rangeEnd);
				for (int j = rangeStart; j < rangeEnd; j = j + 2) {
					// each j represents the index of column
					//System.err.println("J: " + j);
					int colVal = column.get(j);
					//System.err.println("Colval " + colVal);
					int colRangeStart = column.get(j + 1);
					//System.err.println("Colrangestart " + colRangeStart);
					int colRangeEnd = (j < column.size() - 3) ? column.get(j + 3)
							: value.size();
					//System.err.println("Colrangeend " + colRangeEnd);
					double sum = 0;
					for (int k = colRangeStart; k < colRangeEnd; k++) {
						sum += value.get(k);
					}
					if (sum != 0) {
						genMatrix.set(i, colVal, sum);
						// System.out.println("Prepared " + i + "," + colVal + "->"
						// + sum);
					}
				}
			}
		} catch (Throwable t ) {
			t.printStackTrace();
			
			
		}

		return genMatrix;

	}

	protected Generator createSimpleGenerator() {
		int[] mrow = new int[row.size()];
		double[] diag = new double[row.size()];
		int[] matrixCols = new int[column.size() / 2];
		double[] values = new double[matrixCols.length];

		int rowSize = row.size();
		int colCounter = 0;
		mrow[0] = 0;
		double sum = 0;
		double diagsum = 0;
		for (int i = 0; i < rowSize; i++) {
			mrow[i] = colCounter;
			diagsum = 0;
			int rangeStart = getColumnRangeStart(i);
			int rangeEnd = getColumnRangeEnd(i);
			for (int j = rangeStart; j < rangeEnd; j = j + 2) {
				int colVal = column.get(j);
				int colRangeStart = column.get(j + 1);
				int colRangeEnd = (j < column.size() - 3) ? column.get(j + 3)
						: value.size();
				sum = 0;
				for (int k = colRangeStart; k < colRangeEnd; k++) {
					sum += value.get(k);
				}
				if (sum != 0) {
					matrixCols[colCounter] = colVal;
					values[colCounter++] = sum;
					diagsum += sum;
				}
			}
			diag[i] = -diagsum;

		}
		Generator generator = new Generator(mrow, matrixCols, values, diag);
		return generator;
	}

	private int getColumnRangeStart(int row) {
		return this.row.get(row);
	}

	private int getColumnRangeEnd(int row) {
		return (row < this.row.size() - 1) ? this.row.get(row + 1) : column.size();
	}

	private int getValueRangeEnd(int j) {
		return (j < column.size() - 3) ? column.get(j + 3) : value.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#doThroughput(uk.ac.ed.inf.pepa.IProgressMonitor)
	 */
	protected void doThroughput(IProgressMonitor monitor) {
		throughput = EMPTY_THROUGHPUT;
		if (monitor == null)
			monitor = new DoNothingMonitor();
		if (solution == null) {
			monitor.done();
			return;
		}
		double prob;
		int valueSize = value.size();
		HashMap<Short, Double> thMap = new HashMap<Short, Double>();
		for (int i = 0, size = size(); i < size; i++) {
			prob = solution[i];
			int start = row.get(i);
			int valueStart = column.get(++start);
			int valueEnd = (i == size - 1) ? valueSize : column.get(row
					.get(i + 1) + 1);
			for (int k = valueStart; k < valueEnd; k++) {
				short actionId = action.get(k);
				Double d = thMap.get(actionId);
				if (d == null) {
					d = 0.0d;
				}
				d += prob * value.get(k);
				thMap.put(actionId, d);
			}
		}
		ThroughputResult[] result = new ThroughputResult[thMap.size()];
		int i = 0;
		for (Map.Entry<Short, Double> entry : thMap.entrySet()) {
			ThroughputResult r = new ThroughputResult(symbolGenerator
					.getActionLabel(entry.getKey()), entry.getValue());
			result[i++] = r;
		}
		this.throughput = result;
		monitor.done();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#getAction(int,
	 *      int)
	 */
	public NamedAction[] getAction(int source, int target) {
		LinkedList<NamedAction> actions = new LinkedList<NamedAction>();
		int c1 = row.get(source);
		int c2;
		if (source == row.size() - 1) {
			c2 = column.size();
		} else {
			c2 = row.get(source + 1);
		}
		boolean found = false;
		for (int i = c1; i < c2; i += 2) {
			if (column.get(i) == target) {
				found = true;
				int start = column.get(i + 1);
				int stop = (i + 3 <= column.size() - 1) ? column.get(i + 3)
						: action.size();
				for (int j = start; j < stop; j++) {
					actions.add(symbolGenerator.getAction(action.get(j)));
				}
			}
			if (found)
				break;
		}
		return actions.toArray(new NamedAction[actions.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#getIncomingStateIndices(int)
	 */
	@Override
	public int[] getIncomingStateIndices(int stateIndex) {
		// Guess based on average number of transitions
		IntegerArray result = new IntegerArray(20);
		for (int j = 0; j < column.size(); j += 2) {
			int columnNumber = column.get(j);
			if (columnNumber == stateIndex)
				result.add(find(0, row.size() - 1, j));
		}
		return result.toArray();
	}

	private int find(int rangeStart, int rangeEnd, int index) {
		int i = rangeStart + ((rangeEnd - rangeStart) >> 1);
		int cstart = getColumnRangeStart(i);
		int cend = getColumnRangeEnd(i);
		if (index < cend && index >= cstart)
			return i;
		else if (index >= cend)
			return find(i + 1, rangeEnd, index);
		else
			return find(rangeStart, i - 1, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#getOutgoingStateIndices(int)
	 */
	@Override
	public int[] getOutgoingStateIndices(int stateIndex) {
		int columnStart = getColumnRangeStart(stateIndex);
		int columnEnd = getColumnRangeEnd(stateIndex);
		int[] result = new int[(columnEnd - columnStart) >> 1];
		int j = 0;
		int c = 0;
		for (j = columnStart; j < columnEnd; j += 2) {
			result[c++] = column.get(j);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#getRate(int,
	 *      int)
	 */
	@Override
	public double getRate(int source, int target) {
		int columnStart = getColumnRangeStart(source);
		int columnEnd = getColumnRangeEnd(source);
		double sum = 0;
		for (int j = columnStart; j < columnEnd; j += 2) {
			if (this.column.get(j) == target) {
				int valueStart = this.column.get(j + 1);
				int valueEnd = getValueRangeEnd(j);
				for (int k = valueStart; k < valueEnd; k++)
					sum += value.get(k);
			}
		}
		return sum;
	}

	public void dispose() {
	}

}
