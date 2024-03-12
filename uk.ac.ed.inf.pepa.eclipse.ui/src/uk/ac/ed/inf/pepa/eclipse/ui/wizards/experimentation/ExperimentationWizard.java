/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class ExperimentationWizard extends Wizard {

	private ASTSelectionPage selectionPage; // first page

	private ExperimentPage experimentPage; // last page

	/* as many as the number of checked AST nodes */
	private ISettingPage[] settingPages = new ISettingPage[0];

	private AbstractExperimentFactory experimentFactory = new ConcreteExperimentFactory();

	private AbstractPerformanceMetricFactory performanceMetricFactory;

	private IEvaluator evaluator;

	/**
	 * Creates the experimentation wizard for this PEPA model.
	 * <p>
	 * If the user has solved the model at least one, experimentations are run
	 * used the last settings, otherwise default values are used.
	 * 
	 * @param model
	 */
	public ExperimentationWizard(IEvaluator evaluator,
			AbstractPerformanceMetricFactory performanceMetricFactory) {
		if (evaluator.getProcessAlgebraModel() == null)
			throw new NullPointerException("Model cannot be null");
		this.performanceMetricFactory = performanceMetricFactory;
		this.evaluator = evaluator;
		/*
		 * TIP Important: needs to work on a copy of the model!!!
		 */
		// this.model = (ModelNode) ASTSupport.copy(model.getAST());
		this.setForcePreviousAndNextButtons(true);
		this.setNeedsProgressMonitor(true);

	}

	/*
	 * public ModelNode getModelNode() { return model; }
	 */

	public IProcessAlgebraModel getProcessAlgebraModel() {
		return evaluator.getProcessAlgebraModel();
	}
	
	public IEvaluator getEvaluator() {
		return evaluator;
	}

	public void addPages() {
		selectionPage = new ASTSelectionPage(evaluator.getSensibleNodes());
		experimentPage = new ExperimentPage(experimentFactory,
				performanceMetricFactory);
		addPage(selectionPage);
		addPage(experimentPage);
	}

	@Override
	public boolean performFinish() {
		// MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION
		// | SWT.YES | SWT.NO);
		// messageBox.setMessage("Do you want Show As You Go enabled?");
		// messageBox.setText("Show As You Go");
		// int response = messageBox.open();

		Job myJob = new ExperimentationJob(experimentPage.getExperiments(),
				false // response == SWT.YES
		);
		myJob.schedule();
		return true;
	}

	/**
	 * Called by the selection page to update the available nodes
	 * 
	 * @param selectedNodes
	 */
	public void updateSelectionPages(Object[] selectedNodes) {
		/* temporary pages for the new selection */
		ISettingPage[] newPages = new ISettingPage[selectedNodes.length];
		for (int i = 0; i < selectedNodes.length; i++) {
			boolean found = false;
			/* try to reuse the existing ones */
			for (ISettingPage page : settingPages)
				if (selectedNodes[i] == page.getNode()) {
					/* give the cached page */
					newPages[i] = page;
					found = true;
					break;
				}
			if (!found) {
				newPages[i] = SettingPageFactory
						.createSettingPage((ISensibleNode) selectedNodes[i]);
				newPages[i].setWizard(this);
			}
		}
		settingPages = newPages;

		experimentPage.updateAvailableNodes(selectedNodes);

		this.getContainer().updateButtons();
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectionPage) {
			if (settingPages.length != 0)
				return settingPages[0];
		}
		for (int i = 0; i < settingPages.length; i++)
			if (page == settingPages[i])
				if (i < settingPages.length - 1)
					return settingPages[i + 1];
				else if (i == settingPages.length - 1) {
					/*
					 * Feed the experiment page with the new settings, which we
					 * know are correct because the next page is available
					 */
					ISetting[] settings = new ISetting[settingPages.length];
					for (int index = 0; index < settingPages.length; index++)
						settings[index] = settingPages[index].getASTSetting();
					experimentPage.updateAvailableSettings(settings);

					return experimentPage;

				}

		return null;
	}

	public IWizardPage getStartingPage() {
		return selectionPage;
	}

	/**
	 * Called by updateButton
	 */
	public boolean canFinish() {
		// System.err.println("Can finish being called");
		if (!selectionPage.isPageComplete())
			return false;
		for (IWizardPage page : settingPages)
			if (!page.isPageComplete())
				return false;
		if (!experimentPage.isPageComplete())
			return false;
		return true;
	}

}
