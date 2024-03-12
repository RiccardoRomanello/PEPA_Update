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
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;

public class TestCMDL {
	
	private static String[] rules = { "User0", "User1", "EnterBuilding0",
		"EnterBuilding1", "AC_DO_ADMIT1", "AC11", "AC3" };

	private static double start = 0;

	private static double stop = 1000;

	private static int datapoints = 100;

	public static void main(String[] args) throws IOException {
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
		OptionsMap map = new OptionsMap();
		map.setValue(Parameter.Components, rules);
		map.setValue(Parameter.Solver, OptionsMap.Solver.DOPR);
		map.setValue(Parameter.Absolute_Error, 1E-5);
		map.setValue(Parameter.Relative_Error, 1E-5);
		map.setValue(Parameter.Start_Time, start);
		map.setValue(Parameter.Stop_Time, stop);
		map.setValue(Parameter.Data_Points, datapoints);
		String cmdlModel = readText(modelName);
		int[] experiments = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30,
				40, 50, 100, 200 };
		long[] elapsed = new long[experiments.length];
		double[][] results = new double[experiments.length][rules.length];
		long tic, toc;
		for (int i = 0; i < experiments.length; i++) {
			System.out.println("******");
			System.out.println("Experiment " + (i + 1) + " Users="
					+ experiments[i]);
			System.out.println("******");
			String prefix = "USERS = " + experiments[i] + ";\n";
			try {
				SBAtoISBJava modelSBA = SBAtoISBJava.generateModel(prefix
						+ cmdlModel);
				// modelSBA.generateISBJavaModel("name", true);
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
				elapsed[i] = toc - tic;
				System.out.println("Elapsed: " + elapsed[i]);
				// System.out.println("Elapsed " + (toc - tic) + " ms");
				int lastTimePoint = r.getSimpleTimeSeries().length - 1;
				int numberOfElements = r.getSimpleTimeSeries()[lastTimePoint].length;
				for (int j = 1; j < numberOfElements; j++) {
					String species = r.getSpeciesOrdering()[j - 1];
					double value = r.getSimpleTimeSeries()[lastTimePoint][j];
					System.out.print(species + " : ");
					System.out.println(value);
					results[i][j-1] = value;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.gc();
		long average = 0;
		for (long el : elapsed)
			average += el;
		System.out.println("***");
		System.out
				.println("Average Execution time: " + (double) average / experiments.length);
		System.out.println("***");
		// OUTPUT OF CSV FILE
		System.out.print("#");
		for (String rule : rules) {
			System.out.print(rule + ",");
		}
		System.out.println();
		for (int k = 0; k < results.length; k++) {
			for (int z = 0; z < rules.length; z++) {
				System.out.print(results[k][z] + ((z==rules.length-1)?"":","));
			}
			System.out.println();
		}
	}
}
