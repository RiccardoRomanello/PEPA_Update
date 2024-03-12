/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;

/**
 * Naive method of computing Poisson probabilities
 * 
 * @deprecated Use FoxGlynn instead!
 * @author msmith
 */
public class PoissonDistribution {

	/**
	 * Rate
	 */
	private double lambda_t;
	
	/**
	 * Bound accuracy
	 */
	private double epsilon;
	
	private int truncation; // truncation point k
	private ArrayList<Double> cache;
	
	public PoissonDistribution(double lambda_t, double epsilon) {
		this.lambda_t = lambda_t;
		this.epsilon = epsilon;
		initialise();
	}
	
	public int getTruncationPoint() {
		return truncation;
	}
	
	private void initialise() {
		cache = new ArrayList<Double>(100);
		double psi = Math.exp(-lambda_t);
		double sum = psi;
		cache.add(psi);
		truncation = 0;
		while (sum < 1 - epsilon || truncation % 2 == 1) {
			truncation++;
			psi *= lambda_t;
			psi /= truncation;
			sum += psi;
			cache.add(psi);
		}
	}
	
	public double psi(int n) {
		assert n >= 0;
		if (n > truncation) {
			return 0;
		} else {
			return cache.get(n);
		}
	}
	
}
