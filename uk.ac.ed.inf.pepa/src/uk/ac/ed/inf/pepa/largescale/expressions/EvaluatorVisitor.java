package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class EvaluatorVisitor implements ExpressionVisitor {

	private double[] state;

	private double result = 0.0;

	/**
	 * @param expression
	 *            expression to evaluate
	 * @param currentState
	 *            current population levels
	 * @throws DifferentialAnalysisException
	 */
	public EvaluatorVisitor(Expression expression, double[] currentState)
			throws DifferentialAnalysisException {
		if (expression == null)
			throw new NullPointerException("Exception cannot be null");
		if (currentState == null)
			throw new NullPointerException("State cannot be null");
		if (currentState.length == 0)
			throw new IllegalArgumentException(
					"State must have at least one coordinate");
		this.state = currentState;
		expression.accept(this);
	}

	public EvaluatorVisitor() {

	}

	public double getResult(Expression expression, double[] state)
			throws DifferentialAnalysisException {
		this.state = state;
		expression.accept(this);
		return result;
	}

	public double getResult() {
		return result;
	}

	public void visitCoordinate(Coordinate coordinate)
			throws DifferentialAnalysisException {
		int index = coordinate.getCoordinate();
		if (state.length < (index + 1))
			throw new DifferentialAnalysisException(
					"Cannot accept state to evaluate. Too short.");
		result = state[index];

	}

	public void visitDivisionExpression(DivisionExpression div)
			throws DifferentialAnalysisException {
		div.getLhs().accept(this);
		double lhs = result;
		div.getRhs().accept(this);
		double rhs = result;
		if (lhs == 0 && rhs == 0)
			result = 1;
		else
			result = lhs / rhs;
	}

	public void visitMinimumExpression(MinimumExpression min)
			throws DifferentialAnalysisException {
		min.getLhs().accept(this);
		double lhs = result;
		min.getRhs().accept(this);
		result = Math.min(result, lhs);

	}

	public void visitMultiplicationExpression(MultiplicationExpression mult)
			throws DifferentialAnalysisException {
		mult.getLhs().accept(this);
		double lhs = result;
		mult.getRhs().accept(this);
		result = result * lhs;

	}

	public void visitRateExpression(RateExpression rate)
			throws DifferentialAnalysisException {
		result = rate.getRate();

	}

	public void visitSummationExpression(SummationExpression sum)
			throws DifferentialAnalysisException {
		sum.getLhs().accept(this);
		double lhs = result;
		sum.getRhs().accept(this);
		result = result + lhs;
	}

	public void visitSubtractionExpression(
			SubtractionExpression subtractionExpression) {
		throw new IllegalStateException();
	}

}
