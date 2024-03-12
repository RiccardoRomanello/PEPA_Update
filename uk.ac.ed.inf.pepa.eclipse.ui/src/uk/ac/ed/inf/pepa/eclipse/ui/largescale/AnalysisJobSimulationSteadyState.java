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
import uk.ac.ed.inf.pepa.largescale.simulation.SimulationException;
import uk.ac.ed.inf.pepa.largescale.simulation.SteadyStateSimulation;

public class AnalysisJobSimulationSteadyState extends AnalysisJob {

	protected SteadyStateSimulation simulation;

	public AnalysisJobSimulationSteadyState(String name,
			IParametricDerivationGraph derivationGraph, OptionMap map,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors, String[] labels) {
		super(name, derivationGraph, map, estimators, collectors, labels);
		simulation = new SteadyStateSimulation(optionMap, derivationGraph,
				estimators, collectors);
	}

	@Override
	protected IStatus doRun(final IProgressMonitor monitor) {
		try {
			simulation.doSimulation(new PepatoProgressMonitorAdapter(monitor,
					"Steady-state simulation") {
				private int batches = 0;

				public void worked(int worked) {
					super.worked(worked);
					batches++;
					double convergence = simulation.getCurrentConfidenceError();
					String message = "Current batch: " + batches
							+ ". Confidence Interval: ";

					if (!Double.isInfinite(convergence))
						message += new Formatter().format("%4f%%", convergence);
					else
						message += "-";
					monitor.subTask(message);
				}
			});
		} catch (SimulationException e) {
			return new Status(IStatus.ERROR,
					uk.ac.ed.inf.pepa.eclipse.ui.Activator.ID,
					"An error occurred during steady-state simulation", e);
		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public DisplayAction getDisplayAction() {
		return new DisplayAction("Simulation results", true) {
			public void run() {
				// Print report
				StringBuffer message = new StringBuffer();
				message.append("Runtime: " + elapsed + " ms.\n\nResults:\n");
				for (int i = 0; i < simulation.getNumberOfObservers(); i++) {
					double[] results = new double[2];
					double cl = simulation.getRequiredConfidenceLevel();
					simulation.confidenceInterval(i, 0, results);
					message.append("***\nMeasure: " + labels[i] + "\n");
					message.append(new Formatter().format("Average: %6f\n",
							results[0]));
					double ci = results[1] / results[0] * 100;
					message.append(new Formatter().format(
							"%2.2f%% Confidence Interval: %3.3f%%\n", cl*100, ci));
					message.append(new Formatter().format(
							"Lag-1 Correlation: %e\n", simulation
									.computeLagOneAutoCorrelation(i)));
				}
				PEPAMessageDialog.openInformation(Display.getCurrent()
						.getActiveShell(), "Steady-state Simulation Results",
						message.toString());

			}
		};

	}
}
