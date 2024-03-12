package uk.ac.ed.inf.pepa.largescale.internal;

import uk.ac.ed.inf.pepa.largescale.IParametricTransitionTriple;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;

public class ParametricTransitionTriple implements IParametricTransitionTriple {
	
	private short[] source;
	
	private short[] target;
	
	private Expression rate;
	
	private short actionId;
	
	public short[] getSource() {
		return source;
	}

	public void setSource(short[] source) {
		this.source = source;
	}

	public short[] getTarget() {
		return target;
	}

	public void setTarget(short[] target) {
		this.target = target;
	}

	public Expression getRate() {
		return rate;
	}

	public void setRate(Expression rate) {
		this.rate = rate;
	}

	public short getActionId() {
		return actionId;
	}

	public void setActionId(short actionId) {
		this.actionId = actionId;
	}
	
}
