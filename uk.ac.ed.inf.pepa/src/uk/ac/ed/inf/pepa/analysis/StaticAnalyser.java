/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.analysis.internal.*;
import uk.ac.ed.inf.pepa.parsing.*;
import uk.ac.ed.inf.pepa.tools.PepaTools;

/**
 * Performs static analysis on the model
 * 
 * @author mtribast
 * 
 */
// TODO Rather than creating a new instance every time, reuse it!
public class StaticAnalyser {

	private static Logger logger = Logger.getLogger(StaticAnalyser.class);

	private ModelNode model;

	private ProblemManager problemManager;

	private IAlphabetProvider alphabets;

	public StaticAnalyser(ModelNode model) {
		/* Programming error, as this class is package-protected */
		if (model == null)
			throw new NullPointerException();
		this.model = model;
		analyse();
	}

	public IAlphabetProvider getAlphabetProvider() {
		return this.alphabets;
	}

	/**
	 * Analyse the model statically. If the model contains a severe error before
	 * this stage, it simply quits. Message are contained in the returned AST
	 * Node.
	 * 
	 * @return the analysed model containing problem messages.
	 */
	private void analyse() {
		for (IProblem problem : model.getProblems())
			if (problem.isError()) {
				logger
						.debug("At least a severe error, aborting static analysis...");
				return;
			}
		/* Static analysis can take place now */
		problemManager = new ProblemManager(model);

		this.model.accept(new ReferenceCounterVisitor(problemManager));

		if (!problemManager.hasError()) {
			this.alphabets = new AlphabetProvider(this.model);

			unguardedProcesses();

			unusedActions();

			// unsharedActions(sharedActions);

			localDeadLockAnalysis();

			reachabilityAnalysis();

			choiceAnalysis();

			selfLoopAnalysis(); // TODO Implement self loop analysis

			implementCompositionLoop();
			
			// A process P that is used for a reversible action 'a' like: 
			// M :- <a, gamma>.P 
			// cannot appear in no other right side of rules. 
			checkReversibilityConstraints();
			
		}
		problemManager.setProblems();
		return;

	}

	private void checkReversibilityConstraints() {
		// TODO Auto-generated method stub
		// A process P that is used for a reversible action a like: 
		// M :- <a, gamma>.P 
		// cannot appear in no other right side of rules. 
		ASTReversibilityDefinitionVisitor v = new ASTReversibilityDefinitionVisitor(this.model);
		Set<String> wrongProcesses = v.getMultiReferencedProcesses();
		
		for (String processName : wrongProcesses) {
			problemManager.reversibleActionConflict(processName);
		}
	}

	/**
	 * This method checks if hiding and cooperations include actions in their
	 * action set which are not used anywhere else.
	 * 
	 * @return the array of shared action types
	 */
	private Set<String> unusedActions() {

		ASTActionUsageVisitor v = new ASTActionUsageVisitor(this.model);
		ActionTypeNode[] actions = v.getListedButNeverDeclared();
		for (ActionTypeNode action : actions) {
			problemManager.redundantAction(action);
		}
		return v.getSharedNamed();
	}

	/**
	 * Looks for actions shared by sequential components between which there is
	 * no cooperation over.
	 * <p>
	 * Example
	 * 
	 * <pre>
	 * P1 = (a,1).P1
	 * Q1 = (a,1).Q1
	 * 
	 * P1 &lt;&gt; Q1
	 * </pre>
	 * 
	 * @param sharedActions
	 */
	private void unsharedActions(Set<String> sharedActions) {
		HashMap<String, Integer> occurrences = new HashMap<String, Integer>();

		for (Set<String> entry : alphabets.getActionAlphabets().values()) {
			// analysing one single alphabet
			for (String action : entry) {
				/*
				 * analysing one single action within one alphabet
				 */
				if (!sharedActions.contains(action)) {
					// if it's never shared and it's found
					// in more than one sequential component then
					// complain
					Integer value = occurrences.get(action);
					if (value == null) {
						occurrences.put(action, 1);
					} else {
						System.err.println("Complain..." + action);
					}
				}
			}
		}
	}

