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
import no.uib.cipr.matrix.sparse.AMG;
import no.uib.cipr.matrix.sparse.AbstractIterativeSolver;
import no.uib.cipr.matrix.sparse.BiCG;
import no.uib.cipr.matrix.sparse.BiCGstab;
import no.uib.cipr.matrix.sparse.CG;
import no.uib.cipr.matrix.sparse.CGS;
import no.uib.cipr.matrix.sparse.Chebyshev;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.GMRES;
import no.uib.cipr.matrix.sparse.ICC;
import no.uib.cipr.matrix.sparse.ILU;
import no.uib.cipr.matrix.sparse.ILUT;
import no.uib.cipr.matrix.sparse.IR;
import no.uib.cipr.matrix.sparse.Preconditioner;
import no.uib.cipr.matrix.sparse.QMR;
import no.uib.cipr.matrix.sparse.SSOR;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

/**
 * Creates instances of MTJ solvers
 * 
 * @author mtribast
 * 
 */
public class MTJFactory {

	/**
	 * Creates Iterative Solvers from the MTJ package
	 * 
	 * @param solverId
	 *            solver unique identifier
	 * @param x
	 *            template for sizing the solver
	 * @param options
	 *            solver-defined map of options
	 * @return the iterative solver created according to the given parameters
	 */
	public static AbstractIterativeSolver createSolver(int solverId,
			DenseVector x, OptionMap options) {
		switch (solverId) {
		case OptionMap.MTJ_BICG:
			return new BiCG(x);
		case OptionMap.MTJ_BICG_STAB:
			return new BiCGstab(x);
		case OptionMap.MTJ_CG:
			return new CG(x);
		case OptionMap.MTJ_CGS:
			return new CGS(x);
		case OptionMap.MTJ_GMRES:
			return handleGMRES(x, options);
		case OptionMap.MTJ_IR:
			return new IR(x);
		case OptionMap.MTJ_QMR:
			return new QMR(x);
		case OptionMap.MTJ_CHEBYSHEV:
			return handleCheb(x, options);
		}
		throw new IllegalArgumentException();
	}

	public static Preconditioner createPreconditioner(int id, Matrix template,
			OptionMap options) {
		// TODO Implement preconditioner with the right constants
		switch (id) {
		case OptionMap.AMG:
			return handleAMG(true, template, options);
		case OptionMap.AMG_NO_SSOR:
			return handleAMG(false, template, options);
		case OptionMap.ICC:
			// TODO here is a copy of a matrix: study performance!
			// TODO this is a temporary implementation
			return new ICC(new CompRowMatrix(template));
		case OptionMap.ILU:
			// TODO again copy
			return new ILU(new CompRowMatrix(template));
		case OptionMap.ILUT:
			return handleILUT(new FlexCompRowMatrix(template), options);
		case OptionMap.SSOR:
			// TODO here is a copy of a matrix: study performance!
			// TODO this is a temporary implementation
			return handleSSOR(new CompRowMatrix(template), options);
		case OptionMap.DIAGONAL:
			return new DiagonalPreconditioner(template.numRows());
		}
		throw new IllegalArgumentException();
	}

	private static Preconditioner handleSSOR(CompRowMatrix matrix,
			OptionMap options) {
		return new SSOR(matrix, (Boolean) options.get(OptionMap.SSOR_REVERSE),
				(Double) options.get(OptionMap.SSOR_OMEGA_F), (Double) options
						.get(OptionMap.SSOR_OMEGA_R));
	}

	private static Preconditioner handleILUT(FlexCompRowMatrix matrix,
			OptionMap options) {
		return new ILUT(matrix, (Double) options.get(OptionMap.ILUT_TAU),
				(Integer) options.get(OptionMap.ILUT_P));
	}

	private static Preconditioner handleAMG(boolean withSSOR, Matrix template,
			OptionMap options) {
		// TODO handle class cast exception
		double pref, prer, postf, postr, pre, post;
		int nu1 = (Integer) options.get(OptionMap.AMG_NU_1_KEY);
		int nu2 = (Integer) options.get(OptionMap.AMG_NU_2_KEY);
		int min = (Integer) options.get(OptionMap.AMG_MIN_KEY);
		int gamma = (Integer) options.get(OptionMap.AMG_GAMMA_KEY);
		double omega = (Double) options.get(OptionMap.AMG_OMEGA_KEY);
		if (withSSOR == true) {
			pref = (Double) options.get(OptionMap.AMG_OMEGA_PRE_F_KEY);
			prer = (Double) options.get(OptionMap.AMG_OMEGA_PRE_R_KEY);
			postf = (Double) options.get(OptionMap.AMG_OMEGA_POST_F_KEY);
			postr = (Double) options.get(OptionMap.AMG_OMEGA_POST_R_KEY);
			return new AMG(pref, prer, postf, postr, nu1, nu2, gamma, min,
					omega);
		} else {
			pre = (Double) options.get(OptionMap.AMG_OMEGA_PRE_KEY);
			post = (Double) options.get(OptionMap.AMG_OMEGA_POST_KEY);
			return new AMG(pre, post, nu1, nu2, gamma, min, omega);
		}

	}

	private static Chebyshev handleCheb(DenseVector x, OptionMap options) {
		// TODO Handle cast here as well
		double min = (Double) options.get(OptionMap.CHEB_MIN);
		double max = (Double) options.get(OptionMap.CHEB_MAX);
		return new Chebyshev(x, min, max);
	}

	private static GMRES handleGMRES(DenseVector x, OptionMap options) {
		int restart = (Integer) options.get(OptionMap.GMRES_RESTART);
		return new GMRES(x, restart);

	}

}
