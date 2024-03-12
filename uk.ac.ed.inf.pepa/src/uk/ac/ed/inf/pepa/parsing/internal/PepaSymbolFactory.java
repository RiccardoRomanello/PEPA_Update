/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing.internal;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ILocationInfo;

public class PepaSymbolFactory extends ComplexSymbolFactory {

	private static Logger logger = Logger.getLogger(PepaSymbolFactory.class);
	
	private final static Location START_LOCATION = new Location(0, 0);
	
	public Symbol newSymbol(String name, int id, Symbol left, Symbol right,
			Object value) {
		logger.debug("Creating " + name + " l:" + left + " r:" + right + " v:" + value);
		ComplexSymbol sym = (ComplexSymbol) super.newSymbol(name, id, left,
				right, value);
		
		logger.debug("left: " + ((ComplexSymbol)left).getLeft());
		logger.debug("right " + ((ComplexSymbol)right).getRight());
		if (value != null && value instanceof ASTNode) {
			ASTNode node = (ASTNode) value;
			Location locationLeft = ((ComplexSymbol)left).getLeft();
			Location locationRight =((ComplexSymbol)right).getRight();
			if (locationLeft ==null)
				locationLeft = START_LOCATION;
			if (locationRight == null)
				locationRight = START_LOCATION;
			
			node.setLeftLocation(getLocationInfo(locationLeft));
			node.setRightLocation(getLocationInfo(locationRight));
			
		} 
		
		return sym;
	}
	
	public Symbol newSymbol(String name, int id, Location left, Location right, Object value){
        return super.newSymbol(name,id,left,right,value);
    }
    public Symbol newSymbol(String name, int id, Location left, Location right){
        return super.newSymbol(name,id,left,right);
    }
    
	
	/**
	 * Creates a Cup unaware instance for managing information location
	 * @param complexSymbolLocation the Cup information location
	 * @return the Pepa Core location information instace
	 */
	private static ILocationInfo getLocationInfo(final Location complexSymbolLocation) {
		return new ILocationInfo() {

			public int getLine() {
				return complexSymbolLocation.getLine();
			}

			public int getColumn() {
				return complexSymbolLocation.getColumn();
			}

			public int getChar() {
				return ILocationInfo.UNKNOWN;
			}

			public int getLength() {
				return ILocationInfo.UNKNOWN;
			}
			
		};
	}
	
}
