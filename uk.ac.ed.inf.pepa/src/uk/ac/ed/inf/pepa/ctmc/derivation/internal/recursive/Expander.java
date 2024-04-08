/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 15-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.recursive;

import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Visitor;
import uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess;

/**
 * Expand the system equation, i.e. resolves sub-systems which are defined as
 * constants.
 * <p>
 * For example, if the model is defined as
 * 
 * <pre>
 *    P = (a,1).P1;
 *    P1 = (b,1).P;
 *    S = P &lt;&gt; P &lt;&gt; P;
 *    
 *    S
 * </pre>
 * 
 * Then the system equation is
 * 
 * <pre>
 *    P &lt;&gt; P &lt;&gt; P
 * </pre>
 * 
 * The system equation must show the maximum number of top-level components of
 * the model.
 * 
 * @author mtribast
 * 
 */
class Expander implements Visitor {

	private uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess factory = DoMakePepaProcess
			.getInstance();

	public Process expanded;

	public Expander() {

	}

	public void visitPrefix(Prefix prefix) {
		expanded = prefix;
	}

	public void visitChoice(Choice choice) {
		expanded = choice;
	}

	public void visitHiding(Hiding hiding) {
		expanded = hiding;
	}

	public void visitCooperation(Cooperation cooperation) {
		Expander lhs = new Expander();
		cooperation.getLeftHandSide().accept(lhs);
		Expander rhs = new Expander();
		cooperation.getRightHandSide().accept(rhs);

		Cooperation coop = factory.createCooperation(lhs.expanded,
				rhs.expanded, cooperation.getActionSet());

		expanded = coop;
	}

	public void visitConstant(Constant constant) {
		Expander expander = new Expander();
		if (constant.getBinding() instanceof Hiding
				|| constant.getBinding() instanceof Cooperation
				|| constant.getBinding() instanceof Constant) {
			constant.getBinding().accept(expander);
			expanded = expander.expanded;
		} else
			expanded = constant;

	}

	public void visitAggregation(Aggregation aggregation) {
		expanded = aggregation;
	}

}