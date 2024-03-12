package uk.ac.ed.inf.pepa.largescale.internal;

import java.util.BitSet;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.largescale.IParametricStructuralElement;
import uk.ac.ed.inf.pepa.largescale.expressions.DivisionExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;
import uk.ac.ed.inf.pepa.largescale.expressions.MinimumExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.MultiplicationExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.SummationExpression;

public class ParametricOperator extends ParametricStructuralElement {

	private boolean firstVisit = true;

	private BitSet actionSet;

	private IParametricStructuralElement lhs;

	private IParametricStructuralElement rhs;

	public ParametricOperator() {
	}

	public IParametricStructuralElement getLeftChild() {
		return lhs;
	}

	public IParametricStructuralElement getRightChild() {
		return rhs;
	}

	public void setCooperationSet(BitSet bitSet) {
		if (bitSet == null)
			throw new NullPointerException();
		actionSet = bitSet;
	}

	public void setLeftChild(IParametricStructuralElement lhs) {
		this.lhs = lhs;
		updateOffsetAndLength();
	}

	public void setRightChild(IParametricStructuralElement rhs) {
		this.rhs = rhs;
		updateOffsetAndLength();
	}

	private void updateOffsetAndLength() {
		if (this.lhs != null) {
			setOffset(lhs.getOffset());
			if (this.rhs != null) {
				setLength(lhs.getLength() + rhs.getLength());
			}
		}
	}

	public String toString() {
		return "ODE operator:\n left:" + lhs.toString() + " right: "
				+ rhs.toString();

	}

	private void setApparentRates() {
		for (Entry<Short, Expression> entry : ((ParametricStructuralElement) this.lhs).apparentRates
				.entrySet()) {
			apparentRates.put(entry.getKey(), entry.getValue());
		}
		for (Entry<Short, Expression> entry : ((ParametricStructuralElement) this.rhs).apparentRates
				.entrySet()) {
			Expression expression = apparentRates.get(entry.getKey());
			if (actionSet.get(entry.getKey())) {
				apparentRates.put(entry.getKey(), new MinimumExpression(
						expression, entry.getValue()));
			} else {
				// independent action
				if (expression == null) {
					// left hand side does not contain it
					apparentRates.put(entry.getKey(), entry.getValue());
				} else {
					// sum left and right sides
					apparentRates.put(entry.getKey(), new SummationExpression(
							expression, entry.getValue()));
				}
			}
		}
	}

	/**
	 * Sets the first step derivatives and the apparent rates of this composed
	 * process according to the operational semantics of PEPA
	 */
	public void compose(short[] state) {
		// apparentRates.clear();
		if (firstVisit) {
			setApparentRates();
			firstVisit = false;
		}
		derivatives.clear();

		/* Cycle through left hand side */
		for (ParametricTransition leftEntry : lhs.getDerivatives()) {
			/* Cycle through right hand side */
			if (leftEntry.getActionId() == ISymbolGenerator.TAU_ACTION
					|| !actionSet.get(leftEntry.getActionId())) {
				/* Left is not a shared activity */
				createLeftOnlyTransition(state, leftEntry);
			} else {
				/*
				 * Left is a shared activity Right side is being explored
				 */
				for (ParametricTransition rightEntry : rhs.getDerivatives()) {

					/* also this right entry is shared */
					if (rightEntry.getActionId() == leftEntry.getActionId()) {
						/* The right activity has the same action */
						createSharedTransition(state, leftEntry, rightEntry);
					}
				}
			}
		}
		/*
		 * Look for unshared actions on the right
		 */
		for (ParametricTransition rightEntry : rhs.getDerivatives()) {
			if (rightEntry.getActionId() == ISymbolGenerator.TAU_ACTION
					|| !actionSet.get(rightEntry.getActionId())) {
				createRightOnlyTransition(state, rightEntry);
			}
		}
	}

