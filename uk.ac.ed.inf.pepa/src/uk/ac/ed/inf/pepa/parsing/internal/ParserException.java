/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * ParserException.java
 *
 * Created on 26 April 2006, 14:28
 *
 */

package uk.ac.ed.inf.pepa.parsing.internal;

/**
 * @author mtribast
 */
@SuppressWarnings("serial")
public class ParserException extends Exception {

	private int character;

	private int line;

	private int column;

	private int length;

	private String message;

	/**
	 * Creates a new instance of ParserException
	 * 
	 * @param char
	 *            the number of characters up to the start of the matched text
	 * 
	 * @param line
	 *            line in the text where the error occurs
	 * @param column
	 *            column in the text where the error occurs
	 * @param length
	 *            length of the current parsed symbol
	 * @param message
	 *            human-readable error message
	 */
	public ParserException(int initialChar, int line, int column, int length,
			String message) {
		this.line = line;
		this.column = column;
		this.message = message;
		this.length = length;
		this.character = initialChar;
	}

	public int getChar() {
		return this.character;
	}

	public int getLine() {
		return this.line;
	}

	public int getColumn() {
		return this.column;
	}

	public String getMessage() {
		return this.message;
	}

	public int getLength() {
		return this.length;
	}

	public String toString() {
		return "Parser exception found at line " + line + " column " + column
				+ " length " + length + ": " + message;

	}
}
