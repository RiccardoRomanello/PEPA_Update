/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.analysis.IProblem;

import java.util.HashSet;

/**
 * PEPA model AST node type. This is the type of the root of an AST, as returned
 * by the parser ({@link} ASTParser).
 * <p>
 * The source range for this type of node is ordinarily the entire source file,
 * including leading and trailing whitespace and comments.
 * </p>
 */
public class ModelNode extends ASTNode {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ModelNode.class);

	private LevelDeclarations levelDeclarations = new LevelDeclarations();
	
	private HashSet<String> reversibleActionTypes = new HashSet<String>();
	
	private RateDefinitions rateDefinitions = new RateDefinitions();

	private ProcessDefinitions processDefinitions = new ProcessDefinitions();

	private ProcessNode equation = null;

	private IProblem[] problems = new IProblem[0];
	
	private NodeResolver locationResolver = new Resolver(this);

	ModelNode() {
		super();
	}
	
	public NodeResolver getResolver() {
		return locationResolver;
	}

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitModelNode(this);
	}

	/**
	 * The action level declarations for this model. 
	 * 
	 * @return the action level declarations of this model
	 */
	public LevelDeclarations levelDeclarations() {
		return levelDeclarations;
	}

	/**
	 * The reversible action types for this model. 
	 * 
	 * @return the reversible action types for this model
	 */
	public HashSet<String> reversibleActionTypes() {
		return reversibleActionTypes;
	}
	
	/**
	 * The rate definitions for this model. This property is a list-property of
	 * of instances of <code>RateDefinitionNode</code>
	 * 
	 * @return rate definitions of this model
	 */
	public RateDefinitions rateDefinitions() {
		return rateDefinitions;
	}

	/**
	 * The process definitions of this model. This is a list-property of
	 * instances of <code>ProcessDefinitionNode</code>
	 * 
	 * @return process definitions of this model
	 * 
	 */
	public ProcessDefinitions processDefinitions() {
		return processDefinitions;
	}

	public ProcessNode getSystemEquation() {
		return equation;
	}

	/**
	 * The system equation for this model
	 * 
	 * @param equation
	 */
	public void setSystemEquation(ProcessNode equation) {
		this.equation = equation;
	}

	/**
	 * Set a problem array for this model
	 * 
	 * @param problems
	 *            problem array for this model
	 * @throw <code>NullPointerException</code> if the array is null
	 */
	public void setProblems(IProblem[] problems) {
		if (problems == null)
			throw new NullPointerException();
		this.problems = problems;
	}

	/**
	 * Gets the problem array for this model
	 * 
	 * @return the problem array for this model. It never returns null: If this
	 *         model has no problems, then it returns a zero array.
	 */
	public IProblem[] getProblems() {
		return this.problems;
	}
}
