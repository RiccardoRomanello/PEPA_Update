/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * Contains location information for AST nodes.
 * 
 * @author mtribast
 * 
 */
public interface ILocationInfo {

	/**
	 * Constant for unknown location. This value is returned when
	 * location-related calls are called on abstract syntax trees generated
	 * programmatically.
	 */
	public int UNKNOWN = -1;

	/**
	 * Convenience class for unknown locations.
	 */
	public static final ILocationInfo Unknown = new ILocationInfo() {

		public int getLine() {
			return UNKNOWN;
		}

		public int getColumn() {
			return UNKNOWN;
		}

		public int getChar() {
			return UNKNOWN;
		}
		
	};

	/**
	 * Line in the source code at which the AST is located
	 * 
	 * @return the 1-indexed line in the source code
	 */
	public int getLine();

	/**
	 * Column in the source code at which the AST is located
	 * 
	 * @return the 1-indexed line in the source code
	 */
	public int getColumn();

	/**
	 * Zero-indexed position in the source code at which the first character of
	 * the AST node
	 * 
	 * @return position in the source code for the first character of a AST node
	 */
	public int getChar();
	
}
