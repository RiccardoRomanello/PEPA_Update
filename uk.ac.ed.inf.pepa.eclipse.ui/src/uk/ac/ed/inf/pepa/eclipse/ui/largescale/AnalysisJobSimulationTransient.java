package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.util.Formatter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import uk.ac.ed.inf.common.ui.plotting.Plotting;
import uk.ac.ed.inf.common.ui.plotting.data.ConfidenceSeries;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.Series;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoProgressMonitorAdapter;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;
import uk.ac.ed.inf.pepa.largescale.simulation.SimulationException;
import uk.ac.ed.inf.pepa.largescale.simulation.TransientSimulation;

public class AnalysisJobSimulationTransient extends AnalysisJob {

	private TransientSimulation simulation;

	public AnalysisJobSimulationTransient(String name,
			IParametricDerivationGraph derivationGraph, OptionMap map,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors,
			String[] labels) {
		super(name, derivationGraph, map, estimators, collectors, labels);
		simulation = new TransientSimulation(optionMap, derivationGraph,
				estimators, collectors);
	}

	@Override
	protected IStatus doRun(final IProgressMonitor monitor) {
		try {
			simulation.doSimulation(new PepatoProgressMonitorAdapter(monitor,
					"Transient simulation") {

				private int replications = 0;

				public void worked(int worked) {
					super.worked(worked);
					replications++;
					monitor.subTask(getMessage());
				}

				private String getMessage() {
					double convergence = simulation.getCurrentConfidenceError();
					String message = "Replications completed: " + replications
							+ ". Confidence Interval: ";

					if (!Double.isInfinite(convergence))
						message += new Formatter().format("%4f%%", convergence);
					else
						message += "-";
					return message;

				}
			});
		} catch (SimulationException e) {
			return new Status(IStatus.ERROR,
					uk.ac.ed.inf.pepa.eclipse.ui.Activator.ID,
					"A problem occurred during transient simulation", e);
		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public DisplayAction getDisplayAction() {
		return new DisplayAction("Results", false) {

			public void run() {

				InfoWithAxes info = new InfoWithAxes();
				info.setGraphTitle(getName());
				info.setHas3DEffect(false);
				info.setShowLegend(true);
				info.setShowMarkers(false);
				info.setXSeries(Series.create(simulation.getTimes(), "Time"));
				if (simulation.canComputeConfidenceInterval())
					averagesAndRadii(info);
				else
					onlyAverages(info);
				uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
						.getPlottingTools().createTimeSeriesChart(info);
				PerformanceMetricDialog.display(chart);
			}

			private void onlyAverages(InfoWithAxes info) {
				for (int i = 0; i < simulation.getNumberOfObservers(); i++) {
					double[] results = simulation.getAverages(i);
					Series series = Series.create(results, labels[i]);
					info.getYSeries().add(series);
				}
			}

			private void averagesAndRadii(InfoWithAxes info) {
				double[] avg = new double[simulation.getNumberOfTimePoints()];
				double[] radii = new double[simulation.getNumberOfTimePoints()];
				for (int i = 0; i < simulation.getNumberOfObservers(); i++) {
					simulation.averagesAndRadii(i, avg, radii);
					ConfidenceSeries series = ConfidenceSeries.create(avg,
							radii, labels[i], simulation
									.getRequiredConfidenceLevel());
					info.getYSeries().add(series);
				}
			}
		};
	}
}