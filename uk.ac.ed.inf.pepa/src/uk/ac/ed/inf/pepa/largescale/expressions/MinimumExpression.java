package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class MinimumExpression extends BinaryExpression {

	public MinimumExpression(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	@Override
	public String toString() {
		return "min(" + lhs.toString() + "," + rhs.toString() + ")";
	}

	@Override
	public void accept(ExpressionVisitor visitor) throws DifferentialAnalysisException {
		visitor.visitMinimumExpression(this);
	}
	
	
}