	private void createLeftOnlyTransition(short[] state,
			ParametricTransition leftEntry) {
		/*
		 * The new state is the same as the current for the right child of this
		 * composition, because it is a left-only transition
		 */

		ParametricTransition t = ParametricTransition.create(state, rhs
				.getOffset(), rhs.getLength(), checkAction(leftEntry
				.getActionId()), leftEntry.getParametricRate());
		short[] targetProcess = t.getTarget();
		for (int i = 0; i < lhs.getLength(); i++) {
			targetProcess[lhs.getOffset() + i] = leftEntry.getTarget()[lhs
					.getOffset()
					+ i];
		}
		/*
		 * Creates the apparent rate for an unshared variable Here the right
		 * might not have the apparent rate
		 */
		Expression left = lhs.getApparentRate(t.getActionId());
		/* Left is null iff action id is tau */
		if (left == null) {
			if (t.getActionId() != ISymbolGenerator.TAU_ACTION)
				throw new IllegalStateException(
						"There must be an apparent rate!");
		}
		Expression right = rhs.getApparentRate(t.getActionId());

		if (right == null) { // there may not be the action in rhs
			update(t, left);
		} else {
			update(t, new SummationExpression(left, right));
		}
	}

	private void createRightOnlyTransition(short[] state,
			ParametricTransition rightEntry) {
		/*
		 * The new state is the same as the current for the left child of this
		 * composition, because it is a right-only transition
		 */
		ParametricTransition t = ParametricTransition.create(state, lhs
				.getOffset(), lhs.getLength(), checkAction(rightEntry
				.getActionId()), rightEntry.getParametricRate());
		short[] targetProcess = t.getTarget();
		for (int i = 0; i < rhs.getLength(); i++) {
			targetProcess[rhs.getOffset() + i] = rightEntry.getTarget()[rhs
					.getOffset()
					+ i];
		}

		/*
		 * Creates the apparent rate for an unshared variable Here the left
		 * might not have the apparent rate
		 */
		Expression left = lhs.getApparentRate(t.getActionId());
		Expression right = rhs.getApparentRate(t.getActionId());
		if (right == null) {
			if (t.getActionId() != ISymbolGenerator.TAU_ACTION)
				throw new IllegalStateException(
						"There must be an apparent rate!");
		}
		if (left == null) { // there may not be the action in left
			update(t, right);

		} else {
			update(t, new SummationExpression(left, right));
		}
	}

	private void createSharedTransition(short[] state,
			ParametricTransition leftEntry, ParametricTransition rightEntry) {
		// a new state to be filled

		// calculates the apparent rate
		short sharedActionId = leftEntry.getActionId();
		Expression apparentRateLeft = this.lhs.getApparentRate(sharedActionId);
		Expression apparentRateRight = this.rhs.getApparentRate(sharedActionId);
		Expression minApparentRates = new MinimumExpression(apparentRateLeft,
				apparentRateRight);
		Expression first = new DivisionExpression(
				leftEntry.getParametricRate(), apparentRateLeft);
		Expression second = new DivisionExpression(rightEntry
				.getParametricRate(), apparentRateRight);

		/**
		 * Note Here: the transition is created with the final rate, whereas the
		 * apparent rate of this process is the minimum of the apparent rates of
		 * the cooperating processes
		 */
		Expression temp = new MultiplicationExpression(first, second);
		Expression finalRate = new MultiplicationExpression(temp,
				minApparentRates);

		ParametricTransition t = ParametricTransition.create(state, 0, 0,
				checkAction(sharedActionId), finalRate);
		short[] newState = t.getTarget();
		// populates left child
		for (int i = 0; i < lhs.getLength(); i++) {
			newState[lhs.getOffset() + i] = leftEntry.getTarget()[lhs
					.getOffset()
					+ i];
		}

		// populates right child
		for (int i = 0; i < rhs.getLength(); i++) {
			newState[rhs.getOffset() + i] = rightEntry.getTarget()[rhs
					.getOffset()
					+ i];
		}

		// update with the apparent rates, which will be used later
		update(t, minApparentRates);

	}

	private final short checkAction(short action) {
		if (action == ISymbolGenerator.TAU_ACTION)
			return action;
		else
			return hidingSet.get(action) ? ISymbolGenerator.TAU_ACTION : action;
	}

	/**
	 * Updates the state of the operator, i.e. updates its first step
	 * derivatives as well as the apparent rates which will be used by upper
	 * composers
	 */
	private final void update(ParametricTransition transition,
			Expression apparentRate) {
		derivatives.add(transition);
		// if (transition.getActionId() != ISymbolGenerator.TAU_ACTION)
		// apparentRates.put(transition.getActionId(), apparentRate);
	}

}
