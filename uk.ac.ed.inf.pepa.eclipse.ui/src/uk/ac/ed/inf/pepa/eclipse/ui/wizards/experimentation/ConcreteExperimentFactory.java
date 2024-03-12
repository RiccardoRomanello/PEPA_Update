/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

public class ConcreteExperimentFactory extends
		AbstractExperimentFactory {
	
	private static final String BASIC_XY = "XY Plot";
	private static final String XY_PARAMETER = "Parametrised XY Plot";
	
	private static final String[] DESCRIPTIONS = new String[] {
		BASIC_XY, XY_PARAMETER
	};
	
	@Override
	public IExperiment createExperiment(String description) {
		if (description.equals(BASIC_XY)) {
			return  new BasicXYExperiment(BASIC_XY);
		}
		if (description.equals(XY_PARAMETER)) {
			return new XYParameterExperiment(XY_PARAMETER);
		}
		return null;
	}

	@Override
	public String[] getDescriptions() {
		return DESCRIPTIONS;
	}

}
