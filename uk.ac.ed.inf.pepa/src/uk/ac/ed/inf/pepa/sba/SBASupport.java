/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.*;
import java.util.regex.*;

import uk.ac.ed.inf.pepa.analysis.IAlphabetProvider;
import uk.ac.ed.inf.pepa.analysis.internal.AlphabetProvider;
import uk.ac.ed.inf.pepa.parsing.*;

/**
 * 
 * @author ajduguid
 * 
 */
public class SBASupport {

	private static class ConstantCountVisitor extends MoveOnVisitor {

		HashMap<String, HashSet<String>> alphabet;

		private HashMap<String, Integer> constantCount;

		HashSet<String> multipleConstants;

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			HashSet<String> hashSet = new HashSet<String>(alphabet.get(constant
					.getName()));
			hashSet.add(constant.getName());
			int i;
			for (String name : hashSet) {
				i = constantCount.get(name);
				constantCount.put(name, i + 1);
			}
		}

		public void visitModelNode(ModelNode model) {
			constantCount = new HashMap<String, Integer>();
			for (String constant : alphabet.keySet())
				constantCount.put(constant, 0);
			model.getSystemEquation().accept(this);
			multipleConstants = new HashSet<String>();
			for (Map.Entry<String, Integer> me : constantCount.entrySet())
				if (me.getValue().intValue() > 1)
					multipleConstants.add(me.getKey());
			constantCount = null;
			alphabet = null;
		}
	}

	private static class ConstantRenamerVisitor extends MoveOnVisitor {
		HashMap<String, HashSet<String>> alphabet;

		private HashMap<String, Integer> constantCount;

		HashSet<String> multipleConstants;

		LinkedList<HashMap<String, String>> nameMapping;

		private ProcessDefinitions newDefinitions;

		private String postfix;

		private HashMap<String, ProcessDefinitionNode> processMap;

		private boolean renaming;

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			String constantsName = constant.getName();
			if (!renaming) {
				nameMapping.add(new HashMap<String, String>());
				HashSet<String> tHashSet = alphabet.get(constantsName);
				tHashSet.add(constantsName);
				int i;
				for (String name : tHashSet) {
					i = constantCount.get(name);
					constantCount.put(name, i + 1);
				}
				renaming = true;
				for (String name : tHashSet) {
					ProcessDefinitionNode pdn = (ProcessDefinitionNode) ASTSupport
							.copy(processMap.get(name));
					newDefinitions.add(pdn);
					pdn.accept(this);
				}
				renaming = false;
			}
			if (multipleConstants.contains(constantsName)) {
				String newName;
				if(constantsName.startsWith("\""))
					newName = constantsName.substring(0, constantsName.length()-1) + postfix + constantCount.get(constantsName).toString() + "\"";
				else
					newName = constantsName + postfix + constantCount.get(constantsName).toString();
				constant.setName(newName);
				nameMapping.getLast().put(newName, constantsName);
			} else
				nameMapping.getLast().put(constantsName, constantsName);
		}

		public void visitModelNode(ModelNode model) {
			constantCount = new HashMap<String, Integer>();
			processMap = new HashMap<String, ProcessDefinitionNode>();
			newDefinitions = new ProcessDefinitions();
			for (ProcessDefinitionNode pdn : model.processDefinitions())
				processMap.put(pdn.getName().getName(), pdn);
			for (String constant : processMap.keySet())
				constantCount.put(constant, 0);
			boolean postfixFound = false;
			int underscores = 0;
			LinkedList<String> definedNames;
			Pattern p;
			String currentDefinedName;
			while (!postfixFound) {
				underscores++;
				postfixFound = true;
				definedNames = new LinkedList<String>(constantCount.keySet());
				search: while (!definedNames.isEmpty()) {
					currentDefinedName = definedNames.remove();
					if(currentDefinedName.startsWith("\""))
						p = Pattern.compile(currentDefinedName.substring(0, currentDefinedName.length()-1) + "_{" + underscores + "}\\d+\"");
					else
						p = Pattern.compile(currentDefinedName + "_{" + underscores + "}\\d+");
					for (String next : definedNames)
						if (p.matcher(next).matches()) {
							postfixFound = false;
							break search;
						}
				}
			}
			postfix = "";
			while (underscores-- > 0)
				postfix = postfix + "_";
			renaming = false;
			nameMapping = new LinkedList<HashMap<String, String>>();
			model.getSystemEquation().accept(this);
			model.processDefinitions().clear();
			model.processDefinitions().addAll(newDefinitions);
			alphabet = null;
			processMap = null;
			newDefinitions = null;
			constantCount = null;
			multipleConstants = null;
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			processDefinition.getName().accept(this);
			processDefinition.getNode().accept(this);
		}
	}

	/**
	 * Flattens (or expands) the system equation in regards to compositional
	 * definitions. Traverses the system equation and process definitions. If a
	 * constant process node is found in the system equation that defines a
	 * compositional definition (cooperation, aggregation or hiding) the
	 * constant process node in the system equation is replaced with the
	 * definition node.<br>
	 * Expected use is creation of the visitor, accept visitor on a ModelNode
	 * and getExpandedSystemEquation(). Use of any of the other methods will
	 * result in corruption of the object.
	 * 
	 * @author ajduguid
	 * @since 1.5
	 * 
	 */
	private static class ExpandEquationVisitor implements ASTVisitor {

		String currentConstant;

		ProcessNode currentNode;

		ProcessNode expandedSystemEquation;

		boolean expanding, compositional, toReplace, processDefinitionNode;

		HashMap<String, ProcessNode> replaceableNodes;

		public void visitActionTypeNode(ActionTypeNode actionType) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitActivityNode(ActivityNode activity) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			if (!expanding)
				compositional = true;
		}

		private void visitBinaryOperatorProcessNode(
				BinaryOperatorProcessNode binary) {
			if (!expanding)
				compositional = true;
			else {
				binary.getLeft().accept(this);
				if (toReplace) {
					binary.setLeft(currentNode);
					toReplace = false;
					currentNode.accept(this);
				}
				binary.getRight().accept(this);
				if (toReplace) {
					binary.setRight(currentNode);
					toReplace = false;
					currentNode.accept(this);
				}
			}
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitChoiceNode(ChoiceNode choice) {
			choice.getLeft().accept(this);
			if (expanding && toReplace) {
				choice.setLeft(currentNode);
				toReplace = false;
				currentNode.accept(this);
			}
			choice.getRight().accept(this);
			if (expanding && toReplace) {
				choice.setRight(currentNode);
				toReplace = false;
				currentNode.accept(this);
			}
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			if (!expanding) {
				if (processDefinitionNode)
					currentConstant = constant.getName();
			} else {
				if (replaceableNodes.containsKey(constant.getName())) {
					toReplace = true;
					currentNode = (ProcessNode) ASTSupport
							.copy(replaceableNodes.get(constant.getName()));
				}
			}
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			visitBinaryOperatorProcessNode(cooperation);
		}

		public void visitHidingNode(HidingNode hiding) {
			if (!expanding)
				compositional = true;
		}

		public void visitModelNode(ModelNode model) {
			expanding = false;
			replaceableNodes = new HashMap<String, ProcessNode>();
			for (ProcessDefinitionNode pdn : model.processDefinitions())
				pdn.accept(this);
			expanding = true;
			expandedSystemEquation = (ProcessNode) ASTSupport.copy(model
					.getSystemEquation());
			expandedSystemEquation.accept(this);
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			throw new SBAVisitorException(Thread.currentThread()
					.getStackTrace());
		}

		public void visitPrefixNode(PrefixNode prefix) {
			prefix.getTarget().accept(this);
			if (expanding && toReplace) {
				prefix.setTarget(currentNode);
				toReplace = false;
				currentNode.accept(this);
			}
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			processDefinitionNode = true;
			processDefinition.getName().accept(this);
			currentNode = processDefinition.getNode();
			processDefinitionNode = false;
			compositional = false;
			currentNode.accept(this);
			if (compositional)
				replaceableNodes.put(currentConstant, currentNode);
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
			visitBinaryOperatorProcessNode(cooperation);
		}
	}

	private static IAlphabetProvider alphabetProvider;

	private static ConstantCountVisitor ccv = new ConstantCountVisitor();

	private static ConstantRenamerVisitor crv = new ConstantRenamerVisitor();

	private static ExpandEquationVisitor eev = new ExpandEquationVisitor();

	public static synchronized ProcessNode expandSystemEquation(
			final ModelNode model) {
		model.accept(eev);
		return eev.expandedSystemEquation;
	}

	public static synchronized LinkedList<HashMap<String, String>> flattenNameSpace(
			ModelNode model, boolean aggregateWherePossible) {
		model.setSystemEquation(expandSystemEquation(model));
		if (aggregateWherePossible)
			model.setSystemEquation(ASTSupport.enforceAggregation(model
					.getSystemEquation(), model));
		alphabetProvider = new AlphabetProvider(model);
		ccv.alphabet = alphabetProvider.getProcessAlphabets();
		crv.alphabet = ccv.alphabet;
		model.accept(ccv);
		crv.multipleConstants = ccv.multipleConstants;
		model.accept(crv);
		return crv.nameMapping;
	}
}
