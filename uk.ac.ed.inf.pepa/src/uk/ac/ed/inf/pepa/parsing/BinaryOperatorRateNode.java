/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import org.systemsbiology.math.Expression.Element;
import org.systemsbiology.math.Expression.ElementCode;

/**
 * AST node for binary operators on finite rates
 * 
 * @author mtribast
 * 
 */
public class BinaryOperatorRateNode extends FiniteRateNode {

	private RateNode left;

	private RateNode right;

	private Operator operator;

	/**
	 * Enumeration of the available binary operations between finite rates
	 * 
	 * @author mtribast
	 * 
	 */
	public enum Operator {
		PLUS("+", 10)
			{public double evaluate(double a, double b) {return a + b;}
			public Element getElement() {return new Element(ElementCode.ADD);}},
		MINUS("-", 10)
			{public double evaluate(double a, double b) {return a - b;}
			public Element getElement() {return new Element(ElementCode.SUBT);}},
		MULT("*", 20)
			{public double evaluate(double a, double b) {return a * b;}
			public Element getElement() {return new Element(ElementCode.MULT);}},
		DIV("/", 20)
			{public double evaluate(double a, double b) {return a / b;}
			public Element getElement() {return new Element(ElementCode.DIV);}};
		String name;
		int precedence;

		Operator(String name, int precedence) {
			this.name = name;
			this.precedence = precedence;
		}

		public String toString() {
			return name;
		}

		public int precedence() {
			return precedence;
		}

		public abstract double evaluate(double left, double right);
		
		public abstract Element getElement();
	}

	BinaryOperatorRateNode() {

	}

	/**
	 * @return the left
	 */
	public RateNode getLeft() {
		return left;
	}

	/**
	 * @param left
	 *            the left to set
	 * @throws NullPointerException
	 *             if the rate is null
	 */
	public void setLeft(RateNode left) {
		if (left == null)
			throw new NullPointerException();
		this.left = left;
	}

	/**
	 * @return the right
	 */
	public RateNode getRight() {
		return right;
	}

	/**
	 * @param right
	 *            the right to set
	 * @throws NullPointerException
	 *             if the rate is null
	 */
	public void setRight(RateNode right) {
		if (right == null)
			throw new NullPointerException();
		this.right = right;
	}

	public Operator getOperator() {
		return this.operator;
	}

	/**
	 * Set the operator
	 * 
	 * @param operator
	 * @throws NullPointerException
	 *             if the operator is null
	 */
	public void setOperator(Operator operator) {
		if (operator == null)
			throw new NullPointerException();
		this.operator = operator;
	}

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitBinaryOperatorRateNode(this);
	}

}
