/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.*;

import uk.ac.ed.inf.pepa.analysis.IAlphabetProvider;
import uk.ac.ed.inf.pepa.parsing.*;

public class AlphabetProvider implements IAlphabetProvider {

	private final AlphabetVisitor visitor;

	public AlphabetProvider(ModelNode model) {
		visitor = new AlphabetVisitor();
		model.accept(visitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.analysis.IAlphabetProvider#getProcessAlphabets()
	 */
	public HashMap<String, HashSet<String>> getProcessAlphabets() {
		return visitor.processMap;
	}

	/*
	 * 
	 * Get all of the actions performed by the model, done using
	 * 'getProcessAlphabets'
	 */
	public HashSet<String> getModelAlphabet(){
		HashMap<String, HashSet<String>> processMaps = getViewableActionAlphabets();
		Collection<HashSet<String>> processAlphabets = processMaps.values();
		HashSet<String> allActions = new HashSet<String> ();

		
		for (HashSet<String> actionSet : processAlphabets){
			allActions.addAll(actionSet);
			// for (String name : actionSet){
			//	System.out.println(name);
			// }
		}
		
		return allActions;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.analysis.IAlphabetProvider#getActionAlphabets()
	 */
	public HashMap<String, HashSet<String>> getActionAlphabets() {
		return visitor.actionMap;
	}

	public HashMap<String, HashSet<String>> getViewableActionAlphabets() {
		return visitor.viewableActionMap;
	}

}

/**
 * This class visits a process definition right hand side in order to collect
 * the action types it declares it may fire.
 * 
 * @author mtribast
 * 
 */
class AlphabetVisitor extends MoveOnVisitor {

	private HashSet<String> high_actions;

	private HashSet<String> low_actions;
	
	private HashSet<String> actions;

	private HashSet<String> processes;

	HashMap<String, HashSet<String>> actionMap, viewableActionMap;

	HashMap<String, HashSet<String>> processMap;

	private Map<String, ProcessNode> processesWithHidden;

	boolean hidden = false;
	
	HiddenVisitor hiddenVisitor = new HiddenVisitor();

	class HiddenVisitor extends MoveOnVisitor {
		
		HashSet<String> hvActions;
		
		public void visitBinaryOperatorProcessNode(BinaryOperatorProcessNode binary) {
			binary.getLeft().accept(this);
			if(hvActions == null)
				return;
			Set<String> tActions = hvActions;
			binary.getRight().accept(this);
			if(hvActions == null)
				return;
			hvActions.addAll(tActions);
		}
		
		public void visitChoiceNode(ChoiceNode choice) {
			visitBinaryOperatorProcessNode(choice);
		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			String constantName = constant.getName();
			if(processesWithHidden.keySet().contains(constantName)) {
				if(viewableActionMap.keySet().contains(constantName)) {
					hvActions = new HashSet<String>();
					hvActions.addAll(viewableActionMap.get(constantName));
				} else {
					hvActions = null;
				}
			} else {
				hvActions = new HashSet<String>();
				hvActions.addAll(actionMap.get(constantName));
			}
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			visitBinaryOperatorProcessNode(cooperation);
		}

		public void visitHidingNode(HidingNode hiding) {
			hiding.getProcess().accept(this);
			if(hvActions == null)
				return;
			Set<String> hidingSet = new HashSet<String>();
			for(ActionTypeNode atn : hiding.getActionSet())
				hidingSet.add(atn.getType());
			hvActions.removeAll(hidingSet);
		}

		public void visitPrefixNode(PrefixNode prefix) {
			prefix.getTarget().accept(this);
			if(hvActions == null)
				return;
			if(prefix.getActivity().getAction() instanceof ActionTypeNode)
				hvActions.add(((ActionTypeNode) prefix.getActivity().getAction()).getType());
		}

		public void visitAggregationNode(AggregationNode aggregation) {
			aggregation.getProcessNode().accept(this);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void visitModelNode(ModelNode model) {
		actionMap = new HashMap<String, HashSet<String>>(model
				.processDefinitions().size());
		processMap = new HashMap<String, HashSet<String>>();
		processesWithHidden = new HashMap<String, ProcessNode>();

		high_actions = new HashSet<String>();
		low_actions = new HashSet<String>();
		
		String name;
		for (ProcessDefinitionNode pdn : model.processDefinitions()) {
			actions = new HashSet<String>();
			processes = new HashSet<String>();
			hidden = false;
			pdn.getNode().accept(this);
			name = pdn.getName().getName();
			if (hidden)
				processesWithHidden.put(name, pdn.getNode());
			actionMap.put(name, actions);
			processMap.put(name, processes);
		}
		HashSet<String> tActions, tProcesses;
		HashMap<String, HashSet<String>> pActionMap = new HashMap<String, HashSet<String>>(), pProcessMap = new HashMap<String, HashSet<String>>();
		viewableActionMap = new HashMap<String, HashSet<String>>();
		for (String tName : actionMap.keySet()) {
			pActionMap.put(tName, (HashSet<String>) actionMap.get(tName)
					.clone());
			pProcessMap.put(tName, (HashSet<String>) processMap.get(tName)
					.clone());
		}
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Map.Entry<String, HashSet<String>> me : processMap.entrySet()) {
				tActions = new HashSet<String>();
				tProcesses = new HashSet<String>();
				for (String constant : me.getValue()) {
					tActions.addAll(actionMap.get(constant));
					tProcesses.addAll(processMap.get(constant));
				}
				if (me.getValue().addAll(tProcesses))
					changed = true;
				if (actionMap.get(me.getKey()).addAll(tActions))
					changed = true;
			}
		}
		if(processesWithHidden.size() == 0) {
			viewableActionMap = actionMap;
			return;
		}
		// actionMap holds actions which could be hidden in definitions. So not
		// suitable for determining potential cooperation sets.
		while (!viewableActionMap.keySet().containsAll(processesWithHidden.keySet()))
			for(Map.Entry<String, ProcessNode> me : processesWithHidden.entrySet()) {
				me.getValue().accept(hiddenVisitor);
				if(hiddenVisitor.hvActions != null)
					viewableActionMap.put(me.getKey(), hiddenVisitor.hvActions);
			}
		for(Map.Entry<String, HashSet<String>> me : pActionMap.entrySet())
			if(!viewableActionMap.containsKey(me.getKey()))
				viewableActionMap.put(me.getKey(), me.getValue());
		for(String s : processesWithHidden.keySet()) {
			HashSet<String> tHashSet = new HashSet<String>();
			tHashSet.add(s);
			pProcessMap.put(s, tHashSet);
		}
		changed = true;
		while (changed) {
			changed = false;
			for (Map.Entry<String, HashSet<String>> me : pProcessMap.entrySet()) {
				tActions = new HashSet<String>();
				for (String constant : me.getValue())
					tActions.addAll(pActionMap.get(constant));
				if (viewableActionMap.get(me.getKey()).addAll(tActions))
					changed = true;
			}
		}
	}

	public void visitConstantProcessNode(ConstantProcessNode constant) {
		processes.add(constant.getName());
	}

	public void visitHidingNode(HidingNode hiding) {
		hidden = true;
		super.visitHidingNode(hiding);
	}

	public void visitPrefixNode(PrefixNode prefix) {
		ActionSuperNode action = prefix.getActivity().getAction();
		if (action instanceof ActionTypeNode)
			actions.add(((ActionTypeNode) action).getType());
		prefix.getTarget().accept(this);
	}
}
