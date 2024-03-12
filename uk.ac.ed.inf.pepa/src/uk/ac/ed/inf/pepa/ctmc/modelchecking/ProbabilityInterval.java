/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

import java.text.DecimalFormat;
import java.util.Arrays;

public class ProbabilityInterval {

	private double lowerProbability;
	private double upperProbability;
	
	public ProbabilityInterval(double lowerProbability, double upperProbability) {
		this.lowerProbability = lowerProbability;
		this.upperProbability = upperProbability;
	}
	
	public double getLower() {
		return lowerProbability;
	}
	
	public double getUpper() {
		return upperProbability;
	}
	
	public String toString() {
		return "[" + lowerProbability + "," + upperProbability + "]";
	}
	
	public String toString(int dp) {
		char[] zeroes = new char[dp];
		Arrays.fill(zeroes, '#');
		String trail = new String(zeroes);
		DecimalFormat format = new DecimalFormat("0." + trail);
		format.setGroupingUsed(false);
		String s = "[" + format.format(lowerProbability) + ","
		               + format.format(upperProbability) + "]";
		return s;
	}
	
}
