/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

public interface NodeResolver {

	public ASTNode getProcessDefinition(String name);

	public ASTNode[] getActionLevelDeclarations(String name);
	
	public ASTNode getRateDefinition(String name);

	public ASTNode[] getConstantUsage(String name);

	public ASTNode[] getRateUsage(String name);

	public ASTNode[] getActionUsage(String constantName, String actionName);

}
