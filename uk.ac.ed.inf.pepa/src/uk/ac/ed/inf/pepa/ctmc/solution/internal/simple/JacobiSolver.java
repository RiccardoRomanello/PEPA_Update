/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution.internal.simple;

import java.util.Arrays;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;

public final class JacobiSolver extends AbstractSolver {

	public JacobiSolver(Generator generator, OptionMap options) {
		super(generator, options);
	}

	protected final double[] doSolve(int[] rows, int[] columns,
			double[] values, double[] diagonal, IProgressMonitor monitor)
			throws SolverException {
		double[] x_k = new double[rows.length];
		double[] x_k_1 = new double[rows.length];
		Arrays.fill(x_k_1, 1 / (double) x_k.length);
		//Arrays.fill(x_k_1, 0.0d);
		
		double b = 0;
		// norm is norm(b - Ax) / norm(b) = norm(b - Ax)
		double max_norm = (Double) options.get(OptionMap.SIMPLE_TOLERANCE);
		double norm = Double.MAX_VALUE, last_norm = Double.MAX_VALUE;
		int iteration = 0;
		double sum = 0;
		double bMinusSum = 0;
		double w = (Double) options
				.get(OptionMap.SIMPLE_OVER_RELAXATION_FACTOR);
		while (max_norm < norm) {
			System.err.println("Norm:" + norm);
			if (iteration++ == maxIteration)
				throw new SolverException(createMaximumIterationsMessage(
						maxIteration, norm), SolverException.NOT_CONVERGED);
			// notifies monitor
			if (iteration % REFRESH_RATE == 0 && iteration != 0) {
				monitor.worked(REFRESH_RATE);
			}

			System.arraycopy(x_k_1, 0, x_k, 0, x_k_1.length);
			
			
			for (int i = 0; i < rows.length; i++) {
				sum = 0;
				for (int j = rows[i], range = (i == rows.length - 1) ? values.length
						: rows[i + 1]; j < range; j++) {
					int j_index = columns[j];
					if (j_index != i) {
						sum += values[j] * x_k[j_index];
					}
				}
				// personalised b vector
				b = (i == rows.length - 1) ? 1.0d : 0.0d;
				bMinusSum = b - sum;
				x_k_1[i] = (1 - w) * x_k[i] + w * bMinusSum / diagonal[i];

				// x_k_1[i] = bMinusSum / diagonal[i];
				norm += Math.pow(b - (sum + diagonal[i]*x_k[i]), 2);

			}
			
			norm = Math.sqrt(norm);
			if (norm > last_norm) {
				throw new SolverException(createDivergenceDetected(iteration, norm),
						SolverException.NOT_CONVERGED);
			} else {
				last_norm = norm;
			}
		}
		return x_k_1;
	}

}
