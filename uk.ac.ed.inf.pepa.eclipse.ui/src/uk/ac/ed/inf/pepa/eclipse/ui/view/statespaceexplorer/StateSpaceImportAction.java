/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;

public class StateSpaceImportAction extends Action {

	private ProcessAlgebraModelPage page;

	public StateSpaceImportAction(ProcessAlgebraModelPage pepaModelPage) {
		super("Import", Action.AS_PUSH_BUTTON);
		setImageDescriptor(ImageManager.getInstance().getImageDescriptor(
				ImageManager.IMPORT));
		setToolTipText("Import steady-state distribution from ASCII file");
		this.page = pepaModelPage;
	}

	public void run() {
		Shell shell = page.getSite().getShell();
		String errorMessage = "An error occurred while reading the file";
		String errorTitle = "Error";
		FileDialog dialog = new FileDialog(shell);
		String fileName = dialog.open();
		if (fileName != null) {
			File file = new File(fileName);
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				setSolution(reader);
				MessageDialog.openConfirm(shell, "Success",
						"Solution imported.");
			} catch (IOException e) {
				MessageDialog.openError(shell, errorTitle, errorMessage + " : "
						+ e.getMessage());
			} catch (Throwable t) {
				t.printStackTrace();
				MessageDialog.openError(shell, "Unexpected Error", t.getClass()
						.getName()
						+ ": " + t.getMessage());
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// discard this
					}
				}
			}
		}

	}

	private void setSolution(FileReader fileReader) throws IOException {
		BufferedReader r = new BufferedReader(fileReader);
		double[] solution = new double[page.model.getStateSpace().size()];
		String line = null;
		int stateSpaceSize = page.model.getStateSpace().size();
		int i = 0;
		while ((line = r.readLine()) != null) {
			if (i == stateSpaceSize)
				throw new IOException("Too many values");
			try {
				solution[i++] = Double.parseDouble(line);
			} catch (NumberFormatException e) {
				throw new IOException("Format unsupported");
			}
		}
		try {
			page.model.setSolution(solution);
		} catch (DerivationException e) {
			MessageDialog.openError(page.getSite().getShell(), "Error",
					"An error occurred while setting the new solution."
							+ e.getMessage());
		}
		return;
	}

}
