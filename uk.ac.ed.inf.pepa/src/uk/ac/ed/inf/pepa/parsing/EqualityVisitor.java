/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import java.util.LinkedList;

public class EqualityVisitor implements ASTVisitor {

	private ASTNode node1;
	private ASTNode node2;
	
	private boolean isEqual;
	
	private EqualityVisitor(ASTNode node1, ASTNode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	private boolean checkEquality() {
		if (node1 == null) {
			isEqual = false;
		} else {
			node1.accept(this);
		}
		return isEqual;
	}
	
	public static boolean compare(ASTNode node1, ASTNode node2) {
		EqualityVisitor visitor = new EqualityVisitor(node1, node2);
		return visitor.checkEquality();
	}
	
	public void visitActionTypeNode(ActionTypeNode node) {
		if (!(node2 instanceof ActionTypeNode)) {
			isEqual = false;
			return;
		}
		ActionTypeNode otherNode = (ActionTypeNode) node2;
		isEqual = node.getType().equals(otherNode.getType());
	}

	public void visitActivityNode(ActivityNode node) {
		if (!(node2 instanceof ActivityNode)) {
			isEqual = false;
			return;
		}
		ActivityNode otherNode = (ActivityNode) node2;
		boolean compare1 = compare(node.getAction(), otherNode.getAction());
		boolean compare2 = compare(node.getRate(), otherNode.getRate());
		isEqual = compare1 && compare2;		
	}

	public void visitAggregationNode(AggregationNode node) {
		if (!(node2 instanceof AggregationNode)) {
			isEqual = false;
			return;
		}
		AggregationNode otherNode = (AggregationNode) node2;
		boolean compare1 = compare(node.getProcessNode(), otherNode.getProcessNode());
		boolean compare2 = compare(node.getCopies(), otherNode.getCopies());
		isEqual = compare1 && compare2;
	}

	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode node) {
		if (!(node2 instanceof BinaryOperatorRateNode)) {
			isEqual = false;
			return;
		}
		BinaryOperatorRateNode otherNode = (BinaryOperatorRateNode) node2;
		boolean compare1 = compare(node.getLeft(), otherNode.getLeft());
		boolean compare2 = compare(node.getRight(), otherNode.getRight());
		boolean compare3 = node.getOperator().name.equals(otherNode.getOperator().name);
		isEqual = compare1 && compare2 && compare3;
	}

	public void visitChoiceNode(ChoiceNode node) {
		if (!(node2 instanceof ChoiceNode)) {
			isEqual = false;
			return;
		}
		ChoiceNode otherNode = (ChoiceNode) node2;
		boolean compare1 = compare(node.getLeft(), otherNode.getLeft());
		boolean compare2 = compare(node.getRight(), otherNode.getRight());
		isEqual = compare1 && compare2;
	}

	public void visitConstantProcessNode(ConstantProcessNode node) {
		if (!(node2 instanceof ConstantProcessNode)) {
			isEqual = false;
			return;
		}
		ConstantProcessNode otherNode = (ConstantProcessNode) node2;
		isEqual = node.getName().equals(otherNode.getName());	
	}

	public void visitCooperationNode(CooperationNode node) {
		if (!(node2 instanceof CooperationNode)) {
			isEqual = false;
			return;
		}
		CooperationNode otherNode = (CooperationNode) node2;
		boolean compare1 = compare(node.getLeft(), otherNode.getLeft());
		boolean compare2 = compare(node.getRight(), otherNode.getRight());
		boolean compare3 = node.getActionSet().size() == otherNode.getActionSet().size();
		isEqual = compare1 && compare2 && compare3;
		for (int i = 0; i < node.getActionSet().size(); i++) {
			isEqual = isEqual && compare(node.getActionSet().get(i), otherNode.getActionSet().get(i)); 
		}
	}

	public void visitHidingNode(HidingNode node) {
		if (!(node2 instanceof HidingNode)) {
			isEqual = false;
			return;
		}
		HidingNode otherNode = (HidingNode) node2;
		boolean compare1 = compare(node.getProcess(), otherNode.getProcess());
		boolean compare2 = node.getActionSet().size() == otherNode.getActionSet().size();
		isEqual = compare1 && compare2;
		for (int i = 0; i < node.getActionSet().size(); i++) {
			isEqual = isEqual && compare(node.getActionSet().get(i), otherNode.getActionSet().get(i)); 
		}
	}
	
