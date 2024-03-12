/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import uk.ac.ed.inf.pepa.parsing.ASTVisitor;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.ActivityNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PassiveRateNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.parsing.UnknownActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.VariableRateNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * 
 * @author ajduguid
 * 
 */
public abstract class VEVisitor implements ASTVisitor {

	public void visitActionTypeNode(ActionTypeNode actionType) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitActivityNode(ActivityNode activity) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitAggregationNode(AggregationNode aggregation) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitChoiceNode(ChoiceNode choice) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitConstantProcessNode(ConstantProcessNode constant) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitCooperationNode(CooperationNode cooperation) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitHidingNode(HidingNode hiding) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitModelNode(ModelNode model) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitPassiveRateNode(PassiveRateNode passive) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitPrefixNode(PrefixNode prefix) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitProcessDefinitionNode(
			ProcessDefinitionNode processDefinition) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitRateDoubleNode(RateDoubleNode doubleRate) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitUnknownActionTypeNode(
			UnknownActionTypeNode unknownActionTypeNode) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitVariableRateNode(VariableRateNode variableRate) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}

	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String declaringName = getClass().getDeclaringClass().getName();
		for (StackTraceElement e : ste)
			if (e.getClassName().equals(declaringName))
				throw new SBAVisitorException(ste[2], e);
	}
}
