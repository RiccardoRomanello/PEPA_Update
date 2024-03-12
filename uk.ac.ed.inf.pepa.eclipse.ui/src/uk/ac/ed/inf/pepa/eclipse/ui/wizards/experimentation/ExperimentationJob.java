/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;

public class ExperimentationJob extends Job {

	private IExperiment[] experiments;

	private boolean showAsYouGo;

	public ExperimentationJob(IExperiment[] experiments, boolean showAsYouGo) {
		super("Experimentation");
		Assert.isNotNull(experiments);
		Assert.isTrue(experiments.length > 0);
		this.experiments = experiments;
		this.showAsYouGo = showAsYouGo;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		//long tic = System.currentTimeMillis();
		boolean isCanceled = false;
		final int SCALING_UNIT = 100;
		monitor.beginTask("Experiments", experiments.length * SCALING_UNIT);
		for (int i = 0; i < experiments.length; i++) {
			if (monitor.isCanceled() == true) {
				isCanceled = true;
				break;
			}
			monitor.subTask(experiments[i].getName());
			Assert.isNotNull(experiments[i].isCanRun());
			try {
				experiments[i].run(new SubProgressMonitor(monitor,
						SCALING_UNIT), showAsYouGo);
					
			} catch (EvaluationException e) {
				PepaLog.logError(e);
				return createErrorStatus(e);
			}

		}
		//long toc = System.currentTimeMillis();
		//System.err.println(showAsYouGo + ": " + (toc-tic) );
		monitor.done();

		if (!isCanceled) {
			
			return Status.OK_STATUS;

		} else
			return Status.CANCEL_STATUS;
	}
	
	private IStatus createErrorStatus(EvaluationException e) {
		return new Status(IStatus.ERROR, Activator.ID, IStatus.OK, e
				.getMessage(), e);
	}

}
