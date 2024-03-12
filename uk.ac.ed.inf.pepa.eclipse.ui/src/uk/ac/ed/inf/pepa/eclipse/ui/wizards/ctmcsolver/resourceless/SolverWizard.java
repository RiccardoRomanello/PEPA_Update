/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.AbstractConfigurationWizardPage;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

/**
 * Open the wizard for setting solver parameters for steady-state analysis of
 * the underlying CTMC of a PEPA model and provide services for storing these
 * data to disk.
 * 
 * @author mtribast
 * 
 */
public final class SolverWizard extends Wizard {

	private OptionMap fOptionMap;

	SolverSelectionPage fSelectSolverPage;

	AbstractConfigurationWizardPage fSolverPage = null;

	AbstractConfigurationWizardPage fPreconditionerPage = null;

	/**
	 * Create the wizard and passes the model on which CTMC steady-state
	 * analysis has to be carried out.
	 * <p>
	 * A model with a correctly derived state space has to be given.
	 * 
	 * @param model
	 *            the PEPA model
	 * @throws IllegalArgumentException
	 *             if the model is not solvable
	 * @throws NullPointerException
	 *             if the given model is <code>null</code>
	 */
	public SolverWizard(OptionMap optionMap) {
		super();
		org.eclipse.core.runtime.Assert.isNotNull(optionMap);
		fOptionMap = optionMap;
	}

	@Override
	public boolean canFinish() {
		if (fSelectSolverPage.isPageComplete()) {
			if (fSolverPage != null) {
				if (fSolverPage.isPageComplete()) {
					if (fPreconditionerPage != null) {
						if (fPreconditionerPage.isPageComplete())
							return true;
						else
							return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else
			return false;

	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == fSelectSolverPage) {
			return fSolverPage;
		}
		if (page == fSolverPage)
			return fPreconditionerPage;
		return null;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		Assert.isNotNull(page);
		if (page == fPreconditionerPage)
			return fSolverPage;
		if (page == fSolverPage)
			return fSelectSolverPage;
		return null;
	}

	public OptionMap getOptionMap() {
		return fOptionMap;
	}

	@Override
	public void addPages() {

		setForcePreviousAndNextButtons(true);
		setWindowTitle(WizardMessages.STEADY_STATE_CTMC_WIZARD_TITLE);
		setHelpAvailable(false);
		setNeedsProgressMonitor(true);
		fSelectSolverPage = new SolverSelectionPage();
		addPage(fSelectSolverPage);

	}

	@Override
	/*
	 * If returns false, the dialog won't close.
	 */
	public boolean performFinish() {
		if (fSolverPage != null)
			fSolverPage.setOptions(fOptionMap);

		if (fPreconditionerPage != null)
			fPreconditionerPage.setOptions(fOptionMap);

		fOptionMap.put(OptionMap.SOLVER,
				(int) fSelectSolverPage.fSelectedSolverId);

		return true;

	}

}

class SolverSelectionPage extends WizardPage {

	int fSelectedSolverId;

	private Combo solvers;

	/**
	 * Constructor for the first page of the wizard
	 * 
	 * @param resource
	 *            the file where the model is stored
	 */
	protected SolverSelectionPage() {
		super("selectSolver");
		setTitle(WizardMessages.STEADY_STATE_CTMC_SOLVERS_PAGE_TITLE);
		setDescription(WizardMessages.STEADY_STATE_CTMC_SOLVERS_PAGE_DESCRIPTION);

	}

	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		composite.setLayout(layout);
		setControl(composite);

		Label solverLabel = new Label(composite, SWT.NONE);
		solverLabel.setText("Choose solver");
		GridData solverData = new GridData();
		solverData.horizontalSpan = 3;
		solverData.grabExcessHorizontalSpace = true;
		solverData.grabExcessVerticalSpace = false;
		solverLabel.setLayoutData(solverData);

		solvers = new Combo(composite, SWT.READ_ONLY);
		GridData comboData = new GridData();
		comboData.horizontalSpan = 3;
		comboData.grabExcessHorizontalSpace = true;
		comboData.grabExcessVerticalSpace = false;
		solvers.setLayoutData(comboData);

		populateSolverCombo(solvers);

		setPageComplete(false);

		initSettings();

	}

	private void initSettings() {

		fSelectedSolverId = Integer.MIN_VALUE;
		int newSolver = (Integer) ((SolverWizard) getWizard())
				.getOptionMap().get(OptionMap.SOLVER);
		setSolver(newSolver);
	}

	private void setSolver(int id) {
		String name = Solvers.getInstance().getSolverName(id);
		solvers.setText(name);
		newSolverSelected(id);
	}

	private void newSolverSelected(int newSolverId) {
		setPageComplete(false);

		int preconditionerId = Integer.parseInt(((SolverWizard) getWizard())
				.getOptionMap().get(OptionMap.PRECONDITIONER)
				+ "");
		ISolverPageProxy solverPage = ISolverPageProxyactory.createPageFor(
				newSolverId, preconditionerId);
		if (solverPage == null) {
			/* An unexpected error occurred */
			setMessage(null);
			String error = "No solver available";
			setErrorMessage(error);
			return;
		}

		if (newSolverId != fSelectedSolverId) {
			/* a page was not set or a different one has been selected */
			AbstractConfigurationWizardPage newPage = solverPage.getPage();
			((SolverWizard) getWizard()).fSolverPage = newPage;
			if (newPage != null) {
				newPage.setWizard(getWizard());
			}
		}
		fSelectedSolverId = newSolverId;

		setMessage(null);
		setErrorMessage(null);
		setPageComplete(true);
	}

	/**
	 * The combo is populated with the name of the available solvers
	 */
	private void populateSolverCombo(final Combo solvers) {
		Collection<String> solverStrings = Solvers.getInstance()
				.getAvailableSolvers();
		for (String solver : solverStrings) {
			solvers.add(solver);
		}

		solvers.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				newSolverSelected(Solvers.getInstance().getSolverId(
						solvers.getText()));
			}
		});
	}

}
