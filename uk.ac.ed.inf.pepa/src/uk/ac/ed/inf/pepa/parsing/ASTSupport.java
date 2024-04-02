/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import java.util.*;
import java.util.regex.Pattern;

import uk.ac.ed.inf.pepa.analysis.*;
import uk.ac.ed.inf.pepa.analysis.internal.AlphabetProvider;
import uk.ac.ed.inf.pepa.analysis.internal.ProblemFactory;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * Collection of methods for maintaining the AST.
 * 
 * @author Adam Duguid
 * @since 1.5
 */
public class ASTSupport {

	private static ASTCopyVisitor astCV = new ASTCopyVisitor();

	private static ASTNamedFormVisitor astNFV = new ASTNamedFormVisitor();

	private static ASTAggregationVisitor astAV = new ASTAggregationVisitor();

	private static ASTStringVisitor astSV = new ASTStringVisitor();

	/**
	 * Uses the {@link uk.ac.ed.inf.pepa.parsing.Visitor Visitor pattern} to
	 * clone the AST.
	 * 
	 * @param abstractSyntaxTreeNode
	 *            The root node to be cloned.
	 * @return A deep copy of abstractSyntaxTreeNode and all sub-nodes
	 */
	public static synchronized ASTNode copy(final ASTNode abstractSyntaxTreeNode) {
		abstractSyntaxTreeNode.accept(astCV);
		return astCV.copy;
	}

	/**
	 * Uses the {@link uk.ac.ed.inf.pepa.parsing.Visitor Visitor pattern} to
	 * remove all unlabeled/intermediate local states for each component. Also
	 * returns a mapping of new names to old to allow comparison against the
	 * original model.
	 * 
	 * @param model
	 *            The original model. Unlike other ASTSupport methods this
	 *            alters the ModelNode object. This was done to allow a mapping
	 *            of the new names to the string representations of the
	 *            intermediate states.
	 * @return HashMap&lt;String1, HashMap&lt;String2, String3&gt;&gt;.<br>
	 *         String1 is the defined constant in the original process
	 *         definition i.e. String1 = ...<br>
	 *         String2 is the new name generated within the method<br>
	 *         String3 is the String representation of the intermediate state.
	 */
	public static synchronized HashMap<String, HashMap<String, String>> generateNamedForm(
			ModelNode model) {
		astSV.setMap(new HashMap<String, String>());
		model.accept(astNFV);
		return astNFV.nameMap;
	}

	/**
	 * Same as {@link generateNamedForm} method but with additional map to allow
	 * for other name changes performed elsewhere i.e. if alternateNames
	 * includes Browser1 --&gt; Browser then when constructing the String
	 * representation if a constant is found with the name Browser1 it is
	 * replaced with Browser.
	 * 
	 * @param model
	 *            see {@link generateNamedForm}
	 * @param alternateNames
	 *            HasMap of previous mapping. When constructing the String
	 *            representation of the intermediate form, if a constant is
	 *            found in this map the alternative name will be used.
	 * @return see {@link generateNamedForm}
	 */
	public static synchronized HashMap<String, HashMap<String, String>> generateNamedForm(
			ModelNode model, HashMap<String, String> alternateNames) {
		astSV.setMap(alternateNames);
		model.accept(astNFV);
		return astNFV.nameMap;
	}

	/**
	 * Replaces standard cooperation (<>) operators with aggregated nodes where
	 * identical components are cooperating with each other over identical
	 * cooperation sets. Browser &lt;a&gt; Browser... &lt;a&gt; Browser is
	 * converted to Browser[n]{a}.
	 * 
	 * @param systemEquation
	 *            ProcessNode to aggregate over. Doesn't have to be the system
	 *            equation, this was just its original use.
	 * @return A new ProcessNode with aggregated nodes where possible.
	 */
	public static synchronized ProcessNode enforceAggregation(
			final ProcessNode systemEquation, ModelNode model) {
		astAV.start(systemEquation, model);
		return astAV.newSystemEquation;
	}

