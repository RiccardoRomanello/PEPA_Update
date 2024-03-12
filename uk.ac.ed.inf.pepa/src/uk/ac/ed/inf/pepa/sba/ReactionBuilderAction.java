/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import uk.ac.ed.inf.pepa.parsing.*;

/**
 * 
 * @author ajduguid
 * 
 */
class ReactionBuilderAction implements Cloneable {

	String action, product;

	ReactionBuilder next;

	boolean noPrefix;

	RateNode rate;

	StringBuilder reactant;

	ReactionBuilderAction() {
		reactant = new StringBuilder();
		action = null;
		product = null;
		rate = null;
		next = null;
		noPrefix = false;
	}

	public ReactionBuilderAction clone() {
		ReactionBuilderAction clone = new ReactionBuilderAction();
		clone.reactant = new StringBuilder(reactant.toString());
		clone.action = action;
		clone.product = product;
		clone.noPrefix = noPrefix;
		clone.rate = (RateNode) ASTSupport.copy(rate);
		if (next != null)
			clone.next = next.clone();
		return clone;
	}

	String getReactant() {
		if (reactant.length() != 0)
			return reactant.toString();
		return null;
	}

	void noPrefix() {
		rate = null;
		action = null;
		noPrefix = true;
		if (product != null)
			update();
	}

	void setAction(String action) {
		this.action = action;
		if (product != null && action != null && rate != null)
			update();
	}

	void setGoesTo(ReactionBuilder next) {
		this.next = next;
		product = this.next.getName();
		if (product != null && action != null && rate != null)
			update();
	}

	void setGoesTo(String constant) {
		product = constant;
		next = null;
		if (product != null && action != null && rate != null)
			update();
	}

	void setRate(RateNode rate) {
		this.rate = rate;
		if (product != null && action != null && rate != null)
			update();
	}

	private void update() {
		reactant.setLength(0);
		if (noPrefix)
			reactant.append(product);
		else {
			reactant.append("(").append(action).append(",");
			reactant.append(CompiledRate.toString(rate));
			reactant.append(").").append(product);
		}
	}

}
