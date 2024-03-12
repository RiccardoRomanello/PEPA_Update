package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import uk.ac.ed.inf.common.ui.plotting.Plotting;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.Series;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DoubleArray;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoProgressMonitorAdapter;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.IODESolver;
import uk.ac.ed.inf.pepa.ode.ISolutionRoutineCallback;
import uk.ac.ed.inf.pepa.ode.ODESolverFactory;

public class AnalysisJobFluidTransient extends AnalysisJob {

	private DoubleArray times;
	private DoubleArray[] results;

	public AnalysisJobFluidTransient(String name,
			IParametricDerivationGraph derivationGraph, OptionMap map,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors, String[] labels) {
		super(name, derivationGraph, map, estimators, collectors, labels);
	}

	@Override
	protected IStatus doRun(IProgressMonitor monitor) {
		IODESolver solver = ODESolverFactory.create(derivationGraph);
		times = new DoubleArray(100);
		results = new DoubleArray[estimators.length];
		for (int i = 0; i < results.length; i++)
			results[i] = new DoubleArray(100);
		final ISolutionRoutineCallback callback = new ISolutionRoutineCallback() {

			public void timePointComputed(double timePoint, double[] solution)
					throws DifferentialAnalysisException {
				times.add(timePoint);
				for (int i = 0; i < estimators.length; i++)
					results[i].add(estimators[i].computeEstimate(timePoint,
							solution));
			}

		};
		try {
			solver.solve(optionMap, callback, new PepatoProgressMonitorAdapter(
					monitor, getName()));
		} catch (DifferentialAnalysisException e) {
			return new Status(IStatus.ERROR,
					uk.ac.ed.inf.pepa.eclipse.ui.Activator.ID,
					"An error occurred during the numerical integration", e);
		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	@Override
	public DisplayAction getDisplayAction() {
		return new DisplayAction("",false) {
			public void run() {
				InfoWithAxes info = new InfoWithAxes();
				info.setGraphTitle(getName());
				info.setHas3DEffect(false);
				info.setShowLegend(true);
				info.setShowMarkers(false);

				info.setXSeries(Series.create(times.toArray(), "Time"));
				for (int i = 0; i < estimators.length; i++) {
					Series series = Series.create(results[i].toArray(),
							labels[i]);
					info.getYSeries().add(series);
				}
				uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
						.getPlottingTools().createTimeSeriesChart(info);
				PerformanceMetricDialog.display(chart);
			}
		};
	}

}
