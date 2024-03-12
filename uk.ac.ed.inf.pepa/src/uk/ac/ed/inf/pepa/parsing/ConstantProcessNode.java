/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST Node for a PEPA Constant.
 * <p>
 * The name it is given using {@link #setName(String)} must conform to
 * {@link #isContantValidName(String)}.
 * 
 * @author mtribast
 * 
 */
public class ConstantProcessNode extends ProcessNode {

	private String name;

	ConstantProcessNode() {
	}

	/**
	 * Determine if the given string is a valid PEPA constant name.
	 * <p>
	 * Valid names must start with an uppercase character according to
	 * {@link Character#isUpperCase(char)} and have all the other character
	 * conforming to {@link Character#isJavaIdentifierPart(char)}.
	 * 
	 * @param name
	 *            the given name
	 * @return <code>true</code> if the name is a valid PEPA constant name
	 */
	public static boolean isValidConstantName(String name) {
		if (name == null || name.length() == 0)
			return false;
		// extension
		if (name.charAt(0) == '\"')
			if (name.charAt(name.length() - 1) == '\"')
				return true;
			else 
				return false;
		// normal case
		if (!Character.isUpperCase(name.charAt(0)))
			return false;
		for (int i = 1; i < name.length(); i++) {
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
				return false;
		}
		return true;
	}

	public String getName() {
		return name;
	}

	/**
	 * Set the name of this constant.
	 * <p>
	 * The method throws an {@link IllegalArgumentException} if
	 * {@link #isContantValidName(String)} would return <code>false</code>
	 * when the given name was passed in
	 * 
	 * @param name
	 *            the name for the constant
	 * @throws IllegalArgumentException
	 * 
	 */
	public void setName(String name) {
		if (!isValidConstantName(name)) {
			throw new IllegalArgumentException(name
					+ " not a valid PEPA Constant name");
		}
		this.name = name;
	}

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitConstantProcessNode(this);
	}
}
