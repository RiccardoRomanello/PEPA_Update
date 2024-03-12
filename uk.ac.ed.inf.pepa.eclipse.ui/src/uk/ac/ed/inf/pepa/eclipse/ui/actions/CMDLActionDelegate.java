/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import java.io.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis.CMDLWizard;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;

public class CMDLActionDelegate implements IObjectActionDelegate {
	
	Shell cachedShell = null;
	IFile cmdl = null;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		cachedShell = targetPart.getSite().getShell();
	}

	public void run(IAction action) {
		if(cmdl != null) {
			SBAtoISBJava model = null;
			StringBuilder sb = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(cmdl.getContents()));
				sb = new StringBuilder();
				String s;
				s = br.readLine();
				while(s != null) {
					sb.append(s).append("\n");
					s = br.readLine();
				}
				model = SBAtoISBJava.generateModel(sb.toString());
			} catch(Throwable t) {
				MessageDialog.openError(cachedShell, "Error loading cmdl file", t.getMessage());
				return;
			}
			CMDLWizard wizard = new CMDLWizard(cmdl, model);
			WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
			dialog.open();
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;
		cmdl = (IFile) ss.getFirstElement();
	}

}
