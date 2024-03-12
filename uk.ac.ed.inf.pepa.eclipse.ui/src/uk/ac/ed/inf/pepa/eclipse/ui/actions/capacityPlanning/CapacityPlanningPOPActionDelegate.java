/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions.capacityPlanning;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;

import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.ui.actions.ActionCommands;
import uk.ac.ed.inf.pepa.eclipse.ui.actions.BasicProcessAlgebraModelActionDelegate;

public class CapacityPlanningPOPActionDelegate extends BasicProcessAlgebraModelActionDelegate
		implements IEditorActionDelegate {

	@Override
	protected void checkStatus() {
		this.action.setEnabled(model.isDerivable());
	}

	@Override
	public void run(IAction action) {
		ActionCommands.capacityPlanning((IPepaModel) model,Config.SEARCHSINGLE, Config.EVALPOPU);
	}

}
