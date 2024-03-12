package uk.ac.ed.inf.pepa.largescale.simulation;

import java.io.IOException;
import java.util.Formatter;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DoubleArray;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;
import fern.simulation.Simulator.FireType;
import fern.simulation.observer.Observer;
import fern.tools.Stochastics;

/**
 * Transient simulation for a PEPA model. It uses the method of independent
 * replications. Confidence intervals are accumulated for each of the time
 * points specified in the simulation settings
 * 
 * @author Mirco
 * 
 */
public class TransientSimulation extends AbstractStochasticSimulation {

	public static void main(String[] args) throws IOException,
			DifferentialAnalysisException, InterruptedException,
			SimulationException {
		ModelNode model = (ModelNode) PepaTools.parse(PepaTools
				.readText(args[0]));
		final IParametricDerivationGraph derivationGraph = ParametricDerivationGraphBuilder
				.createDerivationGraph(model, null);
		OptionMap map = new OptionMap();
		map.put(OptionMap.SSA_STOP_TIME, 20000000d);
		map.put(OptionMap.SSA_CRITERION_OF_CONVERGENCE, OptionMap.SSA_MAX_ITERATIONS_CRITERION);
		map.put(OptionMap.SSA_MAX_ITERATIONS, 100);

		
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
		final TransientSimulation simulation = new TransientSimulation(map,
				derivationGraph, estimators, collectors);
		simulation.doSimulation(new IProgressMonitor() {

			private int r = 0;

			private double[] times = null;

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
				r++;
				if (r == 1) {
					times = simulation.getTimes();
					System.out.print("  n  ");
					for (double t : times) {
						System.out.printf("       %6.2f       ", t);
					}
					System.out.println();
				}
			}

		});
		double[] results = new double[2];
		System.out.print("[");
		for (int i = 0 ; i < 75; i++) {
			simulation.confidenceInterval(i, simulation.getTimes().length - 1, results);
			System.out.print(results[0] + " ");
		}
		System.out.println("]");
		
	}

	private DoubleArray time = new DoubleArray(100);

	private int replications = 0;

	/**
	 * Initialises a transient simulation
	 * 
	 * @param map
	 *            option map with simulation settings
	 * @param derivationGraph
	 *            PEPA model
	 * @param estimators
	 *            array of estimators denoting the indices of performance
	 *            required
	 */
	public TransientSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors, int[] excluded) {
		super(map, derivationGraph, estimators, collectors, excluded);
		if (startTime >= stopTime)
			throw new IllegalArgumentException();
	}

	public TransientSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors) {
		this(map, derivationGraph, estimators, collectors, new int[0]);
	}

	public TransientSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators) {
		this(map, derivationGraph, estimators, DefaultCollector
				.create(estimators), new int[0]);
	}

	public double[] getTimes() {
		if (replications == 0) {
			return new double[0];
		} else {
			return time.toArray();
		}
	}

	public void doSimulation(IProgressMonitor monitor)
			throws SimulationException, InterruptedException {

		if (monitor == null) {
			monitor = new DoNothingMonitor();
		}
		if (convergenceCriterion == MAX_ITERATIONS_CONVERGENCE)
			monitor.beginTask(maxIterations);
		else
			monitor.beginTask(IProgressMonitor.UNKNOWN);
		//
		simulator.addObserver(getObserver());
		replications = 0;
		boolean converged = false;
		while (true) {
			replications++;
			Stochastics.getInstance().resetSeed();
			try {
				simulator.start(stopTime);
			} catch (IllegalStateException e) {
				monitor.done();
				throw new SimulationException(e.getCause());
			}
			if (monitor.isCanceled()) {
				monitor.done();
				throw new InterruptedException("Simulation was cancelled");
			}
			if (replications >= MINIMUM_SAMPLE_SIZE_FOR_CONFIDENCE) {
				// test all tallies
				this.currentConfidenceError = convergenceChecker
						.computeConvergenceError(TransientSimulation.this);
				if (convergenceCriterion == CONFIDENCE_LEVEL_CONVERGENCE
						&& currentConfidenceError < requiredConfidenceError) {
					converged = true;
					break;
				}

			}
			monitor.worked(1);
			if (replications == maxIterations) {
				// simulation terminates in either case
				converged = (this.convergenceCriterion == MAX_ITERATIONS_CONVERGENCE);
				break;
			}

		}
		monitor.done();
		if (!converged) {
			String message = ""
					+ new Formatter()
							.format(
									"Simulation has not converged after %d replications. Confidence interval: %6f",
									replications, currentConfidenceError);
			throw new SimulationException(message);
		}

	}

	private Observer getObserver() {
		Observer transientObserver = new Observer(simulator) {

			private boolean firstReplication = true;

			private int timeIndex = 0;

			@Override
			public void activateReaction(int mu, double tau, FireType fireType,
					int times) {
			}

			@Override
			public void finished() {
				firstReplication = false;
				timeIndex = 0;
			}

			@Override
			public void started() {
				setTheta(startTime);
			}

			@Override
			public void step() {
			}

			@Override
			public void theta(double theta) {
				if (firstReplication)
					time.add(theta); // add time point
				double[] currentSolution = new double[derivationGraph
						.getInitialState().length];
				for (int i = 0; i < currentSolution.length; i++) {
					currentSolution[i] = simulator.getAmount(i);
				}
				double[] estimates = new double[estimators.length];
				for (int i = 0; i < estimators.length; i++)
					try {
						estimates[i] = estimators[i].computeEstimate(theta,
								currentSolution);
					} catch (DifferentialAnalysisException e) {
						throw new IllegalStateException(e);
					}
				for (int i = 0; i < collectors.length; i++) {
					double currentResult = collectors[i]
							.computeObservation(estimates);
					tallies[i][timeIndex].add(currentResult);
				}
				if (++timeIndex == getNumberOfTimePoints())
					return;
				setTheta(theta + timeInterval);
			}

		};
		return transientObserver;
	}

	public int getNumberOfTimePoints() {
		return this.timeStep;
	}

}
