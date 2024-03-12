/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * AST Node for a variable representig a rate
 * 
 * @author mtribast
 *
 */
public class VariableRateNode extends FiniteRateNode {
	
	private String name;
	
	VariableRateNode() { 
	}

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitVariableRateNode(this);
	}
	
	/**
	 * Determine if the given string is a valid PEPA rate name.
	 * <p>
	 * Valid names must start with a lowercase character according to
	 * {@link Character#isLowerCase(char)} and have all the other character
	 * conforming to {@link Character#isJavaIdentifierPart(char)}.
	 * 
	 * @param name the given name
	 * @return <code>true</code> if the name is a valid PEPA rate name
	 */
	public static boolean isValidRateName(String name) {
		if (name == null || name.length() == 0)
			return false;
		if (!Character.isLowerCase(name.charAt(0)))
			return false;
		for (int i = 1; i < name.length(); i++) {
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this rate.
	 * <p>
	 * The method throws an {@link IllegalArgumentException} if
	 * {@link #isValidRateName(String)} would return <code>false</code>
	 * when the given name was passed in
	 * 
	 * @param name
	 *            the name for the rate
	 * @throws IllegalArgumentException
	 * 
	 */
	public void setName(String name) {
		if (!isValidRateName(name))
			throw new IllegalArgumentException(name + 
					" is not a valid PEPA rate name");
		this.name = name;
	}
	
	/**
	 * Temporary measure until sba package can find a better way of doing its work.
	 * @param name
	 */
	public void bypassCheckSetName(String name) {
		this.name = name;
	}

}
