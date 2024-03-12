/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 15-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.recursive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.ActionSet;
import uk.ac.ed.inf.pepa.model.Activity;
import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Rate;
import uk.ac.ed.inf.pepa.model.RateMath;
import uk.ac.ed.inf.pepa.model.SilentAction;
import uk.ac.ed.inf.pepa.model.Visitor;
import uk.ac.ed.inf.pepa.model.internal.AggregationImpl;
import uk.ac.ed.inf.pepa.model.internal.Cloner;
import uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess;

/**
 * This visitor builds the set of reachable states of the visited Process.
 * 
 * @author mtribast
 * 
 */
public class ActivityMultisetVisitor implements Visitor {

	private TransitionList transitions = new TransitionList();

	private DerivationException exception = null;

	private boolean success = true;

	//private IDynamicAnalyser analyser;

	private static Logger logger = Logger
			.getLogger(ActivityMultisetVisitor.class);
	
	private static HashMap<Constant, TransitionList> CONSTANT_CACHE =
		new HashMap<Constant, TransitionList>();
	
	//private static HashMap<>
	
	//public static int misses;
	
	//public static int hits;
	
	public static void init() {
		CONSTANT_CACHE.clear();
		//misses = 0;
		//hits = 0;
	}

	private static final DoMakePepaProcess factory = DoMakePepaProcess
			.getInstance();

	public ActivityMultisetVisitor() {
	}

	public boolean isSuccess() {
		return success;
	}

