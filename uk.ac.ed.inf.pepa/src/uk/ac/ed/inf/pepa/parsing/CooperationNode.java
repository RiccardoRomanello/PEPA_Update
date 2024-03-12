/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

import org.apache.log4j.Logger;
/**
 * AST Node for a PEPA Cooperation operator 
 * 
 * @author mtribast
 *
 */
public class CooperationNode extends BinaryOperatorProcessNode {
	
	static Logger logger = Logger.getLogger(CooperationNode.class);
	
	private Actions actionSet;
	
	CooperationNode() {
		super();
	}
	
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitCooperationNode(this);
	}

	/**
	 * Returns the action set property for this cooperation set
	 * @return the actionSet
	 */
	public Actions getActionSet() {
		return actionSet;
	}

	/**
	 * @param actionSet the actionSet to set
	 * @throws NullPointerException if the action set is null
	 */
	public void setActionSet(Actions actionSet) {
		if (actionSet == null) {
			throw new NullPointerException();
		}
		this.actionSet = actionSet;
	}

}
