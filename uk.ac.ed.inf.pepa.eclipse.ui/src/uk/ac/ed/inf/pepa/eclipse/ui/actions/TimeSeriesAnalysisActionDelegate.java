/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis.TimeSeriesAnalysisWizard;

public class TimeSeriesAnalysisActionDelegate extends BasicProcessAlgebraModelActionDelegate {

	@Override
	protected void checkStatus() {
		if (this.action == null || this.model == null)
			return;
		this.action.setEnabled(((IPepaModel)this.model ).isSBAParseable().length > 0);
	}

	@Override
	public void run(IAction action) {
		TimeSeriesAnalysisWizard wizard = new TimeSeriesAnalysisWizard((IPepaModel)model);
		WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
		try{
			dialog.open();
		} catch(SWTException e) {}
	}

}
