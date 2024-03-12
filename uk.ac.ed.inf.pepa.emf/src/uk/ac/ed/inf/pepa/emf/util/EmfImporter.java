/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.util;

import org.eclipse.emf.ecore.EObject;

import uk.ac.ed.inf.pepa.emf.Action;
import uk.ac.ed.inf.pepa.emf.Activity;
import uk.ac.ed.inf.pepa.emf.Aggregation;
import uk.ac.ed.inf.pepa.emf.BinaryOperator;
import uk.ac.ed.inf.pepa.emf.Choice;
import uk.ac.ed.inf.pepa.emf.NumberLiteral;
import uk.ac.ed.inf.pepa.emf.ProcessAssignment;
import uk.ac.ed.inf.pepa.emf.ProcessIdentifier;
import uk.ac.ed.inf.pepa.emf.Cooperation;
import uk.ac.ed.inf.pepa.emf.FiniteRate;
import uk.ac.ed.inf.pepa.emf.Hiding;
import uk.ac.ed.inf.pepa.emf.Model;
import uk.ac.ed.inf.pepa.emf.ActionIdentifier;
import uk.ac.ed.inf.pepa.emf.RateAssignment;
import uk.ac.ed.inf.pepa.emf.RateExpression;
import uk.ac.ed.inf.pepa.emf.RateIdentifier;
import uk.ac.ed.inf.pepa.emf.PassiveRate;
import uk.ac.ed.inf.pepa.emf.Prefix;
import uk.ac.ed.inf.pepa.emf.ProcessWithSet;
import uk.ac.ed.inf.pepa.emf.Rate;
import uk.ac.ed.inf.pepa.emf.RateOperator;
import uk.ac.ed.inf.pepa.emf.SilentAction;
import uk.ac.ed.inf.pepa.model.ModelElement;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.ActionSuperNode;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.Actions;
import uk.ac.ed.inf.pepa.parsing.ActivityNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.FiniteRateNode;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PassiveRateNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.parsing.RateNode;
import uk.ac.ed.inf.pepa.parsing.UnknownActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.VariableRateNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/*
 * An instance of ASTNode is returned rather that one of the compiled model
 * because static analysis is applied to ASTs and not to compiled models.
 */
final class EmfImporter {

	Model emfModel;

	class Visitor extends EmfSwitch {

		public Object caseActionIdentifier(ActionIdentifier object) {
			ActionTypeNode actionNode = ASTFactory.createActionType();
			actionNode.setType(object.getName());
			return actionNode;
		}
		
		public Object caseSilentAction(SilentAction object) {
			UnknownActionTypeNode actionNode = ASTFactory.createUnknownActionType();
			return actionNode;
		}

		public Object caseActivity(Activity object) {
			ActivityNode astActivity = ASTFactory.createActivity();
			astActivity.setAction((ActionSuperNode) new Visitor()
					.doSwitch(object.getAction()));
			astActivity.setRate((RateNode) new Visitor().doSwitch(object
					.getRate()));
			return astActivity;
		}

		public Object caseAggregation(Aggregation object) {
			AggregationNode astAggregation = ASTFactory.createAggregation();
			astAggregation.setProcessNode((ProcessNode) new Visitor()
					.doSwitch(object.getProcess()));
			astAggregation.setCopies((FiniteRateNode) new Visitor().doSwitch(object.getCopies()));
			return astAggregation;
		}

		public Object caseChoice(Choice object) {
			ChoiceNode astChoice = ASTFactory.createChoice();
			astChoice.setLeft((ProcessNode) new Visitor().doSwitch(object
					.getLeftHandSide()));
			astChoice.setRight((ProcessNode) new Visitor().doSwitch(object
					.getRightHandSide()));
			return astChoice;
		}

		public Object caseProcessIdentifier(ProcessIdentifier object) {
			ConstantProcessNode node = ASTFactory.createConstant();
			node.setName(object.getName());
			return node;
		}

		public Object caseCooperation(Cooperation object) {
			CooperationNode astCooperation = ASTFactory.createCooperation();
			astCooperation.setActionSet((Actions) caseProcessWithSet(object));
			astCooperation.setLeft((ProcessNode) new Visitor().doSwitch(object
					.getLeftHandSide()));
			astCooperation.setRight((ProcessNode) new Visitor().doSwitch(object
					.getRightHandSide()));
			return astCooperation;

		}

