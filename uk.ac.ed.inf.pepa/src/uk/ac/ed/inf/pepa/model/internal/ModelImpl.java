/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 14-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model.internal;

import java.util.Collection;
import java.util.LinkedHashSet;

import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.NamedRate;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * @author mtribast
 * 
 */
public class ModelImpl implements Model {

	private Process process;
	
	private ModelNode modelNode;
	
	private LinkedHashSet<NamedRate> rates = new LinkedHashSet<NamedRate>();

	private LinkedHashSet<Constant> constants = new LinkedHashSet<Constant>();

	
	public ModelImpl(ModelNode modelNode) {
		this.modelNode= modelNode;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Model#getSystemEquation()
	 */
	public Process getSystemEquation() {
		return process;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.Model#setSystemEquation(uk.ac.ed.inf.pepa.Process)
	 */
	public void setSystemEquation(Process equation) {
		if (equation == null)
			throw new NullPointerException();
		this.process = equation;
	}

	public Collection<Constant> getProcessDefinitions() {
		return this.constants;
	}
	
	public Collection<NamedRate> getRateDefinitions() {
		return this.rates;
	}

	public ModelNode getASTModel() {
		return this.modelNode;
	}

}
