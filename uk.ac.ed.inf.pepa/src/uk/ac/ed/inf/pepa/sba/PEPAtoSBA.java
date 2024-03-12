/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.*;
import java.util.regex.Pattern;

import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.SilentAction;
import uk.ac.ed.inf.pepa.parsing.*;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * 
 * @author ajduguid
 * 
 */
public class PEPAtoSBA implements SBAInterface {

	private class MappingVisitor extends MoveOnVisitor {

		Iterator<HashMap<String, String>> iterator;

		HashSet<String> labelledComponents;

		MappingVisitor(HashSet<String> labelledComponents) {
			this.labelledComponents = labelledComponents;
			iterator = nameMap.iterator();
		}

		public void createNext() {
			Mapping newMapping = new Mapping();
			newMapping.previous = map;
			if (map != null)
				map.next = newMapping;
			map = newMapping;
		}

		public void visitConstantProcessNode(
				ConstantProcessNode constantProcessNode) {
			for (Map.Entry<String, String> me : iterator.next().entrySet())
				if (labelledComponents.contains(me.getValue()))
					map.labelled.put(me.getKey(), me.getValue());
				else
					map.unlabelled.put(me.getKey(), me.getValue());
		}
	}

	private class ParseVisitor implements ASTVisitor {

		// mtribast - compiled model to handle aggregation
		Model compiledModel;

		ReactionBuilder currentReaction;

		Set<SBAReaction> currentReactions;

		Set<Link> done = new HashSet<Link>();

		Set<String> synchedActions = new HashSet<String>();

		String lastConstant, error = null;

		Stack<Link> todo = new Stack<Link>();

		public void visitActionTypeNode(ActionTypeNode actionType) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitActivityNode(ActivityNode activity) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			aggregation.getProcessNode().accept(this);
			ExpressionVisitor v = new ExpressionVisitor(compiledModel);
			aggregation.getCopies().accept(v);
			sbaPopulations.put(lastConstant, new Integer(v.eval()));
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitChoiceNode(ChoiceNode choice) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			lastConstant = constant.getName();
			sbaPopulations.put(lastConstant, new Integer(1));
			genReac();
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			Set<String> coopSet = new HashSet<String>(), synchedHere = new HashSet<String>();
			for (ActionTypeNode atn : cooperation.getActionSet())
				coopSet.add(atn.getType());
			for (String s : coopSet)
				if (synchedActions.add(s))
					synchedHere.add(s);
			Set<SBAReaction> left, right;
			cooperation.getLeft().accept(this);
			left = currentReactions;
			System.out.println("----- Left -----");
			for(SBAReaction sbar : left)
				System.out.println(sbar);
			cooperation.getRight().accept(this);
			right = currentReactions;
			System.out.println("----- Right -----");
			for(SBAReaction sbar : right)
				System.out.println(sbar);
			currentReactions = new HashSet<SBAReaction>();

			SBAReaction sbar = null;
			boolean leftUsed, synchedOn, topSynch;
			Map<String, CompiledRate> rates = new HashMap<String, CompiledRate>();
			String name;
			for (SBAReaction l : left) {
				leftUsed = false;
				name = l.getName();
				synchedOn = coopSet.contains(name);
				topSynch = synchedHere.contains(name);
				for (SBAReaction r : right) {
					if (name.equals(r.getName())) {
						leftUsed = true;
						if (synchedOn) {
							sbar = l.merge(r);
							if (topSynch) {
								if (sbar.overall == null)
									sbar.overall = new CompiledRate(1);
								sbar.overall = sbar.overall.op(Operator.MULT,
										sbar.numerator);
							}
						} else {
							// Parallel
							if (l.passive == r.passive) {
								sbar = l.clone();
								sbar.denominator = sbar.denominator.op(
										Operator.PLUS, r.denominator);
								rates.put(name, sbar.denominator.clone());
								if (topSynch) {
									if (sbar.overall == null)
										sbar.overall = new CompiledRate(1);
									sbar.overall = sbar.overall.op(
											Operator.MULT, sbar.numerator.op(
													Operator.DIV,
													sbar.denominator));
								}
							} else
								throw new SBAVisitorException(""); // TODO
						}
						currentReactions.add(sbar);
					}
				}
				if (!leftUsed)
					currentReactions.add(l);
			}
			CompiledRate c;
			for (SBAReaction r : right)
				if (!coopSet.contains(r.getName())) {
					c = rates.get(r.getName());
					topSynch = synchedHere.contains(r.getName());
					if (c != null) {
						r.denominator = c;
						if (topSynch) {
							if (r.overall == null)
								r.overall = new CompiledRate(1);
							r.overall = r.overall.op(Operator.MULT, r.numerator
									.op(Operator.DIV, r.denominator));
						}
					}
					currentReactions.add(r);
				}
			for (String s : synchedHere)
				synchedActions.remove(s);
		}