		public Object caseNumberLiteral(NumberLiteral object) {
			RateDoubleNode rateDoubleNode = ASTFactory.createRate();
			rateDoubleNode.setValue(object.getValue());
			return rateDoubleNode;
		}

		public Object caseRateExpression(RateExpression object) {
			BinaryOperatorRateNode rateExp = ASTFactory
					.createBinaryOperationRate();
			rateExp.setLeft((RateNode) new Visitor().doSwitch(object
					.getLeftHandSide()));
			rateExp.setRight((RateNode) new Visitor().doSwitch(object
					.getRightHandSide()));
			if (object.getOperator() == RateOperator.DIVIDE_LITERAL)
				rateExp.setOperator(Operator.DIV);
			if (object.getOperator() == RateOperator.MINUS_LITERAL)
				rateExp.setOperator(Operator.MINUS);
			if (object.getOperator() == RateOperator.TIMES_LITERAL)
				rateExp.setOperator(Operator.MULT);
			if (object.getOperator() == RateOperator.PLUS_LITERAL)
				rateExp.setOperator(Operator.PLUS);

			return rateExp;
		}

		public Object caseProcessAssignment(ProcessAssignment object) {
			ProcessDefinitionNode procDef = ASTFactory
					.createProcessDefinition();
			procDef.setName((ConstantProcessNode) new Visitor().doSwitch(object
					.getProcessIdentifier()));
			procDef.setNode((ProcessNode) new Visitor().doSwitch(object
					.getProcess()));
			return procDef;
		}

		public Object caseRateAssignment(RateAssignment object) {
			RateDefinitionNode rateDef = ASTFactory.createRateDefinition();
			rateDef.setName((VariableRateNode) new Visitor().doSwitch(object
					.getRateIdentifier()));
			rateDef
					.setRate((RateNode) new Visitor()
							.doSwitch(object.getRate()));
			return rateDef;
		}

		public Object caseHiding(Hiding object) {
			HidingNode astHiding = ASTFactory.createHiding();
			astHiding.setActionSet((Actions) caseProcessWithSet(object));
			astHiding.setProcess((ProcessNode) new Visitor().doSwitch(object
					.getHiddenProcess()));
			return astHiding;
		}

		public Object caseModel(Model object) {
			ModelNode model = ASTFactory.createModel();
			for (Object rateDef : object.getRateAssignments()) {
				model.rateDefinitions().add(
						(RateDefinitionNode) new Visitor()
								.doSwitch((RateAssignment) rateDef));

			}
			for (Object procDef : object.getProcessAssignments()) {
				model.processDefinitions().add(
						(ProcessDefinitionNode) new Visitor()
								.doSwitch((ProcessAssignment) procDef));

			}

			model.setSystemEquation((ProcessNode) new Visitor().doSwitch(object
					.getSystemEquation()));

			return model;
		}

		public Object caseRateIdentifier(RateIdentifier object) {
			VariableRateNode rate = ASTFactory.createRateVariable();
			rate.setName(object.getName());
			return rate;
		}

		public Object casePassiveRate(PassiveRate object) {
			PassiveRateNode node = ASTFactory.createPassiveRate();
			node.setMultiplicity(object.getWeight());
			return node;
		}

		public Object casePrefix(Prefix object) {
			PrefixNode astPrefix = ASTFactory.createPrefix();
			astPrefix.setActivity((ActivityNode) new Visitor().doSwitch(object
					.getActivity()));
			astPrefix.setTarget((ProcessNode) new Visitor().doSwitch(object
					.getTargetProcess()));
			return astPrefix;
		}

		public Object caseProcessWithSet(ProcessWithSet object) {
			// returns the action set only
			Actions astSet = new Actions();

			for (Object action : object.getActions())
				astSet.add((ActionTypeNode) new Visitor()
						.doSwitch((Action) action));

			return astSet;
		}
		
		public Object defaultCase(EObject object) {
			throw new IllegalStateException();
		}
	};

	public EmfImporter(Model emfModel) {
		this.emfModel = emfModel;
	}

	public ModelNode convert() {
		
		return (ModelNode) new Visitor().doSwitch(emfModel);
	}

}
