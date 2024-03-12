/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc;

/**
 * Throughput of action types.
 * 
 * @author mtribast
 *
 */
public class ThroughputResult {
	
	private String actionType;
	
	private double throughput;
	
	public ThroughputResult(String actionType, double throughput) {
		this.actionType = actionType;
		this.throughput = throughput;
	}
	
	/**
	 * Gets the action type.
	 * 
	 * @return
	 */
	public String getActionType() {
		return actionType;
	}
	
	/**
	 * Gets the throughput.
	 * @return
	 */
	public double getThroughput() {
		return throughput;
	}
}
