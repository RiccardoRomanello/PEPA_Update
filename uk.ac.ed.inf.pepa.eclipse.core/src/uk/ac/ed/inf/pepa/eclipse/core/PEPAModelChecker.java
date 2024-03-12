/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ICSLModelChecker;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ILogListener;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.IMRMCGenerator;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ModelCheckingException;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ProbabilityInterval;
import uk.ac.ed.inf.pepa.eclipse.core.internal.PepaModel;

public class PEPAModelChecker implements ILogListener {

	private PepaModel model;
	
	private ICSLModelChecker modelChecker;
	private ModelCheckingException exception = null;
	
	public PEPAModelChecker(PepaModel model, ICSLModelChecker modelChecker) {
		this.model = model;
		this.modelChecker = modelChecker;
		modelChecker.addLogListener(this);
	}
	
	public ModelCheckingException getModelCheckingException() {
		return exception;
	}
	
	public AbstractBoolean checkProperty(CSLAbstractStateProperty property) {
		AbstractBoolean result = AbstractBoolean.MAYBE;
		long tic = System.currentTimeMillis();
		try {
			result = modelChecker.checkProperty(property);
		} catch (ModelCheckingException e) {
			exception = e;
		}
		long elapsed = System.currentTimeMillis() - tic;
		model.modelCheckingEvent(new ProcessAlgebraModelChangedEvent(
				       ProcessAlgebraModelChangedEvent.MODEL_CHECKED, model, exception, elapsed, property.toString()));
		return result;
	}
	
	public ProbabilityInterval testProperty(CSLAbstractStateProperty property) {
		ProbabilityInterval result = null;
		long tic = System.currentTimeMillis();
		try {
			result = modelChecker.testProperty(property);
		} catch (ModelCheckingException e) {
			result = new ProbabilityInterval(0,1);
			exception = e;
		}
		long elapsed = System.currentTimeMillis() - tic;
		model.modelCheckingEvent(new ProcessAlgebraModelChangedEvent(
				       ProcessAlgebraModelChangedEvent.MODEL_CHECKED, model, exception, elapsed, property.toString()));
		return result;
	}
	
	public IMRMCGenerator getMRMCGenerator() {
		return modelChecker.getMRMCGenerator();
	}
	
	public void notifyLogEntry(String information) {
		model.modelCheckingEvent(new ProcessAlgebraModelChangedEvent(
			       ProcessAlgebraModelChangedEvent.MODEL_CHECKING_INFO, model, null, 0, information));
	}

}
