/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import java.util.*;

public class Resolver implements NodeResolver {

	private ModelNode model;

	public Resolver(ModelNode node) {
		this.model = node;
	}

	public ASTNode getProcessDefinition(String name) {
		for (ProcessDefinitionNode def : model.processDefinitions())
			if (def.getName().getName().equals(name))
				return def;
		return null;
	}

	public ASTNode[] getActionLevelDeclarations(String type)
	{
		final LinkedList<ASTNode> usage = new LinkedList<ASTNode>();
		
		model.accept(new uk.ac.ed.inf.pepa.parsing.MoveOnVisitor() {
			public void visitModelNode(ModelNode model) {
				for (Actions actions : model.levelDeclarations().levelDefinitions) {
					for (ActionTypeNode action : actions) {
						if (action.getType().equals(type)) {
							usage.add(action);
						}
					}
				}
			}
		});
		return usage.toArray(new ASTNode[usage.size()]);
	}
	
	

	public ASTNode getRateDefinition(String name) {
		for (RateDefinitionNode def : model.rateDefinitions())
			if (def.getName().getName().equals(name))
				return def;
		return null;
	}

	public ASTNode[] getConstantUsage(final String name) {
		final LinkedList<ASTNode> usage = new LinkedList<ASTNode>();

		model.accept(new uk.ac.ed.inf.pepa.parsing.MoveOnVisitor() {

			public void visitModelNode(ModelNode model) {
				for (ProcessDefinitionNode def : model.processDefinitions())
					def.accept(this);
				model.getSystemEquation().accept(this);
			}

			public void visitProcessDefinitionNode(ProcessDefinitionNode node) {
				node.getName().accept(this);
				node.getNode().accept(this);
			}

			public void visitConstantProcessNode(ConstantProcessNode node) {
				if (node.getName().equals(name))
					usage.add(node);
			}

			public void visitAggregationNode(AggregationNode node) {
				node.getProcessNode().accept(this);
			}

		});
		return usage.toArray(new ASTNode[usage.size()]);
	}

	public ASTNode[] getRateUsage(final String name) {
		final LinkedList<ASTNode> usage = new LinkedList<ASTNode>();

		model.accept(new uk.ac.ed.inf.pepa.parsing.MoveOnVisitor() {

			public void visitModelNode(ModelNode model) {
				for (RateDefinitionNode def : model.rateDefinitions())
					def.accept(this);
				for (ProcessDefinitionNode def : model.processDefinitions())
					def.accept(this);
				model.getSystemEquation().accept(this);
			}

			public void visitRateDefinitionNode(RateDefinitionNode rateDef) {
				rateDef.getName().accept(this);
				rateDef.getRate().accept(this);
			}

			public void visitProcessDefinitionNode(ProcessDefinitionNode node) {
				node.getNode().accept(this);
			}

			public void visitPrefixNode(PrefixNode node) {
				node.getActivity().getRate().accept(this);
			}

			public void visitVariableRateNode(VariableRateNode variableRate) {
				if (variableRate.getName().equals(name))
					usage.add(variableRate);

			}

			public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
				rate.getLeft().accept(this);
				rate.getRight().accept(this);
			}

		});
		return usage.toArray(new ASTNode[usage.size()]);

	}

	public ASTNode[] getActionUsage(String constantName, final String actionName) {
		ASTNode processDefinition = getProcessDefinition(constantName);
		if (processDefinition == null
				|| !(processDefinition instanceof ProcessDefinitionNode))
			return null;
		ProcessDefinitionNode procDef = (ProcessDefinitionNode) processDefinition;
		ActionTypeVisitor v = new ActionTypeVisitor(actionName);
		procDef.getNode().accept(v);
		return v.getUsage();

	}

}

class ActionTypeVisitor extends MoveOnVisitor {
	
	private String actionName;
	
	private Set<ASTNode> usages = new HashSet<ASTNode>();
	
	private ASTNode currentNode = null;
	
	public ActionTypeVisitor(String actionName) {
		this.actionName = actionName;
	}
	
	public ASTNode[] getUsage() {
		return usages.toArray(new ASTNode[usages.size()]);
	}

	public void visitActionTypeNode(ActionTypeNode actionType) {
		if (actionType.getType().equals(actionName))
			usages.add(currentNode);
			
	}

	public void visitActivityNode(ActivityNode activity) {
		activity.getAction().accept(this);
	}

	public void visitPrefixNode(PrefixNode prefix) {
		currentNode = prefix;
		prefix.getActivity().accept(this);
		prefix.getTarget().accept(this);
	}
}