	/**
	 * Returns a String representation of the node supplied. If a ModelNode is
	 * supplied a String representation of a pepa model is returned.
	 * 
	 * @param node
	 * @return
	 */
	public static synchronized String toString(final ASTNode node) {
		return astSV.toString(node, new HashMap<String, String>());
	}

	public static synchronized Map<String, Set<String>> getProcessAlphabets(
			ModelNode model) {
		IAlphabetProvider iap = new AlphabetProvider(model);
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (Map.Entry<String, HashSet<String>> me : iap.getProcessAlphabets()
				.entrySet())
			map.put(me.getKey(), me.getValue());
		return map;
	}

	public static synchronized ProcessNode suggestSystemEquation(
			final ModelNode model) {
		// TODO
		// AlphabetProvider alphabetProvider = new AlphabetProvider(model);
		return null;
	}

	private static class ASTCopyVisitor implements ASTVisitor {

		ASTNode copy;

		private void setLocations(ASTNode a) {
			copy.setLeftLocation(a.getLeftLocation());
			copy.setRightLocation(a.getRightLocation());
		}

		public void visitActionTypeNode(ActionTypeNode actionType) {
			copy = new ActionTypeNode();
			((ActionTypeNode) copy).setType(actionType.getType());
			setLocations(actionType);
		}

		public void visitActivityNode(ActivityNode activity) {
			ActivityNode an = new ActivityNode();
			activity.getAction().accept0(this);
			an.setAction((ActionSuperNode) copy);
			activity.getRate().accept0(this);
			an.setRate((RateNode) copy);
			copy = an;
			setLocations(activity);
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			AggregationNode a = new AggregationNode();
			// AggregationNode.getCopies now is an AST node!
			aggregation.getCopies().accept(this);
			a.setCopies((FiniteRateNode) copy);
			aggregation.getProcessNode().accept0(this);
			a.setProcessNode((ProcessNode) copy);
			copy = a;
			setLocations(aggregation);
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			BinaryOperatorRateNode bORN = new BinaryOperatorRateNode();
			bORN.setOperator(rate.getOperator());
			rate.getLeft().accept0(this);
			bORN.setLeft((RateNode) copy);
			rate.getRight().accept0(this);
			bORN.setRight((RateNode) copy);
			copy = bORN;
			setLocations(rate);
		}

		public void visitChoiceNode(ChoiceNode choice) {
			ChoiceNode c = new ChoiceNode();
			choice.getLeft().accept0(this);
			c.setLeft((ProcessNode) copy);
			choice.getRight().accept0(this);
			c.setRight((ProcessNode) copy);
			copy = c;
			setLocations(choice);
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			copy = new ConstantProcessNode();
			((ConstantProcessNode) copy).setName(constant.getName());
			setLocations(constant);
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			CooperationNode c = new CooperationNode();
			Actions a = new Actions();
			cooperation.getLeft().accept0(this);
			c.setLeft((ProcessNode) copy);
			cooperation.getRight().accept0(this);
			c.setRight((ProcessNode) copy);
			for (ActionTypeNode aTN : cooperation.getActionSet()) {
				aTN.accept0(this);
				a.add((ActionTypeNode) copy);
			}
			c.setActionSet(a);
			copy = c;
			setLocations(cooperation);
		}

		public void visitHidingNode(HidingNode hiding) {
			HidingNode h = new HidingNode();
			Actions a = new Actions();
			hiding.getProcess().accept0(this);
			h.setProcess((ProcessNode) copy);
			for (ActionTypeNode aTN : hiding.getActionSet()) {
				aTN.accept0(this);
				a.add((ActionTypeNode) copy);
			}
			h.setActionSet(a);
			copy = h;
			setLocations(hiding);
		}

