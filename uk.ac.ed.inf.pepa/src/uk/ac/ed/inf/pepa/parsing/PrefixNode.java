/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST Node for a Prefix
 * 
 * @author mtribast
 *
 */
public class PrefixNode extends ProcessNode {
	
	private ActivityNode activity;

	private ProcessNode target;
	
	PrefixNode() {
		super();
	}
	
	@Override
	protected void accept0(ASTVisitor v) {
		v.visitPrefixNode(this);
	}

	/**
	 * @return the activity
	 */
	public ActivityNode getActivity() {
		return activity;
	}

	/**
	 * 
	 * @param activity the activity to set
	 */
	public void setActivity(ActivityNode activity) {
		if (activity == null)
			throw new NullPointerException();
		this.activity = activity;
	}

	/**
	 * @return the target
	 */
	public ProcessNode getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(ProcessNode target) {
		if (target == null)
			throw new NullPointerException();
		this.target = target;
	}
}
