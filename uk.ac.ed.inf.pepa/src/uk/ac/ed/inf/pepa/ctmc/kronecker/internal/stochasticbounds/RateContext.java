/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialOrder;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.Rate;
import no.uib.cipr.matrix.sparse.SparseVector;

/**
 * Stores and constructs upper and lower bounds of the rate matrices of a set of components (that synchronise
 * over a shared action type). As we add components to the context, it calculates the internal bound and
 * comparative bound over the context (i.e. all the other components that we potentially synchronise with).
 * 
 * @author msmith
 */
public class RateContext {

	/**
	 * Upper and lower bounding rate vectors for each component
	 */
	private ArrayList<SparseVector> upperRates;
	private ArrayList<SparseVector> originalRates;
	private ArrayList<SparseVector> lowerRates;
	
	private ArrayList<Double> lowerInternalRateMeasures;
	private ArrayList<Double> upperInternalRateMeasures;
	private ArrayList<Double> lowerComparativeRateMeasures;
	private ArrayList<Double> upperComparativeRateMeasures;
	
	private int numComponents;
	
	private RateContext(int numComponents) {
		this.numComponents = numComponents;
		upperRates    = new ArrayList<SparseVector>(numComponents);
		originalRates = new ArrayList<SparseVector>(numComponents);
		lowerRates    = new ArrayList<SparseVector>(numComponents);
		lowerInternalRateMeasures    = new ArrayList<Double>(numComponents);
		upperInternalRateMeasures    = new ArrayList<Double>(numComponents);
		lowerComparativeRateMeasures = new ArrayList<Double>(numComponents);
		upperComparativeRateMeasures = new ArrayList<Double>(numComponents);
	}
	
	public static RateContext[] makeNewContext(int numComponents, int numSyncActions) {
		RateContext[] context = new RateContext[numSyncActions];
		for (int i = 0; i < numSyncActions; i++) {
			context[i] = new RateContext(numComponents);
		}
		return context;
	}
	
	private double getMaxRateBlock(SparseVector rateVector, int start, int end) {
		double max = 0;
		for (int i = start; i <= end; i++) {
			double current = rateVector.get(i);
			max = Rate.max(max, current);
		}
		return max;
	}
	
	private double getMinRateBlock(SparseVector rateVector, int start, int end) {
		double min = -1;
		for (int i = start; i <= end; i++) {
			double current = rateVector.get(i);
			min = Rate.min(min, current);
		}
		return min;
	}
	
	/**
	 * Upper bound of the rate vector - must be monotone with respect to the ordering, and
	 * greater than the original elements of the vector.
	 */
	private SparseVector upperBoundRateVector(SparseVector rateVector, SequentialOrder order) {
		SparseVector upperBound = new SparseVector(rateVector.size());
		double previous_maximum = 0;
		double current_maximum = 0;
		for (int i = 0; i < rateVector.size(); i++) {
			if (order.isComparableIndex(i)) {
				previous_maximum = current_maximum;
			}
			double newRate = Rate.max(previous_maximum, rateVector.get(i));
			current_maximum = Rate.max(current_maximum, newRate);
			upperBound.set(i, newRate);
		}
		return upperBound;
	}
	
	/**
	 * Lower bound of the rate vector - must be monotone with respect to the ordering, and
	 * greater than the original elements of the vector.
	 */
	private SparseVector lowerBoundRateVector(SparseVector rateVector, SequentialOrder order) {
		SparseVector lowerBound = new SparseVector(rateVector.size());
		double previous_minimum = -1;
		double current_minimum = -1;
		// Trying this out - decreasing rate function
		for (int i = 0; i < rateVector.size(); i++) {
			if (order.isComparableIndex(i)) {
				previous_minimum = current_minimum;
			}
			double newRate = Rate.min(previous_minimum, rateVector.get(i));
			current_minimum = Rate.min(current_minimum, newRate);
			lowerBound.set(i, newRate);
		}
		return lowerBound;
//		for (int i = rateVector.size() - 1; i >= 0; i--) {
//			if (order.isComparableIndex(i)) {
//				previous_minimum = current_minimum;
//			}
//			double newRate = Rate.min(previous_minimum, rateVector.get(i));
//			current_minimum = Rate.min(current_minimum, newRate);
//			lowerBound.set(i, newRate);
//		}
//		return lowerBound;
	}
	
	/**
	 * Make the rate vector lumpable, by choosing either the maximum rate or minimum
	 * rate in each partition, according to the argument maximise.
	 */
	private void makeLumpable(boolean maximise, SparseVector rateVector, PartitionIndices partitions) {
		for (int k = 0; k < partitions.size(); k++) {
			int start = partitions.getStart(k);
			int end = partitions.getEnd(k);
			double newRate = maximise ? getMaxRateBlock(rateVector, start, end)
			                          : getMinRateBlock(rateVector, start, end);
			for (int i = start; i<= end; i++) {
				rateVector.set(i, newRate);
			}
		}		
	}
	
//	private void checkLumpable(SparseVector rateVector, PartitionIndices partitions) {
//		for (int k = 0; k < partitions.size(); k++) {
//			int start = partitions.getStart(k);
//			int end = partitions.getEnd(k);
//			double rate = rateVector.get(start);
//			for (int i = start; i<= end; i++) {
//				assert rate == rateVector.get(i);
//			}
//		}
//	}
	
	private double divideRate(double rate1, double rate2) {
		if (rate1 == rate2) {
			return 1;
		} else if (rate1 >= 0 && rate2 < 0) {
			// divide by passive rate (infinity)
			return 0;
		} else {
			assert rate2 != 0;
			return rate1 / rate2;
		}
	}
	
