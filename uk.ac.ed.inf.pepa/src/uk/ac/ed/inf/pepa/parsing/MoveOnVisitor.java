/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;


/**
 * A visitor that always visits target processes
 * @author mtribast
 *
 */
public class MoveOnVisitor extends DefaultVisitor {
	
	public void visitChoiceNode(ChoiceNode choice) {
		choice.getLeft().accept(this);
		choice.getRight().accept(this);
	}

	public void visitCooperationNode(CooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
	}

	public void visitHidingNode(HidingNode hiding) {
		hiding.getProcess().accept(this);
	}
	
	public void visitPrefixNode(PrefixNode prefix) {
		prefix.getTarget().accept(this);
	}
	
	public void visitAggregationNode(AggregationNode aggregation) {
		aggregation.getProcessNode().accept(this);
	}
	
	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
	}
}
