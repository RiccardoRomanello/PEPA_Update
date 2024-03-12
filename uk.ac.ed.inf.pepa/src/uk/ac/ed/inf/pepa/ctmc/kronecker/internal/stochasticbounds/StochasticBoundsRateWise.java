/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

import java.awt.Point;

import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialOrder;

/**
 * The algorithms for producing upper and lower monotone, lumpable stochastic bounding matrices.
 * This implements the context bounded rate-wise bounds for PEPA sequential components, for
 * partially ordered state spaces. 
 * 
 * @author msmith
 *
 */
public class StochasticBoundsRateWise {
	
	private static double rate_ratio(double rate1, double rate2) {
		if (rate2 == 0) {
			assert rate1 == 0;
			return 0;
		} else {
			return rate1 / rate2;
		}
	}
	
	private static double truncate(double probability) {
		return Math.max(0, Math.min(1, probability));
	}
	
	private static double matrix_sum(AbstractMatrix M, int row, int start_column, int end_column) {
		double sum = 0;
		for (int j = start_column; j <= end_column; j++) {
			sum += M.get(row, j);
		}
		return truncate(sum);
	}
	
	private static double max_sum_block(AbstractMatrix M, int start_row, int end_row, int start_column, int end_column) {
		double max_sum_block = 0;
		for (int i = start_row; i <= end_row; i++) {
			double sum = matrix_sum(M, i, start_column, end_column);
			max_sum_block = Math.max(max_sum_block, sum);
		}
		return truncate(max_sum_block);
	}
	
	private static double min_sum_block(AbstractMatrix M, int start_row, int end_row, int start_column, int end_column) {
		double min_sum_block = 1;
		for (int i = start_row; i <= end_row; i++) {
			double sum = matrix_sum(M, i, start_column, end_column);
			min_sum_block = Math.min(min_sum_block, sum);
		}
		return truncate(min_sum_block);
	}
	
	private static double average_sum_block(AbstractMatrix M, int start_row, int end_row, int start_column, int end_column) {
		int counter = 0;
		double sum = 0;
		for (int i = start_row; i <= end_row; i++) {
			for (int j = start_column; j <= end_column; j++) {
				sum += M.get(i, j);
				counter++;
			}
		}
		return truncate(sum / counter);
	}

	
	/**
	 * Sets the values of the given column of matrix R, so that it respects the rate-wise
	 * upper-bound and monotonicity (and irreducibility).
	 */
	private static void refresh_sum_rate_wise_upper(AbstractMatrix P, AbstractMatrix R, int start_column, int end_column, SequentialOrder order, ComponentRateContext context) {
		//System.out.println("Refreshing sum: columns " + start_column + " to " + end_column);
		int n = R.numRows();
		double comparativeBound = context.getUpperComparativeBound(); // Corresponds to 1/A in the notes
		double internalBound    = context.getUpperInternalBound();    // Corresponds to 1/B in the notes
		assert comparativeBound >= 0;
		assert internalBound >= 0;
		Point previous_block = null;
		Point current_block = order.getCurrent(0);
		double previous_maximum_in_R = 0;
		double current_maximum_in_R = 0;
		double current_maximum_in_P = max_sum_block(P, current_block.x, current_block.y, start_column, n-1);
		double current_comparative_sum = 1 - Math.min(comparativeBound, context.getUpperRatio(current_block)) * (1 - current_maximum_in_P);
		//System.out.println("current_comparative_sum = " + current_comparative_sum);
		assert current_comparative_sum >= 0 && current_comparative_sum <= 1;
		double current_internal_sum    = 0;
		// Iterate forwards over the rows
		for (int i = 0; i < n; i++) {
			// compute the maximum sum for the current block in P
			if (order.isComparableIndex(i)) {
				previous_block = current_block;
				current_block = order.getCurrent(i);
				previous_maximum_in_R = current_maximum_in_R;
				current_maximum_in_P = max_sum_block(P, current_block.x, current_block.y, start_column, n-1);
				current_comparative_sum = 1 - Math.min(comparativeBound, context.getUpperRatio(current_block)) * (1 - current_maximum_in_P);
				current_internal_sum    = 1 - Math.min(internalBound, rate_ratio(context.getUpperRate(previous_block), context.getUpperRate(current_block))) * (1 - previous_maximum_in_R);
				//System.out.println("current_comparative_sum = " + current_comparative_sum);
				//System.out.println("current_internal_sum = " + current_internal_sum);
				//System.out.println("current_maximum_in_P = " + current_maximum_in_P);
				assert current_comparative_sum >= 0 && current_comparative_sum <= 1;
				assert current_internal_sum >= 0 && current_internal_sum <= 1;
				assert current_maximum_in_P >= 0 && current_maximum_in_P <= 1;
			}
			//System.out.println("*** row = " + i);
			double current_assigned = matrix_sum(R, i, end_column + 1, n-1);
			//System.out.println("current_assigned = " + current_assigned);
			double new_sum = Math.max(previous_maximum_in_R, current_maximum_in_P);
			if (start_column <= i) {
				// Only need to care about the comparative and internal bounds on or below the diagonal.
				new_sum = Math.max(new_sum, Math.max(current_comparative_sum, current_internal_sum));
			}
			//System.out.println("new sum = " + new_sum);
			double current_in_P = matrix_sum(P, i, start_column, end_column);
			//System.out.println("current in P = " + current_in_P);
			double new_probability = new_sum - current_assigned;
			//System.out.println("new probability = " + new_probability);
			// assign the probability mass respecting the relative probabilities that
			// were there before, if this is possible
			if (new_probability > 0 && current_in_P > 0) {
				for (int j = start_column; j <= end_column; j++) {
					double new_prob = (P.get(i,j) / current_in_P) * new_probability; 
					R.set(i, j, new_prob);
				}					
			} else if (new_probability == 0) {
				// Have no choice but to delete the transitions
				for (int j = start_column; j <= end_column; j++) {
					R.set(i, j, 0);
				}
			} else if (new_probability > 0 && current_in_P == 0) {
				// Adding a transition - so add equal probability mass to each state
				double new_prob = new_probability / (end_column - start_column + 1); 
				for (int j = start_column; j <= end_column; j++) {
					R.set(i, j, new_prob);
				}
			} else {
				// Should never reach here
				assert false;
			}
			current_maximum_in_R = Math.max(current_maximum_in_R, new_sum);
		}
	}
	
