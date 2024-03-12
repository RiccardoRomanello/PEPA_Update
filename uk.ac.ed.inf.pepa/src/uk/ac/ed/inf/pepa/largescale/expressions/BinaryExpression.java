package uk.ac.ed.inf.pepa.largescale.expressions;

public abstract class BinaryExpression extends Expression {

	protected Expression lhs;
	
	protected Expression rhs;

	BinaryExpression(Expression lhs, Expression rhs) {
		if (lhs == null)
			throw new NullPointerException("Lhs in expression is null");
		if (rhs == null)
			throw new NullPointerException("Rhs in expression is null");
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Expression getLhs() {
		return lhs;
	}
	
	public void setLhs(Expression lhs) {
		this.lhs = lhs;
	}
	
	public Expression getRhs() {
		return rhs;
	}
	
	public void setRhs(Expression rhs) {
		this.rhs = rhs;
	}
	
}
