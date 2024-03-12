package uk.ac.ed.inf.pepa.largescale.simulation;

import java.io.IOException;
import java.util.Date;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;
import umontreal.iro.lecuyer.stat.Tally;
import fern.simulation.Simulator;
import fern.simulation.Simulator.FireType;
import fern.simulation.controller.SimulationController;
import fern.simulation.observer.Observer;
import fern.tools.Stochastics;

public class SteadyStateSimulation extends AbstractStochasticSimulation {

	public static void main(String[] args) throws IOException,
			DifferentialAnalysisException, InterruptedException,
			SimulationException {
		ModelNode model = (ModelNode) PepaTools.parse(PepaTools
				.readText(args[0]));
		final IParametricDerivationGraph derivationGraph = ParametricDerivationGraphBuilder
				.createDerivationGraph(model, null);
		OptionMap map = new OptionMap();
		map.put(OptionMap.SSA_STOP_TIME, 50000d);
		IPointEstimator[] estimators = new IPointEstimator[75];
		for (int i = 0; i < 75; i++) {
			final int index = i;
			estimators[i] = new IPointEstimator() {

				public double computeEstimate(double timePoint,
						double[] solution) throws DifferentialAnalysisException {
					return solution[index];
				}

			};
		}
		final IStatisticsCollector[] collectors = DefaultCollector
				.create(estimators);
		final SteadyStateSimulation simulation = new SteadyStateSimulation(map,
				derivationGraph, estimators, collectors);
		simulation.doSimulation(new IProgressMonitor() {

			private int batches = 0;

			public void beginTask(int amount) {
				System.out.println("Simulation started: " + amount);
			}

			public void done() {
				System.out.println("Simulation completed");
			}

			public boolean isCanceled() {
				return false;
			}

			public void setCanceled(boolean state) {
			}

			public void worked(int worked) {
				batches++;
				if (batches == 1)
					System.out.println("Transient removed");
				else {
					if (batches > 3) {
						double[] result = new double[2];
						simulation.confidenceInterval(0, 0, result);
						System.out.printf("%3d : %9.6f (%9.6f)\n", batches,
								result[0], result[1]);
					}
				}
			}
		});

		double[] results = new double[2];
		System.out.print("[");
		for (int i = 0 ; i < 75; i++) {
			simulation.confidenceInterval(i, 0, results);
			System.out.print(results[0] + " ");
		}
		System.out.println("]");
		//System.out.println("Ci: " + results[1]);
		// System.out.println("lag-1 correlation: "
		// + computeLagOneAutoCorrelation(simulation.getMeansPerBatch(0)));

	}

	private int batchLengthFactor;

