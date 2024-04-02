/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.astview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.PEPAEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.view.AbstractView;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ASTVisitor;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.Actions;
import uk.ac.ed.inf.pepa.parsing.ActivityNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorProcessNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ILocationInfo;
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
 * This view shows detailed information about the Abstract Syntax Tree of
 * the model of the current active editor.
 * <p>
 * This is a read-only view. Changes to the model must be done within the
 * editor.
 * <p>
 * 
 * @author mtribast
 * 
 */
public class ASTView extends AbstractView {

	private Tree fTree;

	@Override
	protected void updateView(final IProcessAlgebraEditor editor) {
		
		fTree.getDisplay().syncExec(new Runnable() {

			public void run() {
				fTree.removeAll();
				if (editor == null || !(editor instanceof PEPAEditor))
					return;
				ModelNode modelNode = ((IPepaModel) editor.getProcessAlgebraModel()).getAST();
				modelNode.accept(new TreeVisitor(fTree));
			}
			
		});
		
	}

	@Override
	protected void handleModelChanged(ProcessAlgebraModelChangedEvent event) {
		if (event.getType() == ProcessAlgebraModelChangedEvent.PARSED)
			updateView(this.fEditor);
	}

	@Override
	public void setFocus() {
		if (fTree != null && !fTree.isDisposed())
			fTree.setFocus();

	}

	@Override
	protected void internalCreatePartControl(Composite parent) {
		fTree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
	}

	private class TreeVisitor implements ASTVisitor {

		private TreeItem item;

		private Tree tree;

		public TreeVisitor(TreeItem treeItem) {
			item = treeItem;
		}

		public TreeVisitor(Tree tree) {
			this.tree = tree;
		}

		private String getLocation(ASTNode node) {
			ILocationInfo info = node.getLeftLocation();
			return "[" + info.getLine() + "," + info.getColumn() + "]";
		}

		public void visitActivityNode(ActivityNode activity) {
			TreeItem activityItem = new TreeItem(item, 0);
			activityItem.setText("Activity " + getLocation(activity));
			TreeItem action = new TreeItem(activityItem, 0);
			action.setText("Action");
			activity.getAction().accept(new TreeVisitor(action));
			TreeItem rate = new TreeItem(activityItem, 0);
			rate.setText("Rate");
			activity.getRate().accept(new TreeVisitor(rate));
		}

		public void visitActionTypeNode(ActionTypeNode actionType) {
			TreeItem actionItem = new TreeItem(item, 0);
			actionItem.setText("Action Type " + getLocation(actionType));
			TreeItem value = new TreeItem(actionItem, 0);
			value.setText("Name: " + actionType.getType());
		}

		public void visitChoiceNode(ChoiceNode choice) {
			TreeItem choiceItem = new TreeItem(item, 0);
			choiceItem.setText("Choice " + getLocation(choice));
			this.handleBinaryOperatorProcessNode(choiceItem, choice);

		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			TreeItem constantItem = new TreeItem(item, 0);
			constantItem.setText("Constant " + getLocation(constant));
			TreeItem name = new TreeItem(constantItem, 0);
			name.setText("Name: " + constant.getName());
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			TreeItem coopItem = new TreeItem(item, 0);
			coopItem.setText("Cooperation " + getLocation(cooperation));
			handleBinaryOperatorProcessNode(coopItem, cooperation);
			handleActionSet(coopItem, cooperation.getActionSet());
		}

		public void visitHidingNode(HidingNode hiding) {
			TreeItem hidingItem = new TreeItem(item, 0);
			hidingItem.setText("Hiding " + this.getLocation(hiding));
			TreeItem process = new TreeItem(hidingItem, 0);
			process.setText("Process");
			hiding.getProcess().accept(new TreeVisitor(process));
			handleActionSet(hidingItem, hiding.getActionSet());
		}

		private void handleActionSet(TreeItem parent, Actions set) {
			TreeItem actionSet = new TreeItem(parent, 0);
			actionSet.setText("ACTIONS (" + set.size() + ")");
			for (ActionTypeNode actionType : set)
				actionType.accept(new TreeVisitor(actionSet));
		}

		private void handleBinaryOperatorProcessNode(TreeItem parent,
				BinaryOperatorProcessNode node) {
			TreeItem lhs = new TreeItem(parent, 0);
			lhs.setText("Lhs");
			node.getLeft().accept(new TreeVisitor(lhs));
			TreeItem rhs = new TreeItem(parent, 0);
			rhs.setText("Rhs");
			node.getRight().accept(new TreeVisitor(rhs));

		}

