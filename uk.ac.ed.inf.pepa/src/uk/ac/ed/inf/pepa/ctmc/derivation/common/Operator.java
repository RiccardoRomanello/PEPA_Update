/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.Arrays;
import java.util.BitSet;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;

/**
 * An <code>Operator</code> is an internal node of the model's tree. Its main
 * operation is <code>compose</code>, whose task is to create first step
 * derivatives from the first step derivatives of its children according to the
 * semantics of PEPA.
 * 
 * @author mtribast
 * 
 */
public class Operator extends Component {

	/**
	 * Left child.
	 */
	private Component fLeft;

	/**
	 * Right child.
	 */
	private Component fRight;

	/**
	 * An action set is represented by a set of action identifiers. For
	 * performance it is required that it be a set backed by a hash map.
	 */
	private BitSet fActionSet;
	/**
	 * Creates this operator with an action set for cooperation
	 * 
	 * @param stateExplorer
	 *            the state explorer this component belongs to
	 * @param actionSet
	 *            the action set for cooperation
	 */
	public Operator() {
		this(null);
	}

	public Operator(String name) {
		super(name);
	}
	
	public void setCooperationSet(BitSet bitSet) {
		if (bitSet == null)
			throw new NullPointerException();
		fActionSet = bitSet;
	}

	/**
	 * Sets the first step derivatives and the apparent rates of this composed
	 * process according to the operational semantics of PEPA
	 */
	public void compose(short[] state) throws DerivationException {
		Arrays.fill(fApparentRates, 0.0d);
		fFirstStepDerivatives.clear();
		
		/* Cycle through left hand side */
		for (Transition leftEntry : fLeft.fFirstStepDerivatives) {
			/* Cycle through right hand side */
			if (!fActionSet.get(leftEntry.fActionId)) {
				/* Left is not a shared activity */
				createLeftOnlyTransition(state, leftEntry);
			} else {
				/*
				 * Left is a shared activity Right side is being explored
				 */
				for (Transition rightEntry : fRight.fFirstStepDerivatives) {

					/* also this right entry is shared */
					if (rightEntry.fActionId == leftEntry.fActionId) {
						/* The right activity has the same action */
						createSharedTransition(state, leftEntry, rightEntry);
					}
				}
			}
		}
		/*
		 * Look for unshared actions on the right
		 */
		for (Transition rightEntry : fRight.fFirstStepDerivatives) {
			if (!fActionSet.get(rightEntry.fActionId)) {
				createRightOnlyTransition(state, rightEntry);
			}
		}
	
		
	}

	private void createLeftOnlyTransition(short[] state, Transition leftEntry)
			throws DerivationException {
		/*
		 * The new state is the same as the current for the right child of this
		 * composition, because it is a left-only transition
		 */
		
		/*short[] newState = buf.getState(state, fRight.fOffset,
				fRight.fLength);
		// populates left hand side
		for (int i = 0; i < fLeft.fLength; i++) {
			newState[fLeft.fOffset + i] = leftEntry.fTargetProcess[fLeft.fOffset
					+ i];
		}
		Transition t = createTransition(newState, leftEntry.fActionId,
				leftEntry.fRate);*/
		
		Transition t = buf.getTransition(state, fRight.fOffset, 
				fRight.fLength, checkAction(leftEntry.fActionId), leftEntry.fLevel,
				leftEntry.fRate);
		for (int i = 0; i < fLeft.fLength; i++) {
			t.fTargetProcess[fLeft.fOffset + i] = leftEntry.fTargetProcess[fLeft.fOffset + i];
		}
		/*
		 * Creates the apparent rate for an unshared variable Here the right
		 * might not have the apparent rate
		 */
		double left = this.fLeft.fApparentRates[t.fActionId];
		/* Left is null iff action id is tau */
		if (left == 0) {
			if (t.fActionId != ISymbolGenerator.TAU_ACTION)
				throw new IllegalStateException(
						"There must be an apparent rate!");
		}
		double right = this.fRight.fApparentRates[t.fActionId];
		
		// the two apparent rates must be of the same type
		assertSameType(left, right);
		update(t, left + right);
	}

