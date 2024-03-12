/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution.internal.hydra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.HydraExporter;
import uk.ac.ed.inf.pepa.tools.HydraImporter;
import uk.ac.ed.inf.pepa.tools.PepaTools;

/**
 * 
 * Porting of Hydra
 * 
 * @author mtribast
 * 
 */
public class SteadyStateAnalyser implements ISolver {

	private static final boolean DEBUG = false;

	private long trans;

	// private int doSteady = 1;

	// private int doPassage = 0;

	// private int doTransient = 0;

	// private long genRepInterval = 100000;

	// private long solRepInterval = 5;

	private int maxIterations = 5000;

	private double accuracy = 1E-10;

	// private double relaxParameter = 1.0;

	private double EPS = 2.22045E-16;

	private int tangible;

	// Information info;
	private MatrixType solver;

	private FlexCompColMatrix Q, QC;

	private double[] ai_b, ai_d;

	private double ai_start;

	private IStateSpace stateSpace;

	private double[] scale;

	public enum MatrixType {
		COLUMN
	};

	/*
	 * public class Information {
	 * 
	 * private int maxChildren;
	 * 
	 * void setMaxChildren(int _maxChildren) { maxChildren = _maxChildren; }
	 * 
	 * int getMaxChildren() { return maxChildren; }
	 * 
	 * }
	 */

	public SteadyStateAnalyser(IStateSpace ss, OptionMap map) {
		if (map == null)
			map = new OptionMap();
		// info = new Information();
		// info.setMaxChildren(maxChildren);
		this.stateSpace = ss;
		this.accuracy = (Double) map.get(OptionMap.HYDRA_ACCURACY);
		this.maxIterations = (Integer) map.get(OptionMap.HYDRA_MAX_ITERATIONS);
		if (DEBUG) {
			System.out.println("Accuracy:" + accuracy);
			System.out.println("MaxIterations:" + maxIterations);
		}
		tangible = ss.size();
		// method air
		solver = MatrixType.COLUMN;
		QC = new FlexCompColMatrix(tangible, tangible);
		Q = QC;
		this.scale = new double[tangible];
	}

	private void readStatesQC() {
		int states = 0;
		int n = 0;
		double weight = 0;
		double sum = 0;
		double factor = 0;
		ai_b = new double[tangible];
		ai_d = new double[tangible];
		ai_start = 0.0;
		for (n = 0; n < tangible; n++) {
			ai_d[n] = 0.0;
			ai_b[n] = 0.0;
		}
		for (int i = 0; i < tangible; i++) {
			sum = 0;
			int[] jj = stateSpace.getOutgoingStateIndices(i);
			for (int j : jj) {
				sum += stateSpace.getRate(i, j);
			}
			factor = 1.0 / sum;
			for (int j : jj) {
				weight = stateSpace.getRate(i, j) * factor;
				if (j > states)
					ai_b[states] += weight;
				else
					ai_d[states] += weight;
				QC.add(i, j, weight);
			}
			if (states == 0) {
				for (int j : jj) {
					weight = stateSpace.getRate(i, j) * factor;
					if (j > 1)
						ai_start += weight;
				}
			}
			scale[states] = factor;
			trans += jj.length;
			states++;
		}
		if (DEBUG) {
			System.out.printf("INITIAL ai_start:%E\n", ai_start);
		}

	}