		public void visitHidingNode(HidingNode hiding) {
			hiding.getProcess().accept(this);
			HashSet<String> hideSet = new HashSet<String>();
			for (ActionTypeNode atn : hiding.getActionSet())
				hideSet.add(atn.getType());
			for(SBAReaction sbar : currentReactions)
				if(hideSet.contains(sbar.name))
					sbar.hide();
		}

		public void visitModelNode(ModelNode model) {
			compiledModel = new uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler(model).getModel();
			synchedActions.clear();
			model.getSystemEquation().accept(this);
			for (SBAReaction sbar : currentReactions)
				if (sbar.reactants.size() == 1) {
					sbar.numerator = sbar.overall = CompiledRate.compileRate(
							sbar.reactants.getFirst().rate, sbaRates);
				}
			sbaReactions = currentReactions;
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitPrefixNode(PrefixNode prefix) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitUnknownActionTypeNode(
				UnknownActionTypeNode unknownActionTypeNode) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitWildcardCooperationNode(
				WildcardCooperationNode cooperation) {
			Set<String> synchedHere = new HashSet<String>();
			for (String s : actions)
				if (synchedActions.add(s))
					synchedHere.add(s);
			Set<SBAReaction> left, right;
			cooperation.getLeft().accept(this);
			left = currentReactions;
			cooperation.getRight().accept(this);
			right = currentReactions;
			currentReactions = new HashSet<SBAReaction>();
			Set<String> used = new HashSet<String>();
			String name;
			boolean unused, topSynch;
			SBAReaction sbar;
			for (SBAReaction l : left) {
				unused = true;
				name = l.getName();
				used.add(name);
				topSynch = synchedHere.contains(name);
				for (SBAReaction r : right)
					if (r.getName().equals(name)) {
						sbar = l.merge(r);
						if (topSynch) {
							if (sbar.overall == null)
								sbar.overall = new CompiledRate(1);
							sbar.overall = sbar.overall.op(Operator.MULT,
									sbar.numerator);
						}
						currentReactions.add(sbar);
						unused = false;
					}
				if (unused) {
					if (topSynch) {
						if (l.overall == null)
							l.overall = new CompiledRate(1);
						l.overall = l.overall.op(Operator.MULT, l.numerator);
					}
					currentReactions.add(l);
				}
			}
			for (SBAReaction r : right)
				if (!used.contains(r.getName())) {
					if (synchedHere.contains(r.getName())) {
						if (r.overall == null)
							r.overall = new CompiledRate(1);
						r.overall = r.overall.op(Operator.MULT, r.numerator);
					}
					currentReactions.add(r);
				}
			for (String s : synchedHere)
				synchedActions.remove(s);
		}

		private final void genReac() throws SBAVisitorException{
			todo.clear();
			done.clear();
			currentReactions = new HashSet<SBAReaction>();
			ReactionsSet reactionsSet;
			Link currentConstant;
			todo.push(new Link(lastConstant, null));
			while (!todo.isEmpty()) {
				currentConstant = todo.pop();
				if (done.add(currentConstant)) {
					currentReaction = reactions.get(currentConstant.to == null ? currentConstant.from : currentConstant.to);
					reactionsSet = currentReaction
							.generateReactions(currentConstant.from);
					currentReactions.addAll(reactionsSet.reactions);
					for (Map.Entry<String, String> me : reactionsSet.reactionsToIterate
							.entrySet())
						todo.add(new Link(me.getKey(), me.getValue()));
				}
			}
			// Apparent Rate to be generated here now instead.
			Map<String, List<SBAReaction>> map = new HashMap<String, List<SBAReaction>>();
			for (SBAReaction sbar : currentReactions) {
				if (!map.containsKey(sbar.name))
					map.put(sbar.name, new LinkedList<SBAReaction>());
				map.get(sbar.name).add(sbar);
			}
			assert !map.containsKey(SilentAction.TAU);
			map.remove(SilentAction.TAU);
			SBAComponent sbac;
			CompiledRate c1 = null, c2;
			boolean passive = false;
			List<SBAReaction> list;
			String firstComponent;
			for (Map.Entry<String, List<SBAReaction>> me : map.entrySet()) {
				list = me.getValue();
				c2 = null;
				passive = CompiledRate.isPassive(list.get(0).reactants
						.getFirst());
				firstComponent = list.get(0).sourceDefinition;
				for (SBAReaction sbar : list) {
					sbac = sbar.reactants.getFirst();
					sbar.passive = passive;
					if (passive) {
						c1 = CompiledRate.passive(sbac);
						if (c1 == null) {
							if(firstComponent.equals(sbar.sourceDefinition))
								error = "Cannot parse model for time-series analysis. Action " + me.getKey() + " is defined as both passive and active in " + firstComponent + ", a reachable component state from " + lastConstant + ".";
							else
								error = "Cannot parse model for time-series analysis. Action " + me.getKey() + " is defined as passive in " + firstComponent + " and active in " + sbar.sourceDefinition + ", both reachable component states from " + lastConstant + ".";
							throw new SBAVisitorException(error);
						}
					} else {
						if (CompiledRate.isPassive(sbac)) {
							if(firstComponent.equals(sbar.sourceDefinition))
								error = "Cannot parse model for time-series analysis. Action " + me.getKey() + " is defined as both passive and active in " + firstComponent + ", a reachable component state from " + lastConstant + ".";
							else
								error = "Cannot parse model for time-series analysis. Action " + me.getKey() + " is defined as passive in " + sbar.sourceDefinition + " and active in " + firstComponent + ", both reachable component states from " + lastConstant + ".";
							throw new SBAVisitorException(error);
						}
						c1 = CompiledRate.compileRate(sbac.rate, sbaRates);
						c1 = c1.op(Operator.MULT, new CompiledRate(sbac));
					}
					if (c2 == null)
						c2 = c1;
					else {
						c2 = c1.op(Operator.PLUS, c2);
					}
					sbar.numerator = c1;
				}
				for (SBAReaction sbar : list)
					sbar.denominator = c2;
			}
		}

		private class Link {
			String from, to;

			Link(String from, String to) {
				if (from == null)
					throw new NullPointerException("Key in Link cannot be null");
				this.from = from;
				this.to = to;
			}

			public boolean equals(Object o) {

				if (o == null || !(o instanceof Link))
					return false;
				Link l = (Link) o;
				return (this.from.equals(l.from) && (this.to == null ? l.to == null
						: this.to.equals(to)));
			}

			public int hashCode() {
				return (to == null ? from : from + to).hashCode();
			}
		}
	}

