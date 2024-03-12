/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 09-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.emf;

/**
 * Represents a 'tau' action, i.e. an action whose type is unspecified
 * 
 * @see "A Compositional Approach to Performance Modelling"
 * @author mtribast
 * @model
 */
public interface SilentAction extends Action {
    
	/**
	 * Representation of a silent action
	 */
	public static final String TAU = "tau";

}