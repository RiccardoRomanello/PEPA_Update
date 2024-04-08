/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.Generator;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.internal.NamedActionImpl;

public class DiskBasedStateSpace extends AbstractStateSpace {

	private static final int BYTES_TO_SKIP = 4;

	private static final int BUFFER_SIZE = 4096 * 4;

	private DiskIntegerArray row;

	private DiskIntegerArray column;

	private DiskDoubleArray value;

	private DiskShortArray action;

	private String actionPath;

	private String valuePath;

	private String columnPath;

	private String rowPath;

	private int t_count;

	private IResourceManager manager;

	private static class Range {
		int start;
		int end;
	}

	protected DiskBasedStateSpace(ISymbolGenerator symbolGenerator,
			ArrayList<State> states, IResourceManager manager, String rowPath,
			String columnPath, String actionPath, String valuePath, int t_count,
			boolean hasVariableLengthStates, int maximumLength)
			throws IOException {
		super(symbolGenerator, states, hasVariableLengthStates,maximumLength);
		this.rowPath = rowPath;
		this.columnPath = columnPath;
		this.actionPath = actionPath;
		this.valuePath = valuePath;
		this.manager = manager;
		this.t_count = t_count;
		row = new DiskIntegerArray(rowPath);
		column = new DiskIntegerArray(columnPath);
		action = new DiskShortArray(actionPath);
		value = new DiskDoubleArray(valuePath);
	}

	/*
	 * Does one seek instead of two when using rangestart and range end
	 */
	private Range getColumnRange(int row) throws IOException {
		Range range = new Range();
		RandomAccessFile f = this.row.getFile();
		f.seek(row << DiskIntegerArray.BYTE_SIZE);
		range.start = f.readInt();
		range.end = (row < this.row.size() - 1) ? f.readInt() : column.size();
		return range;
	}

