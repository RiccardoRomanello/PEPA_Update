package uk.ac.ed.inf.pepa.largescale.simulation;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import umontreal.iro.lecuyer.stat.TallyStore;
import fern.simulation.Simulator;
import fern.simulation.algorithm.GibsonBruckSimulator;
import fern.simulation.algorithm.GillespieEnhanced;

public abstract class AbstractStochasticSimulation {

	protected static final int MINIMUM_SAMPLE_SIZE_FOR_CONFIDENCE = 3;

	protected static final int MAX_ITERATIONS_CONVERGENCE = 0;

	protected static final int CONFIDENCE_LEVEL_CONVERGENCE = 1;

	protected double currentConfidenceError = Double.POSITIVE_INFINITY;

	protected double startTime;

	protected double stopTime;

	protected double timeInterval;

	protected int convergenceCriterion;

	protected double requiredConfidenceLevel;

	protected double requiredConfidenceError;

	protected int maxIterations;

	protected Simulator simulator;

	protected IPointEstimator[] estimators;

	protected IParametricDerivationGraph derivationGraph;

	protected IStatisticsCollector[] collectors;

	protected TallyStore[][] tallies;

	protected int timeStep;

	protected IConvergenceChecker convergenceChecker;

	public AbstractStochasticSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors,
			int[] collectorsExcludedFromConvergence) {
		if (estimators == null || estimators.length == 0)
			throw new IllegalArgumentException();
		if (collectors == null || collectors.length == 0)
			throw new IllegalArgumentException();
		startTime = (Double) map.get(OptionMap.SSA_START_TIME);
		stopTime = (Double) map.get(OptionMap.SSA_STOP_TIME);
		timeStep = (Integer) map.get(OptionMap.SSA_TIME_POINTS);
		timeInterval = (stopTime - startTime) / (timeStep - 1);
		this.derivationGraph = derivationGraph;
		this.estimators = estimators;
		this.collectors = collectors;
		PEPANetwork network = new PEPANetwork(derivationGraph);
		String algorithm = (String) map.get(OptionMap.SSA_ALGORITHM);
		simulator = null;
		if (algorithm.equals(OptionMap.SSA_ALGORITHM_GIBSON_BRUCK))
			simulator = new GibsonBruckSimulator(network);
		else if (algorithm.equals(OptionMap.SSA_ALGORITHM_GILLESPIE))
			simulator = new GillespieEnhanced(network);
		else
			throw new IllegalArgumentException();
		this.requiredConfidenceLevel = (Double) map
				.get(OptionMap.SSA_CONFIDENCE_LEVEL);
		this.maxIterations = (Integer) map.get(OptionMap.SSA_MAX_ITERATIONS);
		this.requiredConfidenceError = (Double) map
				.get(OptionMap.SSA_CONFIDENCE_PERCENT_ERROR);
		if (((String) map.get(OptionMap.SSA_CRITERION_OF_CONVERGENCE))
				.equals(OptionMap.SSA_MAX_ITERATIONS_CRITERION)) {
			// System.out.println("Convergence max criterion");
			this.convergenceCriterion = TransientSimulation.MAX_ITERATIONS_CONVERGENCE;
		} else {
			// System.out.println("Convergence confidence criterion");
			this.convergenceCriterion = TransientSimulation.CONFIDENCE_LEVEL_CONVERGENCE;
		}
		this.tallies = new TallyStore[collectors.length][getNumberOfTimePoints()];
		for (int i = 0; i < collectors.length; i++)
			for (int j = 0; j < getNumberOfTimePoints(); j++)
				tallies[i][j] = new TallyStore("Collector " + i + "," + j);
		convergenceChecker = new DefaultConvergenceChecker();

	}

	public AbstractStochasticSimulation(OptionMap map,
			IParametricDerivationGraph derivationGraph,
			IPointEstimator[] estimators, IStatisticsCollector[] collectors) {
		this(map, derivationGraph, estimators, collectors,
				new int[0]);
	}

	public abstract int getNumberOfTimePoints();

	public void averagesAndRadii(int collectorIndex, double[] averages,
			double[] radii) {
		if (collectorIndex > collectors.length - 1)
			throw new IllegalArgumentException();
		if (averages.length != getNumberOfTimePoints())
			throw new IllegalArgumentException();
		if (radii.length != getNumberOfTimePoints())
			throw new IllegalArgumentException();
		for (int i = 0; i < getNumberOfTimePoints(); i++) {
			double[] result = new double[2];
			tallies[collectorIndex][i].confidenceIntervalStudent(
					requiredConfidenceLevel, result);
			averages[i] = result[0];
			radii[i] = result[1];
		}
	}

	public double[] getAverages(int collectorIndex) {
		if (collectorIndex > collectors.length - 1)
			throw new IllegalArgumentException();
		double[] result = new double[getNumberOfTimePoints()];
		for (int i = 0; i < result.length; i++)
			result[i] = tallies[collectorIndex][i].average();
		return result;
	}

	public void confidenceInterval(int collectorIndex, int timeIndex,
			double[] result) {
		tallies[collectorIndex][timeIndex].confidenceIntervalStudent(
				requiredConfidenceLevel, result);
	}

	public boolean canComputeConfidenceInterval() {
		return tallies[0][0].numberObs() >= 3;
	}

	public int getNumberOfObservers() {
		return collectors.length;
	}

	public double getRequiredConfidenceLevel() {
		return this.requiredConfidenceLevel;
	}

	public double getRequiredConfidenceError() {
		return this.requiredConfidenceError;
	}

	public double getCurrentConfidenceError() {
		return this.currentConfidenceError;
	}

	public abstract void doSimulation(IProgressMonitor monitor)
			throws SimulationException, InterruptedException;

}