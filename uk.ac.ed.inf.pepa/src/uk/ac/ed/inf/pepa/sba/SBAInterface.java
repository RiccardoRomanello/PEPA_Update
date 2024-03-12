/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.*;

import uk.ac.ed.inf.pepa.parsing.RateNode;

/**
 * 
 * @author ajduguid
 * 
 */
public interface SBAInterface {

	public Mapping getMapping();

	public Map<String, Number> getPopulations();

	public Map<String, RateNode> getRates();

	/**
	 * Purpose of this method is to allow modification of stoichiometric
	 * information of the reactions. No other actions will be permissible
	 * 
	 * @return Should return a copy of the reactions
	 */
	public Set<SBAReaction> getReactions();

	public boolean isParseable();

	public void updateReactions(Set<SBAReaction> reactions);

}