	private static double bound_divide(double num, double dem) {
		if (dem == 0) {
			if (num == 0) {
				return 1;
			} else {
				assert false;
			}
		}
		return num / dem;
	}
	
	private static void refresh_sum_rate_wise_lower(AbstractMatrix P, AbstractMatrix R, int start_column, int end_column, SequentialOrder order, ComponentRateContext context) {
		int n = R.numRows();
		double comparativeBound = context.getLowerComparativeBound(); // Corresponds to 1/A in the notes
		double internalBound    = context.getLowerInternalBound();    // Corresponds to 1/B in the notes
		assert comparativeBound >= 0;
		assert internalBound >= 0;
		Point next_block = null;
		Point current_block = order.getCurrent(n-1);
		double next_minimum_in_R = 1;
		double current_minimum_in_R = 1;
		double current_minimum_in_P = min_sum_block(P, current_block.x, current_block.y, start_column, n-1);
		double current_comparative_sum = 1 - Math.min(comparativeBound, context.getLowerRatio(current_block)) * (1 - current_minimum_in_P);
		assert current_comparative_sum >= 0 && current_comparative_sum <= 1;
		double current_internal_sum = 0;
		// Iterate backwards over the rows
		for (int i = n - 1; i >= 0; i--) {
			// compute the maximum sum for the current block in P
			if (order.isComparableIndex(i)) {
				next_block = current_block;
				current_block = order.getCurrent(i);
				next_minimum_in_R = current_minimum_in_R;
				current_minimum_in_P = min_sum_block(P, current_block.x, current_block.y, start_column, n-1);
				
				// TODO - fix this!! - need to make sure the lower bound _can_ be made rate-wise monotone
				
				//current_comparative_sum = 1 - bound_divide(1 - current_minimum_in_P, Math.min(comparativeBound, context.getLowerRatio(current_block)));
				//current_internal_sum    = 1 - bound_divide(1 - next_minimum_in_R, Math.min(internalBound, rate_ratio(context.getLowerRate(current_block), context.getLowerRate(next_block))));
				current_comparative_sum = 1 - (1 - current_minimum_in_P) * Math.min(comparativeBound, context.getLowerRatio(current_block));
				current_internal_sum    = 1 - (1 - next_minimum_in_R) * Math.min(internalBound, rate_ratio(context.getLowerRate(next_block), context.getLowerRate(current_block)));
				assert current_comparative_sum >= 0 && current_comparative_sum <= 1;
				assert current_internal_sum >= 0 && current_internal_sum <= 1;
				assert current_minimum_in_P >= 0 && current_minimum_in_P <= 1;
			}
			double current_assigned = matrix_sum(R, i, end_column + 1, n-1);
			double new_sum = Math.min(next_minimum_in_R, current_minimum_in_P);
			if (start_column <= i) {
				// Only need to care about the comparative and internal bounds on or below the diagonal.
				new_sum = Math.min(new_sum, Math.min(current_comparative_sum, current_internal_sum));
			}
			double current_in_P = matrix_sum(P, i, start_column, end_column);
			double new_probability = new_sum - current_assigned;
			// assign the probability mass respecting the relative probabilities that
			// were there before, if this is possible
			if (new_probability > 0 && current_in_P > 0) {
				for (int j = start_column; j <= end_column; j++) {
					double new_prob = (P.get(i,j) / current_in_P) * new_probability; 
					R.set(i, j, new_prob);
				}					
			} else if (new_probability == 0) {
				// Have no choice but to delete the transitions
				for (int j = start_column; j <= end_column; j++) {
					R.set(i, j, 0);
				}
			} else if (new_probability > 0 && current_in_P == 0) {
				// Adding a transition - so add equal probability mass to each state
				double new_prob = new_probability / (end_column - start_column + 1); 
				for (int j = start_column; j <= end_column; j++) {
					R.set(i, j, new_prob);
				}
			} else {
				// Should never reach here
				assert false;
			}
			current_minimum_in_R = Math.min(current_minimum_in_R, new_sum);
		}
	}
	
