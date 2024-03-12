/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution.internal.mtj;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;

public class DirectSolver implements ISolver {

	private FlexCompRowMatrix generator;
	private DenseVector x;
	private DenseVector b;

	public DirectSolver(FlexCompRowMatrix generator, DenseVector b,
			DenseVector x) {
		this.generator = generator;
		this.x = x;
		this.b = b;
	}

	public double[] solve(IProgressMonitor monitor) throws SolverException {
		if (monitor == null)
			monitor = new DoNothingMonitor();
		
		monitor.beginTask(IProgressMonitor.UNKNOWN);
		DenseMatrix A = new DenseMatrix(generator);
		double[] solution = new DenseVector(A.solve(b, x)).getData();
		monitor.done();
		return solution;
	}

}
