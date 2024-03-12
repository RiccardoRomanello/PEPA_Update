/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST Node for PEPA Choice
 * @author mtribast
 *
 */
public class ChoiceNode extends BinaryOperatorProcessNode {
	
	ChoiceNode() { super(); }
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitChoiceNode(this);
	}

}
