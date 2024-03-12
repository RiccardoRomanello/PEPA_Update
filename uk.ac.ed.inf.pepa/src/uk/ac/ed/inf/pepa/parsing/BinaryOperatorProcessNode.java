/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST Node for binary operators on <code>ProcessNode</code>s
 * 
 * @author mtribast
 *
 */
public abstract class BinaryOperatorProcessNode extends ProcessNode {
	
	protected ProcessNode left;
	protected ProcessNode right;
	
	BinaryOperatorProcessNode() {
		
	}
	
	public BinaryOperatorProcessNode(ProcessNode left, ProcessNode right) {
		setLeft(left);
		setRight(right);
	}

	/**
	 * @return the left
	 */
	public ProcessNode getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 * @throws NullPointerException if left is null
	 */
	public void setLeft(ProcessNode left) {
		if (left == null)
			throw new NullPointerException();
		this.left = left;
	}

	/**
	 * @return the right
	 */
	public ProcessNode getRight() {
		return right;
	}

	/**
	 * @param right the right to set
	 * @throws NullPointerException if right is null
	 */
	public void setRight(ProcessNode right) {
		if (right == null)
			throw new NullPointerException();
		this.right = right;
	}
	

}
