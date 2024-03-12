package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

/**
 * A generic expression used in the ODE context. It may be
 * a minimum function, a fraction, multiplication, and so on
 * @author Mirco
 *
 */
public abstract class Expression {
	
	public abstract String toString();
	
	public abstract void accept(ExpressionVisitor visitor) throws DifferentialAnalysisException;
}
