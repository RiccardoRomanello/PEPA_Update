/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.eclipse.core.IOptionHandler;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.AggregationWizard;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.PassageTimeWizard;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.CapacityPlanningWizard;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless.SolverWizard;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ConcretePerformanceMetricFactory;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ExperimentationWizard;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.pepa.PEPAEvaluator;

/**
 * Actual implementation of actions shared by both the text editor and the
 * visual EMF Pepa model editor
 * 
 * @author mtribast
 * 
 */
public class ActionCommands {

	public static void derive(final IProcessAlgebraModel model) {

		IProgressService progressService = PlatformUI.getWorkbench()
				.getProgressService();
		try {

			progressService.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("State Space Derivation",
							IProgressMonitor.UNKNOWN);
					try {
						model.derive(monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}

				}

			});

		} catch (InvocationTargetException e) {
			PepaLog.logError(e);
			if (e.getTargetException() instanceof DerivationException)
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"State Space Derivation Error",
						((DerivationException) e.getTargetException())
								.getMessage());
			else
				MessageDialog
						.openError(Display.getCurrent().getActiveShell(),
								"Unexpected Error", e.getTargetException()
										.getMessage());

		} catch (InterruptedException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Job Interrupted",
					"State space derivation interrupted by user");

		}
	}

	public static void experiment(IPepaModel model) {
		ExperimentationWizard wizard = new ExperimentationWizard(
				new PEPAEvaluator(model),
				new ConcretePerformanceMetricFactory());
		WizardDialog dialog = new WizardDialog(Display.getDefault()
				.getActiveShell(), wizard);
		dialog.setPageSize(400, 400);
		dialog.open();
	}
	
	public static void aggregation(IOptionHandler optHandler) {
		AggregationWizard wizard = new AggregationWizard(optHandler);
		WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.setPageSize(400, 400);
		dialog.open();
	}
	
	public static void capacityPlanning(IPepaModel model, String search, String evaluation) {
		CPTAPI.setModel(model.getAST());
		CPTAPI.getSearchControls().setValue(search);
		CPTAPI.getEvaluationControls().setValue(evaluation);
		CapacityPlanningWizard wizard = new CapacityPlanningWizard(model);
		WizardDialog dialog = new WizardDialog(Display.getDefault()
				.getActiveShell(), wizard);
		dialog.setPageSize(400, 400);
		dialog.open();
	}

	
	public static void passageTime(IPepaModel model) {
		PassageTimeWizard wizard = new PassageTimeWizard(model) ;
				// new PEPAEvaluator(model),
				// new ConcretePerformanceMetricFactory());
		WizardDialog dialog = new WizardDialog(Display.getDefault()
				.getActiveShell(), wizard);
		dialog.setPageSize(400, 400);
		dialog.open();
	}
	
	
	public static void steadyState(final IProcessAlgebraModel model) {
		OptionMap originalMap = model.getOptionMap();
		// System.out.println("Old option map:");
		// System.out.println("***************");
		// System.out.println(originalMap.prettyPrint());
		SolverWizard wizard = new SolverWizard(originalMap);
		Shell activeShell = Display.getDefault().getActiveShell();
		WizardDialog dialog = new WizardDialog(activeShell, wizard);
		if (dialog.open() == WizardDialog.OK) {
			OptionMap map = wizard.getOptionMap();
			// System.out.println("\n\nNew option map:");
			// System.out.println("***************");
			// System.out.println(map.prettyPrint());
			// model.solveCTMCSteadyState(map, null);
			model.setOptionMap(map);
			IRunnableWithProgress runnable = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						model.solveCTMCSteadyState(monitor);
					} catch (SolverException e) {
						throw new InvocationTargetException(e);
					}
				}

			};
			ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(
					activeShell);
			try {
				monitorDialog.run(true, true, runnable);
			} catch (InvocationTargetException e) {
				MessageDialog.openError(activeShell, "Error during solution", e
						.getCause().getMessage());
			} catch (InterruptedException e) {
				MessageDialog.openInformation(activeShell, "Operation aborted",
						"Operation interrupted.");

			}

		}
	}
}