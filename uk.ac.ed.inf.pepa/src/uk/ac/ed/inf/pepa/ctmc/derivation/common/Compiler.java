/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.*;

import uk.ac.ed.inf.pepa.analysis.internal.AlphabetProvider;
import uk.ac.ed.inf.pepa.model.*;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.internal.*;
import uk.ac.ed.inf.pepa.parsing.*;

/**
 * 
 * @author mtribast
 *
 */
public class Compiler {
	
	HashMap<String, Constant> processes;

	HashMap<String, NamedRate> rates;
	
	boolean aggregateArrays;
	
	Model compiledModel;
	
	public Compiler(boolean aggregateArrays, ModelNode model) {
		this.aggregateArrays = aggregateArrays;
		this.processes = new HashMap<String, Constant>();
		this.rates = new HashMap<String, NamedRate>();
		CompilerVisitor c = new CompilerVisitor(this);
		model.accept(c);
		compiledModel = (Model) c.compiledObject;
	}
	
	public Compiler(ModelNode model) {
		this(true, model);
	}
	
	public Model getModel() {
		return compiledModel;
	}

	private static class CompilerVisitor implements ASTVisitor {

		Object compiledObject;
		
		Compiler newCompiler;

		static ActionLevel action_level;
		
		static HashMap<String, ActionLevel> action_levels;

		static HashSet<String> currentActions;
		
		static HashMap<String, HashSet<String>> viewableActions;
		
		private static final DoMakePepaProcess factory = DoMakePepaProcess
				.getInstance();

		public CompilerVisitor(Compiler newCompiler) {
			this.newCompiler = newCompiler;
		}

		public void visitActivityNode(ActivityNode activity) {
			CompilerVisitor compAction = new CompilerVisitor(newCompiler);
			activity.getAction().accept(compAction);
			Action action = (Action) compAction.compiledObject;
			CompilerVisitor compRate = new CompilerVisitor(newCompiler);
			activity.getRate().accept(compRate);
			Rate rate = (Rate) compRate.compiledObject;
			compiledObject = factory.createActivity(action, rate);
		}

		public void visitActionTypeNode(ActionTypeNode actionType) {
			String type = actionType.getType();
			if (action_levels.containsKey(type)) {
				compiledObject = factory.createNamedAction(type, action_levels.get(type));
			} else {
				if (action_level != null) {
					action_levels.put(type, action_level);
				}
				compiledObject = factory.createNamedAction(type, action_level);
			}
		}

		public void visitChoiceNode(ChoiceNode choice) {
			CompilerVisitor compLHS = new CompilerVisitor(newCompiler);
			CompilerVisitor compRHS = new CompilerVisitor(newCompiler);
			choice.getLeft().accept(compLHS);
			HashSet<String> leftActions = currentActions;
			choice.getRight().accept(compRHS);
			currentActions.addAll(leftActions);
			compiledObject = factory.createChoice(
					(Process) compLHS.compiledObject,
					(Process) compRHS.compiledObject);
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			Constant memConstant = null;
			currentActions = new HashSet<String>();
			String name = constant.getName();
			currentActions.addAll(viewableActions.get(name));
			if (!newCompiler.processes.containsKey(name)) {
				memConstant = factory.createConstant(name);
				newCompiler.processes.put(name, memConstant);
			} else
				memConstant = newCompiler.processes.get(name);
			compiledObject = memConstant;
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			CompilerVisitor compLHS = new CompilerVisitor(newCompiler);
			CompilerVisitor compRHS = new CompilerVisitor(newCompiler);
			cooperation.getLeft().accept(compLHS);
			HashSet<String> leftActions = currentActions;
			cooperation.getRight().accept(compRHS);
			currentActions.addAll(leftActions);
			compiledObject = factory.createCooperation(
					(Process) compLHS.compiledObject,
					(Process) compRHS.compiledObject,
					createActionSet(cooperation.getActionSet()));
		}

		private ActionSet createActionSet(Actions actions) {
			ActionSetImpl set = (ActionSetImpl) factory.createActionSet();
			for (ActionTypeNode action : actions) {
				CompilerVisitor v = new CompilerVisitor(newCompiler);
				action.accept(v);
				set.add((Action) v.compiledObject);
			}
			return set;
		}

