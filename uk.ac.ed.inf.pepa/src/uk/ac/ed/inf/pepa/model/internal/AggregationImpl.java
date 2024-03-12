/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 16-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model.internal;

import java.util.*;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.*;

/**
 * @author mtribast
 * 
 */
public class AggregationImpl implements Aggregation {

	private HashMap<Process, Integer> subProcesses = new HashMap<Process, Integer>();

	/* Total number of copies */
	private int copies = 0;
	
	/**
	 * Add a number of copies of a distinct top-level component to this
	 * aggregation
	 * 
	 * @param process
	 *            the new top-level components
	 * @param copies
	 *            its number of copies
	 * @throws IllegalStateException
	 *             if the process is already contained or if <code>copies</code>
	 *             is less than one
	 */
	public void add(Process process, int copies) {
		if (process == null || subProcesses.containsKey(process))
			throw new IllegalStateException(
					"SubProcess null or already present");
		if (copies == 0)
			throw new IllegalStateException("Number of copies null");
		subProcesses.put(process, copies);
		this.copies += copies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Aggregation#getCopies()
	 */
	public int getCopies() {
		return copies;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Aggregation#getSubProcesses()
	 */
	public Map<Process, Integer> getSubProcesses() {
		return Collections.unmodifiableMap(subProcesses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Process#accept(uk.ac.ed.inf.pepa.Visitor)
	 */
	public void accept(Visitor v) {
		v.visitAggregation(this);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Aggregation))
			return false;
		Map<Process, Integer> otherMap = ((Aggregation) o).getSubProcesses();
		for (Map.Entry<Process, Integer> entry : subProcesses.entrySet()) {
			if (otherMap.containsKey(entry.getKey())
					&& otherMap.get(entry.getKey()) == entry.getValue())
				continue;
			else
				return false;
		}
		return true;
	}

	public int hashCode() {
//		int hashCode = this.set.hashCode();
		int hashCode = 0;
		for (Map.Entry<Process, Integer> entry : subProcesses.entrySet()) {
			hashCode += entry.getKey().hashCode() + entry.getValue().hashCode();
		}
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Process#prettyPrint()
	 */
	public String prettyPrint() {
		StringBuffer buf = new StringBuffer();
		HashMap<String, Process> names = new HashMap<String, Process>();
		List<String> toBeSorted = new ArrayList<String>();
		for (Process p : subProcesses.keySet()) {
			String s = p.prettyPrint();
			names.put(s, p);
			toBeSorted.add(s);
		}
		Collections.sort(toBeSorted);
		for (String name : toBeSorted) {
			int copies = subProcesses.get(names.get(name));
			buf.append(name + ((copies > 1) ? "[" + copies + "]" : "") + ", ");
		}
		buf.deleteCharAt(buf.length() - 1);
		buf.deleteCharAt(buf.length() - 1);
		return buf.toString();
	}
	
	/**
	 * Decreases the replication of the given subprocess in the
	 * aggregation by the given number of copies
	 * @param process
	 * @param copies
	 */
	public void decreaseCopies(Process process, int copies) {
		if (copies < 0)
			throw new IllegalStateException("Non negative value");

		if (!subProcesses.containsKey(process)) {
			throw new IllegalStateException("No such process in aggregation "
					+ process.prettyPrint());
		}
		Integer newValue = subProcesses.get(process) - copies;
		if (newValue < 0)
			throw new IllegalStateException("Negative number of copies");
		if (newValue == 0)
			subProcesses.remove(process);
		else
			subProcesses.put(process, newValue);

	}
	
	/**
	 * Increase the number of copies. If no subprocess is already existing,
	 * it is created
	 * @param process
	 * @param copies
	 */
	public void increaseCopies(Process process, int copies) {
		if (copies < 0)
			throw new IllegalStateException("Non negative value");

		if (!subProcesses.containsKey(process)) {
			add(process, copies);
		} else {
			Integer newValue = subProcesses.get(process) + copies;
			subProcesses.put(process, newValue);
		}
	}

}