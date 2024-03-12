/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST for a passive rate. Algebric expressions are not allowed by the grammar,
 * but only the multiplicity can be set.
 * 
 * @author mtribast
 * 
 */
public class PassiveRateNode extends RateNode {
	
	public static final String INFTY = "infty";

	private int multiplicity = 1; //default multiplicity

	PassiveRateNode() {
	}

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitPassiveRateNode(this);
	}

	/**
	 * @return the value
	 */
	public int getMultiplicity() {
		return multiplicity;
	}

	/**
	 * @param multiplicity
	 *            the value to set
	 * @throws IllegalArgumentException
	 *             is <code>multiplicity</code> is less than 1
	 */
	public void setMultiplicity(int multiplicity) {
		if (multiplicity < 1)
			throw new IllegalArgumentException();
		this.multiplicity = multiplicity;
	}
}
