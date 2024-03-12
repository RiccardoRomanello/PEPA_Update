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

public class CGS extends AbstractSolver {

	public CGS(Generator generator, OptionMap options) {
		super(generator, options);
	}

	@Override
	protected double[] doSolve(int[] rows, int[] columns, double[] values,
			double[] diagonal, IProgressMonitor monitor) throws SolverException {

		// double[] u = new double[rows.length];
		double[] u_i = new double[rows.length];

		double[] p_i = new double[rows.length];

		double[] q_tilde = new double[rows.length];
		double[] q_i = new double[rows.length];

		double[] r_0 = new double[rows.length];
		double[] r_i = new double[rows.length];

		double[] v = new double[rows.length];
		double[] x = new double[rows.length];

		double rho_i_1 = 0.0d;
		double rho_i_2 = 0.0d;
		double alpha = 0.0d;
		double beta = 0.0d;
		// initial guess

		Arrays.fill(x, 1 / (double) x.length);
		// compute r_0 = b - A * x0
		mvp(rows, columns, values, diagonal, x, r_0);
		for (int i = 0; i < r_0.length; i++) {
			r_0[i] = (i == r_0.length - 1) ? (1 - r_0[i]) : -r_0[i];
		}

		// we choose r = r_0
		// System.arraycopy(r_0, 0, r, 0, r.length);
		// initialise r_i_1
		System.arraycopy(r_0, 0, r_i, 0, r_0.length);
		boolean converged = false;
		int i = 0;
		while (true) {
			// debug("r:" ,r_i);
			// debug(x);
			i++;
			rho_i_1 = vvp(r_0, r_i);
			// debug("rho:", new double[] { rho_i_1 });
			if (rho_i_1 == 0) {
				return x;
				// throw new SolverException("Method failed",
				// SolverException.NOT_CONVERGED);
			}
			if (i == 1) {
				System.arraycopy(r_0, 0, u_i, 0, r_0.length);
				System.arraycopy(u_i, 0, p_i, 0, u_i.length);
			} else {
				beta = rho_i_1 / rho_i_2;
				// debug("beta:",new double[] {beta});
				double current_u_i = 0.0d;
				for (int j = 0; j < u_i.length; j++) {
					current_u_i = r_i[j] + beta * q_i[j];
					u_i[j] = current_u_i;
					p_i[j] = current_u_i + beta * (q_i[j] + beta * p_i[j]);
				}
			}
			// debug("u_i:",u_i);
			// debug("p_i",p_i);

			// solve Mp = p_i
			// in our case M = I, so p = p_i
			// ...

			// v = Ap
			mvp(rows, columns, values, diagonal, p_i, v);
			// debug("v", v);
			alpha = rho_i_1 / vvp(r_0, v);
			// debug("alpha", new double[] { alpha } );

			// q_i = u_i - alpha * v
			// Mu = u_i + q_i
			for (int j = 0; j < q_i.length; j++) {
				// updates q_i_1
				q_i[j] = u_i[j] - alpha * v[j];
				u_i[j] = u_i[j] + q_i[j];
			}
			// debug("q",q_i );
			// debug("u", u_i);

			for (int j = 0; j < x.length; j++) {
				x[j] += alpha * u_i[j];
			}
			// debug("x", x);

			mvp(rows, columns, values, diagonal, u_i, q_tilde);

			// debug("q_tilde", q_tilde);

			double norm = 0;
			double diff = 0;
			// debug("old r:", r_i);
			for (int j = 0; j < r_i.length; j++) {
				diff = alpha * q_tilde[j];
				r_i[j] = r_i[j] - diff;
				norm += Math.abs(diff);
			}

			double absoluteNorm = 0.0;
			double[] result = new double[x.length];
			mvp(rows, columns, values, diagonal, x, result);
			for (int j = 0; j < result.length; j++) {
				absoluteNorm += Math
						.pow((j == result.length - 1) ? 1 - result[j]
								: result[j], 2);
			}

			// debug("r", r_i);
			double absNorm = Math.sqrt(absoluteNorm);
			System.err.println("Absolute norm:" + absNorm);
			// System.err.println("Residual Norm: "+norm);
			if (absNorm < 1E-8)
				break;
			if (i == 5000)
				throw new SolverException("CGS did not converge after " + i
						+ " iterations", SolverException.NOT_CONVERGED);
			rho_i_2 = rho_i_1;
		}
		return x;
	}

	private static final void debug(String message, double[] vector) {
		/*
		 * System.err.println(message); for (double d : vector)
		 * System.err.print(d + " "); System.err.println();
		 */
	}

	/**
	 * Matrix vector product for HBF Matrices
	 * 
	 * @param rows
	 *            row vector
	 * @param columns
	 *            column vector
	 * @param values
	 *            values vector
	 * @param diagonal
	 *            diagonal vector
	 * @param vector
	 *            full vector (RHS)
	 * @return the multiplication
	 */
	private static final void mvp(int[] rows, int[] columns, double[] values,
			double[] diagonal, double[] vector, double[] solution) {

		double sum = 0;

		for (int i = 0; i < rows.length; i++) {
			sum = diagonal[i] * vector[i];
			for (int j = rows[i], range = (i == rows.length - 1) ? values.length
					: rows[i + 1]; j < range; j++) {
				int j_index = columns[j];
				if (j_index != i) {
					sum += values[j] * vector[j_index];
				}
			}
			solution[i] = sum;
		}
	}

	/**
	 * Vector-vector product
	 * 
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return solution
	 */
	private static final double vvp(double[] v1, double[] v2) {
		double sum = 0.0d;
		for (int i = 0; i < v1.length; i++) {
			sum += v1[i] * v2[i];
		}
		return sum;
	}

}
