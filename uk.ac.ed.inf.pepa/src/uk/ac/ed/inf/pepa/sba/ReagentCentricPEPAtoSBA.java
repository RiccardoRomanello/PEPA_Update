/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.*;

import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.analysis.internal.ProblemFactory;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.parsing.*;

/**
 * 
 * @author ajduguid
 * 
 */
public class ReagentCentricPEPAtoSBA implements SBAInterface {

	/**
	 * Handles creation of sub-reactions, Generation of species names and rates.
	 * 
	 * @author ajduguid
	 * 
	 */
	private class ParseProcessesVisitor extends MoveOnVisitor {

		private Set<String> highPostfixes, acceptablePostfixes;

		private boolean leftHigh, rightHigh;

		private String leftName, rightName, actionName;

		List<IProblem> rcProblems;

		Map<String, Set<SBAReaction>> reactionMap;

		private SBAComponent sbaComponent;

		private SBAReaction sbaReaction;

		private Map<String, RateNode> tSBARates;

		ParseProcessesVisitor(Set<String> allPostfixes,
				Set<String> highPostfixes) {
			if (allPostfixes == null || allPostfixes.size() == 0)
				throw new IllegalArgumentException(
						"Reagent-centric postfixes cannot be null or empty.");
			if (highPostfixes == null || highPostfixes.size() == 0)
				throw new IllegalArgumentException(
						"Reagent-centric postfixes representing high states cannot be null or empty.");
			acceptablePostfixes = allPostfixes;
			this.highPostfixes = highPostfixes;
			tSBARates = new HashMap<String, RateNode>();
			rcProblems = new LinkedList<IProblem>();
			reactionMap = new HashMap<String, Set<SBAReaction>>();
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			rightName = constant.getName();
			boolean valid = false;
			for (String postfix : acceptablePostfixes)
				if (rightName.endsWith(postfix)
						&& rightName.equals(leftName + postfix)) {
					valid = true;
					rightHigh = highPostfixes.contains(postfix);
					break;
				}
			if (!valid)
				rcProblems
						.add(ProblemFactory
								.buildProblem(IProblem.SyntaxError, constant,
										"Constant does not end with valid reagent-centrix postfix."));
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			reactionMap.remove(leftName);
		}

		public void visitHidingNode(HidingNode hiding) {
			rcProblems
					.add(ProblemFactory
							.buildProblem(
									IProblem.UndefinedError,
									hiding,
									"The hiding operator is not currently permissible within the reagent-centric approach."));
		}

		public void visitModelNode(ModelNode model) {
			for (RateDefinitionNode rdn : model.rateDefinitions())
				tSBARates.put(rdn.getName().getName(), rdn.getRate());
			for (ProcessDefinitionNode pdn : model.processDefinitions())
				pdn.accept(this);
		}

		public void visitPrefixNode(PrefixNode prefix) {
			if (!(prefix.getTarget() instanceof ConstantProcessNode)) {
				rcProblems
						.add(ProblemFactory
								.buildProblem(IProblem.UndefinedError, prefix,
										"The sequential component to the prefix is not a constant."));
				return;
			}
			if (!(prefix.getActivity().getAction() instanceof ActionTypeNode)) {
				rcProblems
						.add(ProblemFactory
								.buildProblem(IProblem.UndefinedRate, prefix,
										"All actions must be defined with the reagent-centric approach."));
				return;
			}
			prefix.getTarget().accept(this);
			prefix.getActivity().getRate().accept(this);
			sbaComponent = new SBAComponent(leftName, prefix.getActivity()
					.getRate());
			sbaReaction = new SBAReaction(((ActionTypeNode) prefix
					.getActivity().getAction()).getType());
			if (leftHigh) {
				if (rightHigh) // Catalyst, otherwise reactant
					sbaComponent.makeCatalyst();
				sbaReaction.addReactant(sbaComponent);
			} else {
				if (rightHigh) // Product
					sbaReaction.addProduct(sbaComponent);
				else { // Inhibitor
					sbaComponent.makeInhibitor();
					sbaReaction.addReactant(sbaComponent);
				}
			}
			reactionMap.get(leftName).add(sbaReaction);
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			String processDefName = processDefinition.getName().getName();
			boolean valid = false;
			for (String postfix : acceptablePostfixes)
				if (processDefName.endsWith(postfix)) {
					leftName = processDefName.substring(0, processDefName
							.length()
							- postfix.length());
					if (!reactionMap.containsKey(leftName))
						reactionMap.put(leftName, new HashSet<SBAReaction>());
					valid = true;
					leftHigh = highPostfixes.contains(postfix);
					processDefinition.getNode().accept(this);
					break;
				}
			if (!valid)
				rcProblems
						.add(ProblemFactory
								.buildProblem(IProblem.SyntaxError,
										processDefinition.getName(),
										"Constant does not end with valid reagent-centrix postfix."));
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			actionName = variableRate.getName();
			if (tSBARates.containsKey(actionName))
				sbaRates.put(actionName, tSBARates.remove(actionName));
		}

