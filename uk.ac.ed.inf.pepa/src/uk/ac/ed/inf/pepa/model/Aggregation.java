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
package uk.ac.ed.inf.pepa.model;

import java.util.Map;

/**
 * Representation of an aggregation of components for which canonical
 * representation is requested. It codifies processes such:
 * 
 * <pre>
 * P[1000]
 * </pre>
 * 
 * where
 * 
 * <pre>
 *   P = (a,1).P1;
 *   ...
 * </pre>
 * 
 * The transitions from an aggregated process are aggregations as well. With
 * regards to the above example, a possible transition might be:
 * 
 * <pre>
 * P1 || P[999]
 * </pre>
 * 
 * This is represented in one <code>Aggregation</code> which takes into
 * account that P1 has actually been derived from an original aggregation.
 * 
 * @author mtribast
 * 
 */
public interface Aggregation extends Process {
	
	
	/**
	 * Get an unmodifiable map of the subprocesses of this aggregation.
	 * 
	 * @return a map of subprocesses to the number of copies
	 */
	public Map<Process, Integer> getSubProcesses();

	/**
	 * Gets the total number of components in this aggregation
	 * 
	 * @return the total number of components in this aggregation
	 */
	public int getCopies();


}