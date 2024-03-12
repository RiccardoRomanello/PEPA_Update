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
 * Abstract superclass of all Abstract Syntax Tree (AST) node types.
 * <p>
 * An AST node represents a PEPA language construct, such as a rate declaration,
 * process definition, system equation.
 * </p>
 * <p>
 * Each AST node belongs to a unique AST instance, called the owning AST. The
 * children of an AST node always have the same owner as their parent node.
 * </p>
 * <p>
 * ASTs do not contain "holes" (missing subtrees). If a node is required to have
 * a certain property, a syntactically plausible initial value is always
 * supplied.
 * </p>
 * <p>
 * The method <code>PepaTools.parse(String)</code> parses a string containing a PEPA model and
 * returns an abstract syntax tree for it. The resulting nodes carry source
 * ranges relating the node back to the original source characters. The source
 * range covers the construct as a whole.
 * </p>
 * <p>
 * Each AST node is capable of carrying an open-ended collection of
 * client-defined properties. Newly created nodes have none.
 * <code>getProperty</code> and <code>setProperty</code> are used to access
 * these properties.
 * </p>
 * <p>
 * ASTs also support the visitor pattern; see the class <code>ASTVisitor</code>
 * for details.
 * </p>
 * 
 * @see ASTVisitor
 */
public abstract class ASTNode {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ASTNode.class);

	private ILocationInfo fLeft = ILocationInfo.Unknown;

	private ILocationInfo fRight = ILocationInfo.Unknown;

	/**
	 * Set the right position in source code for this node. This is done by
	 * parsers.
	 * 
	 * @param info
	 *            the right position in source code for this node
	 */
	public void setRightLocation(ILocationInfo info) {
		this.fRight = info;
	}

	/**
	 * Set the left position in source code for this node. This is done by
	 * parsers.
	 * 
	 * @param info
	 *            the left position in source code for this node
	 */
	public void setLeftLocation(ILocationInfo info) {
		this.fLeft = info;
	}

	/**
	 * Get the right position in source code for this node.
	 * 
	 * @return the right position in source code for this node
	 */
	public ILocationInfo getRightLocation() {
		return fRight;
	}

	/**
	 * Get the left position in source code for this node.
	 * 
	 * @return the left position in source code for this node
	 */
	public ILocationInfo getLeftLocation() {
		return fLeft;
	}

	/**
	 * Accepts the given visitor on a visit of the current node.
	 * 
	 * @param visitor
	 *            the visitor object
	 */
	protected abstract void accept0(ASTVisitor v);

	/**
	 * Accepts the given visitor on a visit of the current node.
	 * 
	 * @param visitor
	 *            the visitor object
	 * @throw NullPointerException is the visitor is null
	 */
	public void accept(ASTVisitor v) {
		if (v == null)
			throw new NullPointerException();
		accept0(v);

	}

	/**
	 * Strong physical equality implementation
	 * 
	 * @see Object#equals(Object)
	 */
	public final boolean equals(Object o) {
		return this == o;
	}

}