		public void visitModelNode(ModelNode model) {
			ModelNode m = new ModelNode();
			RateDefinitions r = m.rateDefinitions();
			for (RateDefinitionNode rDN : model.rateDefinitions()) {
				rDN.accept0(this);
				r.add((RateDefinitionNode) copy);
			}
			Actions h_level = m.levelDeclarations().getHigh();
			for (ActionTypeNode h_action : model.levelDeclarations().getHigh()) {
				h_action.accept0(this);
				h_level.add((ActionTypeNode) copy);
			}
			Actions l_level = m.levelDeclarations().getLow();
			for (ActionTypeNode l_action : model.levelDeclarations().getLow()) {
				l_action.accept0(this);
				l_level.add((ActionTypeNode) copy);
			}
			ProcessDefinitions p = m.processDefinitions();
			for (ProcessDefinitionNode pDN : model.processDefinitions()) {
				pDN.accept0(this);
				p.add((ProcessDefinitionNode) copy);
			}
			if (model.getSystemEquation() != null) {
				model.getSystemEquation().accept0(this);
				m.setSystemEquation((ProcessNode) copy);
			}
			IProblem[] i = model.getProblems(), newi = new IProblem[i.length];
			for (int index = 0; index < i.length; index++)
				newi[index] = ProblemFactory.createProblem(i[index].getId(),
						i[index].getStartLine(), i[index].getStartColumn(),
						i[index].getEndLine(), i[index].getEndColumn(),
						i[index].getChar(), i[index].getLength(), i[index]
								.getMessage());
			copy = m;
			setLocations(model);
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			copy = new PassiveRateNode();
			((PassiveRateNode) copy).setMultiplicity(passive.getMultiplicity());
			setLocations(passive);
		}

		public void visitPrefixNode(PrefixNode prefix) {
			PrefixNode p = new PrefixNode();
			prefix.getActivity().accept0(this);
			p.setActivity((ActivityNode) copy);
			prefix.getTarget().accept0(this);
			p.setTarget((ProcessNode) copy);
			copy = p;
			setLocations(prefix);
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			ProcessDefinitionNode p = new ProcessDefinitionNode();
			processDefinition.getName().accept0(this);
			p.setName((ConstantProcessNode) copy);
			processDefinition.getNode().accept0(this);
			p.setNode((ProcessNode) copy);
			copy = p;
			setLocations(processDefinition);
		}

		public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
			RateDefinitionNode r = new RateDefinitionNode();
			rateDefinition.getName().accept0(this);
			r.setName((VariableRateNode) copy);
			rateDefinition.getRate().accept(this);
			r.setRate((RateNode) copy);
			copy = r;
			setLocations(rateDefinition);
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			copy = new RateDoubleNode();
			((RateDoubleNode) copy).setValue(doubleRate.getValue());
			setLocations(doubleRate);
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			copy = new VariableRateNode();
			((VariableRateNode) copy).setName(variableRate.getName());
			setLocations(variableRate);
		}

		public void visitUnknownActionTypeNode(
				UnknownActionTypeNode unknownActionTypeNode) {
			copy = new UnknownActionTypeNode();
			setLocations(unknownActionTypeNode);
		}

