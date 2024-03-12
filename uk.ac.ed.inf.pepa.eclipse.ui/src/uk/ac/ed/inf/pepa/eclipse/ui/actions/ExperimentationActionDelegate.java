/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;

public class ExperimentationActionDelegate extends BasicProcessAlgebraModelActionDelegate
		implements IEditorActionDelegate {

	@Override
	protected void checkStatus() {
		this.action.setEnabled(model.isDerivable());
	}

	@Override
	public void run(IAction action) {
		ActionCommands.experiment((IPepaModel)model);
	}

}
