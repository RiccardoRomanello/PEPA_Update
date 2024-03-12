/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

/**
 * Provides static methods for doing maths in the PEPA style.
 * <p>
 * PEPA has the notion of passive rates in the form <code>wT</code> where
 * <code>w</code> is the weight and <code>T</code> is a passive rate. An
 * active rate must be represented with its value, which is a positive double.
 * Negative doubles are considered as passive rates whose weight is the double
 * itself.
 * 
 * @author mtribast
 * 
 */
public class OptimisedRateMath {

	public static boolean areSameType(double rate1, double rate2) {
		return !(rate1 * rate2 < 0);
	}
	
	public static double min(double d1, double d2) {
		if (d1 > 0)
			if (d2 > 0)
				return Math.min(d1, d2);
			else
				return d1;
		else if (d2 > 0)
			return d2;
		else
			return Math.max(d1, d2);
	}
	
	private static double old_mult(double d1, double d2) {
		if (d1 > 0)
			if (d2 > 0)
				return d1 * d2;
			else {
				return -d1 ;
			}
		else
			return d2 * d1;	
	}

}
