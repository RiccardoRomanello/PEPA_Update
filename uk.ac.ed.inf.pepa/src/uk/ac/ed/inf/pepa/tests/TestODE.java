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

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.sba.PEPAtoSBA;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class TestODE {
	private static String[] rules = { "User1", "AC_DO_ADMIT1", "AC11", "AC3"};
	
	private static double start = 0;
	private static double stop = 10000;
	private static int datapoints = 100;

	public static void main(String[] args) throws IOException {
		System.out.println("ODE Analysis");
		System.out.println("Reading file: " + args[0]);
		dothejob(args[0]);
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

	private static void dothejob(String modelName) throws IOException {
		ModelNode model = (ModelNode) PepaTools.parse(readText(modelName));
		//System.out.println(ASTSupport.toString(model));
		int[] experiment = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30,
				40, 50, 100, 200 };
		// Get the user process
		ProcessNode coop = model.getSystemEquation();
		while (coop instanceof CooperationNode)
			coop = ((CooperationNode) coop).getLeft();
		AggregationNode user = (AggregationNode) coop;
		int RUNS = 1;
		long[] elapsed = new long[RUNS];
		for (int e = 0; e < experiment.length; e++) {
			RateDoubleNode node = ASTFactory.createRate();
			node.setValue(experiment[e]);
			user.setCopies(node);
			System.out.println("******");
			System.out.println("Running experiment with " + experiment[e] + " copies");
			System.out.println("******");
			long tic, toc;
			for (int run = 0; run < RUNS; run++) {
				try {
					OptionsMap map = new OptionsMap();
					map.setValue(Parameter.Components, rules);
					map.setValue(Parameter.Solver,
							OptionsMap.Solver.DOPR);
					map.setValue(Parameter.Absolute_Error, 1E-4);
					map.setValue(Parameter.Relative_Error, 1E-4);
					map.setValue(Parameter.Step_Size, 1E-3);
					map.setValue(Parameter.Start_Time, start);
					map.setValue(Parameter.Stop_Time, stop);
					map.setValue(Parameter.Data_Points, datapoints);
					PEPAtoSBA p2s = new PEPAtoSBA(model);
					p2s.parseModel();
					SBAtoISBJava modelSBA = new SBAtoISBJava(p2s);
					modelSBA.generateISBJavaModel("name", true);
					modelSBA.initialiseSimulator(map);
					Results r = null;
					// We need to have the parameter order in the first place,
					// and
					// no-need to do it several times
					tic = System.currentTimeMillis();
					r = modelSBA.runModel();
					// System.out.println(r.returnSimpleResults());
					// System.out.println(r.getSimpleTimeSeries().length);
					// System.out.println(r.getSimpleTimeSeries()[0].length);
					toc = System.currentTimeMillis();
					elapsed[run] = toc - tic;
					System.out.println("Elapsed: " + elapsed[run]);
					// System.out.println("Elapsed " + (toc - tic) + " ms");
					int lastTimePoint = r.getSimpleTimeSeries().length - 1;
					int numberOfElements = r.getSimpleTimeSeries()[lastTimePoint].length;
					for (int j = 1; j < numberOfElements; j++) {
						System.out.print(r.getSpeciesOrdering()[j - 1] + " : ");
						System.out
								.println(r.getSimpleTimeSeries()[lastTimePoint][j]);
					}
					// for (String s : r.getSpeciesOrdering())
					// System.out.println(s);
					// System.out.println(r.returnSimpleResults());
		
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.gc();
				long average = 0;
				for (long el : elapsed)
					average += el;
				System.out.println("Execution time: " + (double) average / RUNS);
			}
			
			
		}
	}
}