	public double[] solve(IProgressMonitor monitor) throws SolverException {
		readStatesQC();
		if (monitor == null)
			monitor = new DoNothingMonitor();
		monitor.beginTask(this.maxIterations);

		double[] solutionArray = new double[tangible];
		solutionArray[0] = 1.0 / tangible;
		for (int i = 1; i < tangible; i++) {
			solutionArray[i] = solutionArray[0];
		}
		DenseVector solution = new DenseVector(solutionArray);

		double omega = 1.0;
		double aa, cc, ee, ff, a, b, c, d, e, f, sum;
		int i, k;
		int rk;
		SparseVector _col;
		double l, pi, u, _u; // , ll, uu;
		double maxDiff, solNorm, lastResidInfNorm, residInfNorm;
		double improve, convergence = 1.0;
		int iterations = 0;
		double[] last = new double[tangible];
		DenseVector result = new DenseVector(solutionArray);
		double tol = EPS / tangible;

		int[] count = new int[41];
		int index = 0, maxIndex = 0, stop = 0;
		double[] average = new double[41];
		double max = 0;
		for (i = 0; i < 41; i++) {
			count[i] = 0;
			average[i] = 0;
		}
		double QC01 = QC.get(0, 1), QCnn = QC.get(tangible - 1, tangible - 1), QCnn_1 = QC
				.get(tangible - 1, tangible - 2);

		// initialise solution, last vectors
		for (i = 0; i < tangible; i++) {
			last[i] = solution.get(i);
		}
		// AIR extension
		Q.transMult(solution, result);
		for (i = 0; i < tangible; i++) {
			// result[i] -= solution[i];
			double sol_i = solution.get(i);
			double res_i = result.get(i);
			result.set(i, res_i - sol_i);
		}
		lastResidInfNorm = result.norm(no.uib.cipr.matrix.Vector.Norm.Infinity);
		if (DEBUG) {
			// System.out.println("Printing result");
			for (int x = 0; x < result.size(); x += 1000) {
				// System.out.printf("Res(%d)=%f\n",x,result.get(x));
			}
		}
		while (iterations < maxIterations && convergence > accuracy) {
			if (monitor.isCanceled())
				throw new SolverException("Operation cancelled by the user.",
						SolverException.UNKNOWN);
			if (iterations > 0 && (iterations % 5) == 0)
				monitor.worked(5);
			// System.out.println(iterations);
			// System.out.println(omega);
			// System.out.println(convergence);

			l = solution.get(0);

			u = 1.0 - (solution.get(0) + solution.get(1));

			c = QC01;

			aa = ai_start * l;
			a = ai_start;

			_col = QC.getColumn(0);
			ff = 0;
			for (rk = 0; rk < _col.getIndex().length; rk++) {
				k = _col.getIndex()[rk];
				if (k > 1)
					ff += solution.get(k) * _col.getData()[rk];
			}

			f = ff / u;

			_col = QC.getColumn(1);
			ee = 0;
			for (rk = 0; rk < _col.getIndex().length; rk++) {
				k = _col.getIndex()[rk];
				if (k > 1)
					ee += solution.get(k) * _col.getData()[rk];
			}
			e = ee / u;

			b = ai_b[1];
			d = ai_d[1];
			sum = 1 / (e + f);
			a *= sum;
			b *= sum;
			c += a * e;
			d += b * f;
			if (DEBUG) {
				if (iterations == 0) {
					System.out.printf("aa:%E\n", aa);
					System.out.printf("a:%E\n", a);
					System.out.printf("l:%E\n", l);
					System.out.printf("u:%E\n", u);
					System.out.printf("c:%E\n", c);
					System.out.printf("ff:%E\n", ff);
					System.out.printf("f:%E\n", f);
					System.out.printf("e:%E\n", e);
					System.out.printf("ee:%E\n", ee);
				}
			}
			pi = c / d;

			_u = a + pi * b;

			sum = 1.0 / (1.0 + pi + _u);
			l = sum;
			pi *= sum;

			if (l < tol)
				l = tol;
			if (pi < tol)
				pi = tol;

			solution.set(0, l);
			solution.set(1, pi);

			l = solution.get(0) + solution.get(1);

			for (i = 2; i < tangible - 2; i++) {
				_col = QC.getColumn(i);
				d = ai_d[i];
				u -= solution.get(i);

				ff = ff + ee - solution.get(i) * d; // ai_d(i) + ee;
				f = ff / u;

				cc = 0;
				ee = 0;
				for (rk = 0; rk < _col.getIndex().length; rk++) {
					k = _col.getIndex()[rk];
					if (k < i)
						cc += solution.get(k) * _col.getData()[rk];
					else if (k > i)
						ee += solution.get(k) * _col.getData()[rk];
				}
				c = cc / l;
				e = ee / u;

				aa = aa - cc + solution.get(i - 1) * ai_b[i - 1]; // - cc;
				a = aa / l;

				sum = 1 / (e + f);
				b = ai_b[i];
				a *= sum;
				b *= sum;
				c += a * e;
				d += b * f;

				pi = c / d;

				_u = a + pi * b;

				sum = 1.0 / (1.0 + pi + _u);
				l = sum; // ### tres questionable
				pi *= sum;
				if (pi < tol)
					pi = tol;

				solution.set(i, pi); // *omega + (1-omega)*last(i);

				l += solution.get(i);
			}

			u -= solution.get(tangible - 2);

			i = tangible - 2;
			d = ai_d[i]; // ###
			_col = QC.getColumn(i);

			cc = 0;
			for (rk = 0; rk < _col.getIndex().length; rk++) {
				k = _col.getIndex()[rk];
				if (k < i)
					cc += solution.get(k) * _col.getData()[rk];
			}
			c = cc / l;

			aa = aa + solution.get(i - 1) * ai_b[i - 1] - cc;
			a = aa / l;

			e = QCnn_1;
			f = 1 - (QCnn + e);

			b = ai_b[i];

			sum = 1 / (e + f);
			a *= sum;
			b *= sum;
			c += a * e;
			d += b * f;

			pi = c / d;
			u = a + pi * b;

			sum = 1.0 / (1.0 + pi + u);
			l = sum;
			pi *= sum;
			u *= sum;

			if (pi < tol)
				pi = tol;
			if (u < tol)
				u = tol;
			solution.set(tangible - 2, pi);
			solution.set(tangible - 1, u);

			normalise(solution);
			for (i = 0; i < tangible; i++) {
				solution
						.set(i, (1 - omega) * last[i] + omega * solution.get(i));
				if (solution.get(i) < tol)
					solution.set(i, tol);
			}
			normalise(solution);

			iterations++;

			if ((iterations % 5) != 0) {
				maxDiff = 0;
				solNorm = 0;
				for (i = 0; i < tangible; i++) {
					if (Math.abs(solution.get(i)) > solNorm)
						solNorm = Math.abs(solution.get(i));
					if (Math.abs(solution.get(i) - last[i]) > maxDiff)
						maxDiff = Math.abs(solution.get(i) - last[i]);
					last[i] = solution.get(i);
				}
				if (maxDiff != 0)
					convergence = maxDiff / solNorm;
				else
					convergence = 0;

				residInfNorm = 0;
				QC.transMult(solution, result);
				for (i = 0; i < tangible; i++) {
					double sol_i = solution.get(i);
					double res_i = result.get(i);
					result.set(i, res_i - sol_i);
					if (Math.abs(result.get(i)) > residInfNorm)
						residInfNorm = Math.abs(result.get(i));
				}
				improve = (lastResidInfNorm - residInfNorm) / lastResidInfNorm;
				if (improve < 0)
					improve = 0;

				if (count[index] != 0)
					average[index] = improve;
				else {
					if (improve > average[index])
						average[index] = 0.1 * average[index] + 0.9 * improve;
					else
						average[index] = 0.3 * average[index] + 0.7 * improve;
				}
				if (average[index] > max) {
					max = average[index];
					maxIndex = index;
				} else {
					max = 0;
					for (i = 0; i < 41; i++)
						if (average[i] > max) {
							max = average[i];
							maxIndex = i;
						}
				}

				count[index]++;
				if (improve <= 0 && iterations > 5)
					stop = 1;
				if (iterations < 200 && stop != 0) {
					omega += 0.05;
					index += 2;
				} else {
					omega = (0.025 * maxIndex) + 1.0;
					index = maxIndex;
					if (index < 40 && count[index + 1] == 0) {
						omega += 0.025;
						index++;
					}
				}
				if (omega > 2.0) {
					omega = 2.0;
					index = 40;
				}

				lastResidInfNorm = residInfNorm;
			}
		}

		if (convergence > accuracy) {
			monitor.done();
			throw new SolverException("AIR failed to converge within "
					+ maxIterations + " iterations",
					SolverException.NOT_CONVERGED);
		}

		residInfNorm = 0;
		QC.transMult(solution, result);
		for (i = 0; i < tangible; i++) {
			double sol_i = solution.get(i);
			double res_i = result.get(i);
			result.set(i, res_i - sol_i);
			if (Math.abs(result.get(i)) > residInfNorm)
				residInfNorm = Math.abs(result.get(i));
		}
		try {
			checkVector(solution, residInfNorm);
		} finally {
			monitor.done();
		}
		unScale(solution);
		normalise(solution);
		return solution.getData();
	}

