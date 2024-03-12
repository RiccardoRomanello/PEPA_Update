/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.*;

import uk.ac.ed.inf.pepa.parsing.*;

/**
 * Walks the model to find actions which are listed in action sets but never
 * declared. In addition, it collects the set of shared action types in the 
 * model. This information will by used by static analysis to detected use of
 * the same action name by different sequential components which do not
 * cooperate over that action.
 * 
 * @author mtribast
 * 
 */
public class ASTActionUsageVisitor {

	private Set<String> declared = null;

	private Set<String> listed = null;

	private Set<String> shared = null;
	
	private HashMap<String, List<ActionTypeNode>> usages;

	public ASTActionUsageVisitor(ModelNode model) {

		declared = new HashSet<String>();

		listed = new HashSet<String>();
		
		shared = new HashSet<String>();

		usages = new HashMap<String, List<ActionTypeNode>>();

		model.accept(new DefaultVisitor() {

			public void visitActionTypeNode(ActionTypeNode actionType) {
				declared.add(actionType.getType());
			}

			public void visitChoiceNode(ChoiceNode choice) {
				choice.getLeft().accept(this);
				choice.getRight().accept(this);
			}

			public void visitCooperationNode(CooperationNode cooperation) {
				handle(cooperation.getActionSet(),true);
				cooperation.getLeft().accept(this);
				cooperation.getRight().accept(this);
			}

			public void visitHidingNode(HidingNode hiding) {
				handle(hiding.getActionSet(),false);
			}

			

			public void visitModelNode(ModelNode model) {
				for (ProcessDefinitionNode procDef : model.processDefinitions()) {
					procDef.accept(this);
				}
				model.getSystemEquation().accept(this);

			}

			public void visitPrefixNode(PrefixNode prefix) {
				prefix.getActivity().getAction().accept(this);
				prefix.getTarget().accept(this);
			}

			public void visitProcessDefinitionNode(
					ProcessDefinitionNode processDefinition) {
				processDefinition.getNode().accept(this);

			}
			
			public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
				cooperation.getLeft().accept(this);
				cooperation.getRight().accept(this);
			}
		});
	}
	
	private void handle(Actions actionSet, boolean addToShared) {
		for (ActionTypeNode action : actionSet) {
			listed.add(action.getType());
			if (addToShared)
				shared.add(action.getType());
			registerUsage(action.getType(), action);
		}
	}

	private void registerUsage(String name, ActionTypeNode node) {
		List<ActionTypeNode> nodes = null;
		if (!usages.containsKey(name)) {
			nodes = new ArrayList<ActionTypeNode>();
			usages.put(name, nodes);
		} else {
			nodes = usages.get(name);
		}
		nodes.add(node);
	}

	public ActionTypeNode[] getListedButNeverDeclared() {
		listed.removeAll(declared);
		List<ActionTypeNode> all = new ArrayList<ActionTypeNode>();
		for (String key : listed) {
			all.addAll(usages.get(key));
		}
		return all.toArray(new ActionTypeNode[all.size()]);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getSharedNamed() {
		return shared;
	}

}
