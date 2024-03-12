/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST Hiding node
 * 
 * @author mtribast
 * 
 */
public class HidingNode extends ProcessNode {

	private ProcessNode process;

	private Actions set;

	HidingNode() {
		super();
	}

	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitHidingNode(this);
	}

	/**
	 * @return the process
	 */
	public ProcessNode getProcess() {
		return process;
	}

	/**
	 * @param process
	 *            the process to set
	 * @throws NullPointerException
	 *             if the process is null
	 */
	public void setProcess(ProcessNode process) {
		if (process == null)
			throw new NullPointerException();
		this.process = process;
	}

	/**
	 * @return the action set of this hiding process
	 */
	public Actions getActionSet() {
		return set;
	}

	/**
	 * @param set
	 *            the action set 
	 */
	public void setActionSet(Actions set) {
		if (set == null)
			throw new NullPointerException();
		this.set = set;
	}
}