		public void visitModelNode(ModelNode model) {
			TreeItem rates = new TreeItem(tree, 0);
			rates.setText("RATES (" + model.rateDefinitions().size()
					+ ")");
			TreeItem high_level = new TreeItem(tree, 0);
			high_level.setText("HIGH LEVEL ACTIONS ("
					+ model.levelDeclarations().getHigh().size() + ")");
			TreeItem low_level = new TreeItem(tree, 0);
			low_level.setText("LOW LEVEL ACTIONS ("
					+ model.levelDeclarations().getLow().size() + ")");
			TreeItem processes = new TreeItem(tree, 0);
			processes.setText("PROCESSES ("
					+ model.processDefinitions().size() + ")");
			TreeItem equation = new TreeItem(tree, 0);
			equation.setText("System Equation");
			TreeItem problems = new TreeItem(tree, 0);
			problems.setText("PROBLEMS (" + model.getProblems().length
					+ ")");
			for (ActionTypeNode high_level_action : model.levelDeclarations().getHigh()) {
				high_level_action.accept(new TreeVisitor(high_level));
			}
			for (ActionTypeNode low_level_action : model.levelDeclarations().getLow()) {
				low_level_action.accept(new TreeVisitor(low_level));
			}
			for (RateDefinitionNode rateDefinition : model
					.rateDefinitions())
				rateDefinition.accept(new TreeVisitor(rates));
			for (RateDefinitionNode rateDefinition : model
					.rateDefinitions())
				rateDefinition.accept(new TreeVisitor(rates));
			for (ProcessDefinitionNode procDef : model
					.processDefinitions())
				procDef.accept(new TreeVisitor(processes));
			boolean foundError = false;
			for (IProblem problem : model.getProblems()) {
				TreeItem aProblem = new TreeItem(problems, 0);
				String text;
				if (problem.isError()) {
					text = "Error: ";
					foundError = true;
				} else
					text = "Warning: ";
				aProblem.setText(text + problem.getMessage());
			}
			if (!foundError)
				model.getSystemEquation().accept(new TreeVisitor(equation));
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			TreeItem passiveValue = new TreeItem(item, 0);
			passiveValue.setText("Passive " + getLocation(passive));
			TreeItem v = new TreeItem(passiveValue, 0);
			v.setText("Multiplicity: " + passive.getMultiplicity());
		}

		public void visitPrefixNode(PrefixNode prefix) {
			TreeItem prefixItem = new TreeItem(item, 0);
			prefixItem.setText("Prefix " + getLocation(prefix));
			TreeItem activity = new TreeItem(prefixItem, 0);
			activity.setText("Activity");
			prefix.getActivity().accept(new TreeVisitor(activity));
			TreeItem target = new TreeItem(prefixItem, 0);
			target.setText("Target");
			prefix.getTarget().accept(new TreeVisitor(target));
		}

		public void visitProcessDefinitionNode(
				ProcessDefinitionNode processDefinition) {
			TreeItem processDefinitionItem = new TreeItem(item, 0);
			processDefinitionItem.setText("Process definition "
					+ getLocation(processDefinition));
			TreeItem name = new TreeItem(processDefinitionItem, 0);
			name.setText("Name");
			processDefinition.getName().accept(new TreeVisitor(name));
			TreeItem value = new TreeItem(processDefinitionItem, 0);
			value.setText("Process");
			processDefinition.getNode().accept(new TreeVisitor(value));

		}

		public void visitRateDefinitionNode(
				RateDefinitionNode rateDefinition) {
			TreeItem rate = new TreeItem(item, 0);
			rate
					.setText("Rate definition "
							+ getLocation(rateDefinition));
			TreeItem name = new TreeItem(rate, 0);
			name.setText("Name");
			rateDefinition.getName().accept(new TreeVisitor(name));
			TreeItem value = new TreeItem(rate, 0);
			value.setText("Value");
			rateDefinition.getRate().accept(new TreeVisitor(value));
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			TreeItem doubleValue = new TreeItem(item, 0);
			doubleValue.setText("Double " + getLocation(doubleRate));
			TreeItem v = new TreeItem(doubleValue, 0);
			v.setText("Value: " + doubleRate.getValue());

		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			TreeItem varItem = new TreeItem(item, 0);
			varItem.setText("Variable Rate " + getLocation(variableRate));
			TreeItem name = new TreeItem(varItem, 0);
			name.setText("Name: " + variableRate.getName());
		}

		public void visitBinaryOperatorRateNode(
				BinaryOperatorRateNode rate) {
			TreeItem binItem = new TreeItem(item, 0);
			binItem.setText("Binary Operator " + getLocation(rate));
			TreeItem lhs = new TreeItem(binItem, 0);
			lhs.setText("Lhs");
			rate.getLeft().accept(new TreeVisitor(lhs));
			TreeItem rhs = new TreeItem(binItem, 0);
			rhs.setText("Rhs");
			rate.getRight().accept(new TreeVisitor(rhs));
			TreeItem operator = new TreeItem(binItem, 0);
			operator.setText("Operator: " + rate.getOperator().name());
		}

		/**
		 * Aggregation no longer supports action sets -mtribast
		 */
		public void visitAggregationNode(AggregationNode aggregation) {
			TreeItem aggrItem = new TreeItem(item, 0);
			aggrItem.setText("Aggregation " + getLocation(aggregation));

			TreeItem process = new TreeItem(aggrItem, 0);
			process.setText("Process");
			aggregation.getProcessNode().accept(new TreeVisitor(process));

			TreeItem copies = new TreeItem(aggrItem, 0);
			copies.setText("Copies");
			aggregation.getCopies().accept(new TreeVisitor(copies));

//			handleActionSet(aggrItem, aggregation.getActionSet());

		}

		public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {
			TreeItem actionItem = new TreeItem(item, 0);
			actionItem.setText("TAU " + getLocation(unknownActionTypeNode));
		}
		
		public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
			TreeItem coopItem = new TreeItem(item, 0);
			coopItem.setText("Cooperation " + getLocation(cooperation));
			handleBinaryOperatorProcessNode(coopItem, cooperation);
			TreeItem actionSet = new TreeItem(coopItem, 0);
			actionSet.setText("ACTIONS (*)");
		}

	}

}
