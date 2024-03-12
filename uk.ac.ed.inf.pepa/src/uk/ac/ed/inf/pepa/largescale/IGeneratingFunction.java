package uk.ac.ed.inf.pepa.largescale;

import uk.ac.ed.inf.pepa.largescale.expressions.Expression;

public interface IGeneratingFunction {

	public short[] getRepresentativeSource();

	public short[] getRepresentativeTarget();

	public short[] getJump();

	public short getActionId();

	public Expression getRate();

}