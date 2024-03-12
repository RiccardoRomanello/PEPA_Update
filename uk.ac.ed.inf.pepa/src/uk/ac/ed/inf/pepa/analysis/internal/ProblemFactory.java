/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.parsing.ASTNode;

/**
 * Factory for creating <code>IProblems</code>. It is concerned with defining
 * the <code>isError</code> and <code>isWarning</code> methods
 * 
 * @author mtribast
 * 
 */
public class ProblemFactory {

	/**
	 * Creates a new IProblem
	 * 
	 * @param id
	 *            problm severity level
	 * @param startLine
	 *            the source code line where the problem begins
	 * @param startColumn
	 *            the source code column where the problem begins
	 * @param endLine
	 *            the source code line where the problem ends
	 * @param endColumn
	 *            the source code column where the problem ends
	 * @param initChar
	 *            the initial character where the problem begins
	 * @param length
	 *            the length of the source code affected by the problem
	 * @param message
	 *            human-readable message describing the problem
	 * @return the generated <code>IProblem</code>
	 */
	public static IProblem createProblem(final int id,
			final int startLine, final int startColumn,
			final int endLine, final int endColumn, final int initChar,
			final int length, final String message) {

		return new IProblem() {

			public int getId() {
				return id;
			}

			public int getChar() {
				return initChar;
			}

			public int getLength() {
				return length;
			}

			public int getStartLine() {
				return startLine;
			}

			public int getStartColumn() {
				return startColumn;
			}

			public int getEndLine() {
				return endLine;
			}

			public int getEndColumn() {
				return endColumn;
			}

			public String getMessage() {
				return message;
			}

			public boolean isError() {
				return ((id & IProblem.Error) != 0);
			}

			public boolean isWarning() {
				return ((id & IProblem.Warning) != 0);
			}

		};

	}
	
	/**
	 * Utility function for creating error messages affecting AST nodes
	 * @param id
	 * @param affectedNode
	 * @param message
	 */
	public static IProblem buildProblem(int id, ASTNode affectedNode, String message) {
		return createProblem(id, affectedNode
				.getLeftLocation().getLine(), affectedNode
				.getLeftLocation().getColumn(), affectedNode
				.getRightLocation().getLine(), affectedNode
				.getRightLocation().getColumn(), affectedNode.getLeftLocation().getChar(), 
				affectedNode.getRightLocation().getChar() -
				affectedNode.getLeftLocation().getChar(), message);
	}
	
	/* Test for this factory! */
	public static void main(String[] args) {
		int startLine = 1;
		int startColumn = 1;
		int endLine = 1;
		int endColumn = 0, initChar = 0, length = 0;
		String message = null;

		int id = IProblem.SyntaxError;
		System.out.println(ProblemFactory.createProblem(id, startLine,
				startColumn, endLine, endColumn, initChar, length,
				message).isError());
		id = IProblem.TransientState;
		System.out.println(ProblemFactory.createProblem(id, startLine,
				startColumn, endLine, endColumn, initChar, length,
				message).isError());
		id = IProblem.UnusedRate;
		System.out.println(ProblemFactory.createProblem(id, startLine,
				startColumn, endLine, endColumn, initChar, length,
				message).isError());
		
		id = IProblem.UndefinedRate;
		System.out.println(ProblemFactory.createProblem(id, startLine,
				startColumn, endLine, endColumn, initChar, length,
				message).isWarning());
		
		

	}
}