	/**
	 * Computes 1/A, the inverse of the internal rate measure
	 */
	private double internalRateMeasure(SparseVector rateVector, SequentialOrder order) {
		// we only need to compare successive states
		double internalRateMeasure = 1;
		double previousMinimum = 0;
		double currentMinimum = 0;
		for (int i = 0; i < rateVector.size(); i++) {
			if (order.isComparableIndex(i)) {
				Point currentBlock = order.getCurrent(i);
				previousMinimum = currentMinimum;
				currentMinimum = getMinRateBlock(rateVector, currentBlock.x, currentBlock.y);
				assert Rate.min(previousMinimum, currentMinimum) == previousMinimum;
				double currentMaximum = getMaxRateBlock(rateVector, currentBlock.x, currentBlock.y);
				if (i > 0) {
					double ratio = divideRate(previousMinimum, currentMaximum);
					internalRateMeasure = Math.min(internalRateMeasure, ratio);
				}
			}			
		}
		return internalRateMeasure;
	}
	
	private double reverseInternalRateMeasure(SparseVector rateVector, SequentialOrder order) {
		// we only need to compare successive states
		double internalRateMeasure = 1;
		double previousMinimum = 0;
		double currentMinimum = 0;
		for (int i = rateVector.size()-1; i >=0; i--) {
			if (order.isComparableIndex(i)) {
				Point currentBlock = order.getCurrent(i);
				previousMinimum = currentMinimum;
				currentMinimum = getMinRateBlock(rateVector, currentBlock.x, currentBlock.y);
				assert Rate.min(previousMinimum, currentMinimum) == previousMinimum;
				double currentMaximum = getMaxRateBlock(rateVector, currentBlock.x, currentBlock.y);
				if (i > 0) {
					double ratio = divideRate(previousMinimum, currentMaximum);
					internalRateMeasure = Math.min(internalRateMeasure, ratio);
				}
			}			
		}
		return internalRateMeasure;
	}
	
	private double comparativeRateMeasure(SparseVector lowerVector, SparseVector upperVector) {
		double comparativeRateMeasure = 1;
		for (int i = 0; i < lowerVector.size(); i++) {			
			double ratio = divideRate(lowerVector.get(i), upperVector.get(i));
			assert ratio >= 0;
			comparativeRateMeasure = Math.min(comparativeRateMeasure, ratio);
		}
		return comparativeRateMeasure;
	}
	
	private void upperBoundRate(int componentID, SparseVector rateVector, SequentialAbstraction abstraction, SequentialOrder order) {
		PartitionIndices partitions = new PartitionIndices(abstraction);
		SparseVector upperBound;
		if (!order.isAnythingComparable()) {
			upperBound = rateVector.copy();
		} else {
			upperBound = upperBoundRateVector(rateVector, order);		
		}
		makeLumpable(true, upperBound, partitions);
		double comparativeRateMeasure = comparativeRateMeasure(rateVector, upperBound);
		double internalRateMeasure = internalRateMeasure(upperBound, order);
		assert componentID == upperRates.size();
		upperRates.add(upperBound);
		upperInternalRateMeasures.add(internalRateMeasure);
		upperComparativeRateMeasures.add(comparativeRateMeasure);
	}
	
	private void lowerBoundRate(int componentID, SparseVector rateVector, SequentialAbstraction abstraction, SequentialOrder order) {
		PartitionIndices partitions = new PartitionIndices(abstraction);
		SparseVector lowerBound;
		if (!order.isAnythingComparable()) {
			lowerBound = rateVector.copy();
		} else {
			lowerBound = lowerBoundRateVector(rateVector, order);		
		}
		makeLumpable(false, lowerBound, partitions);
		//double comparativeRateMeasure = comparativeRateMeasure(lowerBound, rateVector);
		//double internalRateMeasure = internalRateMeasure(lowerBound, order);
		double comparativeRateMeasure = comparativeRateMeasure(lowerBound, rateVector);
		double internalRateMeasure = reverseInternalRateMeasure(lowerBound, order);
		assert componentID == lowerRates.size();
		lowerRates.add(lowerBound);
		lowerInternalRateMeasures.add(internalRateMeasure);
		lowerComparativeRateMeasures.add(comparativeRateMeasure);
	}
	
	public void addComponent(int componentID, SparseVector rateVector, SequentialAbstraction abstraction, SequentialOrder order) {
		upperBoundRate(componentID, rateVector, abstraction, order);
		lowerBoundRate(componentID, rateVector, abstraction, order);
		assert componentID == originalRates.size();
		originalRates.add(rateVector);
	}
	
	public void addEmptyComponent(int componentID) {
		assert componentID == originalRates.size();
		lowerRates.add(null);
		upperRates.add(null);
		originalRates.add(null);
		upperComparativeRateMeasures.add(null);
		lowerComparativeRateMeasures.add(null);
		upperInternalRateMeasures.add(null);
		lowerInternalRateMeasures.add(null);
	}
	
	private double computeBound(int component, ArrayList<Double> measures) {
		double bound = 1;
		for (int i = 0; i < numComponents; i++) {
			if (component != i) {
				Double measure = measures.get(i);
				if (measure != null) {
					bound = Math.min(bound, measure);
				}
			}
		}
		return bound;
	}
	
	public ComponentRateContext getRateContext(int component) {
		double lowerInternalBound = computeBound(component, lowerInternalRateMeasures);
		double upperInternalBound = computeBound(component, upperInternalRateMeasures);
		double lowerComparativeBound = computeBound(component, lowerComparativeRateMeasures);
		double upperComparativeBound = computeBound(component, upperComparativeRateMeasures);
		return new ComponentRateContext(lowerRates.get(component), originalRates.get(component), upperRates.get(component),
				                        lowerInternalBound, upperInternalBound, lowerComparativeBound, upperComparativeBound);
	}
	
}