	private void createRightOnlyTransition(short[] state, Transition rightEntry)
			throws DerivationException {
		/*
		 * The new state is the same as the current for the left child of this
		 * composition, because it is a right-only transition
		 */
		/*short[] newState = buf.getState(state, fLeft.fOffset,
				fLeft.fLength);
		for (int i = 0; i < fRight.fLength; i++) {
			newState[fRight.fOffset + i] = rightEntry.fTargetProcess[fRight.fOffset
					+ i];
		}
		Transition t = createTransition(newState, rightEntry.fActionId,
				rightEntry.fRate);
		*/
		Transition t = buf.getTransition(state, fLeft.fOffset, 
				fLeft.fLength, checkAction(rightEntry.fActionId), rightEntry.fLevel,
				rightEntry.fRate);
		for (int i = 0; i < fRight.fLength; i++) {
			t.fTargetProcess[fRight.fOffset + i] = rightEntry.fTargetProcess[fRight.fOffset + i];
		}
		
		/*
		 * Creates the apparent rate for an unshared variable Here the left
		 * might not have the apparent rate
		 */
		double left = this.fLeft.fApparentRates[t.fActionId];
		double right = this.fRight.fApparentRates[t.fActionId];
		if (right == 0) {
			if (t.fActionId != ISymbolGenerator.TAU_ACTION)
				throw new IllegalStateException(
						"There must be an apparent rate!");
		}
		assertSameType(left, right);
		update(t, left + right);

	}

	/*
	 * Checks if the two rates are of the same type. If not throws an exception.
	 */
	private static void assertSameType(double rate1, double rate2)
			throws DerivationException {
		if (!OptimisedRateMath.areSameType(rate1, rate2)) {
			throw new DerivationException("Mixing passive and active rates.");
		}
	}

	private void createSharedTransition(short[] state, Transition leftEntry,
			Transition rightEntry) {
		// a new state to be filled

		// calculates the apparent rate
		short sharedActionId = leftEntry.fActionId;
		double apparentRateLeft = this.fLeft.fApparentRates[sharedActionId];
		double apparentRateRight = this.fRight.fApparentRates[sharedActionId];
		double minApparentRates = OptimisedRateMath.min(apparentRateLeft,
				apparentRateRight);
		double first = leftEntry.fRate / apparentRateLeft;
		double second = rightEntry.fRate / apparentRateRight;
		
		/**
		 * Note Here: the transition is created with the final rate, whereas the
		 * apparent rate of this process is the minimum of the apparent rates of
		 * the cooperating processes
		 */
		double temp = first * second;
		double finalRate = temp * minApparentRates;
		
		Transition t = buf.getTransition(state, 0, 0, checkAction(sharedActionId),
										 leftEntry.fLevel, finalRate);
		short[] newState = t.fTargetProcess;
		// populates left child
		for (int i = 0; i < fLeft.fLength; i++) {
			newState[fLeft.fOffset + i] = leftEntry.fTargetProcess[fLeft.fOffset + i];
		}

		// populates right child
		for (int i = 0; i < fRight.fLength; i++) {
			newState[fRight.fOffset + i] = rightEntry.fTargetProcess[fRight.fOffset + i];
		}
		
		// update with the apparent rates, which will be used later
		update(t, minApparentRates);

	}
	
	private final short checkAction(short action) {
		return fHidingSet.get(action) ? ISymbolGenerator.TAU_ACTION : action;
	}

	/**
	 * Creates the transition with the given parameters.
	 * 
	 * @param newState
	 * @param actionId
	 * @param rate
	 */
	private Transition createTransition(short[] newState, short actionId,
			double rate) {
		Transition transition = null;//new Transition();
		transition.fTargetProcess = newState;

		// check if the action is hidden
		if (this.fHidingSet.get(actionId))
			transition.fActionId = ISymbolGenerator.TAU_ACTION;
		else
			transition.fActionId = actionId;
		transition.fRate = rate;

		return transition;
	}

	/**
	 * Updates the state of the operator, i.e. updates its first step
	 * derivatives as well as the apparent rates which will be used by upper
	 * composers
	 */
	private final void update(Transition transition, double apparentRate) {
		this.fFirstStepDerivatives.add(transition);
		fApparentRates[transition.fActionId] =  apparentRate;
	}

	public String toString() {
		return (getName()!= null) ? getName(): 
			"Left: " + fLeft + " Right:" + fRight;
	}

	/**
	 * Return the left child of this operator
	 * 
	 * @return the left child of this operator
	 */
	public Component getLeftChild() {
		return fLeft;
	}

	/**
	 * Return the right child of this operator
	 * 
	 * @return the right child of this operator
	 */
	public Component getRightChild() {
		return fRight;
	}

	/**
	 * Set the left child of this operator
	 */
	public void setLeftChild(Component leftChild) {
		this.fLeft = leftChild;
		updateOffsetAndLength();
	}

	/**
	 * Set the right child of this operator
	 */
	public void setRightChild(Component rightChild) {
		this.fRight = rightChild;
		updateOffsetAndLength();
	}

	private void updateOffsetAndLength() {
		if (this.fLeft != null) {
			this.fOffset = this.fLeft.fOffset;
			if (this.fRight != null) {
				this.fLength = this.fLeft.fLength + this.fRight.fLength;
			}
		}
	}

	
}
