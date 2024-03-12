/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation;

public class MeasurementData {
	
	public long setupTime = 0; //  ns
	
	public long lookupTime = 0; // ns
	
	public long successorTime = 0; // ns
	
	public long wallClockDerivationTime = 0; //ns
	
	public int states = 0;
	
	public int transitions = 0;
}