	private static <T extends ASTNode> boolean compare(LinkedList<T> A, LinkedList<T> B)
	{
		if (A == null) {
			return (B == null);
		}
		
		if (B == null) {
			return false;
		}
		
		if (A.size() != B.size()) {
			return false;
		}
		for (int i = 0; i < A.size(); i++) {
			if (!compare(A.get(i), B.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean compare(LevelDeclarations A, LevelDeclarations B)
	{
		if (A.default_level != B.default_level) {
			return false;
		}
		
		if (!compare(A.getHigh(), B.getHigh())) {
			return false;
		}
		
		if (!compare(A.getLow(), B.getLow())) {
			return false;
		}

		return true;
	}

	public void visitModelNode(ModelNode node) {
		if (!(node2 instanceof ModelNode)) {
			isEqual = false;
			return;
		}
		ModelNode otherNode = (ModelNode) node2;
		if (!compare(node.getSystemEquation(), otherNode.getSystemEquation())) {
			isEqual = false;
			return;
		}
		if (node.rateDefinitions().size() != otherNode.rateDefinitions().size()) {
			isEqual = false;
			return;
		}
		for (int i = 0; i < node.rateDefinitions().size(); i++) {
			if (!compare(node.rateDefinitions().get(i), otherNode.rateDefinitions().get(i))) {
				isEqual = false;
				return;
			}
		}

		if (!compare(node.levelDeclarations(), otherNode.levelDeclarations())) {
			isEqual = false;
			return;
		}

		if (node.processDefinitions().size() != otherNode.processDefinitions().size()) {
			isEqual = false;
			return;
		}
		for (int i = 0; i < node.processDefinitions().size(); i++) {
			if (!compare(node.processDefinitions().get(i), otherNode.processDefinitions().get(i))) {
				isEqual = false;
				return;	
			}
		}
		
		isEqual = true;
	}

	public void visitPassiveRateNode(PassiveRateNode node) {
		if (!(node2 instanceof PassiveRateNode)) {
			isEqual = false;
			return;
		}
		PassiveRateNode otherNode = (PassiveRateNode) node2;
		isEqual = node.getMultiplicity() == otherNode.getMultiplicity();		
	}

	public void visitPrefixNode(PrefixNode node) {
		if (!(node2 instanceof PrefixNode)) {
			isEqual = false;
			return;
		}
		PrefixNode otherNode = (PrefixNode) node2;
		boolean compare1 = compare(node.getActivity(), otherNode.getActivity());
		boolean compare2 = compare(node.getTarget(), otherNode.getTarget());
		isEqual = compare1 && compare2;
	}

	public void visitProcessDefinitionNode(ProcessDefinitionNode node) {
		if (!(node2 instanceof ProcessDefinitionNode)) {
			isEqual = false;
			return;
		}
		ProcessDefinitionNode otherNode = (ProcessDefinitionNode) node2;
		boolean compare1 = compare(node.getNode(), otherNode.getNode());
		boolean compare2 = compare(node.getName(), otherNode.getName());
		isEqual = compare1 && compare2;
	}

	public void visitRateDefinitionNode(RateDefinitionNode node) {
		if (!(node2 instanceof RateDefinitionNode)) {
			isEqual = false;
			return;
		}
		RateDefinitionNode otherNode = (RateDefinitionNode) node2;
		boolean compare1 = compare(node.getRate(), otherNode.getRate());
		boolean compare2 = compare(node.getName(), otherNode.getName());
		isEqual = compare1 && compare2;
	}

	public void visitRateDoubleNode(RateDoubleNode node) {
		if (!(node2 instanceof RateDoubleNode)) {
			isEqual = false;
			return;
		}
		RateDoubleNode otherNode = (RateDoubleNode) node2;
		isEqual = node.getValue() == otherNode.getValue();
	}

	public void visitUnknownActionTypeNode(UnknownActionTypeNode node) {
		if (!(node2 instanceof UnknownActionTypeNode)) {
			isEqual = false;
		}
		isEqual = true;
	}

	public void visitVariableRateNode(VariableRateNode node) {
		if (!(node2 instanceof VariableRateNode)) {
			isEqual = false;
			return;
		}
		VariableRateNode otherNode = (VariableRateNode) node2;
		isEqual = node.getName().equals(otherNode.getName());
	}

	public void visitWildcardCooperationNode(WildcardCooperationNode node) {
		if (!(node2 instanceof WildcardCooperationNode)) {
			isEqual = false;
			return;
		}
		WildcardCooperationNode otherNode = (WildcardCooperationNode) node2;
		boolean compare1 = compare(node.getLeft(), otherNode.getLeft());
		boolean compare2 = compare(node.getRight(), otherNode.getRight());
		isEqual = compare1 && compare2;
	}

}
