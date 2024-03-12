/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;


public interface ICSLModelChecker {

	public AbstractBoolean checkProperty(CSLAbstractStateProperty property) throws ModelCheckingException;
	
	public ProbabilityInterval testProperty(CSLAbstractStateProperty property) throws ModelCheckingException;
	
	public IMRMCGenerator getMRMCGenerator();
	
	public void addLogListener(ILogListener listener);
	
}
