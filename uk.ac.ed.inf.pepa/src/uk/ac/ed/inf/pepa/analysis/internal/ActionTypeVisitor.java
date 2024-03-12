/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Visitor;

public class ActionTypeVisitor implements Visitor {

	private Set<String> actions = new HashSet<String>();
	
	public void visitAggregation(Aggregation aggregation) {
	}

	public void visitChoice(Choice choice) {
	}

	public void visitConstant(Constant constant) {
	}

	public void visitCooperation(Cooperation cooperation) {
		Iterator<Action> iterator = cooperation.getActionSet().iterator();
		while(iterator.hasNext())
			actions.add(iterator.next().prettyPrint());
		cooperation.getLeftHandSide().accept(this);
		cooperation.getRightHandSide().accept(this);
	}

	public void visitHiding(Hiding hiding) {
	}

	public void visitPrefix(Prefix prefix) {
	}
	
	public Set<String> getActions() {
		return actions;
	}

}
