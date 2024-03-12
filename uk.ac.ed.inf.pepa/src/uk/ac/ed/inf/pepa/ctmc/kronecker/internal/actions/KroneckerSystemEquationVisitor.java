/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions;

import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.UnknownActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * Generates a SystemComponentInfo object, which maps process and cooperatioon
 * terms in the system equation to the range of component indices they
 * represent.
 * 
 * @author msmith
 */
public class KroneckerSystemEquationVisitor extends DefaultVisitor {

	private SystemComponentInfo componentInfo;
	
	private boolean canMakeKronecker = true;
	
	public KroneckerSystemEquationVisitor(SystemComponentInfo componentInfo) {
		this.componentInfo = componentInfo;
	}
	
	public void visitConstantProcessNode(ConstantProcessNode constant) {
		componentInfo.addProcess(constant);
	}

	// We will ignore this for now, I guess
	public void visitHidingNode(HidingNode hiding) {
		canMakeKronecker = false;
	}
	
	// Process P[n] - can't deal with this right now
	public void visitAggregationNode(AggregationNode aggregation) {
		canMakeKronecker = false;
	}
	
	public void visitActionTypeNode(ActionTypeNode actionType) {
		// ignore
	}
	
	// tau action - can't deal with this in the system equation
	public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {
		canMakeKronecker = false;
	}

	public void visitCooperationNode(CooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
		if (!canMakeKronecker) return;
		componentInfo.addCooperation(cooperation);
	}
	
	// <*> sync over shared action names
	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
		if (!canMakeKronecker) return;
		componentInfo.addCooperation(cooperation);
	}

	public boolean canMakeKronecker() {
		return canMakeKronecker;
	}
	
}