	/**
	 * Ensures that for each partition, the probability of moving to each partition in [current_start,current_end]
	 * is the same for all states.
	 * @param start_column The start of the block corresponding to the ordering (may contain multiple abstract partitions)
	 * @param end_column   The end of the block corresponding to the ordering 
	 */
	private static void normalise_upper(AbstractMatrix R, int start_column, int end_column, PartitionIndices partitions) {
		//System.out.println("Before Normalisation:");
		//System.out.println(R);
		// Move probability mass into the block corresponding to the ordering
		for (int k = 0; k < partitions.size(); k++) {
			int start_row = partitions.getStart(k);
			int end_row = partitions.getEnd(k);
			double max_assigned = max_sum_block(R, start_row, end_row, start_column, end_column);
			for (int i = start_row; i <= end_row; i++) {
				double current_assigned = matrix_sum(R, i, start_column, end_column);
				if (current_assigned > 0) {
					for (int j = start_column; j <= end_column; j++) {
						double new_prob = (R.get(i,j) * max_assigned) / current_assigned;
						R.set(i, j, new_prob);
					}
				} else {
					for (int j = start_column; j <= end_column; j++) {
						double new_prob = max_assigned / (end_column - start_column + 1);
						R.set(i, j, new_prob);
					}
				}
			}
		}
		//System.out.println("After Normalisation:");
		//System.out.println(R);
	}
	
	private static void normalise_lower(AbstractMatrix R, int start_column, int end_column, PartitionIndices partitions) {
		// Move probability mass into the block corresponding to the ordering
		for (int k = 0; k < partitions.size(); k++) {
			int start_row = partitions.getStart(k);
			int end_row = partitions.getEnd(k);
			double min_assigned = min_sum_block(R, start_row, end_row, start_column, end_column);
			for (int i = start_row; i <= end_row; i++) {
				double current_assigned = matrix_sum(R, i, start_column, end_column);
				if (current_assigned > 0) {
					for (int j = start_column; j <= end_column; j++) {
						double new_prob = (R.get(i,j) * min_assigned) / current_assigned;
						R.set(i, j, new_prob);
					}
				} else {
					for (int j = start_column; j <= end_column; j++) {
						double new_prob = min_assigned / (end_column - start_column + 1);
						R.set(i, j, new_prob);
					}
				}
			}
		}
	}
	
	private static void normalise_partition(AbstractMatrix R, int start_column, int end_column, PartitionIndices partitions) {
		// Assumes that we are already lumpable with respect to the blocks induced by the ordering
		for (int k = 0; k < partitions.size(); k++) {
			int start_row = partitions.getStart(k);
			int end_row = partitions.getEnd(k);
			double new_probability = average_sum_block(R, start_row, end_row, start_column, end_column);
			for (int i = start_row; i <= end_row; i++) {
				for (int j = start_column; j <= end_column; j++) {
					R.set(i,j,new_probability);
				}
			}
		}
	}
	
