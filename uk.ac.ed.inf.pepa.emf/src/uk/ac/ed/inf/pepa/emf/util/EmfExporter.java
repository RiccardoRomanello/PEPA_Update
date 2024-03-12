/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import uk.ac.ed.inf.pepa.emf.*;
import uk.ac.ed.inf.pepa.emf.Process;
import uk.ac.ed.inf.pepa.parsing.*;

/**
 * This class converts compiled PEPA models into ECore model instances.
 * 
 * @author mtribast
 * 
 */
public final class EmfExporter {

	private EmfFactory factory = EmfFactory.eINSTANCE;

	private ModelNode modelNode;

	EmfExporter(ModelNode astNode) {
		this.modelNode = astNode;
	}

	protected Model convert() {
		//System.err.println("Convert called");
		EmfVisitor visitor = new EmfVisitor();
		modelNode.accept(visitor);
		return (Model) visitor.converted;
		
	}

	class EmfVisitor implements ASTVisitor {
		
		EObject converted;
		// New Types
		// **********************************************************
		// **********************************************************
		public void visitActionTypeNode(ActionTypeNode actionType) {
			ActionIdentifier action = factory.createActionIdentifier();
			action.setName(actionType.getType());
			converted = action;

		}
		
		public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {
			uk.ac.ed.inf.pepa.emf.SilentAction action = factory.createSilentAction();
			converted = action;
		}

		public void visitActivityNode(ActivityNode activity) {

			Activity act = factory.createActivity();

			EmfVisitor visitor = new EmfVisitor();
			activity.getAction().accept(visitor);
			act.setAction((Action) visitor.converted);

			visitor = new EmfVisitor();
			activity.getRate().accept(visitor);
			act.setRate((Rate) visitor.converted);

			converted = act;

		}

		public void visitAggregationNode(AggregationNode aggregation) {

			Aggregation agg = factory.createAggregation();

			EmfVisitor visitor = new EmfVisitor();
			aggregation.getProcessNode().accept(visitor);
			agg.setProcess((Process) visitor.converted);
			EmfVisitor v2 = new EmfVisitor();
			aggregation.getCopies().accept(visitor);
			agg.setCopies((FiniteRate) visitor.converted);
			converted = agg;

		}

		private void handleActionSet(Actions from, EList to) {
			for (ActionTypeNode actionNode : from) {
				ActionIdentifier actId = factory.createActionIdentifier();
				actId.setName(actionNode.getType());
				to.add(actId);
			}
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			EmfVisitor rateLhs = new EmfVisitor();
			rate.getLeft().accept(rateLhs);

			EmfVisitor rateRhs = new EmfVisitor();
			rate.getRight().accept(rateRhs);

			RateExpression rateExp = factory.createRateExpression();
			rateExp.setLeftHandSide((FiniteRate) rateLhs.converted);
			rateExp.setRightHandSide((FiniteRate) rateRhs.converted);

			if (rate.getOperator() == BinaryOperatorRateNode.Operator.DIV)
				rateExp.setOperator(RateOperator.DIVIDE_LITERAL);
			if (rate.getOperator() == BinaryOperatorRateNode.Operator.MULT)
				rateExp.setOperator(RateOperator.TIMES_LITERAL);
			if (rate.getOperator() == BinaryOperatorRateNode.Operator.PLUS)
				rateExp.setOperator(RateOperator.PLUS_LITERAL);
			if (rate.getOperator() == BinaryOperatorRateNode.Operator.MINUS)
				rateExp.setOperator(RateOperator.MINUS_LITERAL);

			converted = rateExp;

		}

