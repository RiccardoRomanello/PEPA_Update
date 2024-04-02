/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import uk.ac.ed.inf.pepa.parsing.*;

public class ReferenceCounterVisitor extends MoveOnVisitor {

	private RateUsageManager rateManager;

	private ActionLevelManager levelManager;

	private ProcessUsageManager processManager;
	
	private ProblemManager problemManager;
	/*
	 * Determines if it is investigating rate definitions or process definitions
	 */
	private boolean investigatingRateDefinition = true;
	private boolean investigatingSystemEquation = false;

	public ReferenceCounterVisitor(ProblemManager problemManager) {
		rateManager = new RateUsageManager(problemManager);
		levelManager = new ActionLevelManager(problemManager);
		processManager = new ProcessUsageManager(problemManager);
		this.problemManager = problemManager;
	}

	public ProcessUsageManager getProcessManager() {
		return processManager;
	}

	public RateUsageManager getRateManager() {
		return rateManager;
	}
	
	public ActionLevelManager getActionLevelManager() {
		return levelManager;
	}

	public void visitActivityNode(ActivityNode activity) {
		activity.getRate().accept(this);
	}

	public void visitConstantProcessNode(ConstantProcessNode constant) {
		if (!investigatingSystemEquation)
			this.processManager.rhs(constant.getName());
		else {
			ProcessCounter c = this.processManager.getCounter(constant.getName());
			if (c.lhs == 0)
				problemManager.processNotDefinedError(constant.getName());
		}
	}

	public void visitModelNode(ModelNode model) {
		/* visits everything */
		this.investigatingRateDefinition = true;
		this.investigatingSystemEquation = false;
		for (RateDefinitionNode rateDef : model.rateDefinitions())
			rateDef.accept(this);
		this.investigatingRateDefinition = false;

		for (ActionTypeNode high_action : model.levelDeclarations().getHigh()) {
			levelManager.declare(high_action.getType(), LevelDeclarations.HIGH_LEVEL);
		}

		for (ActionTypeNode low_action : model.levelDeclarations().getLow()) {
			levelManager.declare(low_action.getType(), LevelDeclarations.LOW_LEVEL);
		}
		
		for (ProcessDefinitionNode processDef : model
				.processDefinitions())
			processDef.accept(this);
		this.investigatingSystemEquation = true;
		model.getSystemEquation().accept(this);
		/* generates warnings */
		rateManager.warn();
		levelManager.warn();
		processManager.warn();
	}

	public void visitPrefixNode(PrefixNode prefix) {
		prefix.getActivity().accept(this);
		prefix.getTarget().accept(this);
	}

	public void visitProcessDefinitionNode(
			ProcessDefinitionNode processDefinition) {
		this.processManager.lhs(processDefinition.getName().getName());
		processDefinition.getNode().accept(this);
	}

	public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
		this.rateManager.lhs(rateDefinition.getName().getName());
		rateDefinition.getRate().accept(this);
	}

	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode binRate) {
		binRate.getLeft().accept(this);
		binRate.getRight().accept(this);
	}
	
	public void visitAggregationNode(AggregationNode aggregation) {
		aggregation.getProcessNode().accept(this);
		aggregation.getCopies().accept(this);
	}

	public void visitVariableRateNode(VariableRateNode variableRate) {
		// lhs is recorded in rate definition node
		// this can only belong to rate rhs rate definitions or process
		// definitions
		if (this.investigatingRateDefinition == true)
			this.rateManager.rhs(variableRate.getName());
		else
			this.rateManager.process(variableRate.getName());
	}
}
