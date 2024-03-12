/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc;

/**
 * Utilisation result. A local state of a sequential
 * component.
 * 
 * @author mtribast
 *
 */
public class LocalState extends UtilisationResult {

	protected double fUtilisation;
	
	protected SequentialComponent fParent;
	
	public LocalState(String name, double utilisation) {
		super(name);
		this.fUtilisation = utilisation;
		
	}
	/**
	 * Returns the utilisation of this local state,
	 * a number between zero and one.
	 * 
	 * @return the utilisation of this local state.
	 * 
	 */
	public double getUtilisation() {
		return fUtilisation;
	}
	
	/**
	 * Returns the parent of this local state.
	 * @return the parent.
	 */
	public SequentialComponent getSequentialComponent() {
		return  fParent;
	}
	
	void setParent(SequentialComponent parent) {
		this.fParent = parent;
	}

}
