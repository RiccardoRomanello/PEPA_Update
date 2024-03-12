/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

public class SteadyStateCTMCSolverDelegate extends BasicProcessAlgebraModelActionDelegate {

	public void run(IAction action) {
		
		ActionCommands.steadyState(model);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	protected void checkStatus() {
		if (this.action == null || this.model == null)
			return;
		this.action.setEnabled(this.model.isSolvable());
	}

}
