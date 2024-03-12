/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

/**
 * 
 * @author ajduguid
 * 
 */
public class DataPoint {

	double average, max, min, confidence;

	public DataPoint() {
		average = Double.NaN;
		max = Double.NaN;
		min = Double.NaN;
		confidence = Double.NaN;
	}

	public double getAverage() {
		return average;
	}

	public double getConfidence() {
		return confidence;
	}

	public double getMaxValue() {
		return max;
	}

	public double getMinValue() {
		return min;
	}
}
