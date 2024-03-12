/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

public class ActivityNode extends ASTNode {
	
	private ActionSuperNode action;
	private RateNode rate;
	
	ActivityNode() {
	}
	

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitActivityNode(this);
	}

	/**
	 * @return the action
	 */
	public ActionSuperNode getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(ActionSuperNode action) {
		if (action == null)
			throw new NullPointerException();
		this.action = action;
	}

	/**
	 * @return the rate
	 */
	public RateNode getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(RateNode rate) {
		if (rate == null)
			throw new NullPointerException();
		this.rate = rate;
	}
}
