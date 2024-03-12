/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

import java.awt.Point;

import no.uib.cipr.matrix.sparse.SparseVector;

public class LocalComponentRateContext extends ComponentRateContext {

	int size;
	double uniformisationConstant;
	SparseVector rateVector;
	
	public LocalComponentRateContext(int size, double uniformisationConstant) {
		super(null, null, null, 1, 1, 1, 1);
		this.size = size;
		this.uniformisationConstant = uniformisationConstant;
	}
	
	private void initRateVector() {
		rateVector = new SparseVector(size);
		for (int i = 0; i < size; i++) {
			rateVector.set(i, uniformisationConstant);
		}
	}
	
	public SparseVector getLowerRateVector() {
		if (rateVector == null) initRateVector();
		return rateVector;
	}
	
	public SparseVector getUpperRateVector() {
		if (rateVector == null) initRateVector();
		return rateVector;
	}
	
	public double getLowerRate(Point block) {
		return uniformisationConstant;
	}
	
	public double getUpperRate(Point block) {
		return uniformisationConstant;
	}
	
	public double getUpperRatio(Point block) {
		return 1;
	}
	
	public double getLowerRatio(Point block) {
		return 1;
	}
	
	
}
