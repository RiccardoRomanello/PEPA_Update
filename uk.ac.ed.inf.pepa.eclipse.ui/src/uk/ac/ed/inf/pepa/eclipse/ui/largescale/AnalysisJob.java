package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;

public abstract class AnalysisJob extends Job {

	protected String[] labels;
	
	protected IParametricDerivationGraph derivationGraph;
	
	protected IPointEstimator[] estimators;
	
	protected IStatisticsCollector[] collectors;
	
	protected OptionMap optionMap;
	
	protected long elapsed = 0L;
	
	public AnalysisJob(String name,
			IParametricDerivationGraph derivationGraph, OptionMap map,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors, String[] labels) {
		super(name);
		this.labels = labels;
		this.derivationGraph = derivationGraph;
		this.estimators = estimators;
		this.collectors = collectors;
		this.optionMap = map;
	}

	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		long tic = System.currentTimeMillis();
		IStatus status = doRun(monitor);
		long toc = System.currentTimeMillis();
		elapsed = (toc -tic);
		if (status.getCode() == IStatus.OK) {
			DisplayAction action = getDisplayAction();
			if (!action.showInProgress()
					|| PerformanceMetricDialog.isModal(this)) {
				Display.getDefault().syncExec(
						new RunnableWithAction(action));
			} else {
				setProperty(IProgressConstants.KEEP_PROPERTY,
						Boolean.TRUE);
				setProperty(IProgressConstants.ACTION_PROPERTY,
						action);
			}
		}
		System.err.println("Elapsed " + elapsed);
		return status;
	}
	
	protected abstract IStatus doRun(IProgressMonitor monitor);
	
	public abstract DisplayAction getDisplayAction();

}
