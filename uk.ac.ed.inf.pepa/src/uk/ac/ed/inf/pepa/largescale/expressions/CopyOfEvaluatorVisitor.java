package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

// version with many instantiations
public class CopyOfEvaluatorVisitor implements ExpressionVisitor {

	private double[] state;
	
	private double result = 0.0;
	
	/**
	 * @param expression expression to evaluate
	 * @param currentState current population levels
	 * @throws DifferentialAnalysisException 
	 */
	public CopyOfEvaluatorVisitor(Expression expression, double[] currentState) throws DifferentialAnalysisException {
		if (expression == null) 
			throw new NullPointerException("Exception cannot be null");
		if (currentState == null)
			throw new NullPointerException("State cannot be null");
		if (currentState.length ==0 )
			throw new IllegalArgumentException("State must have at least one coordinate");
		this.state = currentState;
		expression.accept(this);
	}
	
	public double getResult() {
		return result;
	}
	
	public void visitCoordinate(Coordinate coordinate)
			throws DifferentialAnalysisException {
		int index = coordinate.getCoordinate();
		if (state.length < (index+1))
			throw new DifferentialAnalysisException("Cannot accept state to evaluate. Too short.");
		result = state[index];
		
	}

	public void visitDivisionExpression(DivisionExpression div)
			throws DifferentialAnalysisException {
		CopyOfEvaluatorVisitor lhs = new CopyOfEvaluatorVisitor(div.getLhs(), state);
		CopyOfEvaluatorVisitor rhs = new CopyOfEvaluatorVisitor(div.getRhs(), state);
		if (lhs.getResult() == 0 && rhs.getResult() == 0)
			result = 1;
		else
			result = lhs.getResult() / rhs.getResult();
	}

	public void visitMinimumExpression(MinimumExpression min)
			throws DifferentialAnalysisException {
		CopyOfEvaluatorVisitor lhs = new CopyOfEvaluatorVisitor(min.getLhs(), state);
		CopyOfEvaluatorVisitor rhs = new CopyOfEvaluatorVisitor(min.getRhs(), state);
		result = Math.min(lhs.getResult() , rhs.getResult());
		
	}

	public void visitMultiplicationExpression(MultiplicationExpression mult)
			throws DifferentialAnalysisException {
		CopyOfEvaluatorVisitor lhs = new CopyOfEvaluatorVisitor(mult.getLhs(), state);
		CopyOfEvaluatorVisitor rhs = new CopyOfEvaluatorVisitor(mult.getRhs(), state);
		result = lhs.getResult() * rhs.getResult();
	
	}

	public void visitRateExpression(RateExpression rate)
			throws DifferentialAnalysisException {
		result = rate.getRate();
		
	}

	public void visitSummationExpression(SummationExpression sum)
			throws DifferentialAnalysisException {
		CopyOfEvaluatorVisitor lhs = new CopyOfEvaluatorVisitor(sum.getLhs(), state);
		CopyOfEvaluatorVisitor rhs = new CopyOfEvaluatorVisitor(sum.getRhs(), state);
		result = lhs.getResult() + rhs.getResult();
	
	}

	public void visitSubtractionExpression(
			SubtractionExpression subtractionExpression) {
		throw new IllegalStateException();
	}


}