	private void checkVector(DenseVector solution, double infNorm)
			throws SolverException {
		for (int n = 0; n < tangible; n++)
			if (solution.get(n) < 0 && solution.get(n) > -EPS)
				solution.set(n, 0);

		double oneNorm = solution.norm(Norm.One);
		double min, max;

		min = solution.get(0);
		max = solution.get(0);
		double current;
		for (int n = 1; n < tangible; n++) {
			current = solution.get(n);
			if (current < min)
				min = current;
			if (current > max)
				max = current;
			if (!(current > 0) && !(current <= 0))
				throw new SolverException("NaN found in solution vector",
						SolverException.UNKNOWN);
		}

		if (infNorm > 10000 * accuracy) {
			throw new SolverException(
					"Unacceptably large normalised residual norm",
					SolverException.UNKNOWN);
		}
		if (min < 0 || max > 1.0)
			throw new SolverException("Steady-state element range violation",
					SolverException.UNKNOWN);
		if (Math.abs(oneNorm - 1.0) > 1e-10)
			throw new SolverException("Steady-state vector sum violation",
					SolverException.UNKNOWN);
	}

	/*
	 * private void terminate(String string, long maxIterations2) { throw new
	 * IllegalStateException(string);
	 * 
	 * }
	 */

	private void normalise(Vector v) {
		v.scale(1.0 / v.norm(no.uib.cipr.matrix.Vector.Norm.One));
	}

