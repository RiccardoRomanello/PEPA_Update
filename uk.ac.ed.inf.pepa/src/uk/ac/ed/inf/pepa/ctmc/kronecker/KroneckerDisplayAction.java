/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker;

public class KroneckerDisplayAction {

	private short actionID;
	private double rate;
	
	private KroneckerDisplayModel model;
	
	public KroneckerDisplayAction(short actionID, double rate, KroneckerDisplayModel model) {
		this.actionID = actionID;
		this.rate = rate;
		this.model = model;
	}
	
	public short getID() {
		return actionID;
	}
	
	public double getRate() {
		return rate;
	}
	
	private String printRate() {
		if (rate >= 0) {
			return String.valueOf(rate);
		} else if (rate == -1) {
			return "T";
		} else {
			return String.valueOf(-rate) + "T";
		}
	}
	
	public String getLabel(boolean isShort) {
		if (isShort) {
			return model.getActionName(actionID);
		} else {
			return "(" + model.getActionName(actionID) + ", " + printRate() + ")";
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof KroneckerDisplayAction) {
			KroneckerDisplayAction action = (KroneckerDisplayAction)o;
			return actionID == action.actionID && rate == action.rate;
		}
		return false;
	}
	
}
