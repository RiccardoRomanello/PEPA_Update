/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST for a literal finite rate
 * @author mtribast
 *
 */
public class RateDoubleNode extends FiniteRateNode {

	private double value;

	RateDoubleNode() {
	}
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitRateDoubleNode(this);
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 * @throws IllegalArgumentException is value less than 0
	 */
	public void setValue(double value) {
		if (value<0)
			throw new IllegalArgumentException();
		this.value = value;
	}
}
