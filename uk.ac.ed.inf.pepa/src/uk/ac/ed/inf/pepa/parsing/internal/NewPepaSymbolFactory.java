/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing.internal;

import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ILocationInfo;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

public class NewPepaSymbolFactory implements SymbolFactory {

	public Symbol newSymbol(String name, int id, 
			ILocationInfo left, ILocationInfo right,
			Object value) {
		if (value instanceof ASTNode) {
			setASTNodeLocation((ASTNode) value, left, right);
		}
		return new PepaSymbol(id, name, left, right, value);
	}
	
	public Symbol newSymbol(String name, int id, 
			ILocationInfo left, ILocationInfo right) {
		return this.newSymbol(name, id, left, right, null);
	}
	
	public Symbol newSymbol(String arg0, int arg1) {
		return this.newSymbol(arg0, arg1, ILocationInfo.Unknown,
				ILocationInfo.Unknown, null);
	}

	public Symbol newSymbol(String arg0, int arg1, Object arg2) {
		return this.newSymbol(arg0, arg1, ILocationInfo.Unknown,
				ILocationInfo.Unknown, arg2);
	}

	public Symbol newSymbol(String arg0, int arg1, Symbol arg2, Symbol arg3) {
		return this.newSymbol(arg0, arg1, arg2, arg3, null);
	}

	public Symbol newSymbol(String arg0, int arg1, Symbol arg2, Symbol arg3,
			Object arg4) {
		PepaSymbol leftSymbol = (PepaSymbol) arg2;
		PepaSymbol rightSymbol = (PepaSymbol) arg3;
		if (arg4 instanceof ASTNode) {
			ASTNode node = (ASTNode) arg4;
			setASTNodeLocation(node, leftSymbol.getLeftLocation(), leftSymbol.getRightLocation());
		}
		return this.newSymbol(arg0, arg1, leftSymbol.getLeftLocation(),
				rightSymbol.getRightLocation(), arg4);
	}
	
	private void setASTNodeLocation(ASTNode node, ILocationInfo leftLocation, ILocationInfo rightLocation) {
		node.setLeftLocation(leftLocation);
		node.setRightLocation(rightLocation);
	}

	public Symbol startSymbol(String arg0, int arg1, int arg2) {
		PepaSymbol sym =  new PepaSymbol(arg0, arg1, arg2, ILocationInfo.Unknown,
				ILocationInfo.Unknown);
		return sym;
	}
}