		public void visitChoiceNode(ChoiceNode choice) {
			Choice emfChoice = factory.createChoice();

			EmfVisitor processLhs = new EmfVisitor();
			choice.getLeft().accept(processLhs);
			emfChoice.setLeftHandSide((Process) processLhs.converted);

			EmfVisitor processRhs = new EmfVisitor();
			choice.getRight().accept(processRhs);
			emfChoice.setRightHandSide((Process) processRhs.converted);

			converted = emfChoice;
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			ProcessIdentifier procId = factory.createProcessIdentifier();
			procId.setName(constant.getName());
			converted = procId;
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			Cooperation emfCooperation = factory.createCooperation();

			EmfVisitor processLhs = new EmfVisitor();
			cooperation.getLeft().accept(processLhs);
			emfCooperation.setLeftHandSide((Process) processLhs.converted);

			EmfVisitor processRhs = new EmfVisitor();
			cooperation.getRight().accept(processRhs);
			emfCooperation.setRightHandSide((Process) processRhs.converted);

			handleActionSet(cooperation.getActionSet(), emfCooperation
					.getActions());

			converted = emfCooperation;
		}

		public void visitHidingNode(HidingNode hiding) {
			Hiding emfHiding = factory.createHiding();

			EmfVisitor procVisitor = new EmfVisitor();
			hiding.getProcess().accept(procVisitor);
			emfHiding.setHiddenProcess((Process) procVisitor.converted);

			handleActionSet(hiding.getActionSet(), emfHiding.getActions());

			converted = emfHiding;

		}

		public void visitModelNode(ModelNode model) {
			Model emfModel = factory.createModel();
			
			for (RateDefinitionNode rateDef :  model.rateDefinitions()) {
				EmfVisitor rateVisitor = new EmfVisitor();
				rateDef.accept(rateVisitor);
				emfModel.getRateAssignments().add(rateVisitor.converted);
				
			}
			
			for (ProcessDefinitionNode procDef :  model.processDefinitions()) {
				EmfVisitor rateVisitor = new EmfVisitor();
				procDef.accept(rateVisitor);
				emfModel.getProcessAssignments().add(rateVisitor.converted);
				
			}
			
			EmfVisitor sysEq = new EmfVisitor();
			model.getSystemEquation().accept(sysEq);
			emfModel.setSystemEquation((Process) sysEq.converted);
			
			converted = emfModel;
			
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			PassiveRate passiveRate = factory.createPassiveRate();
			passiveRate.setWeight(passive.getMultiplicity());
			converted = passiveRate;
		}

		public void visitPrefixNode(PrefixNode prefix) {
			Prefix emfPrefix = factory.createPrefix();
			
			EmfVisitor procVisitor = new EmfVisitor();
			prefix.getTarget().accept(procVisitor);
			emfPrefix.setTargetProcess((Process) procVisitor.converted);
			
			EmfVisitor actVisitor = new EmfVisitor();
			prefix.getActivity().accept(actVisitor);
			emfPrefix.setActivity((Activity) actVisitor.converted);

			converted = emfPrefix;

		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			
			ProcessAssignment procAss = factory.createProcessAssignment();
			
			EmfVisitor lhsVisitor = new EmfVisitor();
			processDefinition.getName().accept(lhsVisitor);
			
			EmfVisitor rhsVisitor = new EmfVisitor();
			processDefinition.getNode().accept(rhsVisitor);
			
			procAss.setProcessIdentifier((ProcessIdentifier) lhsVisitor.converted);
			procAss.setProcess((Process) rhsVisitor.converted);
			
			converted = procAss;

		}

		public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
			RateAssignment rateAss = factory.createRateAssignment();
			
			EmfVisitor lhsVisitor = new EmfVisitor();
			rateDefinition.getName().accept(lhsVisitor);
			
			EmfVisitor rhsVisitor = new EmfVisitor();
			rateDefinition.getRate().accept(rhsVisitor);
			
			rateAss.setRateIdentifier((RateIdentifier) lhsVisitor.converted);
			rateAss.setRate((FiniteRate) rhsVisitor.converted);
			
			converted = rateAss;


		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			NumberLiteral lit = factory.createNumberLiteral();
			
			lit.setValue(doubleRate.getValue());
			
			converted = lit;
			

		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			RateIdentifier id = factory.createRateIdentifier();
			id.setName(variableRate.getName());
			converted = id;

		}

		public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
			throw new UnsupportedOperationException();
		}

	}
}