	private void unScale(Vector v) {
		for (int i = 0; i < scale.length; i++) {
			v.set(i, v.get(i) * scale[i]);
		}
	}

	public static void main(String[] args) throws IOException,
			DerivationException, InterruptedException, SolverException {
		String modelName = args[0];
		ASTNode node = PepaTools.parse(readText(modelName));
		OptionMap map = new OptionMap();
		map.put(OptionMap.AGGREGATE_ARRAYS, true);

		IStateSpace ss = PepaTools.derive(map, (ModelNode) node,
				new IProgressMonitor() {

					int worked = 0;

					public void beginTask(int amount) {
					}

					public void done() {
					}

					public boolean isCanceled() {
						return false;
					}

					public void setCanceled(boolean state) {
					}

					public void worked(int worked) {
						this.worked += worked;
						System.out.println("Worked:" + this.worked);
					}

				}, null /* IResourceManager.TEMP */);

		long tic = System.currentTimeMillis();
		map.put(OptionMap.SOLVER, OptionMap.HYDRA_AIR);
		// map.put(OptionMap.HYDRA_MAX_ITERATIONS, 1);
		ISolver analyser = SolverFactory.createSolver(ss, map);
		double[] newSolution = analyser.solve(new IProgressMonitor() {

			public void beginTask(int amount) {
				System.out.println("Solution:");
			}

			public void done() {
				System.out.println();
			}

			public boolean isCanceled() {
				return false;
			}

			public void setCanceled(boolean state) {
			}

			public void worked(int worked) {
				System.out.print(".");
			}

		});
		System.out.println("Solution took:"
				+ (System.currentTimeMillis() - tic) + " ms");
		ss.setSolution(newSolution);
		for (ThroughputResult r : ss.getThroughput()) {
			System.out
					.printf("%s : %f\n", r.getActionType(), r.getThroughput());
		}
	}

	private static String readText(String fileName) throws IOException {
		String result = null;

		if (fileName != null) {
			File file = new File(fileName);
			StringBuffer sb = new StringBuffer();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				String lineSearator = System.getProperty("line.separator");
				while ((line = br.readLine()) != null) { // while not
					// at the
					// end of the file
					// stream do
					sb.append(line);
					sb.append(lineSearator);
				}// next line
				result = sb.toString();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException ioe) {
						ioe.printStackTrace(System.err);
					}
				}
			}
		}// else: input unavailable

		return result;
	}// readText()

}
