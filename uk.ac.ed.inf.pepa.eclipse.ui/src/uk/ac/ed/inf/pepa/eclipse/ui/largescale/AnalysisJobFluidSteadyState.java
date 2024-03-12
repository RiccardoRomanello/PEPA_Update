package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.util.Formatter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoProgressMonitorAdapter;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.SteadyStateRoutine;

public class AnalysisJobFluidSteadyState extends AnalysisJob {

	private double[] results = null;
	
	private SteadyStateRoutine routine;

	public AnalysisJobFluidSteadyState(String name,
			IParametricDerivationGraph derivationGraph, OptionMap map,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors,
			String[] labels) {
		super(name, derivationGraph, map, estimators, collectors, labels);
	}

	@Override
	protected IStatus doRun(final IProgressMonitor monitor) {
		routine = new SteadyStateRoutine(optionMap,
				this.derivationGraph);
		try {
			routine.obtainSteadyState(new PepatoProgressMonitorAdapter(monitor,
					"ODE integration") {

				private long tic;

				private int units;

				private boolean measuring;

				private int maxUnits;

				public void beginTask(int amount) {
					super.beginTask(amount);
					tic = System.currentTimeMillis();
					units = 0;
					measuring = true;
				}

				public void done() {
					notifyProgress();
					super.done();
				}

				public void worked(int amount) {
					super.worked(amount);
					if (measuring) {
						units++;
						if (System.currentTimeMillis() - tic > 800) {
							maxUnits = units;
							measuring = false;
						}
					} else {
						units--;
						if (units == 0) {
							notifyProgress();
							units = maxUnits;
						}
					}
				}

				private void notifyProgress() {
					monitor.subTask(""
							+ new Formatter().format(
									"Time: %6.3f  Convergence: %e", routine
											.getTimePoint(), routine
											.getConvergenceNorm()));
				}
			});
		} catch (DifferentialAnalysisException e) {

			if (e.getKind() != DifferentialAnalysisException.NOT_CONVERGED) {
				return new Status(IStatus.ERROR,
						uk.ac.ed.inf.pepa.eclipse.ui.Activator.ID,
						"An error occurred during steady-state analysis", e);
			}

		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}
		try {
			computeResults(routine.getTimePoint(), routine.getSolution());
		} catch (DifferentialAnalysisException e) {
			return new Status(IStatus.ERROR,
					uk.ac.ed.inf.pepa.eclipse.ui.Activator.ID,
					"An error occurred during steady-state analysis", e);
		}
		return Status.OK_STATUS;
	}

	private void computeResults(double timePoint, double[] solution)
			throws DifferentialAnalysisException {
		// calculate results anyway
		double[] estimates = new double[estimators.length];
		results = new double[collectors.length];
		for (int i = 0; i < estimates.length; i++) {
			estimates[i] = estimators[i].computeEstimate(timePoint, solution);
		}
		for (int j = 0; j < collectors.length; j++)
			results[j] = collectors[j].computeObservation(estimates);
	}

	public DisplayAction getDisplayAction() {
		double tolerance = (Double) optionMap
				.get(OptionMap.ODE_STEADY_STATE_NORM);
		final boolean showWarning = routine.getConvergenceNorm() > tolerance;
		return new DisplayAction("Model solved", true) {
			public void run() {
				String title = null;
				String message = "Runtime: " + elapsed + "ms.\n\n";
				for (int i = 0; i < results.length; i++) {
					message += labels[i] + " : "
							+ new Formatter().format("%6f\n", results[i]);
				}
				if (showWarning) {
					title = "Unaccurate estimate";
					message = "The current steady-state convergence norm is too high: "
							+ new Formatter().format("%e.\n", routine.getConvergenceNorm())
							+ "Try to increase integration time.\n" + message;
					MessageDialog.openWarning(Display.getCurrent()
							.getActiveShell(), title, message);
				} else {
					title = "Steady-state analysis";
					message += new Formatter().format(
							"Convergence norm is: %e\nSteady state detetected at %5.3f time units",
							routine.getConvergenceNorm(), routine.getTimePoint());
					PEPAMessageDialog.openInformation(Display.getCurrent()
							.getActiveShell(), title, message);
				}
			}
		};
	}

}