	/**
	 * This method discovers errors such as:
	 * 
	 * <pre>
	 * P = P1 || P2 || Sys
	 * Sys = Q || P
	 * </pre>
	 * 
	 * Where state space derivation would fail.
	 * <p>
	 * The method collects the list of process definitions which define
	 * cooperations and associate their process alphabets to each of those. If
	 * there is a component's process alphabet which is containing the component
	 * itself then a self loop has been detected.
	 * 
	 */
	private void implementCompositionLoop() {
		class DefineCooperation extends MoveOnVisitor {

			private boolean result = false;

			public void visitCooperationNode(CooperationNode cooperation) {

				result = true;
				super.visitCooperationNode(cooperation);
			}

		}

		for (ProcessDefinitionNode def : model.processDefinitions()) {
			DefineCooperation v = new DefineCooperation();
			logger.debug("Visiting " + def.getName().getName() + " ...");
			def.getNode().accept(v);
			if (v.result == true) {
				logger.debug("...which is defining cooperation...");
				/*
				 * If the definition defines a cooperation and contains itself
				 * as a reachable state, then a loop has been identified
				 */
				HashSet<String> reachedProcesses = this.alphabets
						.getProcessAlphabets().get(def.getName().getName());
				if (reachedProcesses.contains(def.getName().getName())) {
					logger.debug("which has got problems!");
					problemManager.cooperationLoop(def.getName().getName());
					return; // no need for continuing
				}
			} else {
				/*
				 * For components which are not defining any cooperation
				 * self-looping is allowed. (E.g., P1 = (a,1).P1)
				 */
				logger.debug("...which is not definining cooperation.");
			}
		}

	}

	private void unguardedProcesses() {
		/*
		 * Check silly assignments such as P1 = P; (any P)
		 */

		UnguardedPathDetector detector = new UnguardedPathDetector(model);
		ConstantProcessNode[] constants = detector.getConstantsAffected();
		for (ConstantProcessNode c : constants) {
			problemManager.unguardedProcess(c.getName());
		}

	}

	private void localDeadLockAnalysis() {
		/**
		 * This class investigates problems occurring when cooperation sets are
		 * wrongly defined, e.g. actions which can never fire or actions which
		 * are fired in both sides but are not declared as cooperating actions
		 * as well as local deadlock occurring because one side of the
		 * cooperation does not fire an action it is declared to.
		 * 
		 * @author mtribast
		 * 
		 */
		this.model.accept(new LocalDeadlock());

	}

	class LocalDeadlock extends MoveOnVisitor {

		HashSet<String> collectedAction = new HashSet<String>();

		public void visitModelNode(ModelNode model) {

			for (ProcessDefinitionNode definition : model.processDefinitions())
				definition.getNode().accept(this);
			model.getSystemEquation().accept(this);

		}

		public void visitConstantProcessNode(ConstantProcessNode constant) {
			// it can never return null because alphabets is stable
			collectedAction.addAll(StaticAnalyser.this.alphabets
					.getActionAlphabets().get(constant.getName()));
		}

		public void visitPrefixNode(PrefixNode prefix) {
			// it may be called on an action defined in the system equation
			// only!
			ActionSuperNode action = prefix.getActivity().getAction();
			if (action instanceof ActionTypeNode)
				collectedAction.add(((ActionTypeNode) action).getType());
			prefix.getTarget().accept(this); // move on
		}
		
		public void visitHidingNode(HidingNode hiding) {
			LocalDeadlock v = new LocalDeadlock();
			hiding.getProcess().accept(v);
			for (ActionTypeNode a :hiding.getActionSet()) {
				v.collectedAction.remove(a);
			}
		}

