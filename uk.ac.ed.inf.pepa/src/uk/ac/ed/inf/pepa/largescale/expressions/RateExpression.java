package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class RateExpression extends Expression {

	private double rate;

	public RateExpression(double rate) {
		this.rate = rate;
	}

	public double getRate() {
		return rate;
	}

	@Override
	public String toString() {
		return "" + rate;
	}

	@Override
	public void accept(ExpressionVisitor visitor) throws DifferentialAnalysisException {
		visitor.visitRateExpression(this);
	}

}