		public void visitWildcardCooperationNode(
				WildcardCooperationNode cooperation) {
			reactionMap.remove(leftName);
		}
	}

	private class ParseSystemEquationVisitor extends MoveOnVisitor {
		
		
		Set<SBAReaction> currentReactions;

		private Set<String> highPostfixes;

		String lastConstant;

		Map<String, Set<SBAReaction>> reactionMap;

		private Model compiledModel;

		ParseSystemEquationVisitor(Map<String, Set<SBAReaction>> reactionMap,
				Set<String> highPostfixes, ModelNode node) {
			compiledModel = new uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler(node).getModel();
			this.reactionMap = reactionMap;
			this.highPostfixes = highPostfixes;
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			aggregation.getProcessNode().accept(this);
			if (sbaPopulations.get(lastConstant).intValue() == 0)
				throw new SBAVisitorException(Thread.currentThread()
						.getStackTrace());
			ExpressionVisitor v = new ExpressionVisitor(compiledModel);
			aggregation.getCopies().accept(v);
			sbaPopulations.put(lastConstant, new Integer(v.eval()));
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			lastConstant = constant.getName();
			while (!reactionMap.keySet().contains(lastConstant))
				lastConstant = lastConstant.substring(0,
						lastConstant.length() - 1);
			sbaPopulations.put(lastConstant, new Integer(0));
			for (String s : highPostfixes)
				if (constant.getName().equals(lastConstant + s)) {
					sbaPopulations.put(lastConstant, new Integer(1));
					break;
				}
			currentReactions = reactionMap.remove(lastConstant);
			if (currentReactions == null)
				throw new SBAVisitorException(Thread.currentThread()
						.getStackTrace());
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			Set<SBAReaction> left, right;
			cooperation.getLeft().accept(this);
			left = currentReactions;
			cooperation.getRight().accept(this);
			right = currentReactions;
			currentReactions = new HashSet<SBAReaction>();
			Set<String> coopSet = new HashSet<String>();
			for (ActionTypeNode atn : cooperation.getActionSet())
				coopSet.add(atn.getType());
			for (SBAReaction l : left) {
				if (coopSet.contains(l.getName())) {
					for (SBAReaction r : right)
						if (r.getName().equals(l.getName()))
							currentReactions.add(l.merge(r));
				} else
					currentReactions.add(l);
			}
			for (SBAReaction r : right)
				if (!coopSet.contains(r.getName()))
					currentReactions.add(r);
		}

		public void visitHidingNode(HidingNode hiding) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitModelNode(ModelNode model) {
			SBASupport.expandSystemEquation(model).accept(this);
			sbaReactions = currentReactions;
		}

		public void visitPrefixNode(PrefixNode prefix) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitWildcardCooperationNode(
				WildcardCooperationNode cooperation) {
			Set<SBAReaction> left, right;
			cooperation.getLeft().accept(this);
			left = currentReactions;
			cooperation.getRight().accept(this);
			right = currentReactions;
			currentReactions = new HashSet<SBAReaction>();
			Set<String> used = new HashSet<String>();
			String name;
			boolean unused;
			for (SBAReaction l : left) {
				unused = true;
				name = l.getName();
				used.add(name);
				for (SBAReaction r : right)
					if (r.getName().equals(name)) {
						currentReactions.add(l.merge(r));
						unused = false;
					}
				if (unused)
					currentReactions.add(l);
			}
			for (SBAReaction r : right)
				if (!used.contains(r.getName()))
					currentReactions.add(r);
		}

	}