		public void visitCooperationNode(CooperationNode cooperation) {
			LocalDeadlock lhs = new LocalDeadlock();
			cooperation.getLeft().accept(lhs);
			LocalDeadlock rhs = new LocalDeadlock();
			cooperation.getRight().accept(rhs);
			collectedAction.addAll(lhs.collectedAction);
			collectedAction.addAll(rhs.collectedAction);
			for (ActionTypeNode cooperationAction : cooperation.getActionSet()) {
				boolean lhsContains = lhs.collectedAction
						.contains(cooperationAction.getType());
				boolean rhsContains = rhs.collectedAction
						.contains(cooperationAction.getType());
				if ((lhsContains && !rhsContains)
						|| (!lhsContains && rhsContains)) {
					problemManager.potentialDeadLock(cooperationAction);
				}

				if (!lhsContains && !rhsContains) {
					problemManager.redundantAction(cooperationAction);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ModelNode node = (ModelNode) PepaTools.parse(PepaTools
				.readText(args[0]));
		StaticAnalyser a = new StaticAnalyser(node);
		for (IProblem p : node.getProblems())
			System.out.println(p.getMessage());
	}

	/**
	 * Carry out analysis on self-loops P = (a,r).P + (b,s).P and no partner for
	 * a or b
	 * 
	 */
	private void selfLoopAnalysis() {
		// TODO Auto-generated method stub
		/*
		 * It re-uses the ConstantReachability visitor to get the constants
		 * defined by this process
		 */

	}

	/**
	 * Visits the model in order to discover errors for choice operators
	 * <p>
	 * Example:<br>
	 * P = (a, r).P2 + (a, T).P3<br>
	 * is illegal in PEPA
	 */
	private void choiceAnalysis() {

		// TODO Implement choice static analysis

	}

	/**
	 * Determines which component definitions are reachable from the system
	 * equation
	 * 
	 */
	private void reachabilityAnalysis() {
		/*
		 * reachable constant set, initialised with the constants of the system
		 * equation
		 */
		final Set<String> reachableConstants = new HashSet<String>();

		this.model.getSystemEquation().accept(new MoveOnVisitor() {

			public void visitConstantProcessNode(ConstantProcessNode constant) {
				reachableConstants.add(constant.getName());
			}
		});

		/*
		 * determines if a process definition has been added during the last
		 * iteration
		 */
		while (true) {
			final Set<String> roundConstants = new HashSet<String>();
			for (String constant : reachableConstants) {
				for (ProcessDefinitionNode def : this.model
						.processDefinitions()) {
					/* visits this reachable constant */
					if (constant.equals((def.getName().getName()))) {
						// ConstantReachabilityAnalysis v = new
						// ConstantReachabilityAnalysis();
						def.getNode().accept(new MoveOnVisitor() {

							public void visitConstantProcessNode(
									ConstantProcessNode constant) {
								roundConstants.add(constant.getName());
							}
						});

					}

				}

			}
			boolean newAdded = reachableConstants.addAll(roundConstants);
			if (newAdded == false)
				break;
			roundConstants.clear();
		}
		// now pick each process definition and see if its lhs is found
		// as reachable
		for (ProcessDefinitionNode def : this.model.processDefinitions()) {
			if (!reachableConstants.contains(def.getName().getName())) {
				this.problemManager.unreachableProcessDefinition(def);
			}

		}

	}
}

/**
 * Visits the reachable action types
 * 
 * @author mtribast
 */
class ActionVisitor extends MoveOnVisitor {

	private HashMap<String, HashSet<String>> alphabets;

	// accessible by caller
	HashSet<String> collectedAction = new HashSet<String>();

	public ActionVisitor(HashMap<String, HashSet<String>> alphabets) {
		this.alphabets = alphabets;
	}

	public void visitConstantProcessNode(ConstantProcessNode constant) {
		// it can never return null because alphabets is stable
		collectedAction.addAll(alphabets.get(constant.getName()));
	}

	public void visitPrefixNode(PrefixNode prefix) {
		// it may be called on an action defined in the system equation only!
		ActionSuperNode action = prefix.getActivity().getAction();
		if (action instanceof ActionTypeNode)
			collectedAction.add(((ActionTypeNode) action).getType());

		prefix.getTarget().accept(this); // move on
	}

}
