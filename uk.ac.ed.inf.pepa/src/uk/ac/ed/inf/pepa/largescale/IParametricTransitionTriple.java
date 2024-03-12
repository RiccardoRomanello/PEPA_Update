package uk.ac.ed.inf.pepa.largescale;

import uk.ac.ed.inf.pepa.largescale.expressions.Expression;

public interface IParametricTransitionTriple {

	public short[] getSource();

	public short[] getTarget();

	public Expression getRate();

	public short getActionId();

}