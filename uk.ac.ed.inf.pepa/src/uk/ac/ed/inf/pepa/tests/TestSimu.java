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
import java.util.ArrayList;
import java.util.HashSet;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.parsing.RateNode;
import uk.ac.ed.inf.pepa.sba.PEPAtoSBA;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class TestSimu {
	private static String[] rules = { "User1", "AC_DO_ADMIT1", "AC11", "AC3" };
	private static HashSet<String> throughputs = new HashSet<String>();

	private static int number_of_experiments = 10000;
	private static double start = 0;
	private static double stop = 10000;
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
		throughputs.add("enterBuilding");
		throughputs.add("doWriteEvent");
		throughputs.add("doReadData");
		ModelNode model = (ModelNode) PepaTools.parse(readText(modelName));
		// System.out.println(ASTSupport.toString(model));
		double[] experiment = new double[] { 100, 200, 300, 400, 500, 600,
		 700,
		 800, 900, 1000 };
		/* TO CHANGE POPULATION OF USERS */
		//int[] experiment = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30,
		//		40, 50, 100, 200 };
		// Get the user process
		//ProcessNode coop = model.getSystemEquation();
		//CooperationNode parent = null;
		//while (coop instanceof CooperationNode) {
		//	parent = (CooperationNode) coop;
		//	coop = parent.getLeft();
		//}
		//AggregationNode user = (AggregationNode) coop;
		//AggregationNode cardReader = (AggregationNode) parent.getRight();
		//System.out.println("Right: " + ((ConstantProcessNode) cardReader.getProcessNode())
		//		.getName());
		int RUNS = 1;
		long[] elapsed = new long[RUNS];
		ArrayList<RateDefinitionNode> rates = new
			ArrayList<RateDefinitionNode>();
		for (int e = 0; e < experiment.length; e++) {
			//cardReader.setCopies(experiment[e]);
			//user.setCopies(experiment[e]);
			  if (e == 0) { for (RateDefinitionNode node :
			  model.rateDefinitions()) { if (node.getRate() instanceof
			  RateDoubleNode) { if (((RateDoubleNode)
			  node.getRate()).getValue() == 2000) { rates.add(node);
			  System.out.println("Node added:" + node.getName().getName()); } }
			  } }
			
			  for (RateDefinitionNode node : rates) { RateDoubleNode newRate =
			  ASTFactory.createRate(); newRate.setValue(experiment[e]);
			  node.setRate(newRate); }
			 
			System.out.println("******");
			System.out.println("Running experiment with fast rate "
					+ experiment[e]);
			System.out.println("******");
			long tic, toc;
			for (int run = 0; run < RUNS; run++) {
				try {
					OptionsMap map = new OptionsMap();
					map.setValue(Parameter.Components, rules);
					map.setValue(Parameter.Solver,
							OptionsMap.Solver.Gibson_Bruck);
					map.setValue(Parameter.Start_Time, start);
					map.setValue(Parameter.Independent_Replications,
							number_of_experiments);
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
					double N = 0;
					for (int j = 1; j < numberOfElements; j++) {
						String species = r.getSpeciesOrdering()[j - 1];
						double value = r.getSimpleTimeSeries()[lastTimePoint][j];
						System.out.print(species + " : ");
						System.out.println(value);
						if (species.equals("User1")) {
							N = value;
						}
					}
					// for (String s : r.getSpeciesOrdering())
					// System.out.println(s);
					// System.out.println(r.returnSimpleResults());
					System.out.println("Throughput");
					for (int i = 0; i < r.getReactionOrdering().length; i++) {
						String throughput = r.getReactionOrdering()[i];
						double th = r.getReactionCounts()[i] / (stop - start);
						if (throughputs.contains(throughput)) {
							System.out.println(r.getReactionOrdering()[i]
									+ " : " + th);
							if (throughput.equals("enterBuilding"))
								System.out.println("Response time: " + N / th);
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.gc();
				long average = 0;
				for (long el : elapsed)
					average += el;
				System.out
						.println("Execution time: " + (double) average / RUNS);
			}

		}
	}
}