	/**
	 * Construct a steady state simulator using the method of batched means. The
	 * option <code>SSA_STOP_TIME</code> in {@link OptionMap} represents an
	 * estimate of the transient period (computed perhaps from the differential
	 * equation). The first
	 * <code>TRANSIENT_DISCARD_FACTOR * SSA_STOP_TIME</code> will be discarded
	 * and each batch will be of length
	 * <code>BATCH_LENGTH_FACTOR * SSA_STOP_TIME</code>
	 * 
	 * @param map
	 * @param derivationGraph
	 * @param estimators
	 */
	public SteadyStateSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors, int[] excluded) {
		super(map, derivationGraph, estimators, collectors, excluded);
		this.batchLengthFactor = (Integer) map
				.get(OptionMap.SSA_BATCH_LENGTH_FACTOR);
	}
	
	public SteadyStateSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors) {
		this(map, derivationGraph, estimators, collectors, new int[0]);
	}

	public double computeLagOneAutoCorrelation(int collectorIndex) {
		double[] allValues = new double[tallies[collectorIndex][0].numberObs()];
		System.arraycopy(tallies[collectorIndex][0].getArray(), 0, allValues,
				0, allValues.length);
		double avg = tallies[collectorIndex][0].average();
		// covariance calculation
		// see Stewart, Probability, Markov chains, queues and simulation
		// pag 710
		double num = 0;
		double den = 0;
		for (int j = 0; j < allValues.length - 2; j++) {
			num += (allValues[j] - avg) * (allValues[j + 1] - avg);
			den += Math.pow(allValues[j] - avg, 2);
		}
		den += Math.pow(allValues[allValues.length - 1], 2);
		return num / den;

	}

	@Override
	public void doSimulation(IProgressMonitor monitor)
			throws SimulationException, InterruptedException {
		if (monitor == null)
			monitor = new DoNothingMonitor();

		SteadyStateController controller = new SteadyStateController();
		simulator.addObserver(getObserver(controller, monitor));
		Stochastics.getInstance().setSeed(new Date());
		simulator.start(controller);
		if (monitor.isCanceled())
			throw new InterruptedException("Simulation was cancelled");

	}

	private class SteadyStateController implements SimulationController {

		private boolean goOn = true;

		public void setGoOn(boolean goOn) {
			this.goOn = goOn;
		}

		public boolean goOn(Simulator sim) {
			return goOn;
		}
	}

	private Observer getObserver(final SteadyStateController controller,
			final IProgressMonitor monitor) {

		final double batchLength = batchLengthFactor * stopTime;

		Observer batchMeansObserver = new Observer(this.simulator) {

			// Tallies for current batch
			// as many as the number of estimates
			// they store all observations - may be very large
			private Tally[] accumulators;

			private boolean collecting = false;

			private int currentBatch;

			private double lastEvent;

			private double[] solutionBeforeEvent = new double[derivationGraph
					.getInitialState().length];

			@Override
			public void activateReaction(int mu, double tau, FireType fireType,
					int times) {
			}

			@Override
			public void finished() {
				monitor.done();
			}

			@Override
			public void started() {
				currentBatch = 0;
				lastEvent = 0.0;
				updateSolutionBeforeEvent();
				monitor.beginTask(IProgressMonitor.UNKNOWN);
				// start collecting results at the end of first batch
				setTheta(stopTime);
			}

			@Override
			public void step() {
				if (monitor.isCanceled()) {
					controller.setGoOn(false);
					return;
				}
				if (!collecting)
					return;
				updateAccumulators(simulator.getTime());

			}

			// the integral is computed with the previous solution
			// the time is the difference between the current one
			// and the previous event
			private void updateAccumulators(double newTime) {
				for (int i = 0; i < estimators.length; i++) {
					try {
						double currentResult = estimators[i].computeEstimate(
								newTime, solutionBeforeEvent);
						accumulators[i].add(currentResult
								* (newTime - lastEvent));
					} catch (DifferentialAnalysisException e) {
						throw new IllegalStateException(e);
					}
				}

				updateSolutionBeforeEvent();
				lastEvent = newTime; // updates last event for new integration
			}

			private void updateSolutionBeforeEvent() {
				for (int i = 0; i < solutionBeforeEvent.length; i++) {
					solutionBeforeEvent[i] = simulator.getAmount(i);
				}

			}

			@Override
			public void theta(double theta) {
				if (collecting == true) {
					// was collecting, update accumulators up to this time
					updateAccumulators(theta);
					// store averages to global tally
					double[] estimates = new double[estimators.length];
					for (int i = 0; i < estimators.length; i++) {
						// accumulators yield the integral...
						estimates[i] = accumulators[i].sum() / batchLength;
					}
					for (int i = 0; i < collectors.length; i++)
						tallies[i][0].add(collectors[i]
								.computeObservation(estimates));
				} else {
					// just gets the solution at this time
					updateSolutionBeforeEvent();
					// and sets this new event
					lastEvent = theta;
				}
				// report regardless of transient batch
				// report after storing the latest sample
				// worked may report some updates
				currentBatch++;
				if (currentBatch >= MINIMUM_SAMPLE_SIZE_FOR_CONFIDENCE) {
					if (checkConvergenceAndSetController())
						return;
				}
				monitor.worked(1);
				collecting = true;
				accumulators = new Tally[estimators.length];
				for (int i = 0; i < estimators.length; i++) {
					accumulators[i] = new Tally("Estimator " + i + ","
							+ currentBatch);
				}
				// schedule new batch
				setTheta(theta + batchLength);
			}

			// check convergence and modifies the controller
			private boolean checkConvergenceAndSetController() {
				double currentError = convergenceChecker
						.computeConvergenceError(SteadyStateSimulation.this);
				SteadyStateSimulation.this.currentConfidenceError = currentError;
				boolean hasConverged = currentError < SteadyStateSimulation.this.requiredConfidenceError;
				controller.setGoOn(!hasConverged);
				return hasConverged;
			}

		};
		return batchMeansObserver;
	}

	@Override
	public int getNumberOfTimePoints() {
		return 1; // only steady state
	}

}
