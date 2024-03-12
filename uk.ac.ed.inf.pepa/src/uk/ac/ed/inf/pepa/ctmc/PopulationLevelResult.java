/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc;

/**
 * Represents the mean number of sequential components.
 * 
 * @author mtribast
 *
 */
public class PopulationLevelResult {
	
	private String fName;
	
	private double fMean;
	
	public PopulationLevelResult(String name, double mean) {
		this.fName = name;
		this.fMean = mean;
	}
	
	/**
	 * The name of the sequential component
	 * @return
	 */
	public String getName() { return fName; }
	
	/** 
	 * The mean.
	 * @return
	 */
	public double getMean() { return fMean; }
}
