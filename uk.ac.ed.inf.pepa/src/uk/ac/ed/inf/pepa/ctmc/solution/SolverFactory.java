/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.AbstractIterativeSolver;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.hydra.SteadyStateAnalyser;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.mtj.DirectSolver;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.mtj.MTJFactory;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.mtj.MTJSolver;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.CGS;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.GaussSeidel;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.Generator;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.simple.JacobiSolver;

public class SolverFactory {

	public static ISolver createSolver(IStateSpace stateSpace, OptionMap options) {
		if (stateSpace == null)
			throw new NullPointerException();
		if (options == null)
			options = new OptionMap();

		int solverId = (Integer) options.get(OptionMap.SOLVER);

		if (solverId == OptionMap.HYDRA_AIR) {
			ISolver hydraSolver = new SteadyStateAnalyser(stateSpace, options);
			return hydraSolver;
		} else if (solverId == OptionMap.SIMPLE_JACOBI
				|| solverId == OptionMap.SIMPLE_GAUSS_SEIDEL
				|| solverId == OptionMap.SIMPLE_CGS) {
			Generator generator = (Generator) stateSpace
					.getGeneratorMatrix(Generator.class);
			if (generator == null)
				throw new IllegalArgumentException("Could not create solver");
			if (solverId == OptionMap.SIMPLE_JACOBI)
				return new JacobiSolver(generator, options);
			else if (solverId == OptionMap.SIMPLE_GAUSS_SEIDEL)
				return new GaussSeidel(generator, options);
			else if (solverId == OptionMap.SIMPLE_CGS)
				return new CGS(generator, options);
			else
				throw new IllegalArgumentException();
		} else {
			return handleMTJSolver(stateSpace, options);
		}

	}

	private static ISolver handleMTJSolver(IStateSpace stateSpace,
			OptionMap options) {
		FlexCompRowMatrix generator = (FlexCompRowMatrix) stateSpace
				.getGeneratorMatrix(FlexCompRowMatrix.class);
		if (generator == null)
			return null;
		/* Transpose Matrix and impose normalisation condition */
		prepareGeneratorForSolution(generator);
		DenseVector b = new DenseVector(stateSpace.size());
		b.set(stateSpace.size() - 1, 1);
		DenseVector x = new DenseVector(stateSpace.size());

		int solverId = (Integer) options.get(OptionMap.SOLVER);
		ISolver solver = null;
		if (solverId == OptionMap.MTJ_DIRECT) {
			solver = new DirectSolver(generator, b, x);
		} else {
			/* iterative solver */
			AbstractIterativeSolver s = MTJFactory.createSolver(solverId, x,
					options);
			solver = new MTJSolver(s, generator, b, x, options);
		}
		return solver;

	}

	private static void prepareGeneratorForSolution(FlexCompRowMatrix generator) {
		int size = generator.numRows();
		diagonalSetup(generator);
		generator.transpose();
		for (int column = 0; column < size; column++) {
			generator.set(size - 1, column, 1);
		}
	}

	private static void diagonalSetup(FlexCompRowMatrix generator) {
		int size = generator.numRows();
		double sum;
		// diagonal setup
		for (int i = 0; i < size; i++) {
			sum = 0;
			for (int j = 0; j < size; j++) {
				sum += generator.get(i, j);
			}
			sum -= generator.get(i, i);
			generator.set(i, i, -sum);
		}
	}

}
