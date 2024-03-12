/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.UnknownActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

public class KroneckerSynchronisationVisitor extends DefaultVisitor {

	private ISymbolGenerator generator;	
	private KroneckerActionManager actionManager;
	
	public KroneckerSynchronisationVisitor(ISymbolGenerator generator, KroneckerActionManager actionManager) {
		this.generator = generator;
		this.actionManager = actionManager;
	}
	
	public void visitConstantProcessNode(ConstantProcessNode constant) {
		// ignore
	}

	// We will ignore this for now, I guess
	public void visitHidingNode(HidingNode hiding) {
		// don't deal with for now
		assert false;
	}
	
	// Process P[n]
	public void visitAggregationNode(AggregationNode aggregation) {
		// don't deal with for now
		assert false;
	}
	
	
	public void visitActionTypeNode(ActionTypeNode actionType) {
		// ignore - handle directly in cooperation node
	}
	
	// tau action
	public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {
		// we shouldn't have any tau actions in the system equation
		assert false;
	}

	public void visitCooperationNode(CooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
		for (ActionTypeNode action : cooperation.getActionSet()) {
			action.accept(this);
			short actionID = generator.getActionId(action.getType());
			actionManager.addSyncAction(actionID);
		}
	}
	
	// <*> sync over shared action names?
	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
		// don't deal with for now
		assert false;
	}
	
}
