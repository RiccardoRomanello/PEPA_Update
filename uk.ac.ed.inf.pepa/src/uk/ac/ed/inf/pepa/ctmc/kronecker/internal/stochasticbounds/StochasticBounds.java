/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.DenseMatrix;
import uk.ac.ed.inf.pepa.ctmc.abstraction.AbstractState;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;

/**
 * The algorithms for producing upper and lower monotone, lumpable stochastic bounding matrices.
 * Both the original algorithms for a uniformised CTMC, and the context bounded rate-wise bounds for
 * PEPA sequential components. We implement the algorithm for a totally ordered state space and 
 * for a class of partial orders.
 * 
 * @author msmith
 *
 */
public class StochasticBounds {

	/**
	 * Factor EPSILON, used by the stochastic bounds algorithm to ensure irreducibility
	 */
	private static final double EPSILON = 0.0001;
	
	private static double matrix_sum (AbstractMatrix M, int row, int start_col, int end_col) {
		double sum = 0;
		for (int j = start_col; j <= end_col; j++) {
			sum += M.get(row, j);
		}
		return sum;
	}
	
	private static double upper_bound(AbstractMatrix P, AbstractMatrix R, int i, int j) {
		int n = R.numRows() - 1;
		
		double prev_sum    = matrix_sum(R, i-1, j,   n); // values stored in the previous row
		double old_sum     = matrix_sum(P, i,   j,   n); // values stored in the same row of the old matrix P
		
		return Math.max(prev_sum, old_sum);
	}
	
	private static double lower_bound(AbstractMatrix P, AbstractMatrix R, int i, int j) {
		int n = R.numRows() - 1;
		
		double next_sum    = matrix_sum(R, i+1, j,   n); // values stored in the next row
		double old_sum     = matrix_sum(P, i,   j,   n); // values stored in the same row of the old matrix P
		
		return Math.min(next_sum, old_sum);
	}
	
	/**
	 * Sets the values of the given column of matrix R, so that it respects the
	 * upper-bound and monotonicity (and irreducibility).
	 */
	private static void refresh_sum_upper(AbstractMatrix P, AbstractMatrix R, int column) {
		// Iterate backwards over each row, and forwards through the columns
		int n = R.numRows();
		for (int i = 1; i < n; i++) {
			double current_sum = matrix_sum(R, i, column+1, n);
			double new_prob = Math.max(0, upper_bound(P, R, i, column) - current_sum);
				
			// Ensure we don't delete transitions unnecessarily
			if ((new_prob == 0) && (current_sum < 1) && (P.get(i, column) > 0 || i == column-1)) {
				new_prob = EPSILON * (1 - current_sum);
			}
				
			R.set(i, column, new_prob);
		}
	}
	
	/**
	 * Sets the values of the given column of matrix R, so that it respects the
	 * upper-bound and monotonicity (and irreducibility).
	 */
	private static void refresh_sum_lower(AbstractMatrix P, AbstractMatrix R, int column) {
		// Iterate backwards over each row, and backwards through the columns
		int n = R.numRows();
		for (int i = n; i >= 0; i--) {
			double current_sum = matrix_sum(R, i, column+1, n);
			double new_prob = Math.max(0, lower_bound(P, R, i, column) - current_sum);
				
			// Ensure we don't delete transitions unnecessarily
			if ((new_prob == 0) && (current_sum < 1) && (P.get(i, column) > 0 || i == column-1)) {
				new_prob = EPSILON * (1 - current_sum);
			}
				
			R.set(i, column, new_prob);
		}
	}
	
	/**
	 * Ensures that for each partition, the probability of moving to a partition [current_start,current_end]
	 * is the same for all states.
	 */
	private static void normalise(AbstractMatrix R, int current_start, int current_end, AbstractState[] partitions) {
		int end_state = 0;
		for (int y = 0; y < partitions.length; y++) {
			int start_state = end_state + 1;
			end_state += partitions[y].size();
			
			// The maximum probability assigned to the partition (by looking at largest state: end_state - 1)
			double max_assigned = matrix_sum(R, end_state - 1, current_start, current_end);
			
			// For each row in the partition, make sure it sums to max_assigned
			for (int i = start_state; i < end_state - 1; i++) {
				double current_assigned = matrix_sum(R, i, current_start + 1, current_end);
				R.set(i, current_start, max_assigned - current_assigned);
			}
		}
	}
	
	/**
	 * Upper-bounding algorithm of Fourneau et al. (not rate-wise), based on a totally ordered state space
	 */
	private static DenseMatrix computeUpperBound(AbstractMatrix P, SequentialAbstraction abstraction) {
		assert P.numColumns() == P.numRows();
		int n = P.numColumns();
		DenseMatrix R = new DenseMatrix(n,n);
		
		// Initialise the first row to be the same as before
		for (int j = 0; j < n; j++) {
			R.set(1, j, P.get(1,j));
		}
		
		// Initialise the last entry in each row
		for (int i = 1; i < n; i++) {
			double new_prob = Math.max(R.get(i-1,n-1), P.get(i,n-1));
			R.set(i, n-1, new_prob);
		}
		
		// Iterate backwards through each of the abstract states
		// This assumes that the partitions are ordered, and contain contiguous state indices
		AbstractState[] partitions = abstraction.getAbstractStateSpace();
		int start_state = n;
		for (int k = partitions.length - 1; k >=0; k--) {
			// Current partition = start_state ... end_state-1
			int end_state = start_state;
			start_state -= partitions[k].size();
			// Complete the matrix R - just for the columns in the partition.
			for (int j = end_state - 1; j >= start_state; j--) {
				refresh_sum_upper(P, R, j);
			}
			normalise(R, start_state, end_state-1, partitions);
		}
		
		return R;
	}
	
	/**
	 * Lower-bounding algorithm of Fourneau et al. (not rate-wise), based on a totally ordered state space
	 */
	private static DenseMatrix computeLowerBound(AbstractMatrix P, SequentialAbstraction abstraction) {
		assert P.numColumns() == P.numRows();
		int n = P.numColumns();
		DenseMatrix R = new DenseMatrix(n,n);
		
		// Initialise the first row to be the same as before
		for (int j = 0; j < n; j++) {
			R.set(1, j, P.get(1,j));
		}
		
		// Initialise the last entry in each row
		for (int i = 1; i < n; i++) {
			double new_prob = Math.min(R.get(i-1,n-1), P.get(i,n-1));
			R.set(i, n-1, new_prob);
		}
		
		// Iterate backwards through each of the abstract states
		// This assumes that the partitions are ordered, and contain contiguous state indices
		AbstractState[] partitions = abstraction.getAbstractStateSpace();
		int start_state = n;
		for (int k = partitions.length - 1; k >=0; k--) {
			// Current partition = start_state ... end_state-1
			int end_state = start_state;
			start_state -= partitions[k].size();
			// Complete the matrix R - just for the columns in the partition.
			for (int j = end_state - 1; j >= start_state; j--) {
				refresh_sum_lower(P, R, j);
			}
			normalise(R, start_state, end_state-1, partitions);
		}
		
		return R;
	}
	
	public static DenseMatrix upperBoundMatrix(AbstractMatrix P, SequentialAbstraction abstraction) {
		return computeUpperBound(P, abstraction);
	}
	
	public static DenseMatrix lowerBoundMatrix(AbstractMatrix P, SequentialAbstraction abstraction) {
		return computeLowerBound(P, abstraction);
	}
	
}
