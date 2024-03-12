/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * An Action Type AST Node
 * @author mtribast
 *
 */
public class ActionTypeNode extends ActionSuperNode {
	
	private String type;
	
	ActionTypeNode() {
	}
	
	/**
	 * The action type
	 * @return the action type
	 */
	public String getType() { 
		return type; 
	}
	
	public void setType(String name) { 
		if (name == null)
			throw new NullPointerException();
		this.type = name;
	}
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitActionTypeNode(this);
	}
	
}
