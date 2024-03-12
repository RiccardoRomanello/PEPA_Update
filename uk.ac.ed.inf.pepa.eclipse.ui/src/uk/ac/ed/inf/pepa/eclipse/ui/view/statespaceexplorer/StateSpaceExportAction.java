/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;

import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;

/**
 * Action for exporting the state space data model to disk
 * 
 * @author mtribast
 * 
 */
public class StateSpaceExportAction extends Action {

	private ProcessAlgebraModelPage page;

	public StateSpaceExportAction(ProcessAlgebraModelPage page) {
		super("Export", Action.AS_PUSH_BUTTON);
		this.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(
				ImageManager.EXPORT));
		this.page = page;
		this.setToolTipText("Export data to file");
	}

	public void run() {
		//MessageDialog.openInformation(page.getSite().getShell(), "Coming Soon", "Coming soon...");
		
		WizardDialog dialog = new WizardDialog(page.getSite().getShell(), 
				new StateSpaceExporterWizard(page));
		dialog.open();
	}

}
