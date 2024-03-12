/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.ctmc.solution.internal.hydra.SteadyStateAnalyser;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

import com.mindprod.ledatastream.LEDataInputStream;
import com.mindprod.ledatastream.LEDataOutputStream;

/**
 * Exports the Markov chain into the binary format for Hydra.
 * <p>
 * The binary format for Hydra on a 64bit machine is as follows
 * 
 * The matrix file has the following pattern
 * <p>
 * 
 * <pre>
 * int (number of outgoing transitions)
 * long1   | long2   | ... | longN   (indices of transitions)
 * double1 | double2 | ... | doubleN (rates)
 * </pre>
 * 
 * </p>
 * <p>
 * Long have different representations in C and Java. In C it is a 32bit number,
 * whereas in Java is 64bit. We use a little endian encoding of a Java int to
 * represent it. A C double is encoded like a Java double.
 * </p>
 * <p>
 * The solution file is a long denoting the number of elements, followed by a
 * list of double. Same considerations hold for the encoding of this long.
 * </p>
 * More needs to be done to test the class on different platforms.
 * 
 * @author mtribast
 * 
 */
public class HydraExporter {

	private IStateSpace stateSpace;

	private String fileName;

	private int maxChildren = -1;

	/**
	 * 
	 * @param stateSpace
	 *            the state space to be exported
	 * @param fileName
	 *            path to the output binary file
	 */
	public HydraExporter(IStateSpace stateSpace, String fileName) {
		if (stateSpace == null)
			throw new NullPointerException();
		if (fileName == null)
			throw new NullPointerException();
		this.stateSpace = stateSpace;
		this.fileName = fileName;
	}

	/**
	 * Performs the export.
	 * 
	 * @param monitor
	 *            a progress monitor, may be null
	 * @throws IOException
	 */
	public void export(IProgressMonitor monitor) throws IOException {
		LEDataOutputStream os = new LEDataOutputStream(
				new BufferedOutputStream(new FileOutputStream(fileName)));
		if (monitor == null)
			monitor = new DoNothingMonitor();
		// Current format is
		// int kids
		// array of longs for destinations
		// array of doubles for weights
		int[] indices = null;
		int reportingPeriod = this.stateSpace.size() / 100 + 1;
		monitor.beginTask(100);
		for (int i = 0; i < stateSpace.size(); i++) {

			if (i % reportingPeriod == 0)
				monitor.worked(1);
			indices = stateSpace.getOutgoingStateIndices(i);
			maxChildren = Math.max(indices.length, maxChildren);
			os.writeInt(indices.length);
			for (int j : indices) {
				os.writeInt(j);
			}
			for (int j : indices) {
				os.writeDouble(stateSpace.getRate(i, j));
			}
		}
		os.close();
		monitor.done();
	}

	/**
	 * Returns the state space size of the model being exported
	 * 
	 * @return
	 */
	public int getStateSpaceSize() {
		return stateSpace.size();
	}

	/**
	 * Returns the maximum number of children. Must be called after
	 * {@link #export(IProgressMonitor)} is called
	 * 
	 * @return the maximum number of children
	 * @throws IllegalStateException
	 *             if called before export is performed
	 */
	public int getMaxChildren() {
		if (maxChildren == -1)
			throw new IllegalStateException();
		return maxChildren;
	}

	public static void main(String[] args) throws IOException,
			DerivationException, InterruptedException, SolverException {
		String SOLVER_PATH = "C:/GanymedeWorkspace/hydra/src/build/";
		String SOLVER_NAME = "hydra-steady-2.exe";
		String MATRIX_PATH = "C:/ipcoutput/out.matrix";
		String RESULT_PATH = MATRIX_PATH + ".RESULT";
		String modelName = args[0];
		ASTNode node = PepaTools.parse(readText(modelName));
		OptionMap map = new OptionMap();
		map.put(OptionMap.AGGREGATE_ARRAYS, true);
		// map.put(OptionMap.DERIVATION_STORAGE,
		// OptionMap.DERIVATION_DISK_STORAGE);

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

		HydraExporter exporter = new HydraExporter(ss, MATRIX_PATH);
		exporter.export(null);
		System.out.printf("Solving model (%d states)\n", ss.size());
		Process p = Runtime.getRuntime().exec(
				new String[] { SOLVER_PATH + SOLVER_NAME, MATRIX_PATH,
						Integer.toString(ss.size()),
						Integer.toString(exporter.getMaxChildren()) });
		InputStream stdout = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(stdout);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ((line = br.readLine()) != null)
			System.out.println(line);
		int exitCode = p.waitFor();
		if (exitCode != 0) {
			InputStream stderr = p.getInputStream();
			InputStreamReader isre = new InputStreamReader(stderr);
			BufferedReader bre = new BufferedReader(isre);
			while ((line = bre.readLine()) != null)
				System.err.println(line);
			System.exit(1);
		}
		double[] solution = new HydraImporter(RESULT_PATH).importSolution();
		ss.setSolution(solution);
		for (ThroughputResult r : ss.getThroughput()) {
			System.out
					.printf("%s : %f\n", r.getActionType(), r.getThroughput());
		}
		long tic = System.currentTimeMillis();
		map.put(OptionMap.SOLVER, OptionMap.HYDRA_AIR);
		//map.put(OptionMap.HYDRA_MAX_ITERATIONS, 1);
		SolverFactory.createSolver(ss, map);
		SteadyStateAnalyser analyser = new SteadyStateAnalyser(ss, map);
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
		System.out.println("Solution took:" + (System.currentTimeMillis() - tic));
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