	/**
	 * Initial pass over the AST. This creates the intermediate state before
	 * analysing the System Equation and creating the equations.
	 * 
	 * @author ajduguid
	 * 
	 */
	private class PreParseVisitor implements ASTVisitor {

		String constantLabel, action;

		ReactionBuilder currentReactionBuilder;

		Map<String, RateNode> rates = new HashMap<String, RateNode>();;

		Map<String, String> mapping = new HashMap<String, String>();

		Set<String> actions = new HashSet<String>();

		PreParseVisitor() {
		}

		public void visitActionTypeNode(ActionTypeNode actionType) {
			action = actionType.getType();
			actions.add(action);
		}

		public void visitActivityNode(ActivityNode activity) {
			activity.getRate().accept(this);
			activity.getAction().accept(this);
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			rate.getLeft().accept(this);
			rate.getRight().accept(this);
		}

		public void visitChoiceNode(ChoiceNode choice) {
			ProcessNode[] nodes = { choice.getLeft(), choice.getRight() };
			ReactionBuilder merged = new ReactionBuilder();
			for (ProcessNode pn : nodes) {
				pn.accept(this);
				if (constantLabel != null) { // create false reaction and
					// merge
					merged.addReaction(constantLabel, null, null);
					constantLabel = null;
				} else { // merge reactions
					merged = merged.merge(currentReactionBuilder);
				}
			}
			currentReactionBuilder = merged;
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			constantLabel = constant.getName();
			currentReactionBuilder = null;
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitHidingNode(HidingNode hiding) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitModelNode(ModelNode model) {
			mapping.clear();
			actions.clear();
			for (RateDefinitionNode rdn : model.rateDefinitions())
				rates.put(rdn.getName().getName(), rdn.getRate());
			for (ProcessDefinitionNode pdn : model.processDefinitions())
				pdn.accept(this);
			ReactionBuilder rb;
			String originalComponent;
			for (Map.Entry<String, String> me : mapping.entrySet()) {
				rb = reactions.get(me.getValue());
				rb = rb.clone();
				originalComponent = originalDef.get(me.getKey());
				if(originalComponent == null)
					originalComponent = me.getKey();
				rb.setSource(originalComponent);
				reactions.put(me.getKey(), rb);
			}
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
		}

		public void visitPrefixNode(PrefixNode prefix) {
			// Traverse the tree first.
			prefix.getTarget().accept(this);
			prefix.getActivity().accept(this);
			ReactionBuilder r = new ReactionBuilder();
			if (constantLabel != null) {
				r.addReaction(constantLabel, action, prefix.getActivity()
						.getRate());
				constantLabel = null;
			} else {
				r.addReaction(currentReactionBuilder, action, prefix
						.getActivity().getRate());
				r.link(currentReactionBuilder);
			}
			currentReactionBuilder = r;
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			processDefinition.getNode().accept(this);
			if (currentReactionBuilder != null) {
				String originalComponent = originalDef.get(processDefinition.getName().getName());
				if(originalComponent == null)
					originalComponent = processDefinition.getName().getName();
				currentReactionBuilder.setSource(originalComponent);
				reactions.put(processDefinition.getName().getName(),
						currentReactionBuilder);
			}
			else
				mapping.put(processDefinition.getName().getName(),
						constantLabel);
		}

		public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
		}

