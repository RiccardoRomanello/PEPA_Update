/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST for a rate definition
 * 
 * <pre>
 * r = 1.0;
 * </pre>
 * 
 * @author mtribast
 * 
 */
public class RateDefinitionNode extends ASTNode {

	private VariableRateNode name;

	private RateNode rate;
	
	public RateDefinitionNode() {
	}
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitRateDefinitionNode(this);
	}

	/**
	 * @return the name
	 */
	public VariableRateNode getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 * @throws NullPointerException
	 *             if name is null
	 */
	public void setName(VariableRateNode name) {
		this.name = name;
	}

	/**
	 * @return the rate
	 */
	public RateNode getRate() {
		return rate;
	}

	/**
	 * @param rate
	 *            the rate to set
	 * @throws NullPointerExeption
	 *             if the rate is null
	 */
	public void setRate(RateNode rate) {
		this.rate = rate;
	}

}