		public void visitHidingNode(HidingNode hiding) {
			CompilerVisitor v = new CompilerVisitor(newCompiler);
			hiding.getProcess().accept(v);
			Hiding memHiding = factory.createHiding((Process) v.compiledObject,
					createActionSet(hiding.getActionSet()));
			compiledObject = memHiding;
			for(ActionTypeNode atn : hiding.getActionSet())
				currentActions.remove(atn.getType());
		}

		private void visitActionLevels(ModelNode model) {
			action_levels = new HashMap<String, ActionLevel>();
			for (ActionTypeNode action: model.levelDeclarations().getHigh()) {
				action_level = ActionLevel.HIGH;
				action.accept(new CompilerVisitor(newCompiler));
			}
			for (ActionTypeNode action: model.levelDeclarations().getLow()) {
				action_level = ActionLevel.LOW;
				action.accept(new CompilerVisitor(newCompiler));
			}
			if (model.levelDeclarations().default_level != null) {
				if (model.levelDeclarations().default_level == LevelDeclarations.LOW_LEVEL) {
					action_level = ActionLevel.LOW;
				} else {
					action_level = ActionLevel.HIGH;
				}
			} else {
				action_level = ActionLevel.UNDEFINED;
			}
		}

		public void visitModelNode(ModelNode model) {
			viewableActions = (new AlphabetProvider(model)).getViewableActionAlphabets();
			CompilerVisitor v = null;
			Model memModel = factory.createModel(model);
			visitActionLevels(model);

			for (RateDefinitionNode def : model.rateDefinitions()) {
				def.accept((v = new CompilerVisitor(newCompiler)));
				memModel.getRateDefinitions().add((NamedRate) v.compiledObject);
			}
			for (ProcessDefinitionNode def : model.processDefinitions()) {
				def.accept((v = new CompilerVisitor(newCompiler)));
				memModel.getProcessDefinitions().add(
						(Constant) v.compiledObject);
			}
			model.getSystemEquation().accept((v = new CompilerVisitor(newCompiler)));
			memModel.setSystemEquation((Process) v.compiledObject);
			compiledObject = memModel;
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			compiledObject = factory.createPassiveRate(passive
					.getMultiplicity());
		}

		public void visitPrefixNode(PrefixNode prefix) {
			CompilerVisitor v = null;
			prefix.getActivity().accept(v = new CompilerVisitor(newCompiler));
			Activity act = (Activity) v.compiledObject;
			v = new CompilerVisitor(newCompiler);
			prefix.getTarget().accept(v);
			Prefix memPrefix = factory.createPrefix(act,
					(Process) v.compiledObject);
			compiledObject = memPrefix;
			//
			if(prefix.getActivity().getAction() instanceof ActionTypeNode)
				currentActions.add(((ActionTypeNode) prefix.getActivity().getAction()).getType());
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			CompilerVisitor v = new CompilerVisitor(newCompiler);
			processDefinition.getNode().accept(v);
			CompilerVisitor v2 = new CompilerVisitor(newCompiler);
			processDefinition.getName().accept(v2);
			Constant constant = (Constant) v2.compiledObject;
			// logger.debug(constant.getName());
			/*
			 * This downcasting is to avoid that the compiled object model
			 * exposes the <code>resolve</code> method, which is not a
			 * read-only function. Thus, the read-only-rule of the API is not
			 * broken
			 */
			((ConstantImpl) constant).resolve((Process) v.compiledObject);
			compiledObject = constant;
		}

