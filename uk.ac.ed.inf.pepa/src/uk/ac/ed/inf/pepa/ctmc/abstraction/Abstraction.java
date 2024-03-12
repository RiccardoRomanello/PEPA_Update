/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.abstraction;

/**
 * Interface for state space abstractions
 * 
 * @author msmith
 *
 */
public interface Abstraction {

	public short getAbstractState(short state);
	
	public short[] getConcreteStates(short state);
	
	public void notifySwap(int index1, int index2);
	
}
