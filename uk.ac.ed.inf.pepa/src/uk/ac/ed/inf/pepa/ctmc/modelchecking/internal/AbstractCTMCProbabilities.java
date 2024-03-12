/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.HashMap;

public class AbstractCTMCProbabilities {

	private HashMap<AbstractCTMCState, Double> upperProbabilities;
	private HashMap<AbstractCTMCState, Double> lowerProbabilities;
	
	public AbstractCTMCProbabilities(AbstractCTMC abstractCTMC) {
		this.upperProbabilities = new HashMap<AbstractCTMCState, Double>(abstractCTMC.size());
		this.lowerProbabilities = new HashMap<AbstractCTMCState, Double>(abstractCTMC.size());
	}
	
	public void setProbability(AbstractCTMCState state, double minProb, double maxProb) {
		lowerProbabilities.put(state, minProb);
		upperProbabilities.put(state, maxProb);
	}
	
	public double getProbability(AbstractCTMCState state, boolean is_minimum) {
		Double probability;
		if (is_minimum) {
			probability = lowerProbabilities.get(state);
		} else {
			probability = upperProbabilities.get(state);
		}
		return (probability == null) ? 0 : probability;		
	}
	
}