		public void visitUnknownActionTypeNode(
				UnknownActionTypeNode unknownActionTypeNode) {
			action = UnknownActionTypeNode.TAU;
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			String s = variableRate.getName();
			if (rates.containsKey(s)) {
				sbaRates.put(s, rates.get(s));
				rates.get(s).accept(this);
			}
		}

		public void visitWildcardCooperationNode(
				WildcardCooperationNode cooperation) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}
	}

	Mapping map = null;

	private MappingVisitor mappingVisitor;

	LinkedList<HashMap<String, String>> nameMap = null;
	
	Map<String, String> originalDef = null;

	ModelNode originalModel = null, model = null;

	Map<String, ReactionBuilder> reactions = null;

	Map<String, Number> sbaPopulations = null;

	Map<String, RateNode> sbaRates = null;

	Set<SBAReaction> sbaReactions = null;

	Set<String> actions;

	public PEPAtoSBA(ModelNode model) {
		if (model == null)
			throw new NullPointerException("Null ModelNode passed.");
		originalModel = model;
	}

	/**
	 * Iterates through the generated SBAReactions and generates unique names
	 * for any current reactions sharing identical names. This can happen due to
	 * activity multisets.
	 * 
	 */
	private void ensureUniqueReactionNames() {
		HashSet<String> names = new HashSet<String>();
		HashSet<String> copies = new HashSet<String>();
		boolean postfixFound, string;
		String duplicatedName, newName;
		int underscores;
		Pattern p;
		while (true) {
			names.clear();
			names.addAll(sbaRates.keySet());
			names.addAll(sbaPopulations.keySet());
			copies.clear();
			for (SBAReaction reaction : sbaReactions)
				if (names.contains(reaction.name))
					copies.add(reaction.name);
				else
					names.add(reaction.name);
			if (copies.size() == 0)
				break;
			// Reactions with identical names
			duplicatedName = copies.iterator().next();
			if (duplicatedName.endsWith("\"")) {
				string = true;
				duplicatedName = duplicatedName.substring(0, duplicatedName
						.length() - 1);
			} else
				string = false;
			postfixFound = false;
			underscores = -1;
			while (!postfixFound) {
				postfixFound = true;
				underscores++;
				p = Pattern.compile(duplicatedName + "_{" + underscores
						+ "}\\d+" + (string ? "\\\"" : ""));
				for (String next : names)
					if (p.matcher(next).matches()) {
						postfixFound = false;
						break;
					}
			}
			newName = duplicatedName;
			while (underscores-- > 0)
				newName = newName + "_";
			underscores = 1; // appalling use of previous variable - oh well
			for (SBAReaction reaction : sbaReactions) {
				if (reaction.name.equals(duplicatedName + (string ? "\"" : "")))
					reaction.setName(newName + (underscores++)
							+ (string ? "\"" : ""));
			}
		}

	}

	public synchronized ModelNode getFlattenedModel() {
		return model;
	}

	synchronized public Mapping getMapping() {
		HashSet<String> labelledComponents = new HashSet<String>();
		for (ProcessDefinitionNode pdn : originalModel.processDefinitions())
			labelledComponents.add(pdn.getName().getName());
		map = null;
		mappingVisitor = new MappingVisitor(labelledComponents);
		matchTrees(originalModel.getSystemEquation(), model.getSystemEquation());
		while (map.previous != null)
			map = map.previous;
		return map;
	}

	public synchronized ModelNode getOriginalModel() {
		return originalModel;
	}

	/**
	 * Returns population sizes as per the system equation.
	 * 
	 * @return
	 */
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
		if (originalModel == null)
			return false;
		for (IProblem problem : originalModel.getProblems())
			if (problem.isError())
				return false;
		return true;
	}

	private void matchTrees(ProcessNode originalEquation,
			ProcessNode expandedEquation) {
		boolean mapBoolean = true;
		if (originalEquation.getClass().equals(expandedEquation.getClass())) {
			mapBoolean = false;
			if (originalEquation instanceof BinaryOperatorProcessNode) {
				matchTrees(((BinaryOperatorProcessNode) originalEquation)
						.getLeft(),
						((BinaryOperatorProcessNode) expandedEquation)
								.getLeft());
				if (originalEquation instanceof CooperationNode) {
					Actions actions = ((CooperationNode) originalEquation)
							.getActionSet();
					if (actions.size() == 0)
						map.cooperation = "||";
					else {
						StringBuilder sb = new StringBuilder("<");
						for (ActionTypeNode atn : actions)
							sb.append(atn.getType()).append(", ");
						sb.delete(sb.length() - 2, sb.length());
						sb.append(">");
						map.cooperation = sb.toString();
					}
				} else if (originalEquation instanceof WildcardCooperationNode)
					map.cooperation = "<*>";
				matchTrees(((BinaryOperatorProcessNode) originalEquation)
						.getRight(),
						((BinaryOperatorProcessNode) expandedEquation)
								.getRight());
			} else if (originalEquation instanceof AggregationNode)
				matchTrees(((AggregationNode) originalEquation)
						.getProcessNode(), ((AggregationNode) expandedEquation)
						.getProcessNode());
			else if (originalEquation instanceof HidingNode)
				matchTrees(((HidingNode) originalEquation).getProcess(),
						((HidingNode) expandedEquation).getProcess());
			else if (originalEquation instanceof PrefixNode)
				matchTrees(((PrefixNode) originalEquation).getTarget(),
						((PrefixNode) expandedEquation).getTarget());
			else if (originalEquation instanceof ConstantProcessNode) {
				mapBoolean = true;
			}
		}
		if (mapBoolean) {
			mappingVisitor.createNext();
			expandedEquation.accept(mappingVisitor);
			map.originalRepresentation = ASTSupport.toString(originalEquation);
		}
	}

	public synchronized void parseModel() throws SBAParseException {
		sbaRates = new HashMap<String, RateNode>();
		reactions = new HashMap<String, ReactionBuilder>();
		sbaReactions = new HashSet<SBAReaction>();
		sbaPopulations = new HashMap<String, Number>();
		// Copy the model so we can keep the original
		model = (ModelNode) ASTSupport.copy(originalModel);
		// Flatten the namespace so we can identify between identical components
		// in different locations in the system equation.
		nameMap = SBASupport.flattenNameSpace(model, true);
		HashMap<String, String> tHashMap = new HashMap<String, String>();
		for (HashMap<String, String> h : nameMap)
			tHashMap.putAll(h);
		// Remove all intermediate states as the names can't be handled by
		// ISBJava
		HashMap<String, HashMap<String, String>> tNameMap = ASTSupport
				.generateNamedForm(model, tHashMap);
		// Build the mapping between new names and old
		originalDef = new HashMap<String, String>();
		String tString;
		for (HashMap<String, String> h : nameMap) {
			HashSet<String> h2 = new HashSet<String>();
			for (String s : h.keySet())
				h2.add(s);
			for (Map.Entry<String, HashMap<String, String>> e : tNameMap
					.entrySet())
				if (h2.contains(e.getKey())) {
					tString = h.get(e.getKey());
					for(String s : e.getValue().keySet())
						originalDef.put(s, tString);
					h.putAll(e.getValue());
				}
		}
		// Generate the ReactionBuilder objects to use when parsing the System
		// Equation.
		PreParseVisitor preParse = new PreParseVisitor();
		model.accept(preParse);
		actions = preParse.actions;
		// Update ReactionBuilder objects for non-existent states
		updateReactions();
		// Parse the expanded System Equation and create the Reaction objects.
		ParseVisitor parse = new ParseVisitor();
		try {
			model.accept(parse);
		} catch(SBAVisitorException e) {
			if(parse.error != null)
				throw new SBAParseException(parse.error);
			else
				throw e;
		}
		// Add zero populations to sbaPopulations
		Set<String> usedNames = new HashSet<String>();
		for (SBAReaction r : sbaReactions) {
			for (SBAComponent c : r.reactants)
				usedNames.add(c.name);
			for (SBAComponent c : r.products)
				usedNames.add(c.name);
		}
		Set<String> toDel = new HashSet<String>();
		for (HashMap<String, String> h : nameMap) {
			toDel.clear();
			for (String s : h.keySet()) {
				if (!usedNames.contains(s))
					toDel.add(s);
				else if (!sbaPopulations.containsKey(s))
					sbaPopulations.put(s, 0);
			}
			for (String s : toDel)
				h.remove(s);
		}
		// Check for unused components due to renaming within PEPA model
		toDel.clear();
		for (String s : sbaPopulations.keySet())
			if (!usedNames.contains(s))
				toDel.add(s);
		for (String s : toDel)
			sbaPopulations.remove(s);
		ensureUniqueReactionNames();
	}

	private final void updateReactions() {
		boolean altered = true;
		List<ReactionBuilder> todo = new LinkedList<ReactionBuilder>();
		List<ReactionBuilderAction> toDel = new LinkedList<ReactionBuilderAction>(), toAdd = new LinkedList<ReactionBuilderAction>();
		ReactionBuilder reactionBuilder, cloned;
		while (altered) {
			altered = false;
			for (ReactionBuilder rb : reactions.values()) {
				todo.clear();
				toDel.clear();
				toAdd.clear();
				todo.add(rb);
				while (!todo.isEmpty()) {
					reactionBuilder = todo.remove(0);
					for (ReactionBuilderAction rba : reactionBuilder.moves) {
						if (rba.next != null)
							todo.add(rba.next);
						else if (rba.noPrefix) {
							cloned = reactions.get(rba.product).clone();
							toAdd.addAll(cloned.moves);
							toDel.add(rba);
							altered = true;
						}
					}
					reactionBuilder.moves.removeAll(toDel);
					reactionBuilder.moves.addAll(toAdd);
				}
			}
		}
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