	private int getValueRangeEnd(int j) throws IOException {

		return (j < column.size() - 3) ? column.get(j + 3) : value.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#doThroughput(uk.ac.ed.inf.pepa.IProgressMonitor)
	 */
	@Override
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
		DataInputStream rowFile = null;
		DataInputStream actionFile = null;
		DataInputStream valueFile = null;
		DataInputStream columnFile = null;
		try {
			// Row data is accessed sequentially
			rowFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(rowPath), BUFFER_SIZE));
			actionFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(actionPath), BUFFER_SIZE));
			valueFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(valuePath), BUFFER_SIZE));
			columnFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(columnPath), BUFFER_SIZE));

			int size = size();
			int valueStart = 0, valueEnd = 0;
			int start = 0, nextStart = 0;
			for (int i = 0; i < size; i++) {
				prob = solution[i];
				if (i == 0) {
					start = rowFile.readInt();
					columnFile.skipBytes(4);
					valueStart = columnFile.readInt();
				} else {
					start = nextStart;
					valueStart = valueEnd;
				}
				if (i == size - 1) {
					valueEnd = valueSize;
				} else {
					nextStart = rowFile.readInt();
					columnFile.skipBytes(4 * (nextStart - start - 1));
					valueEnd = columnFile.readInt();
				}
				int length = valueEnd - valueStart;
				for (int k = 0; k < length; k++) {
					short actionId = actionFile.readShort();
					Double d = thMap.get(actionId);
					if (d == null) {
						d = 0.0d;
					}
					d += prob * valueFile.readDouble();
					thMap.put(actionId, d);
				}
			}

		} catch (IOException e) {

			throw new IllegalStateException(e);

		} finally {
			try {
				if (rowFile != null)
					rowFile.close();
				if (actionFile != null)
					actionFile.close();
				if (valueFile != null)
					valueFile.close();
				if (columnFile != null)
					columnFile.close();
			} catch (IOException e) {
				rowFile = null;
				actionFile = null;
				valueFile = null;
				columnFile = null;
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
	@Override
	public NamedAction[] getAction(int source, int target) {
		LinkedList<NamedAction> actions = new LinkedList<NamedAction>();

		try {
			int c1 = row.get(source);
			int c2;
			if (source == row.size() - 1) {
				c2 = column.size();
			} else {
				c2 = row.get(source + 1);
			}
			boolean found = false;
			for (int i = c1; !found && i < c2; i = i + 2) {
				if (column.get(i) == target) {
					found = true;
					int start = column.get(i + 1);
					int stop = (i + 3 <= column.size() - 1) ? column.get(i + 3)
							: action.size();
					int diff = stop - start;
					short[] values = new short[diff];
					action.getBulk(start, stop, values);
					for (int j = 0; j < diff; j++) {
						actions.add(symbolGenerator.getAction(values[j]));
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
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
		IntegerArray result = new IntegerArray(t_count / size());
		RandomAccessFile columnFile = column.getFile();
		try {
			columnFile.seek(0);
			for (int j = 0, size = column.size(); j < size; j += 2) {
				int columnNumber = columnFile.readInt();
				if (j != size - 2)
					columnFile.skipBytes(BYTES_TO_SKIP);
				if (columnNumber == stateIndex) {
					result.add(find(0, row.size() - 1, j));
				}
			}
		} catch (IOException e) {

			throw new IllegalStateException(e);
		}
		return result.toArray();

	}

	private int find(int rangeStart, int rangeEnd, int index)
			throws IOException {
		// for a small array it should be better to perform
		// a linear search?
		if (rangeEnd - rangeStart < 32)
			return performLinear(rangeStart, rangeEnd, index);

		int i = rangeStart + ((rangeEnd - rangeStart) >> 1);
		Range range = getColumnRange(i);
		if (index < range.end && index >= range.start)
			return i;
		else if (index >= range.end)
			return find(i + 1, rangeEnd, index);
		else
			return find(rangeStart, i - 1, index);
	}

	private int performLinear(int rangeStart, int rangeEnd, int index)
			throws IOException {
		RandomAccessFile is = row.getFile();
		is.seek(rangeStart << 2);
		int rowValue = 0, nextRow = 0;
		for (int i = rangeStart; i < rangeEnd; i++) {
			rowValue = (i == 0) ? is.readInt() : nextRow;
			nextRow = (i != rangeEnd - 1) ? is.readInt() : column.size();
			if (index < nextRow && index >= rowValue) {
				return i;
			}
		}
		throw new IllegalStateException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.AbstractStateSpace#getOutgoingStateIndices(int)
	 */
	@Override
	public int[] getOutgoingStateIndices(int stateIndex) {
		int[] result;
		RandomAccessFile columnFile = column.getFile();

		try {
			Range r = getColumnRange(stateIndex);
			result = new int[(r.end - r.start) >> 1];
			columnFile.seek(r.start << DiskIntegerArray.BYTE_SIZE);
			for (int j = 0; j < result.length; j++) {
				result[j] = columnFile.readInt();
				if (j != result.length - 1)
					columnFile.skipBytes(BYTES_TO_SKIP);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
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
		double sum = 0;
		try {
			Range range = getColumnRange(source);
			for (int j = range.start; j < range.end; j += 2) {
				if (this.column.get(j) == target) {
					int valueStart = this.column.get(j + 1);
					int valueEnd = getValueRangeEnd(j);
					// TODO Test this implementation
					int valueDiff = valueEnd - valueStart;
					double[] values = new double[valueDiff];
					value.getBulk(valueStart, valueEnd, values);
					for (int k = 0; k < valueDiff; k++)
						sum += values[k];
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return sum;

	}

	protected FlexCompRowMatrix createGeneratorMatrix() {
		DataInputStream rowFile = null;
		DataInputStream valueFile = null;
		DataInputStream columnFile = null;
		FlexCompRowMatrix genMatrix;
		try {
			// Row data is accessed sequentially
			rowFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(rowPath), BUFFER_SIZE));
			columnFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(columnPath), BUFFER_SIZE));
			valueFile = new DataInputStream(new BufferedInputStream(
					new FileInputStream(valuePath), BUFFER_SIZE));
			int size = size();
			genMatrix = new FlexCompRowMatrix(size, size);
			int valueSize = value.size();
			int valueStart = 0, valueEnd = 0;
			int column = 0, nextColumn = 0;
			double sum = 0;
			int length;

			int columnRangeStart = 0;
			int columnRangeEnd = rowFile.readInt();
			nextColumn = columnFile.readInt();
			valueEnd = columnFile.readInt();
			for (int i = 0; i < size; i++) {
				columnRangeStart = columnRangeEnd;
				columnRangeEnd = (i == size - 1) ? this.column.size() : rowFile
						.readInt();
				
				for (int j = 0, diff = columnRangeEnd - columnRangeStart; j < diff; j += 2) {
					column = nextColumn;
					valueStart = valueEnd;
					if (i == size - 1 && j == diff - 2) // last row and last
						// column
						valueEnd = valueSize;
					else {
						nextColumn = columnFile.readInt();
						valueEnd = columnFile.readInt();
					}
					length = valueEnd - valueStart;
					sum = 0;
					for (int k = 0; k < length; k++)
						sum += valueFile.readDouble();
					genMatrix.set(i, column, sum);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);

		} finally {
			try {
				if (rowFile != null)
					rowFile.close();
				if (valueFile != null)
					valueFile.close();
				if (columnFile != null)
					columnFile.close();
			} catch (Exception e) {
				rowFile = null;
				valueFile = null;
				columnFile = null;
			}
		}
		return genMatrix;

	}

	@Override
	protected Generator createSimpleGenerator() {
		return null;
	}

	public void dispose() {
		manager.releasePath(this);
	}

}
