/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.dialogs.SaveAsDialog;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.OptionsMap.Solver;
import uk.ac.ed.inf.pepa.eclipse.core.*;

import uk.ac.ed.inf.pepa.eclipse.ui.wizards.IResourceProvider;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

/**
 * 
 * @author ajduguid
 * 
 */
public class TimeSeriesAnalysisWizard extends Wizard implements
		IResourceProvider {

	IPepaModel model;

	OptionsMap optionsMap;

	IWizardPage pswp = null;

	StoichiometricWizardPage swp = null;

	public TimeSeriesAnalysisWizard(IPepaModel model) {
		if (model == null)
			throw new NullPointerException("Error; model does not exist.");
		if (model.isSBAParseable().length == 0)
			throw new IllegalStateException(
					"Error. No parseable types for this model.");
		this.model = model;
		setHelpAvailable(false);
		setNeedsProgressMonitor(true);
		setWindowTitle(WizardMessages.TIME_SERIES_ANALYSIS_WIZARD_TITLE);
		optionsMap = new OptionsMap();
		IResource modelResource = model.getUnderlyingResource();
		String value;
		// Read in persistent settings
		try {
			for (Parameter parameter : optionsMap.keySet()) {
				value = null;
				value = PepatoOptionForwarder.getOptionFromPersistentResource(
						modelResource, parameter.getKey());
				if (value != null && value != "")
					optionsMap.setValue(parameter, value);
			}
		} catch (Exception e) {
			PepaLog.logError(e);
		}
	}

	/**
	 * Ordering of pages is fragile. getNextPage() and getPreviousPage() rely on
	 * this ordering. You've been warned.
	 */
	public void addPages() {
		if (model.isSBAParseable().length > 1)
			addPage(new FormSelectionWizardPage(model));
		else
			model.setForm(model.isSBAParseable()[0]); // Only parseable using
														// normal PEPA semantics
		SpeciesSelectionWizardPage sswp = new SpeciesSelectionWizardPage(model,
				optionsMap);
		addPage(sswp);
		addPage(new AlgorithmWizardPage(model, optionsMap));
		// OutputOptionsWizardPage oowp = new OutputOptionsWizardPage();
		// addPage(oowp);
		// sswp.addListener(oowp);
	}

	public void dispose() {
		if (swp != null)
			swp.dispose();
		super.dispose();
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof FormSelectionWizardPage
				&& IPepaModel.PEPAForm.REAGENT_CENTRIC
						.equals(((FormSelectionWizardPage) page).getForm())) {
			if (swp == null) {
				swp = new StoichiometricWizardPage(model);
				swp.setWizard(this);
			}
			pswp = page;
			return swp;
		} else if (page == swp)
			return super.getNextPage(pswp);
		return super.getNextPage(page);
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page == swp)
			return pswp;
		IWizardPage previousPage = super.getPreviousPage(page);
		if (previousPage instanceof FormSelectionWizardPage
				&& IPepaModel.PEPAForm.REAGENT_CENTRIC
						.equals(((FormSelectionWizardPage) previousPage)
								.getForm()))
			return swp;
		return previousPage;
	}

	public IResource getResource() {
		return model.getUnderlyingResource();
	}

	@Override
	public boolean performFinish() {
		// Save preferences
		IResource modelResource = model.getUnderlyingResource();
		try {
			for (Parameter parameter : optionsMap.keySet())
				PepatoOptionForwarder.saveOptionInPersistentResource(
						modelResource, parameter.getKey(), optionsMap
								.serialise(parameter));
		} catch (CoreException e) {
			PepaLog.logError(e);
		}
		((SpeciesSelectionWizardPage) getPage(SpeciesSelectionWizardPage.name)).saveOptions();
		IWizardPage page = getPage(FormSelectionWizardPage.name);
		if (page != null)
			((FormSelectionWizardPage) page).saveOptions();
		if (swp != null)
			swp.saveStoichiometry();
		try {
			((SpeciesSelectionWizardPage) getPage(SpeciesSelectionWizardPage.name))
					.ensureParse();
		} catch (Exception e) {
			PepaLog.logError(e);
			return false;
		}
		// prepare simulation and creation of graphs
		String title = model.getUnderlyingResource().getName();
		title = title.substring(0, title.length()-5); // remove .pepa
		Solver solver =(Solver) optionsMap.getValue(Parameter.Solver);
		title += " - " + solver.getDescriptiveName();
		for(Parameter parameter : solver.getRequiredParameters())
			if(parameter.equals(Parameter.Independent_Replications)) {
				title += ". " + Integer.toString(((Integer) optionsMap.getValue(Parameter.Independent_Replications))) + " replications";
			}
		double start = ((Double) optionsMap.getValue(Parameter.Start_Time));
		double stop = ((Double) optionsMap.getValue(Parameter.Stop_Time));
		title += " with duration " + Double.toString(stop-start);
		AnalysisJob myJob = new AnalysisJob(title, model, optionsMap); // ,((OutputOptionsWizardPage) getPage("OutputOptions")).getGraphs(),((OutputOptionsWizardPage) getPage("OutputOptions")).saveResults());
		myJob.schedule();
		// independently save CMDL if requested
		if (((AlgorithmWizardPage) getPage("Algorithm")).saveCMDL())
			try {
				ResourceUtilities.generate(modelResource.getFullPath().toOSString()
						+ ".cmdl", model.getCMDL(), null);
				IPath dir = modelResource.getFullPath().removeLastSegments(1);
				String name = modelResource.getName();
				StringBuilder newName = new StringBuilder();
				for(char c : name.toCharArray())
					if(Character.isLetterOrDigit(c))
						newName.append(c);
					else
						newName.append("_");
				newName.append(".m");
				
				SaveAsDialog saveDialog = new SaveAsDialog(getShell());
				saveDialog.setOriginalFile(ResourceUtilities.getIFileFromText(dir.append(newName.toString()).toOSString()));
				saveDialog.open();
				dir = saveDialog.getResult();
				if(dir != null) {
					dir = dir.removeFileExtension().addFileExtension("m");
					ResourceUtilities.generate(dir.toOSString(), model.getMatlab(), null);
				}
			} catch (Exception e) {
				PepaLog.logError(e);
			}
		return true;
	}

}
