package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class SummationExpression extends BinaryExpression {

	public SummationExpression(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	@Override
	public String toString() {
		return "((" + lhs.toString() + ") + (" + rhs.toString() + "))";
	}

	@Override
	public void accept(ExpressionVisitor visitor) throws DifferentialAnalysisException {
		visitor.visitSummationExpression(this);
	}

}