	Mapping map = null;

	ModelNode originalModel;

	Map<String, Number> sbaPopulations = null;

	Map<String, RateNode> sbaRates = null;

	Set<SBAReaction> sbaReactions = null;

	public ReagentCentricPEPAtoSBA(ModelNode model) {
		if (model == null)
			throw new NullPointerException("Null ModelNode passed.");
		originalModel = model;
	}

	public synchronized Mapping getMapping() {
		return map;
	}

	public synchronized Map<String, Number> getPopulations() {
		return sbaPopulations;
	}

	public synchronized Map<String, RateNode> getRates() {
		return sbaRates;
	}

	/**
	 * Returns the reactions built by the {@link parseModel parseModel} method.
	 * 
	 * @return
	 */
	public synchronized Set<SBAReaction> getReactions() {
		Set<SBAReaction> copy = new HashSet<SBAReaction>();
		for (SBAReaction r : sbaReactions)
			copy.add(r.clone());
		return copy;
	}

	public boolean isParseable() {
		sbaRates = new HashMap<String, RateNode>();
		sbaReactions = new HashSet<SBAReaction>();
		Set<String> allPostfixes = new HashSet<String>(), highPostfixes = new HashSet<String>();
		allPostfixes.add("_H");
		allPostfixes.add("_L");
		highPostfixes.add("_H");
		ParseProcessesVisitor parseCheckVisitor = new ParseProcessesVisitor(
				allPostfixes, highPostfixes);
		originalModel.accept(parseCheckVisitor);
		return parseCheckVisitor.rcProblems.size() == 0;
	}

	public synchronized void parseModel() {
		sbaRates = new HashMap<String, RateNode>();
		sbaReactions = new HashSet<SBAReaction>();
		sbaPopulations = new HashMap<String, Number>();
		Set<String> allPostfixes = new HashSet<String>(), highPostfixes = new HashSet<String>();
		allPostfixes.add("_H");
		allPostfixes.add("_L");
		highPostfixes.add("_H");
		ParseProcessesVisitor preParseVisitor = new ParseProcessesVisitor(
				allPostfixes, highPostfixes);
		originalModel.accept(preParseVisitor);
		ParseSystemEquationVisitor parseVisitor = new ParseSystemEquationVisitor(
				preParseVisitor.reactionMap, highPostfixes, originalModel);
		originalModel.accept(parseVisitor);
		// Slightly evil hack to fit with previous work on Eclipse ui
		map = new Mapping();
		map.originalRepresentation = "Species";
		for (String s : sbaPopulations.keySet())
			map.labelled.put(s, s);
	}

	public synchronized void updateReactions(Set<SBAReaction> updatedReactions) {
		boolean found;
		for (SBAReaction reaction : sbaReactions) {
			found = false;
			for (SBAReaction r : updatedReactions)
				if (reaction.equals(r)) {
					found = true;
					break;
				}
			if (!found)
				throw new IllegalArgumentException(
						"Reaction sets do not match.");
		}
		if (sbaReactions.size() != updatedReactions.size())
			throw new IllegalArgumentException("Reaction sets do not match.");
		Set<SBAReaction> tSet = new HashSet<SBAReaction>(sbaReactions);
		List<SBAComponent> components;
		SBAReaction tReaction = null;
		for (SBAReaction newReaction : updatedReactions) {
			for (SBAReaction reaction : tSet)
				if (reaction.equals(newReaction)) {
					tReaction = reaction;
					break;
				}
			tSet.remove(tReaction);
			components = tReaction.getReactants();
			for (SBAComponent updatedComponent : newReaction.getReactants())
				for (SBAComponent component : components)
					if (updatedComponent.equals(component)) {
						component.setStoichiometry(updatedComponent
								.getStoichiometry());
						break;
					}
			components = tReaction.getProducts();
			for (SBAComponent updatedComponent : newReaction.getProducts())
				for (SBAComponent component : components)
					if (updatedComponent.equals(component)) {
						component.setStoichiometry(updatedComponent
								.getStoichiometry());
						break;
					}
		}
	}
}
