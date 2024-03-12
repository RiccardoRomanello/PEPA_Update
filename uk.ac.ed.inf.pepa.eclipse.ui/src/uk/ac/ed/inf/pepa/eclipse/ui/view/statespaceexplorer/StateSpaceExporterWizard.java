/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.common.ui.wizards.SaveAsPage;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;

/**
 * This wizard exports the state space model to disk.
 * 
 * @author mtribast
 * 
 */
public class StateSpaceExporterWizard extends Wizard {

	// Default extension for the generator matrix
	private static final String Q_EXTENSION = "generator";

	// Default extension for the state space
	private static final String S_EXTENSION = "statespace";

	private OptionWizardPage optionWizardPage;

	private SaveAsPage stateSpacePage;

	private SaveAsPage generatorPage;

	ProcessAlgebraModelPage modelPage;

	public StateSpaceExporterWizard(ProcessAlgebraModelPage modelPage) {

		// do some sanity check
		if (modelPage == null || modelPage.model == null)
			throw new NullPointerException();
		if (!modelPage.model.isDerivable())
			throw new IllegalStateException("PEPA Model is not derivable");

		// set state of wizard
		this.modelPage = modelPage;
		this.setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		super.addPages();
		optionWizardPage = new OptionWizardPage(modelPage.model);
		addPage(optionWizardPage);

		IFile stateSpaceFile = getFileFor(S_EXTENSION);
		stateSpacePage = new SaveAsPage("stateSpacePage",
				new StructuredSelection(stateSpaceFile), S_EXTENSION);
		stateSpacePage.setTitle("Save State Space");
		stateSpacePage.setDescription("Save state space to the given location");
		stateSpacePage.setFileName(stateSpaceFile.getName());
		addPage(stateSpacePage);

		IFile generatorFile = getFileFor(Q_EXTENSION);
		generatorPage = new SaveAsPage("generatorPage",
				new StructuredSelection(generatorFile), Q_EXTENSION);
		generatorPage.setFileName(generatorFile.getName());
		generatorPage.setTitle("Save Infinitesimal Generator Matrix");
		generatorPage
				.setDescription("Save infinitesimal generator matrix to the given location");
		addPage(generatorPage);
	}

	private IFile getFileFor(String extension) {
		IResource resource = modelPage.model.getUnderlyingResource();
		IPath fullPath = resource.getFullPath();
		fullPath = fullPath.removeFileExtension();
		fullPath = fullPath.addFileExtension(extension);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(fullPath);
	}

	@Override
	public boolean performFinish() {

		final IFile stateSpaceFile = getFileFor(stateSpacePage);
		final IFile generatorFile = getFileFor(generatorPage);
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
						
						File ssFile = stateSpaceFile.getLocation().toFile();
						/* 45% of work for creating state space */
						createStateSpace(ssFile, subMonitor.newChild(45));

						File genFile = generatorFile.getLocation().toFile();
						createMatrix(genFile, subMonitor.newChild(45));
						
						stateSpaceFile.touch(subMonitor.newChild(5));
						generatorFile.touch(subMonitor.newChild(5));
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}

				}

			});
		} catch (Exception e) {
			MessageDialog.openError(modelPage.getSite().getShell(),
					"Error while saving resources to disk", e
							.getMessage());
			return false;
		}
		try {
			org.eclipse.ui.ide.IDE
					.openEditor(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage(),
							stateSpaceFile);
		} catch (PartInitException e) {
			MessageDialog.openError(modelPage.getSite().getShell(),
					"Error opening saved resource",
					"An error occurred while opening this file: "
							+ stateSpaceFile.getName());
			return false;
		}
		return true;
	}

	/**
	 * Either create or return the file pointed by the page
	 * 
	 * @param page
	 * @return
	 */
	private IFile getFileFor(SaveAsPage page) {
		return page.createNewFile();
	}

	/**
	 * Actual implementation of the state space exporter
	 * 
	 * @return the input stream to be saved to the file
	 * @throws IOException
	 */
	private void createStateSpace(File file, SubMonitor monitor)
			throws InterruptedException, IOException {
		OutputStream output = new BufferedOutputStream(new FileOutputStream(
				file));
		IStateSpace ss = modelPage.model.getStateSpace();
		int size = ss.size();
		monitor.beginTask("Creating state space", size);
		String separator = optionWizardPage.getSeparator();
		String ls = System.getProperty("line.separator");
		String openBracket = "{";
		String closeBracket = "}";

		for (int i = 0; i < size; i++) {

			if (monitor.isCanceled()) {

				monitor.done();
				throw new InterruptedException(
						"State space exporting interrupted by user");
			}
			if (optionWizardPage.isIncludeStateNumber())
				output.write(("" + (i + 1) + separator).getBytes());

			/* separate components */
			int state = i;
			output.write(openBracket.getBytes());
			for (int j = 0; j < ss.getNumberOfSequentialComponents(i); j++)
				output.write((ss.getLabel(state, j) + ((j != ss
						.getNumberOfSequentialComponents(i) - 1) ? separator
						: "")).getBytes());
			output.write(closeBracket.getBytes());
			if (optionWizardPage.isIncludeCurrentSolution()) {
				/* include current solution */
				output.write((separator + ss.getSolution(state)).getBytes());
				// state.append(modelPage.model.getSolution()[i]);

			}
			output.write(ls.getBytes());
			monitor.worked(1);
		}
		output.close();
		monitor.done();
	}

	/**
	 * Actual implementation of the generator exporter
	 * 
	 * @param genFile
	 * 
	 * @return the input stream to be saved to the file
	 * @throws IOException
	 * @throws IOException
	 * @throws DerivationException 
	 */
	private void createMatrix(File genFile, SubMonitor monitor)
			throws InterruptedException, IOException, DerivationException {

		IStateSpace ss = modelPage.model.getStateSpace();
		int size = ss.size();
		OutputStream output = new BufferedOutputStream(new FileOutputStream(
				genFile));
		try {
			String separator = optionWizardPage.getSeparator();
			String ls = System.getProperty("line.separator");

			monitor.beginTask("Creating generator matrix", size);

			for (int s = 0; s < ss.size(); s++) {
				int[] outgoing = ss.getOutgoingStateIndices(s);
				for (int index : outgoing) {
					output.write(Integer.toString(s + 1).getBytes());
					output.write(separator.getBytes());
					output.write(Integer.toString(index + 1).getBytes());
					output.write(separator.getBytes());
					output.write(Double.toString(ss.getRate(s, index)).getBytes());
					output.write(ls.getBytes());
				}
				monitor.worked(1);
			}
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
