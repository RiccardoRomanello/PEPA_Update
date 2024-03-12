package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public interface ExpressionVisitor {
	
	public void visitCoordinate(Coordinate coordinate) throws DifferentialAnalysisException;
	
	public void visitMinimumExpression(MinimumExpression min) throws DifferentialAnalysisException;
	
	public void visitMultiplicationExpression(MultiplicationExpression mult) throws DifferentialAnalysisException;
	
	public void visitDivisionExpression(DivisionExpression div) throws DifferentialAnalysisException;
	
	public void visitRateExpression(RateExpression rate) throws DifferentialAnalysisException;
	
	public void visitSummationExpression(SummationExpression sum) throws DifferentialAnalysisException;

	public void visitSubtractionExpression(
			SubtractionExpression subtractionExpression)
			throws DifferentialAnalysisException;
	
}
