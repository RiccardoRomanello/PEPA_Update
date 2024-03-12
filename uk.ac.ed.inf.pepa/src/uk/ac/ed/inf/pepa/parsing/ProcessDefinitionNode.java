/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST for a process definition
 * 
 * <pre>
 *   	P = (a, 1).P;
 * </pre>
 * 
 * @author mtribast
 * 
 */
public class ProcessDefinitionNode extends ASTNode {

	private ConstantProcessNode name;

	private ProcessNode node;
	
	ProcessDefinitionNode() {
	}
	
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitProcessDefinitionNode(this);
	}

	/**
	 * @return the name
	 */
	public ConstantProcessNode getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 * @throws NullPointerException
	 *             if name is null
	 */
	public void setName(ConstantProcessNode name) {
		if (name == null)
			throw new NullPointerException();
		this.name = name;
	}

	/**
	 * @return the node
	 */
	public ProcessNode getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 * @throws NullPointerException if node is null
	 */
	public void setNode(ProcessNode node) {
		if (node == null)
			throw new NullPointerException();
		this.node = node;
	}

}
