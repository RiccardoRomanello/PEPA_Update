/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.io.IOException;

import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.PopulationLevelResult;
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

public class SolverFramework {

	/**
	 * @param args
	 * @throws SolverException
	 */
	public static void main(String[] args) throws IOException,
			DerivationException, SolverException {
		// String path = "C:/Europa-Workspace/uk.ac.ed.inf.pepa";
		String path = "C:/Europa-Workspace/uk.ac.ed.inf.pepa/tests/state-space-exploration/";
		// String path =
		// "/Users/Mirco/Workspaces/workspace/uk.ac.ed.inf.pepa/tests/state-space-exploration/";
		String[] EXAMPLES = { 
		"C:/Europa-Workspace/uk.ac.ed.inf.pepa/tests/pasm/acs-cpu.pepa",
		// path + "kdc.pepa"
				//path + "ws-9-8-6-6.pepa"
		//   path + "matlab.pepa"
		// path + "ws-6-5-4-4.pepa"
		// path + "large-t.pepa"
		// path + "alternatingbit.pepa"
		// path + "ws-large.pepa"
		// path + "RKIP_MEK.pepa",
		// path + "large-t.pepa"
		// path + "medium-t.pepa"
		//// path + "x-large-t.pepa"
		 //path + "verysimple.pepa"
		//path + "static/deadlockfree/jobshop3.pepa"
		// "C:/runtime-EclipseApplication/WS/kdc.pepa"
		};

		int n = 1;
		OptionMap map = new OptionMap();
		map.put(OptionMap.SOLVER,OptionMap.SIMPLE_CGS);
		map.put(OptionMap.SIMPLE_OVER_RELAXATION_FACTOR,0.1);
		//map.put(OptionMap.SIMPLE_TOLERANCE, 1e-16);
		//map.put(OptionMap.SIMPLE_MAX_ITERATION, 10000);
		map.put(OptionMap.DERIVATION_STORAGE, OptionMap.DERIVATION_MEMORY_STORAGE);
		map.put(OptionMap.DERIVATION_KIND, OptionMap.DERIVATION_SEQUENTIAL);
		map.put(OptionMap.DERIVATION_PARALLEL_NUM_WORKERS, 2);
		IStateSpace ss = null;
		for (String file : EXAMPLES) {
			System.out.println("Reading " + file);
			for (int i = 0; i < n; i++) {
				System.out.println("Run " + i);
				ModelNode model = (ModelNode) PepaTools.parse(TestFilter
				.readText(file));
				long deriveTic = System.currentTimeMillis();
				ss = derive(model, map);
				long deriveToc = System.currentTimeMillis();
				
				System.out.println("Model derived: " + (deriveToc - deriveTic));
				
				ISolver solver = SolverFactory.createSolver(ss, map);
				long solverTic = System.currentTimeMillis();
				double[] solution = solver.solve(null);
				long solverToc = System.currentTimeMillis();
				System.out.println("Solved: " + (solverToc-solverTic));
				if (i == 0) {
					ss.setSolution(solution);
					for (PopulationLevelResult r : ss.getPopulationLevels()) {
							System.out.println(r.getName() + " -> "
									+ r.getMean());
					}
					for (ThroughputResult r : ss.getThroughput()) {
							System.out.println(r.getActionType() + " -> "
									+ r.getThroughput());
					}

				}
				System.gc();
			}
	
		}

	}

	private static IStateSpace derive(ModelNode model, OptionMap map)
			throws DerivationException {
		IStateSpaceBuilder builder = StateSpaceBuilderFactory
				.createStateSpaceBuilder(model, map,
						IResourceManager.TEMP);
		return builder.derive(false, null);
	}

}