	/**
	 * Upper-bounding algorithm, based on a partially ordered state space
	 */
	private static FlexCompRowMatrix computeRateWiseUpperBound(AbstractMatrix P, PartitionIndices partitions, ComponentRateContext context, SequentialOrder order) {
		assert P.numColumns() == P.numRows();
		int n = P.numColumns();
		FlexCompRowMatrix R = new FlexCompRowMatrix(n,n);
		
//		System.out.println(" === UPPER BOUND === ");
//		System.out.println(P);
//		System.out.println(" =================== ");
		
		if (order.isAnythingComparable()) {			
			// Iterate backwards through each of the partitions
			int end_partition = partitions.size() - 1;
			for (int k = partitions.size() - 1; k >= 0; k--) {
				// Complete the matrix R - just for the columns that can be compared
				if (order.isComparableIndex(partitions.getStart(k))) {
					refresh_sum_rate_wise_upper(P, R, partitions.getStart(k), partitions.getEnd(end_partition), order, context);
					assert isSubStochastic(R);
					normalise_upper(R, partitions.getStart(k), partitions.getEnd(end_partition), partitions);
					assert isSubStochastic(R);
					for (int p = end_partition; p >= k; p--) {
						normalise_partition(R, partitions.getStart(p), partitions.getEnd(p), partitions);
					}
					assert isSubStochastic(R);
					end_partition = k-1;
				} 
			}
		} else {
			// should never reach here
			assert false;
			// There are no ordering or monotonicity constrains.
			// (1) Copy P -> R
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					R.set(i, j, P.get(i,j));
				}
			}
			// (2) Make all the partitions lumpable.
			for (int k = partitions.size() - 1; k >= 0; k--) {
				normalise_partition(R, partitions.getStart(k), partitions.getEnd(k), partitions);
			}
			assert isSubStochastic(R);
		}
//		if (!isStochastic(R)) {
//			System.out.println(P);
//			System.out.println("===>");
//			System.out.println(R);
//		}
		assert isStochastic(R);
		return R;
	}	
	
	/**
	 * Upper-bounding algorithm, based on a partially ordered state space
	 */
	private static FlexCompRowMatrix computeRateWiseLowerBound(AbstractMatrix P, PartitionIndices partitions, ComponentRateContext context, SequentialOrder order) {
		assert P.numColumns() == P.numRows();
		int n = P.numColumns();
		FlexCompRowMatrix R = new FlexCompRowMatrix(n,n);
		
		if (order.isAnythingComparable()) {			
			// Iterate backwards through each of the partitions
			int end_partition = partitions.size() - 1;
			for (int k = partitions.size() - 1; k >= 0; k--) {
				// Complete the matrix R - just for the columns that can be compared
				if (order.isComparableIndex(partitions.getStart(k))) {
					refresh_sum_rate_wise_lower(P, R, partitions.getStart(k), partitions.getEnd(end_partition), order, context);
					assert isSubStochastic(R);
					normalise_lower(R, partitions.getStart(k), partitions.getEnd(end_partition), partitions);
					assert isSubStochastic(R);
					for (int p = end_partition; p >= k; p--) {
						normalise_partition(R, partitions.getStart(p), partitions.getEnd(p), partitions);
					}
					assert isSubStochastic(R);
					end_partition = k-1;
				}
			}
		} else {
			// Should never reach here
			assert false;
		}
		//System.out.println(P);
		//System.out.println("===>");
		//System.out.println(R);
		assert isStochastic(R);
		return R;
	}	

	private static boolean isSubStochastic(AbstractMatrix R) {
		boolean isOK = true;
		int n = R.numColumns();
		for (int i = 0; i < n; i++) {
			double sum = 0;
			for (int j = 0; j < n; j++) {
				sum += R.get(i, j);
			}
			if (sum > 1.0000001) isOK = false;
		}
		return isOK;
	}
	
	private static boolean isStochastic(AbstractMatrix R) {
		boolean isOK = true;
		int n = R.numColumns();
		for (int i = 0; i < n; i++) {
			double sum = 0;
			for (int j = 0; j < n; j++) {
				sum += R.get(i, j);
			}
			if (sum <= 0.9999999 || sum >= 1.0000001) {
				isOK = false;
			}
		}
		return isOK;
	}
	
	public static FlexCompRowMatrix upperBoundMatrix(AbstractMatrix P, SequentialAbstraction abstraction, ComponentRateContext context, SequentialOrder order) {
		return computeRateWiseUpperBound(P, new PartitionIndices(abstraction), context, order);
	}
	
	public static FlexCompRowMatrix lowerBoundMatrix(AbstractMatrix P, SequentialAbstraction abstraction, ComponentRateContext context, SequentialOrder order) {
		return computeRateWiseLowerBound(P, new PartitionIndices(abstraction), context, order);
	}
}
