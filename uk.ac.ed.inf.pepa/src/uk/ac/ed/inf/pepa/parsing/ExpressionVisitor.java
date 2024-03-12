/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.NamedRate;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * Used to visit the expression of an aggregation node.
 * The variables are resolved by looking up the compiled
 * model
 * 
 * @author mtribast
 *
 */
public class ExpressionVisitor implements ASTVisitor {

	private double eval = 0;
	
	private Model model;
	
	public ExpressionVisitor(Model model) {
		this.model = model;
	}
	
	public int eval() {
		if (Math.floor(eval) == eval)
			return (int) eval;
		else
			throw new IllegalStateException("Expected integer");
	}

	public void visitActionTypeNode(ActionTypeNode actionType) {
		throw new IllegalStateException();
	}

	public void visitActivityNode(ActivityNode activity) {
		throw new IllegalStateException();
	}

	public void visitAggregationNode(AggregationNode aggregation) {
		throw new IllegalStateException();
	}

	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
		ExpressionVisitor vl = new ExpressionVisitor(model);
		rate.getLeft().accept(vl);
		ExpressionVisitor vr = new ExpressionVisitor(model);
		rate.getRight().accept(vr);
		if (rate.getOperator() == Operator.PLUS)
			eval = vl.eval() + vr.eval();
		else if (rate.getOperator() == Operator.MINUS)
			eval = vl.eval() - vr.eval();
		else if (rate.getOperator() == Operator.DIV)
			eval = vl.eval() / vr.eval();
		else if (rate.getOperator() == Operator.MULT)
			eval = vl.eval() * vr.eval();
	}

	public void visitChoiceNode(ChoiceNode choice) {
		throw new IllegalStateException();
	}

	
	public void visitConstantProcessNode(ConstantProcessNode constant) {
		throw new IllegalStateException();
	}

	
	public void visitCooperationNode(CooperationNode cooperation) {
		throw new IllegalStateException();
	}

	
	public void visitHidingNode(HidingNode hiding) {
		throw new IllegalStateException();
	}

	
	public void visitModelNode(ModelNode model) {
		throw new IllegalStateException();
	}

	
	public void visitPassiveRateNode(PassiveRateNode passive) {
		throw new IllegalStateException();
	}

	
	public void visitPrefixNode(PrefixNode prefix) {
		throw new IllegalStateException();
	}

	
	public void visitProcessDefinitionNode(
			ProcessDefinitionNode processDefinition) {
		throw new IllegalStateException();
	}

	
	public void visitRateDefinitionNode(
			RateDefinitionNode rateDefinition) {
		throw new IllegalStateException();
	}

	
	public void visitRateDoubleNode(RateDoubleNode doubleRate) {
		eval = doubleRate.getValue();

	}

	
	public void visitUnknownActionTypeNode(
			UnknownActionTypeNode unknownActionTypeNode) {
		throw new IllegalStateException();
	}

	
	public void visitVariableRateNode(VariableRateNode variableRate) {
		for (NamedRate rate : model.getRateDefinitions()) {
			if (rate.getName().equals(variableRate.getName())) {
					eval = rate.getValue();
					return;
			}
		}
		throw new IllegalStateException();
	}

	
	public void visitWildcardCooperationNode(
			WildcardCooperationNode cooperation) {
		throw new IllegalStateException();
	}
}

