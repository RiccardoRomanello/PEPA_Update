package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class SubtractionExpression extends BinaryExpression {

	public SubtractionExpression(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	@Override
	public void accept(ExpressionVisitor visitor) throws DifferentialAnalysisException {
		visitor.visitSubtractionExpression(this);
	}

	@Override
	public String toString() {
		return "((" + lhs.toString() + ") - (" + rhs.toString() + "))";
	}

}
