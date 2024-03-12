/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

import java.awt.Point;

import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.Rate;

import no.uib.cipr.matrix.AbstractVector;
import no.uib.cipr.matrix.sparse.SparseVector;

public class ComponentRateContext {

	private AbstractVector lowerRates;
	private AbstractVector originalRates;
	private AbstractVector upperRates;
	
	private double lowerInternalBound;
	private double upperInternalBound;
	private double lowerComparativeBound;
	private double upperComparativeBound;
	
	public ComponentRateContext(AbstractVector lowerRates, AbstractVector originalRates, AbstractVector upperRates,
			                    double lowerInternalBound, double upperInternalBound,
			                    double lowerComparativeBound, double upperComparativeBound) {
		this.lowerRates = lowerRates;
		this.originalRates = originalRates;
		this.upperRates = upperRates;
		this.lowerInternalBound = lowerInternalBound;
		this.upperInternalBound = upperInternalBound;
		this.lowerComparativeBound = lowerComparativeBound;
		this.upperComparativeBound = upperComparativeBound;
	}
	
	public SparseVector getLowerRateVector() {
		assert lowerRates != null;
		return new SparseVector(lowerRates);
	}
	
	public SparseVector getUpperRateVector() {
		assert upperRates != null;
		return new SparseVector(upperRates);
	}
	
	public double getLowerRate(Point block) {
		if (lowerRates == null) return -1;
		double lowerRate = lowerRates.get(block.x);
		for (int i = block.x + 1; i <= block.y; i++) {
			lowerRate = Rate.min(lowerRate, lowerRates.get(i));
		}
		return lowerRate;
	}
	
	public double getUpperRate(Point block) {
		if (upperRates == null) return -1;
		double upperRate = upperRates.get(block.x);
		for (int i = block.x + 1; i <= block.y; i++) {
			upperRate = Rate.max(upperRate, upperRates.get(i));
		}
		return upperRate;
	}

	private double getLowerOriginalRate(Point block) {
		double lowerRate = originalRates.get(block.x);
		for (int i = block.x + 1; i <= block.y; i++) {
			lowerRate = Rate.min(lowerRate, originalRates.get(i));
		}
		return lowerRate;
	}
	
	private double getUpperOriginalRate(Point block) {
		double upperRate = originalRates.get(block.x);
		for (int i = block.x + 1; i <= block.y; i++) {
			upperRate = Rate.max(upperRate, originalRates.get(i));
		}
		return upperRate;
	}
	
	public double getUpperRatio(Point block) {
		if (originalRates == null) return 1;
		double lowerOriginalRate = getLowerOriginalRate(block);
		double upperRate = getUpperRate(block);
		assert Rate.max(lowerOriginalRate, upperRate) == upperRate;
		if (upperRate == 0) {
			return 0;
		} else {
			return lowerOriginalRate / upperRate;
		}
	}
	
	public double getLowerRatio(Point block) {
		if (originalRates == null) return 1;
		double upperOriginalRate = getUpperOriginalRate(block);
		double lowerRate = getLowerRate(block);
		assert Rate.max(lowerRate, upperOriginalRate) == upperOriginalRate;
		if (upperOriginalRate == 0) {
			return 0;
		} else {
			return lowerRate / upperOriginalRate;
		}
	}
	
	public double getLowerInternalBound() {
		return lowerInternalBound;
	}
	
	public double getUpperInternalBound() {
		return upperInternalBound;
	}
	
	public double getLowerComparativeBound() {
		return lowerComparativeBound;
	}
	
	public double getUpperComparativeBound() {
		return upperComparativeBound;
	}
	
}
