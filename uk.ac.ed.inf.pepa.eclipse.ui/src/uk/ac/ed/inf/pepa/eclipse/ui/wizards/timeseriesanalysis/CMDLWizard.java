/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.OptionsMap.Solver;
import uk.ac.ed.inf.pepa.analysis.StaticAnalyser;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModelChangedListener;
import uk.ac.ed.inf.pepa.eclipse.core.PEPAModelChecker;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.sba.*;

/**
 * 
 * @author ajduguid
 * 
 */
public class CMDLWizard extends Wizard {

	private class CMDLsubPEPAModel implements IPepaModel {

		class CMDLProgressMonitorAdapter implements
				uk.ac.ed.inf.pepa.IProgressMonitor {

			IProgressMonitor eclispeMonitor;

			String name;

			CMDLProgressMonitorAdapter(IProgressMonitor monitor, String name) {
				eclispeMonitor = monitor;
				this.name = name;
			}

			public void beginTask(int amount) {
				final int eclipseAmount = (amount == uk.ac.ed.inf.pepa.IProgressMonitor.UNKNOWN) ? IProgressMonitor.UNKNOWN
						: amount;
				eclispeMonitor.beginTask(name, eclipseAmount);
			}

			public void done() {
				eclispeMonitor.done();
			}

			public boolean isCanceled() {
				return this.eclispeMonitor.isCanceled();
			}

			public void setCanceled(boolean state) {
			}

			public void worked(final int worked) {
				eclispeMonitor.worked(worked);
			}
		}

		Results fResults = null;

		SBAtoISBJava model;
		
		IResource resource;

		public void addModelChangedListener(IProcessAlgebraModelChangedListener listener) {
		}

		public void derive(OptionMap map, IProgressMonitor monitor)
				throws DerivationException {
		}

		public void dispose() {
		}

		public boolean generateReactions() throws SBAParseException {
			return false;
		}

		public void generateTimeSeries(OptionsMap options,
				IProgressMonitor monitor) throws SBASimulatorException {
			SBASimulatorException sbaSE = null;
			try {
				model.initialiseSimulator(options);
				fResults = null;
				fResults = model.runModel((monitor == null ? null
						: new CMDLProgressMonitorAdapter(monitor,
								"Time Series Analysis")));
			} catch (SBASimulatorException e) {
				sbaSE = e;
			} catch (Exception e) {
				sbaSE = new SBASimulatorException(e.getMessage(), e);
			}
			if (sbaSE != null)
				throw sbaSE;
		}

		public ModelNode getAST() {
			return null;
		}

		public KroneckerDisplayModel getDisplayModel() {
			return null;
		}
		
		public String getCMDL() {
			return null;
		}
		
		public String getMatlab() {
			return null;
		}

		public Mapping getMapping() {
			return model.getMapping();
		}

		public Set<SBAReaction> getReactions() {
			return null;
		}

		public IStateSpace getStateSpace() {
			return null;
		}

		public Results getTimeSeries() {
			return fResults;
		}

		public IResource getUnderlyingResource() {
			return resource;
		}

		public Solver[] getValidTimeSeriesSolvers() {
			return model.getPermissibleSolvers();
		}

		public boolean isDerivable() {
			return false;
		}

		public boolean isParsable() {
			return false;
		}

		public PEPAForm[] isSBAParseable() {
			return new PEPAForm[] { IPepaModel.PEPAForm.REAGENT_CENTRIC };
		}

		public boolean isSolvable() {
			return false;
		}

		public boolean isSolved() {
			return false;
		}

		public void parse() throws CoreException {
		}

		public void removeModelChangedListener(
				IProcessAlgebraModelChangedListener listener) {
		}

		public boolean sbaParse() throws SBAParseException {
			return false;
		}

		public void setApparentRateUse(boolean apparentRate) {
		}

		public void setForm(PEPAForm newForm) {
		}

		public void solveCTMCSteadyState(OptionMap options,
				IProgressMonitor monitor) throws SolverException {
		}

		public void updateReactions(Set<SBAReaction> updatedReactions) {
		}
		
		public StaticAnalyser getStaticAnalyser() {return null;}

		public void derive(IProgressMonitor monitor) throws DerivationException {
		}

		public void PSNI_verify(IProgressMonitor monitor) throws DerivationException {
		}

		public Boolean isPSNI() {
			return null;
		}

		// mtribast
		public OptionMap getOptionMap() {
			throw new IllegalStateException();
		}

		// mtribast
		public void setOptionMap(OptionMap map) {
			throw new IllegalStateException();
		}

		// mtribast		
		public void solveCTMCSteadyState(IProgressMonitor monitor)
				throws SolverException {
			throw new IllegalStateException();
		}

		// mtribast
		public Object getOption(String key) {
			throw new IllegalStateException();
		}

		public IResource getResource() {
			throw new IllegalStateException();
		}

		public void setSolution(double[] solution) {
			throw new IllegalStateException();
			
		}

		public PEPAModelChecker getModelChecker(double boundAccuracy) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void reversible_verify(IProgressMonitor monitor) {
			// TODO Auto-generated method stub
		}

		@Override
		public Boolean isReversible() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	CMDLsubPEPAModel model;

	OptionsMap optionsMap;

	public CMDLWizard(IResource resource, SBAtoISBJava model) {
		this.model = new CMDLsubPEPAModel();
		this.model.model = model;
		this.model.resource = resource;
		optionsMap = new OptionsMap();
	}

	public void addPages() {
		SpeciesSelectionWizardPage sswp = new SpeciesSelectionWizardPage(model,
				optionsMap);
		addPage(sswp);
		AlgorithmWizardPage awp = new AlgorithmWizardPage(model, optionsMap);
		awp.disableCMDLSave();
		addPage(awp);
		// OutputOptionsWizardPage oowp = new OutputOptionsWizardPage();
		// oowp.disableCMDLSave();
		// addPage(oowp);
		// sswp.addListener(oowp);
	}

	@Override
	public boolean performFinish() {
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
		return true;
	}
}