	public DerivationException getCause() {
		return exception;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Visitor#visitPrefix(uk.ac.ed.inf.pepa.Prefix)
	 */
	public void visitPrefix(Prefix prefix) {
		transitions.addTransition(prefix.getActivity().getAction(), prefix
				.getActivity().getRate(), prefix.getTargetProcess());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Visitor#visitChoice(uk.ac.ed.inf.pepa.Choice)
	 */
	public void visitChoice(Choice choice) {
		choice.getLeftHandSide().accept(this);
		choice.getRightHandSide().accept(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Visitor#visitHiding(uk.ac.ed.inf.pepa.Hiding)
	 */
	public void visitHiding(Hiding hiding) {
		ActivityMultisetVisitor hiddenProcessVisitor = new ActivityMultisetVisitor();
		hiding.getHiddenProcess().accept(hiddenProcessVisitor);
		if (!hiddenProcessVisitor.isSuccess()) {
			this.success = false;
			this.exception = hiddenProcessVisitor.getCause();
			return;
		}

		TransitionList hiddenProcessTransitions = hiddenProcessVisitor
				.getTransitions();

		Iterator<TransitionEntry> iterator = hiddenProcessTransitions
				.iterator();

		Hiding hidingTarget;

		/*
		 * For each action of the hiding activity set, determine if it is a
		 * hidden action or a visible one. If hidden, create a tau action type,
		 * otherwise keep the action name...
		 */

		/* Check each possible (a,r) for E */
		while (iterator.hasNext()) {
			TransitionEntry transition = iterator.next();

			/* hidingTarget is : E'/L */
			hidingTarget = factory.createHiding(transition.target, hiding
					.getActionSet());

			/* Now determine if the transition E/L -> E'/L */
			if (hiding.getActionSet().contains(transition.activity.getAction())) {
				/* is (tau, r) */
				SilentAction silentAction = factory
						.createSilentAction((NamedAction) (transition.activity)
								.getAction());
				transitions.addTransition(silentAction, transition.activity
						.getRate(), hidingTarget);
			} else
				/* or (a, r) */
				transitions.addTransition(transition.activity.getAction(),
						transition.activity.getRate(), hidingTarget);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Visitor#visitCooperation(uk.ac.ed.inf.pepa.Cooperation)
	 */
	public void visitCooperation(Cooperation cooperation) {

		//logger.debug("Visiting Cooperation" + cooperation.prettyPrint());

		ActivityMultisetVisitor lhsVisitor = new ActivityMultisetVisitor(
				);
		ActivityMultisetVisitor rhsVisitor = new ActivityMultisetVisitor(
				);

		cooperation.getLeftHandSide().accept(lhsVisitor);
		if (!lhsVisitor.isSuccess()) {
			this.success = false;
			this.exception = lhsVisitor.getCause();
			return;
		}

		cooperation.getRightHandSide().accept(rhsVisitor);
		if (!rhsVisitor.isSuccess()) {
			this.success = false;
			this.exception = rhsVisitor.getCause();
			return;
		}

		/*
		 * Add not synchronised activities to the activity multiset of the
		 * process
		 */
		ActionSet actions = cooperation.getActionSet();

		try {
			addNotSynchronisedActivities(lhsVisitor.getTransitions(),
					cooperation, true);
			addNotSynchronisedActivities(rhsVisitor.getTransitions(),
					cooperation, false);
			/* Add synchronised activities */
			/* Callback on the synchronised activities */
			addSynchronisedActivities(lhsVisitor.getTransitions(), rhsVisitor
					.getTransitions(), cooperation, actions);
		} catch (DerivationException e) {
			this.success = false;
			this.exception = e;
		}
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Visitor#visitConstant(uk.ac.ed.inf.pepa.Constant)
	 */
	public void visitConstant(Constant constant) {
		if (CONSTANT_CACHE.containsKey(constant)) {
			transitions = CONSTANT_CACHE.get(constant);
			//hits++;
			return;
		}
		//misses++;
		
		constant.getBinding().accept(this);
		/*
		 * For each obtained target, check if it is the same (i.e., equals()) as
		 * the process resolved by the constant. If it is true, then the target
		 * is substituted by the constant itself. This solves the problem which
		 * may occur in models like the following:
		 * 
		 * P = P1 P1 = (a,r).P1
		 * 
		 * In which an incorrect transition P -> P1 is derived. The wrong state
		 * space size is thus 2. The correct size should be 1 instead.
		 */
		Iterator<TransitionEntry> iter = transitions.iterator();
		while (iter.hasNext()) {
			Process target = iter.next().target;
			if (constant.getBinding().equals(target))
				transitions.changeTarget(target, constant);
		}
		CONSTANT_CACHE.put(constant, transitions);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Visitor#visitAggregation(uk.ac.ed.inf.pepa.Aggregation)
	 */
	public void visitAggregation(Aggregation aggregation) {
		/*
		 * Running example P1 = (a,1).P2; P2 = (b,1).P3;
		 * 
		 * P1[3] is the first aggregation
		 * 
		 */
		for (Map.Entry<Process, Integer> entry : aggregation.getSubProcesses()
				.entrySet()) {
			/* The first (and only subProcess is P1) */
			Process subProcess = entry.getKey();
			Integer numberOfCopies = entry.getValue();
			ActivityMultisetVisitor v = new ActivityMultisetVisitor(
					);
			subProcess.accept(v);
			if (!v.isSuccess()) {
				this.success = false;
				this.exception = v.getCause();
				return;
			}
			// v's transitions are subprocesses
			Iterator<TransitionEntry> iterator = v.getTransitions().iterator();
			while (iterator.hasNext()) {
				TransitionEntry transition = iterator.next();
				/* the target is P2. */
				Process target = transition.target;
				/*
				 * this means that the corresponding transition for the
				 * aggregation is the same aggregation with one more target and
				 * one less copy of the subprocess. In this case the subprocess
				 * was P1[3] -> P1[2] and the one more target is P2 so the
				 * aggregation is P1[2], P2 and the rate of the transition is
				 * the number of copies of the subprocess (3)
				 */
				AggregationImpl targetAggregation = (AggregationImpl) Cloner
						.clone(aggregation);
				targetAggregation.increaseCopies(target, 1);
				targetAggregation.decreaseCopies(subProcess, 1);
				transitions.addTransition(transition.activity.getAction(),
						RateMath.mult(transition.activity.getRate(),
								numberOfCopies), targetAggregation);

			}

		}

	}

	public TransitionList getTransitions() {
		return transitions;
	}

	/**
	 * Add not synchronised activities to the activity multiset of the process
	 * 
	 * @param transitionsP
	 *            List of activities to be added
	 * @param set
	 *            Set of synchronised activities for this cooperation
	 * @param activities
	 *            Activity Multiset for this cooperation
	 * @param lhs
	 *            <code>true</code> if the method has to work on the left hand
	 *            side of the cooperation. This parameter is used to create the
	 *            target process
	 * @throws DerivationException
	 */
	private void addNotSynchronisedActivities(TransitionList transitionsP,
			Cooperation cooperation, boolean lhs) throws DerivationException {

		ActionSet set = cooperation.getActionSet();
		// logger.debug("Set " + set.prettyPrint());
		Iterator<TransitionEntry> iter = transitionsP.iterator();
		// logger.debug("Size left/right: " + transitionsP.size() + " "
		// + cooperation.prettyPrint());
		Action action = null; /* Action to be tested */
		while (iter.hasNext() == true) {
			TransitionEntry transition = iter.next();
			action = transition.activity.getAction();
			if (set.contains(action) == false) {
				/*
				 * This is an unshared activity, which thus can be added to the
				 * activity multiset of this cooperation. Consistency check
				 * requires the activity be defined by a finite rate
				 */
				Cooperation target = null;
				if (lhs == true) {
					target = factory.createCooperation(transition.target,
							cooperation.getRightHandSide(), set);
				} else {
					target = factory.createCooperation(cooperation
							.getLeftHandSide(), transition.target, set);

				}
				transitions.addTransition(transition.activity.getAction(),
						transition.activity.getRate(), target);
				/*logger
						.debug("Transition added in a not synchronised activity: "
								+ transition.activity.prettyPrint()
								+ " by lhs " + lhs);*/
			}
		}

	}

	/**
	 * Add synchronised activities to the activity multiset of this cooperation
	 * 
	 * @param transitionsP
	 * @param transitionsQ
	 * @param set
	 * @throws DerivationException
	 */
	private void addSynchronisedActivities(TransitionList transitionsP,
			TransitionList transitionsQ, Cooperation process, ActionSet set)
			throws DerivationException {

		//logger.debug("Syncronised activities for: " + process.prettyPrint());

		Iterator<Action> syncActionIter = set.iterator();
		Action currentAction = null;
		while (syncActionIter.hasNext() == true) {

			currentAction = syncActionIter.next();

			if (transitionsP.containsAction(currentAction)
					&& transitionsQ.containsAction(currentAction)) {

				
				Rate appP = RateMath.getApparentRate(process.getLeftHandSide(),
						currentAction);

				Rate appQ = RateMath.getApparentRate(
						process.getRightHandSide(), currentAction);

				Rate minRate = RateMath.min(appP, appQ);

				List<Activity> activitiesP = transitionsP
						.getActivity(currentAction);
				List<Activity> activitiesQ = transitionsQ
						.getActivity(currentAction);

				for (Activity activityP : activitiesP) {

					Rate r1 = RateMath.div(activityP.getRate(), appP);

					for (Activity activityQ : activitiesQ) {

						Rate r2 = RateMath.div(activityQ.getRate(), appQ);

						/*
						 * Doing some castings, check if the instanceof is OK,
						 * otherwise throw an exception
						 */
						/*
						 * if (!(r1 instanceof FiniteRate) || !(r2 instanceof
						 * FiniteRate) || !(minRate instanceof FiniteRate))
						 * throw new DerivationException( "Found passive rates
						 * in synchronised activities"); FiniteRate finalRate =
						 * factory .createFiniteRate(((FiniteRate)
						 * r1).getValue() ((FiniteRate) r2).getValue()
						 * ((FiniteRate) minRate).getValue());
						 */

						Cooperation target = factory.createCooperation(
								transitionsP.getTarget(activityP), transitionsQ
										.getTarget(activityQ), set);


						Rate finalRate = RateMath.mult(RateMath.mult(r1, r2),
								minRate);
						/*
						 * Changed, final rate can be passive also
						 */
						transitions.addTransition(currentAction, finalRate,
								target);
					}
				}
			}
		}

	}
}