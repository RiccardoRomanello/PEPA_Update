/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.LinkedList;

import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;

/**
 * This class produces parsing and static analysis problems. Problem reporting
 * is conveniently grouped in order to debug user messages more effectively.
 * 
 * @author mtribast
 * 
 */
public class ProblemManager {

	private ModelNode model;

	class Problems extends LinkedList<IProblem> {
		
		public Problems() {
			super();
		}

		public boolean add(IProblem problem) {
			if (hasError())
				return false;
			return super.add(problem);
		}

		public boolean hasError() {
			for (IProblem problem : problems)
				if (problem.isError())
					return true;
			return false;

		}
	}

	private Problems problems = new Problems();

	public ProblemManager(ModelNode model) {
		this.model = model;
	}

	public boolean hasError() {
		return problems.hasError();
	}

	/**
	 * Return true if there is at one critical error
	 * 
	 * @return true if there is one critical error
	 */
	public void transientStateProblem(String name) {
		String message = "Process " + name
				+ " could be a transient local state";
		problems.add(ProblemFactory.buildProblem(IProblem.TransientState,
				model.getResolver().getProcessDefinition(name), message));
	}

	public void processMultipleDeclaration(String name) {
		String message = "Process " + name + " declared multiple times";
		problems.add(ProblemFactory.buildProblem(
				IProblem.DuplicatedProcess, model.getResolver()
						.getProcessDefinition(name), message));
	}

	public void processNotDefinedError(String name) {
		String message = "Process " + name + " not defined";
		ASTNode[] affectedNodes = model.getResolver().getConstantUsage(
				name);
		for (ASTNode node : affectedNodes)
			problems.add(ProblemFactory.buildProblem(
					IProblem.UndefinedConstant, node, message));
	}

	public void rateNotDefinedProblem(String name) {
		String message = "Rate " + name + " not defined";
		for (ASTNode node : model.getResolver().getRateUsage(name))
			problems.add(ProblemFactory.buildProblem(
					IProblem.UndefinedRate, node, message));

	}
	

	public void actionLevelConflict(String name) {
		String message = "The level of action \"" + name + "\" has been declared both low and high";
		ASTNode[] affectedNodes = model.getResolver().getActionLevelDeclarations(name);
		for (ASTNode node : affectedNodes) {
			problems.add(ProblemFactory.buildProblem(IProblem.LevelConflict,
				node, message));
		}
	}

	public void actionLevelMultipleDeclaration(String name) {
		String message = "The level of action " + name + " has been declared multiple times";
		ASTNode[] affectedNodes = model.getResolver().getActionLevelDeclarations(name);
		for (ASTNode node : affectedNodes) {
			problems.add(ProblemFactory.buildProblem(IProblem.RedudantLevel,
				node, message));
		}

	}

	/* 
	 * Handling reversible actions related errors
	 */
	
	public void reversibleActionConflict(String name) {
		String message = "Process " + name + " which is reached by a reversible action, has more than one incoming edge";
		problems.add(ProblemFactory.buildProblem(IProblem.NonReversibleAction,
				model.getResolver().getProcessDefinition(name), message));

	}
	
	public void rateNotUsedProblem(String name) {
		String message = "Rate " + name + " not used";
		problems.add(ProblemFactory.buildProblem(IProblem.UnusedRate,
				model.getResolver().getRateDefinition(name), message));

	}

	public void rateMultipleDeclaration(String name) {
		String message = "Rate " + name + " declared multiple times";
		problems.add(ProblemFactory.buildProblem(IProblem.DuplicatedRate,
				model.getResolver().getRateDefinition(name), message));

	}

	public void unreachableProcessDefinition(
			ProcessDefinitionNode definition) {
		String message = "Process definition: "
				+ definition.getName().getName() + " never used";
		problems.add(ProblemFactory.buildProblem(
				IProblem.UnreachableDefinition, definition, message));
	}

	public void redundantAction(ActionTypeNode action) {
		String message = "Redundant action declared: " + action.getType();
		problems.add(ProblemFactory.buildProblem(
				IProblem.RedundantAction, action, message));
	}

	public void potentialDeadLock(ActionTypeNode action) {
		String message = "Potential local deadlock: " + action.getType();
		problems.add(ProblemFactory.buildProblem(
				IProblem.PotentialDeadLock, action, message));
	}

	public void unguardedProcess(String name) {
		String message = "Process: " + name + " unguarded";
		problems.add(ProblemFactory.buildProblem(
				IProblem.UnguardedProcess, model.getResolver()
						.getProcessDefinition(name), message));
	}

	public void cooperationLoop(String name) {
		String message = "Process: "
				+ name
				+ " defines a cooperation and contains a loop. (Please change this horrible sentence)";
		problems.add(ProblemFactory.buildProblem(
				IProblem.CooperationLoop, model.getResolver()
						.getProcessDefinition(name), message));
	}

	public void setProblems() {
		IProblem[] totalProblems = new IProblem[model.getProblems().length
				+ +problems.size()];
		System.arraycopy(model.getProblems(), 0, totalProblems, 0, model
				.getProblems().length);
		System.arraycopy(problems.toArray(), 0, totalProblems, model
				.getProblems().length, problems.size());
		model.setProblems(totalProblems);
	}
}