		public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
			CompilerVisitor v = new CompilerVisitor(newCompiler);
			rateDefinition.getRate().accept(v);
			NamedRate namedRate = factory.createNamedRate(rateDefinition
					.getName().getName(), ((FiniteRate) v.compiledObject)
					.getValue());
			newCompiler.rates.put(rateDefinition.getName().getName(), namedRate);
			compiledObject = namedRate;
		}

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode binRate) {
			CompilerVisitor v1 = new CompilerVisitor(newCompiler);
			binRate.getLeft().accept(v1);
			CompilerVisitor v2 = new CompilerVisitor(newCompiler);
			binRate.getRight().accept(v2);
			switch (binRate.getOperator()) {
			case DIV:
				compiledObject = RateMath.div((Rate) v1.compiledObject,
						(Rate) v2.compiledObject);
				break;
			case MINUS:
				compiledObject = RateMath.minus((Rate) v1.compiledObject,
						(Rate) v2.compiledObject);
				break;
			case MULT:
				compiledObject = RateMath.mult((Rate) v1.compiledObject,
						(Rate) v2.compiledObject);
				break;
			case PLUS:
				compiledObject = RateMath.sum((Rate) v1.compiledObject,
						(Rate) v2.compiledObject);
				break;
			default:
				throw new UnsupportedOperationException();
			}
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			compiledObject = factory.createFiniteRate(doubleRate.getValue());
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			compiledObject = newCompiler.rates.get(variableRate.getName());
		}

		/**
		 * Aggregation no longer supports action sets -mtribast
		 */
		public void visitAggregationNode(AggregationNode aggregation) {
			if (newCompiler.aggregateArrays) {
				aggregateArrays(aggregation);
			} else {
				doNotAggregateArrays(aggregation);
			}
		}

		/**
		 * Aggregation is seen as a number of cooperations
		 * 
		 * @param aggregation
		 */
		private void doNotAggregateArrays(AggregationNode aggregation) {
			CompilerVisitor v = new CompilerVisitor(newCompiler);
			aggregation.getProcessNode().accept(v);
			Process subProcess = (Process) v.compiledObject;
			int copies = getCopies(aggregation);
			if (copies == 1) {
				// simple case, just one copy
				compiledObject = subProcess;
				return;
			}
			Cooperation coop = factory.createCooperation(subProcess,
					subProcess, factory.createActionSet());
			for (int i = 0; i < copies - 2; i++) {
				coop = factory.createCooperation(coop, subProcess, factory
						.createActionSet());
			}
			compiledObject = coop;
		}

		/**
		 * Arrays are aggregated
		 * 
		 * @param aggregation
		 */
		private void aggregateArrays(AggregationNode aggregation) {
			Aggregation compiledAggregation = factory.createAggregation();
			CompilerVisitor v = new CompilerVisitor(newCompiler);
			aggregation.getProcessNode().accept(v);
			((AggregationImpl) compiledAggregation).add(
					(Process) v.compiledObject, getCopies(aggregation));
			compiledObject = compiledAggregation;
		}
		
		private int getCopies(AggregationNode aggregation) {
			CompilerVisitor vrate = new CompilerVisitor(newCompiler);
			aggregation.getCopies().accept(vrate);
			int copies = 0;
			if (vrate.compiledObject instanceof FiniteRate) { 
				double dcopies = ((FiniteRate) vrate.compiledObject).getValue();
				if (Math.floor(dcopies) == dcopies) 
					copies = (int) dcopies;
				else
					throw new IllegalArgumentException("Expected integer");
			} else 
				throw new IllegalArgumentException("Expected finite integer in aggregation");
			return copies;
		}

		public void visitUnknownActionTypeNode(
				UnknownActionTypeNode unknownActionTypeNode) {
			this.compiledObject = factory.createSilentAction(null);
		}
		
		public NamedAction createNameAction(String type)
		{
			if (action_levels.containsKey(type)) {
				return factory.createNamedAction(type, action_levels.get(type));
			}
			
			return factory.createNamedAction(type);
		}
		
		public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
			CompilerVisitor compLHS = new CompilerVisitor(newCompiler);
			CompilerVisitor compRHS = new CompilerVisitor(newCompiler);
			cooperation.getLeft().accept(compLHS);
			HashSet<String> leftActions = currentActions;
			cooperation.getRight().accept(compRHS);
			HashSet<String> intersection = new HashSet<String>();
			for(String s : leftActions)
				if(currentActions.contains(s))
					intersection.add(s);
			currentActions.addAll(leftActions);
			ActionSetImpl set = (ActionSetImpl) factory.createActionSet();
			for(String s : intersection)
				set.add(createNameAction(s));
			compiledObject = factory.createCooperation(
					(Process) compLHS.compiledObject,
					(Process) compRHS.compiledObject,
					set);
		}
	}
}
