/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.pepa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.IEvaluator;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ThroughputPerformanceMetric;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.ActivityNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;

public class PEPAThroughputPerformanceMetric extends
		ThroughputPerformanceMetric {

	public PEPAThroughputPerformanceMetric(String description, IEvaluator evaluator) {
		super(description, evaluator);
	}

	@Override
	protected Collection<String> fillActionTypes() {
		final Set<String> actionTypes = new HashSet<String>();
		((IPepaModel) getModel()).getAST().accept(new DefaultVisitor() {

			public void visitActionTypeNode(ActionTypeNode actionType) {
				actionTypes.add(actionType.getType());
			}

			public void visitActivityNode(ActivityNode activity) {
				activity.getAction().accept(this);
			}

			public void visitAggregationNode(AggregationNode aggregation) {
				aggregation.getProcessNode().accept(this);
			}

			public void visitChoiceNode(ChoiceNode choice) {
				choice.getLeft().accept(this);
				choice.getRight().accept(this);
			}

			public void visitCooperationNode(CooperationNode cooperation) {
				cooperation.getLeft().accept(this);
				cooperation.getRight().accept(this);
			}

			public void visitHidingNode(HidingNode hiding) {
				hiding.getProcess().accept(this);
			}

			public void visitModelNode(ModelNode model) {
				for (ProcessDefinitionNode procDef : model.processDefinitions())
					procDef.accept(this);
				model.getSystemEquation().accept(this);
			}

			public void visitPrefixNode(PrefixNode prefix) {
				prefix.getActivity().accept(this);
				prefix.getTarget().accept(this);
			}

			public void visitProcessDefinitionNode(
					ProcessDefinitionNode processDefinition) {
				processDefinition.getNode().accept(this);
			}
		});
		return actionTypes;
	}

}
