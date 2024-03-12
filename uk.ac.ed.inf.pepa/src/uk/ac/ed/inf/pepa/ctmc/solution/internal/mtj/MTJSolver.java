/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution.internal.mtj;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.AbstractIterativeSolver;
import no.uib.cipr.matrix.sparse.DefaultIterationMonitor;
import no.uib.cipr.matrix.sparse.IterationMonitor;
import no.uib.cipr.matrix.sparse.IterationReporter;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import no.uib.cipr.matrix.sparse.MatrixIterationMonitor;
import no.uib.cipr.matrix.sparse.Preconditioner;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;

public class MTJSolver implements ISolver {

	private IProgressMonitor monitor;

	private Matrix A;

	private DenseVector b;

	private DenseVector x;

	private OptionMap options;

	private AbstractIterativeSolver solver;

	public MTJSolver(AbstractIterativeSolver solver, Matrix A, DenseVector b,
			DenseVector x, OptionMap options) {
		this.A = A;
		this.b = b;
		this.x = x;
		this.options = options;
		this.solver = solver;
	}

	public double[] solve(IProgressMonitor monitor) throws SolverException {
		if (monitor == null)
			monitor = new DoNothingMonitor();
		this.monitor = monitor;

		try {

			int total = (Integer) options.get(OptionMap.ITER_MON_MAX_ITER);
			this.monitor.beginTask(total);
			solve();
		} catch (IterativeSolverNotConvergedException e) {
			String message = null;
			switch (e.getReason()) {
			case Breakdown:
				message = "The iterative process detected a breakdown";
				break;
			case Divergence:
				message = "Divergence detected. Residual " + e.getResidual()
						+ ", " + " at " + e.getIterations() + " iteration.";
				break;
			case Iterations:
				message = "Maximum number of iterations reached";
				break;
			default:
				message = "";
			}
			throw new SolverException(message, SolverException.NOT_CONVERGED);
		} finally {
			monitor.done();
		}
		return x.getData();
	}

	private Vector solve() throws IterativeSolverNotConvergedException {

		/* Preconditioner settings */
		if ((Integer) options.get(OptionMap.PRECONDITIONER) != OptionMap.NO_PRECONDITIONER) {
			/* A preconditioner has been selected */
			int precondId = (Integer) options.get(OptionMap.PRECONDITIONER);
			Preconditioner M = MTJFactory.createPreconditioner(precondId, A
					.copy(), options);
			M.setMatrix(A);
			solver.setPreconditioner(M);
		}
		handleIterationMonitor();
		handleIterationReporter();
		return solver.solve(A, b, x);

	}

	private void handleIterationMonitor() {
		/* Iteration Monitor settings */
		if (options.get(OptionMap.ITER_MON_TYPE).equals(
				OptionMap.ITER_MON_DEFAULT)) {
			solver.setIterationMonitor(new DefaultIterationMonitor(
					(Integer) options.get(OptionMap.ITER_MON_MAX_ITER),
					(Double) options.get(OptionMap.ITER_MON_RTOL),
					(Double) options.get(OptionMap.ITER_MON_ATOL),
					(Double) options.get(OptionMap.ITER_MON_DTOL)));
		} else if (options.get(OptionMap.ITER_MON_TYPE).equals(
				OptionMap.ITER_MON_MATRIX)) {
			solver.setIterationMonitor(new MatrixIterationMonitor(
					(Double) options.get(OptionMap.ITER_MON_NORM_A),
					(Double) options.get(OptionMap.ITER_MON_NORM_B),
					(Integer) options.get(OptionMap.ITER_MON_MAX_ITER),
					(Double) options.get(OptionMap.ITER_MON_RTOL),
					(Double) options.get(OptionMap.ITER_MON_ATOL),
					(Double) options.get(OptionMap.ITER_MON_DTOL)));
		}
	}

	private void handleIterationReporter() {
		IterationMonitor monitor = solver.getIterationMonitor();
		if (this.monitor != null) {
			monitor.setIterationReporter(new IterationReporter() {

				public void monitor(double residualNorm, Vector stateVector,
						int iterationNumber) {
					MTJSolver.this.monitor.worked(1);
				}

				public void monitor(double residualNorm, int iterationNumber) {
					MTJSolver.this.monitor.worked(1);
				}

			});
		}
	}
}
