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

public class GaussSeidel extends AbstractSolver {

	public GaussSeidel(Generator generator, OptionMap options) {
		super(generator, options);
	}

	protected final double[] doSolve(int[] rows, int[] columns,
			double[] values, double[] diagonal, IProgressMonitor monitor) throws SolverException {
		// System.err.println("Gauss-Seidel");
		double[] x_k = new double[rows.length];
		Arrays.fill(x_k, 1 / (double) x_k.length);
		int iteration = 0;
		double b = 0;
		// norm is norm(b - Ax) / norm(b) = norm(b - Ax)
		double max_norm = (Double) options.get(OptionMap.SIMPLE_TOLERANCE);
		double norm = Double.MAX_VALUE, last_norm = Double.MAX_VALUE;
		double sum = 0;
		double bMinusSum = 0;
		double w = (Double) options.get(OptionMap.SIMPLE_OVER_RELAXATION_FACTOR);
		while (max_norm < norm) {
			
			if (iteration++ == maxIteration)
				throw new SolverException(createMaximumIterationsMessage(
						maxIteration, norm), SolverException.NOT_CONVERGED);
		
			// notifies monitor
			if (iteration % REFRESH_RATE == 0 && iteration != 0) {
				monitor.worked(REFRESH_RATE);
			}

			norm = 0;
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
				// without over-relaxation
				// x_k[i] = bMinusSum / diagonal[i];
				x_k[i] = (1 - w) * x_k[i] + w * bMinusSum / diagonal[i];
				norm += Math.pow(b - (sum + diagonal[i]*x_k[i]), 2);

			}
			norm = Math.sqrt(norm);
			//last_norm = norm;
			if (norm > last_norm) {
				throw new SolverException(createDivergenceDetected(iteration, norm),
						SolverException.NOT_CONVERGED);
			} else {
				last_norm = norm;
			}

		}
		return x_k;
	}

}
