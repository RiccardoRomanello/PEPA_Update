/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A data type which holds a sequential component's first step derivative set as
 * well as the apparent rates
 * 
 * @author mtribast
 * 
 */
public class SequentialComponentData {
	
	public SequentialComponentData() {
		
	}
	public ArrayList<Transition> fFirstStepDerivative = 
		new ArrayList<Transition>();

	//public HashMap<Short, Double> fApparentRates = 
	//	new HashMap<Short, Double>();
	
	public double[] fArrayApparentRates;
}
