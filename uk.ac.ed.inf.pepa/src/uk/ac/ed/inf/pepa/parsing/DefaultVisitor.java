/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;


/**
 * A default visitor for the AST which does nothing. It is useful to subclass it
 * as the extended class has to override the necessary methods only.
 * 
 * @author mtribast
 */
public class DefaultVisitor implements ASTVisitor {

	public void visitActionTypeNode(ActionTypeNode actionType) {
	}

	public void visitActivityNode(ActivityNode activity) {
	}

	public void visitAggregationNode(AggregationNode aggregation) {
	}

	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
	}

	public void visitChoiceNode(ChoiceNode choice) {
	}

	public void visitConstantProcessNode(ConstantProcessNode constant) {
	}

	public void visitCooperationNode(CooperationNode cooperation) {
	}

	public void visitHidingNode(HidingNode hiding) {
	}

	public void visitModelNode(ModelNode model) {
	}

	public void visitPassiveRateNode(PassiveRateNode passive) {
	}

	public void visitPrefixNode(PrefixNode prefix) {
	}

	public void visitProcessDefinitionNode(
			ProcessDefinitionNode processDefinition) {
	}

	public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
	}

	public void visitRateDoubleNode(RateDoubleNode doubleRate) {
	}

	public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {
	}

	public void visitVariableRateNode(VariableRateNode variableRate) {
	}
	
	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
	}

}
