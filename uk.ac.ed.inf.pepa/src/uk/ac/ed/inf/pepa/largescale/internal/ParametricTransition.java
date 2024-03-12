package uk.ac.ed.inf.pepa.largescale.internal;

import uk.ac.ed.inf.pepa.largescale.expressions.Expression;


public class ParametricTransition {
	
	private Expression parametricRate;
	
	private short actionId;
	
	private short[] target;
	
	public static ParametricTransition create(short[] state, int offset, int length,
			short actionId, Expression rate) {
		ParametricTransition newTransition = new ParametricTransition();
		newTransition.setActionId(actionId);
		short[] targetProcess = new short[state.length];
		newTransition.setParametricRate(rate);
		for (int i = 0; i < length; i++) {
			targetProcess[offset + i] = state[offset + i];
		}
		newTransition.setTarget(targetProcess);
		return newTransition;
	}
	public Expression getParametricRate() {
		return parametricRate;
	}

	public void setParametricRate(Expression parametricRate) {
		this.parametricRate = parametricRate;
	}

	public short getActionId() {
		return actionId;
	}

	public void setActionId(short actionId) {
		this.actionId = actionId;
	}

	public short[] getTarget() {
		return target;
	}

	public void setTarget(short[] target) {
		this.target = target;
	}
	
	public String toString() {
		String result =  "ParametricTransition: actionId = " + actionId + ", rate=" + parametricRate + ", target=[";
		for (short s : target) {
			result = result + " " + s;
		}
		return result + "]";
	}
	
}
