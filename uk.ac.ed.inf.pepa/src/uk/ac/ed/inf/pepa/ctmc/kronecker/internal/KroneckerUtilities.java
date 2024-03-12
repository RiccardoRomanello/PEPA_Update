/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;

public class KroneckerUtilities {

	/**
	 * Returns the minimum of two rates, as per the PEPA semantics.
	 * A passive rate is given by r = -1.
	 */
	public static double rateMin(double r1, double r2) {
		if (r1 >= 0 && r2 >= 0) {
			// both rates are active
			return Math.min(r1, r2);	
		} else if (r1 < 0 && r2 >= 0) {
			// passive driven by active rate r2
			return r2;
		} else if (r1 >= 0 && r2 < 0) {
			// passive driven by active rate r1
			return r1;
		} else {
			// both passive, so should equal -1
			return -1;
		}
	}
	
	/**
	 * Adds two rates together - if both of the rates are passive,
	 * it returns -1. If we mix active and passive, it throws a
	 * derivation exception.
	 */
	public static double ratePlus(double r1, double r2) throws DerivationException {
		if (r1 >= 0 && r2 >= 0) {
			// both rates are active
			return r1 + r2;	
		} else if (r1 <= 0 && r2 <= 0) {
			// either both are passive (i.e. -1), or only one is (i.e. -1 and 0)
			// in both cases, we should sum the rates, e.g. for two passive actions
			// we need a rate of -2 to get the correct apparent rate!
			return r1 + r2;
		}
		throw new DerivationException("Mixing active and passive rates (" + r1 + " and " + r2 + ").");
	}
	
	/**
	 * Used by state derivators to iterate over a range of possible
	 * next states. For example, consider a max array of [2,2,3,2]. Then
	 * [0,1,1,1] -> [0,1,2,0] -> [0,1,2,1] -> [1,0,0,0] etc.
	 * 
	 * @param the current state
	 * @param the number of states in each array position.
	 */
	public static void incrementArray(int[] current, int[] max) {
		// increment the first value from the right that's less than the max
		int index = 0;
		for (int i = current.length - 1; i >= 0; i--) {
			if (current[i] < max[i] - 1) {
				current[i]++;
				index = i;
				break;
			}
		}
		for (int i = index + 1; i < current.length; i++) {
			current[i] = 0;
		}
	}
	
	/**
	 * Performs the equivalent of incrementArray for a Boolean array. If we encoded
	 * true = 1 and false = 0, this would be equivalent to
	 * incrementArray(current, [2,2,2,2,2...]). 
	 */
	public static void incrementBooleanArray(boolean[] current) {
		int index = 0;
		for (int i = current.length - 1; i >= 0; i--) {
			if (!current[i]) {
				current[i] = true;
				index = i;
				break;
			}
		}
		for (int i = index + 1; i < current.length; i++) {
			current[i] = false;
		}
	}
	
}
