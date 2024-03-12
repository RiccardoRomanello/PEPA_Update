/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.io.IOException;

import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.StateSpaceBuilderFactory;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class ArrayTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws DerivationException
	 * @throws SolverException
	 */
	public static void main(String[] args) throws IOException,
			DerivationException, SolverException {
		int N = 5;
		long elapsed = 0;
		int solverId = OptionMap.MTJ_DIRECT;
		String path = "/Users/Mirco/Workspaces/workspace/uk.ac.ed.inf.pepa/tests/state-space-exploration/";
		String modelFile = path + "kdc.pepa";
		OptionMap map = new OptionMap();
		map.put(OptionMap.DERIVATION_STORAGE, OptionMap.DERIVATION_MEMORY_STORAGE);
		map.put(OptionMap.SOLVER, solverId);
		for (int i = 0; i < N; i++) {
			ModelNode model = (ModelNode) PepaTools.parse(TestFilter
					.readText(modelFile));
			IStateSpaceBuilder b = StateSpaceBuilderFactory
					.createStateSpaceBuilder(model, map, null);
			long tic = System.currentTimeMillis();
			IStateSpace ss = b.derive(false, null);
			elapsed += System.currentTimeMillis() - tic;
			if (i == 0) {
				System.out.println("Size: " + ss.size());
				ISolver solver = SolverFactory.createSolver(ss, map);
				double[] solution = solver.solve(null);
				ss.setSolution(solution);
				for (ThroughputResult r : ss.getThroughput()) {
					System.out.println(r.getActionType() + " -> "
							+ r.getThroughput());
				}
			}
		}
		System.out.println("Average: " + elapsed / (double) 5);

	}

}
