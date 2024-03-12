/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 14-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.recursive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.Activity;
import uk.ac.ed.inf.pepa.model.PassiveRate;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Rate;
import uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess;

/**
 * This class implements a process transition multiset. I.e., it implements a
 * collection of pairs (<code>Activity</code>,<code>Process</code>),
 * each pair thus specifing the action type, the rate and the target process. It
 * is important to point out that a transition list is a multiset, i.e. it may
 * contain instances of <code>Activity</code> which are equal (according to
 * the logical test performed by <code>Activity.equals(Object)</code>. For
 * example, if we consider this simple process:
 * <p>
 * P = (a,1).P; <br>
 * P || P <br>
 * Then the activity multiset of the parallel is {[(a,1), P || P], [(a,1), P ||
 * P]} The set thus contains <b>two</b> instances of the same activity. As a
 * result, a transition list cannot be implemented as a HashMap because these
 * two pairs would map on to the same item.
 * 
 * 
 * 
 * @author mtribast
 * 
 */
public class TransitionList {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(TransitionList.class);

	private final LinkedList<Activity> keys = new LinkedList<Activity>();

	private final LinkedList<Process> values = new LinkedList<Process>();

	public TransitionList() {
	}

	/**
	 * Adds a transition to this multiset.
	 * <br>
	 * <b>Important</b>. The signature of this method does not accept an instance
	 * of <code>Activity</code>, but it requires action and rate to be passed
	 * separately. The reason is to force the system to create a new <code>Activity</code>
	 * in order to implement an activity multiset whose equality is based on
	 * physical equality.
	 * 
	 * @param action
	 * @param rate
	 *            specifies action type and rate
	 * @param target
	 *            the targe process
	 */
	public void addTransition(Action action, Rate rate, Process target) {
		keys.add(DoMakePepaProcess.getInstance().createActivity(action, rate));
		values.add(target);
	}

	/**
	 * Gets an iterator over the transitions of this list
	 * 
	 * @return the iterator of instances of <code>TransitionEntry</code>
	 * 
	 * @see TransitionEntry
	 */
	public Iterator<TransitionEntry> iterator() {

		return new Iterator<TransitionEntry>() {

			private int index = 0;
			
			private boolean canRemove = true;

			public boolean hasNext() {
				return index < keys.size();
			}

			public TransitionEntry next() {
				
				canRemove = true;
				
				TransitionEntry entry = new TransitionEntry(keys.get(index),
						values.get(index));
				index++;
				return entry;
			}

			public void remove() {
				if (index < 1)
					throw new UnsupportedOperationException(
						"Transitions cannot be removed");
				if (!canRemove) 
					throw new IllegalStateException("Remove cannot be " +
							"called twice on the same element!");
				
				canRemove = false;
				
				keys.remove(index-1);
				values.remove(index-1);
				index--;
				
			}

		};
	}

	/**
	 * Determines whether an action type is performed in this multiset
	 * 
	 * @param action
	 *            action type to be tested
	 * @return <code>true</code> if the action type is performed
	 */
	public boolean containsAction(Action action) {
		for (Activity activity : keys) {
			if (action.equals(activity.getAction()))
				return true;
		}
		return false;
	}

	/**
	 * Gets the activities performing an action type
	 * 
	 * @param action
	 *            action type to be tested
	 * @return the array of activities or <code>null</code> if no matching activity has
	 *         been found
	 */
	public List<Activity> getActivity(Action action) {
		ArrayList<Activity> activities = new ArrayList<Activity>();
		for (Activity activity : keys) {
			if (action.equals(activity.getAction()))
				activities.add(activity);
		}
		return activities;

	}

	/**
	 * Returns the target of this activity
	 * 
	 * @param activity
	 *            the activity whose target has to be retrieved
	 * @return the target
	 */
	public Process getTarget(Activity activity) {
		Iterator<Activity> iter = keys.iterator();
		Activity current = null;
		int index = 0;
		while (iter.hasNext()) {
			current = iter.next();
			if (current == activity)
				return values.get(index);
			index++;
		}
		throw new NoSuchElementException();

	}

	/**
	 * Change the target process of each activity in the list.
	 * 
	 * @param oldProcess
	 *            process to be changed
	 * @param newProcess
	 *            new process
	 */
	public void changeTarget(Process oldProcess, Process newProcess) {
		Iterator<Process> iter = values.iterator();
		Process current;
		int index = 0;
		while (iter.hasNext()) {
			current = iter.next();
			if (current == oldProcess) {
				values.set(index, newProcess);

			}
			index++;
		}
	}

	/**
	 * 
	 * @return the size of the activity multiset
	 */
	public int size() {
		return keys.size();
	}
	
	/**
	 * This method is called by the main loop of the state space derivator
	 * when unbalanced passive rates are found. That might be due to mistakes
	 * such as synchronisations not defined in the cooperation set.
	 * @return if this transition set has passive rates
	 */
	public boolean hasPassiveRates() {
		for (int i = 0; i<keys.size(); i++) {
			Activity activity = keys.get(i);
			if (activity.getRate() instanceof PassiveRate) {
				return true;
			}
		}
		return false;
	}

}