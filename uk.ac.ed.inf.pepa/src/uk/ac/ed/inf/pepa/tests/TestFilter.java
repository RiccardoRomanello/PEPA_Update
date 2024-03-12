/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.StateSpaceBuilderFactory;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class TestFilter {

	public static void main(String[] args) throws IOException {

		ModelNode model = (ModelNode) PepaTools.parse(readText(args[0]));

		for (uk.ac.ed.inf.pepa.analysis.IProblem p : model.getProblems()) {
			if (p.isError()) {
				System.err.println(p.getMessage());
				System.exit(1);
			}
		}
		OptionMap map = new OptionMap();
		map.put(OptionMap.SOLVER, OptionMap.MTJ_GMRES);
		map.put(OptionMap.AGGREGATE_ARRAYS, true);
		map.put(OptionMap.DERIVATION_STORAGE, OptionMap.DERIVATION_DISK_STORAGE);
		
		IStateSpaceBuilder builder = StateSpaceBuilderFactory
				.createStateSpaceBuilder(
						model, map, null);

		IStateSpace ss;
		try {
			ss = builder.derive(false, null);
			ISolver solver = SolverFactory.createSolver(ss, map);
			solver.solve(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String readText(String fileName) throws IOException {
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