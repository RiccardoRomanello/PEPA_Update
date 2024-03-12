/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

public class Rate {

	public static double max(double rate1, double rate2) {
		if (rate1 < 0 || rate2 < 0) {
			assert rate1 == -1 || rate2 == -1;
			return -1;
		} else {
			return Math.max(rate1, rate2);
		}
	}
	
	public static double min(double rate1, double rate2) {
		if (rate1 < 0 || rate2 < 0) {
			assert rate1 == -1 || rate2 == -1;
			return Math.max(rate1, rate2);
		} else {
			return Math.min(rate1, rate2);
		}
	}
	
}
