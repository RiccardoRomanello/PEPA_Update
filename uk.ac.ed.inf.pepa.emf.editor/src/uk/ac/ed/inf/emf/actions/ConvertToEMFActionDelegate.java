/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.emf.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.ResourceUtilities;
import uk.ac.ed.inf.pepa.eclipse.ui.actions.BasicProcessAlgebraModelActionDelegate;
import uk.ac.ed.inf.pepa.emf.Model;
import uk.ac.ed.inf.pepa.emf.util.EmfSupportException;
import uk.ac.ed.inf.pepa.emf.util.EmfTools;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class ConvertToEMFActionDelegate extends BasicProcessAlgebraModelActionDelegate {

	public void run(IAction action) {
		
		firstImpl();
		
		/*
		 * ContainerGenerator gen = new ContainerGenerator(path);
		 * 
		 * IContainer res = null; try { res = gen.generateContainer(null); }
		 * catch (Exception e) { // handle failure
		 * MessageDialog.openError(shell, "Error While Creating Resource", "An
		 * exception was thrown while creating the resource:\n" + "Reason:\n" +
		 * e.getMessage()); return; }
		 */

	}
	
	private void firstImpl() {
		Shell shell = Display.getCurrent().getActiveShell();
		IResource resource = model.getUnderlyingResource();
		if (resource.getType() != IResource.FILE) {
			MessageDialog.openError(shell, "Resource Error",
					"No underlying resource for this model");
			return;
		}
		SaveAsDialog dialog = new SaveAsDialog(shell);
		/* create a path for the xmi file */
		IPath outputFile = ResourceUtilities.changeExtension((IFile) resource,
				PepaCore.EMFPEPA_EXTENSION);

		dialog.setOriginalFile(ResourcesPlugin.getWorkspace().getRoot()
				.getFile(outputFile));
		dialog.open();
		IPath path = dialog.getResult();
		if (path == null) {
			// cancel was selected
			return;
		}
		ModelNode modelNode = ((IPepaModel) model).getAST();
		if (modelNode == null)
			return;
		try {
			Model model = EmfTools.convertToEmfModel(modelNode);
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
			.getFile(path);
			EmfTools.serialise(model, file);
			/* TIP RFRS 3.5.6 -- Open file in editor */
			org.eclipse.ui.ide.IDE.openEditor(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage(), file);
		} catch (EmfSupportException e) {
			PepaLog.logError(e);
			MessageDialog.openError(shell, "Error while creating resource",
					"An exception was thrown while creating the resource:\n"
							+ "Reason:\n" + e.getMessage());

		} catch (PartInitException e) {
			PepaLog.logError(e);
			MessageDialog.openError(shell, "Error opening resource", e.getMessage());
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void checkStatus() {
		action.setEnabled(model != null && model.isDerivable());
	}

}