		public void visitWildcardCooperationNode(
				WildcardCooperationNode cooperation) {
			WildcardCooperationNode c = new WildcardCooperationNode();
			cooperation.getLeft().accept0(this);
			c.setLeft((ProcessNode) copy);
			cooperation.getRight().accept0(this);
			c.setRight((ProcessNode) copy);
			copy = c;
			setLocations(cooperation);
		}
	}

	private static class ASTNamedFormVisitor extends DefaultVisitor {

		ProcessDefinitions tProcessDefinitions, newProcessDefinitions;

		ProcessNode lastNode;

		String currentDefinedName, postfix, s;

		int postfixCount, currentDepth;

		HashMap<String, String> currentProcessMap;

		HashMap<String, HashMap<String, String>> nameMap;

		public void visitAggregationNode(AggregationNode aggregation) {
			lastNode = (ProcessNode) ASTSupport.copy(aggregation);
		}

		public void visitChoiceNode(ChoiceNode choice) {
			int postfix = -1;
			if (currentDepth > 0)
				postfix = ++postfixCount;
			ChoiceNode newChoiceNode = new ChoiceNode();
			currentDepth = 0;
			choice.getLeft().accept(this);
			newChoiceNode.setLeft(lastNode);
			currentDepth = 0;
			ProcessDefinitions left = tProcessDefinitions;
			tProcessDefinitions = new ProcessDefinitions();
			choice.getRight().accept(this);
			newChoiceNode.setRight(lastNode);
			left.addAll(tProcessDefinitions);
			tProcessDefinitions = left;
			lastNode = newChoiceNode;
			if (postfix > 0) {
				s = createNewProcessDefinition(postfix);
				currentProcessMap.put(s, astSV.toString(choice));
			}
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			ConstantProcessNode newConstant = new ConstantProcessNode();
			newConstant.setName(constant.getName());
			lastNode = newConstant;
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			lastNode = (ProcessNode) ASTSupport.copy(cooperation);
		}

		public void visitHidingNode(HidingNode hiding) {
			lastNode = (ProcessNode) ASTSupport.copy(hiding);
		}

		public void visitModelNode(ModelNode model) {
			nameMap = new HashMap<String, HashMap<String, String>>();
			newProcessDefinitions = new ProcessDefinitions();
			// Process defined names and decide on postfix for intermediates
			LinkedList<String> definedNames = new LinkedList<String>();
			for (ProcessDefinitionNode pdn : model.processDefinitions())
				definedNames.add(pdn.getName().getName());
			boolean postfixFound = false, quoted;
			int underscores = -1;
			LinkedList<String> definedNamesCopy;
			Pattern pattern;
			/*
			 * In converting to named form we need to ensure that the postfixes
			 * to be used are legal and do not conflict with existing names.
			 * This will check to see if use of digits alone, or underscore(s)
			 * followed by digits raises no conflicts with existing names.
			 */
			while (!postfixFound) {
				underscores++;
				pattern = Pattern.compile("_{" + underscores + "}\\d+");
				postfixFound = true;
				definedNamesCopy = new LinkedList<String>(definedNames);
				search: while (!definedNamesCopy.isEmpty()) {
					currentDefinedName = definedNamesCopy.remove();
					if (currentDefinedName.charAt(0) == '\"') {
						quoted = true;
						currentDefinedName = currentDefinedName.substring(0,
								currentDefinedName.length() - 1);
					} else
						quoted = false;
					for (String next : definedNamesCopy)
						if (next.startsWith(currentDefinedName)
								&& pattern.matcher(
										next.substring(currentDefinedName
												.length(), next.length()
												- (quoted ? 1 : 0))).matches()) {
							postfixFound = false;
							break search;
						}
				}
			}
			postfix = "";
			while (underscores-- > 0)
				postfix = postfix + "_";
			for (ProcessDefinitionNode pdn : model.processDefinitions())
				pdn.accept(this);
			model.processDefinitions().clear();
			model.processDefinitions().addAll(newProcessDefinitions);
		}

		public void visitPrefixNode(PrefixNode prefix) {
			int depth = currentDepth++;
			int postfixInt = 0;
			if (depth > 0)
				postfixInt = ++postfixCount;
			prefix.getTarget().accept(this);
			PrefixNode newPrefixNode = new PrefixNode();
			newPrefixNode.setActivity((ActivityNode) ASTSupport.copy(prefix
					.getActivity()));
			newPrefixNode.setTarget(lastNode);
			lastNode = newPrefixNode;
			if (depth > 0) {
				s = createNewProcessDefinition(postfixInt);
				currentProcessMap.put(s, astSV.toString(prefix));
			}
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			currentDefinedName = processDefinition.getName().getName();
			currentProcessMap = new HashMap<String, String>();
			postfixCount = currentDepth = 0;
			tProcessDefinitions = new ProcessDefinitions();
			processDefinition.getNode().accept(this);
			// Add last process definition
			ProcessDefinitionNode pdn = new ProcessDefinitionNode();
			ConstantProcessNode cpn = new ConstantProcessNode();
			cpn.setName(currentDefinedName);
			pdn.setName(cpn);
			pdn.setNode(lastNode);
			newProcessDefinitions.add(pdn);
			newProcessDefinitions.addAll(tProcessDefinitions);
			nameMap.put(currentDefinedName, currentProcessMap);
		}

		private String createNewProcessDefinition(int postfixInt) {
			ProcessDefinitionNode pdn = new ProcessDefinitionNode();
			ConstantProcessNode cpn = new ConstantProcessNode();
			boolean quoted = currentDefinedName.startsWith("\"");
			cpn.setName(currentDefinedName.substring(0, currentDefinedName
					.length()
					- (quoted ? 1 : 0))
					+ postfix + postfixInt + (quoted ? "\"" : ""));
			pdn.setName(cpn);
			pdn.setNode(lastNode);
			tProcessDefinitions.addFirst(pdn);
			lastNode = cpn;
			return cpn.getName();
		}
	}

	private static class ASTAggregationVisitor extends DefaultVisitor {

		Actions lastActionSet;

		int numberOfInstances, depth;

		String lastConstant;

		ProcessNode newSystemEquation;

		Model model;

		/*
		 * mtribast: added modelNode in order to evaluate expressions within
		 * aggregation
		 */
		public void start(ASTNode systemEquation, ModelNode model) {
			this.model = new uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler(
					model).getModel();
			depth = 0;
			numberOfInstances = 0;
			lastConstant = "";
			lastActionSet = null;
			newSystemEquation = (ProcessNode) copy(systemEquation);
			newSystemEquation.accept(this);
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			lastActionSet = new Actions();
			if (aggregation.getProcessNode() instanceof ConstantProcessNode) {
				lastConstant = ((ConstantProcessNode) aggregation
						.getProcessNode()).getName();
				numberOfInstances = getCopies(aggregation);
			} else
				lastConstant = "";
		}

		public void visitChoiceNode(ChoiceNode choice) {
			depth++;
			choice.left.accept(this);
			choice.right.accept(this);
			lastActionSet = null;
			lastConstant = "";
			numberOfInstances = 0;
			depth--;
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			lastActionSet = new Actions();
			lastConstant = constant.getName();
			numberOfInstances = 1;
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			String leftConstant, rightConstant;
			Actions leftActions, rightActions;
			int instancesSoFar;
			depth++;
			cooperation.left.accept(this);
			leftConstant = lastConstant;
			leftActions = lastActionSet;
			instancesSoFar = numberOfInstances;
			cooperation.right.accept(this);
			rightConstant = lastConstant;
			rightActions = lastActionSet;
			instancesSoFar = instancesSoFar + numberOfInstances;
			depth--;
			if (!leftConstant.equals("") && leftConstant.equals(rightConstant)
					&& leftActions.size() == 0 && rightActions.size() == 0
					&& cooperation.getActionSet().size() == 0) {
				if (depth == 0) {
					// This is the system equation and it needs to be an
					// aggregation node
					ConstantProcessNode constantNode = ASTFactory
							.createConstant();
					constantNode.setName(rightConstant);
					AggregationNode newNode = ASTFactory.createAggregation();
					RateDoubleNode doubleNode = ASTFactory.createRate();
					doubleNode.setValue(instancesSoFar);
					newNode.setCopies(doubleNode);
					newNode.setProcessNode(constantNode);
					newSystemEquation = newNode;
				}
				// Otherwise go back up the tree with the correct number of
				// instances, lastConstant and lastActionSet already set to
				// correct values.
				numberOfInstances = instancesSoFar;
				lastActionSet = cooperation.getActionSet();
			} else {
				if ((instancesSoFar - numberOfInstances) > 1
						&& leftActions.size() == 0) {
					// Aggregation possible on left node
					ConstantProcessNode constantNode = ASTFactory
							.createConstant();
					constantNode.setName(leftConstant);
					AggregationNode leftNode = ASTFactory.createAggregation();
					// Mirco
					RateDoubleNode doubleNode = ASTFactory.createRate();
					doubleNode.setValue(instancesSoFar - numberOfInstances);
					leftNode.setCopies(doubleNode);
					leftNode.setProcessNode(constantNode);
					cooperation.setLeft(leftNode);
				}
				if (numberOfInstances > 1 && rightActions.size() == 0) {
					// Aggregation possible on right node
					ConstantProcessNode constantNode = ASTFactory
							.createConstant();
					constantNode.setName(rightConstant);
					AggregationNode rightNode = ASTFactory.createAggregation();
					RateDoubleNode doubleNode = ASTFactory.createRate();
					doubleNode.setValue(numberOfInstances);
					rightNode.setCopies(doubleNode);
					rightNode.setProcessNode(constantNode);
					cooperation.setRight(rightNode);
				}
				lastActionSet = null;
				lastConstant = "";
				numberOfInstances = 0;
			}
		}

		public void visitHidingNode(HidingNode hiding) {
			lastActionSet = null;
			lastConstant = "";
			numberOfInstances = 0;
		}

		public void visitPrefixNode(PrefixNode prefix) {
			lastActionSet = null;
			lastConstant = "";
			numberOfInstances = 0;
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
		}

		private int getCopies(AggregationNode aggregation) {
			ExpressionVisitor v = new ExpressionVisitor(model);
			aggregation.getCopies().accept(v);
			return v.eval();
		}
	}

	private static class ASTStringVisitor implements ASTVisitor {

		Operator currentOp;

		static final String INFTY = "infty";

		StringBuilder string;

		HashMap<String, String> alternateNames;

		public String toString(ASTNode node,
				HashMap<String, String> alternateNames) {
			this.alternateNames = alternateNames;
			string = new StringBuilder();
			currentOp = null;
			node.accept(this);
			return string.toString();
		}

		public String toString(ASTNode node) {
			string = new StringBuilder();
			currentOp = null;
			node.accept(this);
			return string.toString();
		}

		void setMap(HashMap<String, String> alternateNames) {
			this.alternateNames = alternateNames;
		}

		public void visitActionTypeNode(ActionTypeNode actionType) {
			string.append(actionType.getType());
		}

		public void visitUnknownActionTypeNode(
				UnknownActionTypeNode unknownActionTypeNode) {
			string.append(UnknownActionTypeNode.TAU);
		}

		public void visitActivityNode(ActivityNode activity) {
			activity.getAction().accept(this);
			string.append(",");
			StringBuilder sofar = string;
			string = new StringBuilder();
			activity.getRate().accept(this);
			string = sofar.append(string);
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			aggregation.getProcessNode().accept(this);
			StringBuilder sofar = string;
			string = new StringBuilder();
			aggregation.getCopies().accept(this);
			string = sofar.append("[").append(string).append("]");
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			Operator leftOp, rightOp;
			StringBuilder leftSB, rightSB, sofar;
			sofar = string;
			string = new StringBuilder();
			currentOp = null;
			rate.getLeft().accept(this);
			leftSB = string;
			leftOp = currentOp;
			currentOp = null;
			string = new StringBuilder();
			rate.getRight().accept(this);
			rightSB = string;
			rightOp = currentOp;
			currentOp = rate.getOperator();
			if (leftOp != null && leftOp.precedence() < currentOp.precedence())
				sofar.append("(").append(leftSB).append(")");
			else
				sofar.append(leftSB);
			sofar.append(currentOp);
			if (rightOp != null
					&& rightOp.precedence() < currentOp.precedence())
				sofar.append("(").append(rightSB).append(")");
			else
				sofar.append(rightSB);
			string = sofar;
		}

		public void visitChoiceNode(ChoiceNode choice) {
			choice.getLeft().accept(this);
			string.append(" + ");
			choice.getRight().accept(this);
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			if (alternateNames.containsKey(constant.getName()))
				string.append(alternateNames.get(constant.getName()));
			else
				string.append(constant.getName());
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			string.append("(");
			cooperation.getLeft().accept(this);
			string.append(" <");
			for (ActionTypeNode atn : cooperation.getActionSet())
				string.append(atn.getType()).append(",");
			if (string.charAt(string.length() - 1) == ',')
				string.deleteCharAt(string.length() - 1);
			string.append("> ");
			cooperation.getRight().accept(this);
			string.append(")");
		}

		public void visitHidingNode(HidingNode hiding) {
			hiding.getProcess().accept(this);
			string.append("/<");
			for (ActionTypeNode atn : hiding.getActionSet())
				string.append(atn.getType()).append(",");
			// Can do this as hiding set shouldn't be empty
			string.deleteCharAt(string.length() - 1);
			string.append(">");
		}

		private void visitLevelDeclarations(String level, Actions actionDefinitions)
		{
			string.append(level).append(": ");
			for (ActionTypeNode action : actionDefinitions) {
				string.append(action.getType()).append(",");
			}
			if (string.charAt(string.length() - 1) == ',')
				string.deleteCharAt(string.length() - 1);
			string.append(";\n");
		}
		
		private void visitLevelDeclarations(ModelNode model)
		{
			string.append("default level: ");
			if (model.levelDeclarations().default_level == LevelDeclarations.HIGH_LEVEL) {
				string.append("high");
			} else {
				string.append("low");
			}
			string.append(";\n");
			
			visitLevelDeclarations("high", model.levelDeclarations().getHigh());
			visitLevelDeclarations("low", model.levelDeclarations().getLow());
		}
		
		public void visitModelNode(ModelNode model) {
			string.append("//Rates\n");
			for (RateDefinitionNode rtn : model.rateDefinitions()) {
				rtn.accept(this);
				string.append(";\n");
			}
			string.append("//Action Level Definitions\n");
			visitLevelDeclarations(model);
			string.append("\n//Definitions\n");
			for (ProcessDefinitionNode ptn : model.processDefinitions()) {
				ptn.accept(this);
				string.append(";\n");
			}
			string.append("\n//System Equation\n");
			int i = string.length();
			model.getSystemEquation().accept(this);
			if (model.getSystemEquation() instanceof CooperationNode) {
				string.deleteCharAt(i);
				string.deleteCharAt(string.length() - 1);
			}
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			int i = passive.getMultiplicity();
			if (i > 1) {
				string.append(i).append("*");
				currentOp = Operator.MULT;
			}
			string.append(PassiveRateNode.INFTY);
		}

		public void visitPrefixNode(PrefixNode prefix) {
			string.append("(");
			prefix.getActivity().accept(this);
			string.append(").");
			prefix.getTarget().accept(this);
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			processDefinition.getName().accept(this);
			string.append(" = ");
			processDefinition.getNode().accept(this);
		}

		public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
			string.append(rateDefinition.getName().getName()).append(" = ");
			rateDefinition.getRate().accept(this);
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			string.append(doubleRate.getValue());
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			string.append(variableRate.getName());
		}

		public void visitWildcardCooperationNode(
				WildcardCooperationNode cooperation) {
			string.append("(");
			cooperation.getLeft().accept(this);
			string.append(" <*>");
			cooperation.getRight().accept(this);
			string.append(")");
		}
	}
	/*
	 * private static class boo extends MoveOnVisitor {
	 * 
	 * HashSet<String> compositional, nonCompositional;
	 * 
	 * public void visitModelNode(ModelNode model) {
	 * 
	 * } }
	 */
}