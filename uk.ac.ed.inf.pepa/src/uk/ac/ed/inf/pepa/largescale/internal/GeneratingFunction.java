package uk.ac.ed.inf.pepa.largescale.internal;

import uk.ac.ed.inf.pepa.largescale.IGeneratingFunction;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;

public class GeneratingFunction implements IGeneratingFunction {
	
	private short[] representativeSource;
	
	private short[] representativeTarget;
	
	public short[] getRepresentativeSource() {
		return representativeSource;
	}

	public void setRepresentativeSource(short[] representativeSource) {
		this.representativeSource = representativeSource;
	}

	public short[] getRepresentativeTarget() {
		return representativeTarget;
	}

	public void setRepresentativeTarget(short[] representativeTarget) {
		this.representativeTarget = representativeTarget;
	}

	private short[] jump;
	
	private short actionId;
	
	private Expression rate;

	public short[] getJump() {
		return jump;
	}

	public void setJump(short[] jump) {
		this.jump = jump;
	}

	public short getActionId() {
		return actionId;
	}

	public void setActionId(short actionId) {
		this.actionId = actionId;
	}

	public Expression getRate() {
		return rate;
	}

	public void setRate(Expression rate) {
		this.rate = rate;
	}
}
