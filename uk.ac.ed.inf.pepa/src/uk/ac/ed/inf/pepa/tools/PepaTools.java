/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.analysis.StaticAnalyser;
import uk.ac.ed.inf.pepa.analysis.internal.ProblemFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.StateSpaceBuilderFactory;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.internal.NewPepaSymbolFactory;
import uk.ac.ed.inf.pepa.parsing.internal.PepaLexer;
import uk.ac.ed.inf.pepa.parsing.internal.PepaParser;

/**
 * Facade for core PEPA services
 * 
 * @author mtribast
 * 
 */
public class PepaTools {

	static Logger logger = Logger.getLogger(PepaTools.class);

	/**
	 * The method parses and perform static analysis on the source file, here
	 * represented as a <code>String</code>.
	 * <p>
	 * Parsing errors are held by the AST Node <code>ModelNode</code>.
	 * 
	 * @param source
	 *            the string representing the model
	 * @return the abstract syntax tree of the model
	 * @throws NullPointerException
	 *             if source is null
	 */
	public synchronized static ASTNode parse(String source) {
		if (source == null)
			throw new NullPointerException("Source string is null");

		// BasicConfigurator.configure();

		// ComplexSymbolFactory symbolFactory = new PepaSymbolFactory();
		NewPepaSymbolFactory symbolFactory = new NewPepaSymbolFactory();
		PepaParser parser = new PepaParser(new PepaLexer(new StringReader(
				source), symbolFactory), symbolFactory);

		ModelNode result = null;
		try {
			Symbol symbol = parser.parse();
			if (symbol != null)
				result = (ModelNode) symbol.value;
		} catch (Exception e) {
			logger.debug("When parsing, it should never return an exception of"
					+ e.getClass().getName());
		}
		if (result == null) {
			result = ASTFactory.createModel();
			IProblem generalProblem = ProblemFactory.createProblem(
					IProblem.UndefinedError, 0, 0, 0, 0, 0, 0,
					"Model does not have system equation");
			result.setProblems(new IProblem[] { generalProblem });
		}

		return result;

	}

	/**
	 * Perform static analysis on the model. Static analysis warning and
	 * messages are added to the problems of the given model (see
	 * {@link ModelNode#getProblems()}.
	 * 
	 * @param astModel
	 *            the model to perform static analysis on
	 * @throws NullPointerException
	 *             if astModel is null
	 */
	public static StaticAnalyser doStaticAnalysis(ModelNode astModel) {
		if (astModel == null)
			throw new NullPointerException("No model to analyse");
		StaticAnalyser analyser = new StaticAnalyser(astModel);
		return analyser;
	}

	/**
	 * Creates a state space explorator.
	 * 
	 * @param model
	 *            the model to explore
	 * @param map
	 *            a map of options, or null to act on a default one.
	 * @return the state space builder
	 * @throws NullPointerException
	 *             if model is null.
	 */
	public static IStateSpaceBuilder getBuilder(ModelNode model, OptionMap map,
			IResourceManager manager) {
		if (model == null)
			throw new NullPointerException("Model is null.");
		if (map == null) {
			System.err.println("Map is null!");
			map = new OptionMap();
		}
		return StateSpaceBuilderFactory.createStateSpaceBuilder(model, map, manager);
	}

	/**
	 * Derive the state space of a model. An instance of <code>ModelNode</code>
	 * has to be passed, and that model has to be already analysed statically.
	 * If the derivation process is correct, an instance of
	 * <code>IStateSpace</code> is returned. Otherwise, a
	 * <code>DerivationException</code> is raised. <br>
	 * This is a helper method based on
	 * {@link #getBuilder(ModelNode, OptionMap)}.
	 * 
	 * @param derivable
	 *            model to be derived
	 * @param monitor
	 *            progess monitor for long-running operations, or
	 *            <code>null</code>
	 * @return the state space of the model
	 * @throws DerivationException
	 *             if the derivation raises an error.
	 * @throws NullPointerException
	 *             if model is <code>null</code>
	 */
	public static IStateSpace derive(OptionMap map, ModelNode model,
			IProgressMonitor monitor, IResourceManager manager)
			throws DerivationException {
		if (model == null)
			throw new NullPointerException();
		return getBuilder(model, map, manager).derive(false, monitor);
	}

	public static ISolver getSolver(OptionMap map, IStateSpace stateSpace) {
		if (stateSpace == null)
			throw new NullPointerException();
		if (map == null)
			map = new OptionMap();
		return SolverFactory.createSolver(stateSpace, map);
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