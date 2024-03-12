/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.systemsbiology.chem.*;
import org.systemsbiology.chem.odetojava.*;
import org.systemsbiology.math.*;
import org.systemsbiology.util.*;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Solver;
import uk.ac.ed.inf.pepa.parsing.RateNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * 
 * @author ajduguid
 * 
 */
public class SBAtoISBJava {
	
	private static String term = System.getProperty("line.separator");

	public enum Algorithm {
		GIBSON_BRUCK("Gibson-Bruck Stochastic Algorithm",
				SimulatorStochasticGibsonBruck.class,
				OptionsMap.Solver.Gibson_Bruck), GILLESPIE(
				"Gillespies Stochastic Algorithm",
				SimulatorStochasticGillespie.class, OptionsMap.Solver.Gillespie), ODE_DOPR54_ADAPTIVE(
				"Adaptive step-size 5th-order Dormand Prince ODE Solver",
				SimulatorOdeToJavaRungeKuttaAdaptive.class,
				OptionsMap.Solver.DOPR), ODE_IMEX443_STIFF(
				"Implicit-Explicit Runge Kutta ODE Solver",
				SimulatorOdeToJavaRungeKuttaImplicit.class,
				OptionsMap.Solver.IMEX);
		/*
		 * ODE_RK5_ADAPTIVE( "Adaptive step-size 5th-order Runge Kutta ODE
		 * Solver", SimulatorDeterministicRungeKuttaAdaptive.class,
		 * OptionsMap.Solver.RK5_Adaptive), ODE_RK5_FIXED( "Fixed step-size
		 * 5th-order Runge Kutta ODE Solver",
		 * SimulatorDeterministicRungeKuttaFixed.class,
		 * OptionsMap.Solver.RK5_Fixed), TAU_LEAP( "Gillespies Tau Leap
		 * Stochastic Algorithm", SimulatorStochasticTauLeapSimple.class,
		 * OptionsMap.Solver.TauLeap);
		 */

		public static Algorithm getAlgorithm(OptionsMap.Solver solver) {
			for (Algorithm algorithm : Algorithm.values())
				if (algorithm.mapping.equals(solver))
					return algorithm;
			return null;
		}

		OptionsMap.Solver mapping;

		String name;

		Class<?> simulatorClass;

		Algorithm(String name, Class<?> simulatorClass,
				OptionsMap.Solver mapping) {
			this.name = name;
			this.simulatorClass = simulatorClass;
			this.mapping = mapping;
		}

		Simulator getSimulator() throws SBASimulatorException {
			try {
				return (Simulator) simulatorClass.newInstance();
			} catch (InstantiationException e) {
				throw new SBASimulatorException("Error constructing " + name
						+ ". Cannot find class.", e);
			} catch (IllegalAccessException e) {
				throw new SBASimulatorException("Error constructing " + name
						+ ". Cannot find class.", e);
			}
		}

		SimulatorParameters getSimulatorParameters(Simulator simulator)
				throws SBASimulatorException {
			// Works for Tau-Leap as it checks super class as well
			try {
				Class<?> c = simulatorClass.getSuperclass();
				Method m = c.getMethod("getDefaultSimulatorParameters",
						new Class[] {});
				return (SimulatorParameters) m.invoke(c.cast(simulator),
						new Object[0]);
			} catch (NoSuchMethodException e) {
				throw new SBASimulatorException(
						"Error retrieving parameters for " + name
								+ ". Cannot find class.", e);
			} catch (IllegalAccessException e) {
				throw new SBASimulatorException(
						"Error retrieving parameters for " + name
								+ ". Cannot find class.", e);
			} catch (InvocationTargetException e) {
				throw new SBASimulatorException(
						"Error retrieving parameters for " + name
								+ ". Cannot find class.", e.getCause());
			}
		}

		void initializeSimulator(Simulator simulator, Model model)
				throws SBASimulatorException {
			try {
				Method m = simulatorClass.getMethod("initialize",
						new Class[] { Model.class });
				m.invoke(simulator, new Object[] { model });
			} catch (NoSuchMethodException e) {
				throw new SBASimulatorException("Error initializing " + name
						+ ". Cannot find class.", e);
			} catch (IllegalArgumentException e) {
				throw new SBASimulatorException("Error initializing " + name
						+ ". Ensure model is correct.", e);
			} catch (IllegalAccessException e) {
				throw new SBASimulatorException("Error initializing " + name, e);
			} catch (InvocationTargetException e) {
				throw new SBASimulatorException("Error initializing " + name
						+ ". Ensure model is correct.", e.getCause());
			}
		}

		SimulationResults simulate(Simulator simulator, double startTime,
				double stopTime, SimulatorParameters simulatorParameters,
				int numberofSamples, String[] trackedSpecies)
				throws SBASimulatorException {
			try {
				Class<?> c = simulatorClass.getSuperclass();
				Method m = c.getMethod("simulate", new Class[] { double.class,
						double.class, SimulatorParameters.class, int.class,
						(new String[] {}).getClass() });
				return (SimulationResults) m.invoke(c.cast(simulator),
						startTime, stopTime, simulatorParameters,
						numberofSamples, trackedSpecies);
			} catch (NoSuchMethodException e) {
				throw new SBASimulatorException("Error running simulation for "
						+ name + ". Cannot find class.", e);
			} catch (IllegalArgumentException e) {
				throw new SBASimulatorException("Error running simulation.", e);
			} catch (IllegalAccessException e) {
				throw new SBASimulatorException("Error running simulation.", e);
			} catch (InvocationTargetException e) {
				throw new SBASimulatorException("Error running simulation. "
						+ e.getCause().getMessage(), e.getCause());
			}
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Accessor method to allow quick generation from previously written cmdl
	 * files. It is the responsibility of the programmer to ensure the string is
	 * a valid instance of cmdl. No sanity checking is possible using this
	 * method.
	 * 
	 * @param cmdlModel
	 * @return
	 */
	public static SBAtoISBJava generateModel(String cmdlModel) {
		SBAtoISBJava newModel = new SBAtoISBJava();
		ModelBuilderCommandLanguage mb = new ModelBuilderCommandLanguage();
		try {
			newModel.model = mb.buildModel(new ByteArrayInputStream(cmdlModel
					.getBytes()), new IncludeHandler());
		} catch (IOException e) {
			throw new IllegalArgumentException("Error parsing cmdl model. "
					+ e.getMessage());
		} catch (InvalidInputException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		newModel.cmdl = true;
		newModel.map = new Mapping();
		newModel.map.originalRepresentation = "Species";
		for (String s : newModel.model.getOrderedSpeciesNamesArray())
			newModel.map.labelled.put(s, s);
		return newModel;
	}

	Algorithm algorithm;

	boolean apparentRate, cmdl;

	Mapping map;

	Model model;

	int numberOfSamples;

	Map<String, Number> sbaPopulations;

	Map<String, RateNode> sbaRates;

	Set<SBAReaction> sbaReactions;

	SimulationResults simulationResults;

	Simulator simulator;

	SimulationController simulatorController;

	SimulatorParameters simulatorParameters;

	SimulationProgressReporter simulatorProgress;

	double startTime, stopTime;

	String[] trackedSpecies;

	Set<String> zeroedSpecies; // for writing cmdl

	private SBAtoISBJava() {
		simulatorProgress = new SimulationProgressReporter();
		simulatorController = new SimulationController();
		trackedSpecies = null;
		startTime = stopTime = 0.00;
		numberOfSamples = 3;
		cmdl = false;
		simulationResults = null;
		simulatorParameters = null;
		simulator = null;
		zeroedSpecies = null;
		model = null;
		apparentRate = true;
	}

	public SBAtoISBJava(SBAInterface pepaModel) {
		this();
		this.sbaRates = pepaModel.getRates();
		this.sbaReactions = pepaModel.getReactions();
		map = pepaModel.getMapping();
		HashSet<String> tHashSet = new HashSet<String>();
		for (SBAReaction r : sbaReactions)
			tHashSet.add(r.name);
		if (tHashSet.size() < sbaReactions.size())
			throw new IllegalArgumentException(
					"Reactions names must be unique.");
		this.sbaPopulations = pepaModel.getPopulations();
	}

	/**
	 * Allows simulation to be cancelled. Wrapper for ISBJava code.
	 * 
	 */
	public void cancelSimulation() {
		simulatorController.setCancelled(true);
	}

	public synchronized void generateISBJavaModel(String modelName,
			boolean apparentRate) {
		if (cmdl)
			throw new IllegalStateException(
					"Cannot generate model when loading from cmdl file.");
		this.apparentRate = apparentRate;
		model = new Model(modelName);
		Compartment compartment = new Compartment("main");
		zeroedSpecies = new HashSet<String>();
		model
				.setReservedSymbolMapper(new ReservedSymbolMapperChemCommandLanguage());
		// Create Species with non-zero populations
		Species tSpecies;
		HashSet<String> usedIDs = new HashSet<String>();
		Map<String, Species> speciesMap = new HashMap<String, Species>();
		for (Map.Entry<String, Number> me : sbaPopulations.entrySet()) {
			tSpecies = new Species(me.getKey(), compartment);
			tSpecies.setSpeciesPopulation(me.getValue().doubleValue());
			speciesMap.put(me.getKey(), tSpecies);
			usedIDs.add(me.getKey());
		}
		// Create Parameters
		CompiledRate rate;
		for (Map.Entry<String, RateNode> e : sbaRates.entrySet()) {
			rate = CompiledRate.compileRate(e.getValue(), null);
			if (rate.isExpression())
				model.addParameter(new Parameter(e.getKey(), new Expression(
						rate.toString())));
			else
				model.addParameter(new Parameter(e.getKey(), rate.toDouble()));
			usedIDs.add(e.getKey());
		}
		// Create Reactions
		Reaction tReaction;
		for (SBAReaction reaction : sbaReactions) {
			tReaction = new Reaction(reaction.name);
			for (SBAComponent component : reaction.reactants) {
				tSpecies = speciesMap.get(component.name);
				if (tSpecies == null) {
					tSpecies = new Species(component.name, compartment);
					tSpecies.setSpeciesPopulation(0.00);
					speciesMap.put(component.name, tSpecies);
					zeroedSpecies.add(component.name);
				}
				tReaction.addReactant(tSpecies, component.stoichiometry,
						!(component.catalyst || component.inhibitor));
			}
			for (SBAComponent component : reaction.products) {
				tSpecies = speciesMap.get(component.name);
				if (tSpecies == null) {
					tSpecies = new Species(component.name, compartment);
					tSpecies.setSpeciesPopulation(0.00);
					speciesMap.put(component.name, tSpecies);
					zeroedSpecies.add(component.name);
				}
				tReaction.addProduct(tSpecies, component.stoichiometry);
			}
			rate = (apparentRate ? getApparentRate(reaction)
					: getMassActionRate(reaction));
			tReaction.setRate(rate.returnAsValue());
			model.addReaction(tReaction);
		}
		System.err.println(model.toString());
	}

	private CompiledRate getApparentRate(SBAReaction reaction) {
		return reaction.overall;
		/*
		 * Map<String, RateNode> rates = (instantiate ? sbaRates : null);
		 * LinkedList<SBAComponent> passive = new LinkedList<SBAComponent>();
		 * LinkedList<SBAComponent> active = new LinkedList<SBAComponent>();
		 * for (SBAComponent c : reaction.reactants) if
		 * (CompiledRate.isPassive(c)) passive.add(c); else active.add(c);
		 * CompiledRate rate, compiledActive, compiledPassive = null,
		 * compiledRatio = null; SBAComponent sbaComponent; for(SBAComponent c :
		 * passive) { if(c.apparentRates != null) { rate =
		 * CompiledRate.compileRate(c, rates); if(compiledRatio == null)
		 * compiledRatio = CompiledRate.ratioRate(c, rates).op(Operator.DIV,
		 * rate); else compiledRatio = compiledRatio.op(Operator.MULT,
		 * CompiledRate.ratioRate(c, rates).op(Operator.DIV, rate)); } rate =
		 * new CompiledRate(c); if(compiledPassive == null) compiledPassive =
		 * CompiledRate.theta(rate); else compiledPassive =
		 * compiledPassive.op(Operator.MULT, CompiledRate.theta(rate)); } //
		 * Must be at least one active component to define rate sbaComponent =
		 * active.remove(); compiledActive =
		 * CompiledRate.compileRate(sbaComponent.rate, rates);
		 * if(compiledPassive == null && active.size() == 0) {
		 * if(compiledActive.isExpression()) compiledActive =
		 * compiledActive.op(Operator.MULT, new CompiledRate(sbaComponent));
		 * return compiledActive; } compiledActive =
		 * compiledActive.op(Operator.MULT, new CompiledRate(sbaComponent));
		 * if(sbaComponent.apparentRates != null) { if(compiledRatio == null)
		 * compiledRatio = CompiledRate.ratioRate(sbaComponent,
		 * rates).op(Operator.DIV, compiledActive); else compiledRatio =
		 * compiledRatio.op(Operator.MULT, CompiledRate.ratioRate(sbaComponent,
		 * rates).op(Operator.DIV, compiledActive)); } for (SBAComponent c :
		 * active) { rate = CompiledRate.compileRate(c, rates); compiledActive =
		 * CompiledRate.min(compiledActive, rate); if(c.apparentRates != null) {
		 * if(compiledRatio == null) compiledRatio = CompiledRate.ratioRate(c,
		 * rates).op(Operator.DIV, rate); else compiledRatio =
		 * compiledRatio.op(Operator.MULT, CompiledRate.ratioRate(c,
		 * rates).op(Operator.DIV, rate)); } } if(compiledRatio != null)
		 * compiledActive = compiledRatio.op(Operator.MULT, compiledActive);
		 * if(compiledPassive != null) compiledActive =
		 * compiledPassive.op(Operator.MULT, compiledActive); return
		 * compiledActive;
		 */
	}

	public Mapping getMapping() {
		return map;
	}

	private CompiledRate getMassActionRate(SBAReaction reaction) {
		// Map<String, RateNode> rates = (instantiate ? sbaRates : null);
		LinkedList<SBAComponent> reactants = new LinkedList<SBAComponent>();
		LinkedList<SBAComponent> catalysts = new LinkedList<SBAComponent>();
		LinkedList<SBAComponent> inhibitors = new LinkedList<SBAComponent>();
		if (reaction.reactants.size() == 0 && reaction.products.size() == 1) {
			// creation from unspecified source
			return CompiledRate.compileRate(reaction.products.getFirst().rate,
					null);
		}
		for (SBAComponent c : reaction.reactants)
			if (c.isCatalyst())
				catalysts.add(c);
			else if (c.isInhibitor())
				inhibitors.add(c);
			else
				reactants.add(c);
		CompiledRate rate = null, nextRate;
		for (SBAComponent c : reactants) {
			if (CompiledRate.isPassive(c))
				continue;
			else {
				nextRate = CompiledRate.compileRate(c.rate, null);
				if (rate == null)
					rate = nextRate;
				else if (!rate.equals(nextRate))
					throw new IllegalStateException(
							"Non-equal rates in reaction " + reaction.getName()
									+ ".");
			}
		}
		for (SBAComponent c : catalysts) {
			if (CompiledRate.isPassive(c))
				continue;
			else {
				nextRate = CompiledRate.compileRate(c.rate, null);
				if (rate == null)
					rate = nextRate;
				else if (!rate.equals(nextRate))
					throw new IllegalStateException(
							"Non-equal rates in reaction " + reaction.getName()
									+ ".");
			}
		}
		if (rate == null)
			throw new IllegalStateException("Undefined rate for reaction "
					+ reaction.getName() + ".");
		if (inhibitors.size() == 0 && !rate.isExpression())
			return rate; // mass action via numerical value in ISBJava
		for (SBAComponent c : reactants) {
			nextRate = new CompiledRate(c);
			if (c.getStoichiometry() > 1)
				nextRate = CompiledRate.pow(nextRate, c.getStoichiometry());
			rate = rate.op(Operator.MULT, nextRate);
		}
		for (SBAComponent c : catalysts) {
			nextRate = new CompiledRate(c);
			if (c.getStoichiometry() > 1)
				nextRate = CompiledRate.pow(nextRate, c.getStoichiometry());
			rate = rate.op(Operator.MULT, nextRate);
		}
		for (SBAComponent c : inhibitors) {
			nextRate = new CompiledRate(c);
			if (c.getStoichiometry() > 1)
				nextRate = CompiledRate.pow(nextRate, c.getStoichiometry());
			nextRate = nextRate.op(Operator.DIV, CompiledRate.compileRate(
					c.rate, null));
			nextRate = (new CompiledRate(1)).op(Operator.PLUS, nextRate);
			nextRate = (new CompiledRate(1)).op(Operator.DIV, nextRate);
			rate = rate.op(Operator.MULT, nextRate);
		}
		return rate;
	}

	public Solver[] getPermissibleSolvers() {
		Solver[] solvers = Solver.values();
		ArrayList<Solver> pSolvers = new ArrayList<Solver>();
		for (Solver solver : solvers)
			if (solver.getType().equals(OptionsMap.SolverType.ODE)
					|| solver.getType()
							.equals(OptionsMap.SolverType.Stochastic))
				pSolvers.add(solver);
		return pSolvers.toArray(new Solver[] {});
	}

	public synchronized void initialiseSimulator(OptionsMap options)
			throws SBASimulatorException {
		if (model == null)
			throw new IllegalStateException("ISBJava model not generated.");
		// Initialise solver and simulator
		algorithm = Algorithm.getAlgorithm((OptionsMap.Solver) options
				.getValue(OptionsMap.Parameter.Solver));
		if (algorithm == null)
			throw new IllegalArgumentException(((OptionsMap.Solver) options
					.getValue(OptionsMap.Parameter.Solver))
					.getDescriptiveName()
					+ " is not an acceptable solver.");
		simulator = algorithm.getSimulator();
		simulatorParameters = algorithm.getSimulatorParameters(simulator);
		algorithm.initializeSimulator(simulator, model);
		simulator.setProgressReporter(simulatorProgress);
		simulator.setController(simulatorController);
		// Set and check parameters
		startTime = (Double) options.getValue(OptionsMap.Parameter.Start_Time);
		stopTime = (Double) options.getValue(OptionsMap.Parameter.Stop_Time);
		if (startTime < 0.00 || startTime >= stopTime)
			throw new IllegalArgumentException(
					"Start time < 0.00 || start time >= stop time.");
		double tDouble;
		int tInt;
		tDouble = (Double) options.getValue(OptionsMap.Parameter.Step_Size);
		if (tDouble <= 0.00)
			throw new IllegalArgumentException(OptionsMap.Parameter.Step_Size
					.toString()
					+ " (=" + tDouble + ") must be greater than 0.00.");
		simulatorParameters.setStepSizeFraction(tDouble);
		tInt = (Integer) options
				.getValue(OptionsMap.Parameter.Independent_Replications);
		if (tInt < 1)
			throw new IllegalArgumentException(
					"#Replications must be greater than 0.");
		simulatorParameters.setEnsembleSize(tInt);
		tDouble = (Double) options
				.getValue(OptionsMap.Parameter.Confidence_Interval);
		if (tDouble <= 0.00 || tDouble >= 1.00)
			throw new IllegalArgumentException(
					"Confidence interval must be a value between 0.00 and 1.00 (exclusive)");
		simulatorParameters.setConfidenceInterval(tDouble);
		tInt = (Integer) options.getValue(OptionsMap.Parameter.Data_Points);
		if (tInt < 3)
			throw new IllegalArgumentException(
					"#Data points must be greater than 2.");
		numberOfSamples = tInt;
		tDouble = (Double) options
				.getValue(OptionsMap.Parameter.Relative_Error);
		if (tDouble <= 0.00)
			throw new IllegalArgumentException(
					OptionsMap.Parameter.Relative_Error.toString() + " (="
							+ tDouble + ") must be greater than 0.00.");
		simulatorParameters.setMaxAllowedRelativeError(tDouble);
		tDouble = (Double) options
				.getValue(OptionsMap.Parameter.Absolute_Error);
		if (tDouble <= 0.00)
			throw new IllegalArgumentException(
					OptionsMap.Parameter.Absolute_Error.toString() + " (="
							+ tDouble + ") must be greater than 0.00.");
		simulatorParameters.setMaxAllowedAbsoluteError(tDouble);
		trackedSpecies = (String[]) options
				.getValue(OptionsMap.Parameter.Components);
		if (trackedSpecies.length == 0)
			throw new IllegalArgumentException(OptionsMap.Parameter.Components
					.toString()
					+ " must contain at least one component.");
		HashSet<String> knownSpecies = new HashSet<String>(Arrays.asList(model
				.getOrderedSpeciesNamesArray()));
		for (String component : trackedSpecies)
			if (!knownSpecies.contains(component)) {
				trackedSpecies = null;
				throw new IllegalArgumentException(
						OptionsMap.Parameter.Components.toString() + ": "
								+ component + " is not a valid selection.");
			}
	}

	/**
	 * Returns status of simulator, has the simulation finished or not. Wrappe
	 * for ISBJava code.
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return simulatorProgress.getSimulationFinished();
	}

	/**
	 * Returns percentage complete for the current simulation as an integer.
	 * Wrapper for ISBJava code.
	 * 
	 * @return
	 */
	public int percentageComplete() {
		return (int) (simulatorProgress.getFractionComplete() * 100);
	}

	public synchronized Results runModel() throws SBASimulatorException {
		return runModel(null);
	}

	public synchronized Results runModel(final IProgressMonitor monitor)
			throws SBASimulatorException {
		if (stopTime <= startTime)
			throw new IllegalStateException(
					"Stop time must be greater than start time.");
		simulatorProgress.setSimulationFinished(false);
		Thread monitorController = null;
		if (monitor != null) {
			monitorController = new Thread() {
				public void run() {
					try {
						int SCALING_UNIT = 100;
						int previous = 0, current = 0;
						monitor.beginTask(SCALING_UNIT);
						while (!simulatorProgress.getSimulationFinished()) {
							if (monitor.isCanceled())
								simulatorController.setCancelled(true);
							simulatorProgress.waitForUpdate();
							current = (int) (simulatorProgress
									.getFractionComplete() * 100);
							monitor.worked(current - previous);
							previous = current;
						}
					} finally {
						monitor.done();
					}
				}
			};
			monitorController.start();
		}
		try {
			simulationResults = algorithm.simulate(simulator, startTime,
					stopTime, simulatorParameters, numberOfSamples,
					trackedSpecies);
		} catch (SBASimulatorException e) {
			if (monitorController != null)
				monitorController.interrupt();
			throw e;
		}
		return new Results(simulationResults);
	}

	/**
	 * Wrapper for ISBJava code.
	 * 
	 */
	public void waitForUpdate() {
		simulatorProgress.waitForUpdate();
	}

	public String writeCMDL() {
		if (cmdl)
			throw new IllegalStateException(
					"Cannot write cmdl file when model generated from cmdl file.");
		if (model == null)
			throw new NullPointerException(
					"Must generate ISBJava model to write CMDL file.");
		StringBuilder cmdl = new StringBuilder();
		cmdl.append("//Rates").append(term);
		String[] sA = new String[sbaRates.size()];
		sbaRates.keySet().toArray(sA);
		Arrays.sort(sA);
		for (String name : sA) {
			cmdl.append(name).append(" = ");
			cmdl.append(CompiledRate.toString(sbaRates.get(name)));
			cmdl.append(";").append(term);
		}
		HashMap<String, String> completeNameMapping = new HashMap<String, String>();
		Mapping tMapping = map;
		while (tMapping.previous != null)
			tMapping = tMapping.previous;
		while (tMapping != null) {
			completeNameMapping.putAll(tMapping.labelled);
			completeNameMapping.putAll(tMapping.unlabelled);
			tMapping = tMapping.next;
		}
		cmdl.append(term).append("//Population sizes").append(term);
		sA = new String[sbaPopulations.size()];
		sbaPopulations.keySet().toArray(sA);
		Arrays.sort(sA);
		for (String name : sA) {
			cmdl.append(name).append(" = ");
			cmdl.append(sbaPopulations.get(name)).append("; // ");
			cmdl.append(completeNameMapping.get(name)).append(term);
		}
		cmdl.append(term).append("//Reactions").append(term);
		HashMap<String, SBAReaction> tHashMap = new HashMap<String, SBAReaction>();
		for (SBAReaction sbaReaction : sbaReactions)
			tHashMap.put(sbaReaction.name, sbaReaction);
		sA = new String[tHashMap.size()];
		tHashMap.keySet().toArray(sA);
		Arrays.sort(sA);
		SBAReaction tSBAReaction;
		CompiledRate rate;
		for (String name : sA) {
			tSBAReaction = tHashMap.get(name);
			cmdl.append(tSBAReaction.toCMDL()).append(", ");
			rate = (apparentRate ? getApparentRate(tSBAReaction)
					: getMassActionRate(tSBAReaction));
			if (rate.isExpression())
				cmdl.append("[").append(rate.toString()).append("]");
			else
				cmdl.append(rate.toString());
			cmdl.append(";").append(term);
		}
		return cmdl.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String writeMatlab() {
		String functionName = "PEPA_model_ODEs";
		StringBuilder m = new StringBuilder(), clearList = new StringBuilder();
		Map<String, String> mMap = new HashMap<String, String>();
		Map<String, Integer> cMap = new HashMap<String, Integer>();
		Set<String> set = new HashSet<String>();
		// m file
		m.append("% Rates").append(term);
		String[] sA = new String[sbaRates.size()];
		sbaRates.keySet().toArray(sA);
		Arrays.sort(sA);
		String s;
		// variable names sanitization
		for(String name : sA) {
			s = matlabName(name);
			if(cMap.containsKey(s)) {
				cMap.put(s, cMap.get(s).intValue() + 1);
				set.remove(s);
			} else {
				cMap.put(s, 1);
				set.add(s);
			}
			mMap.put(name, s);
		}
		// species names sanitization
		sA = new String[sbaPopulations.size()];
		sbaPopulations.keySet().toArray(sA);
		Arrays.sort(sA);
		for(String name : sA)
			if(sbaPopulations.get(name).doubleValue() != 0.00) {
				s = matlabName(name);
				if(cMap.containsKey(s)) {
					cMap.put(s, cMap.get(s).intValue() + 1);
					set.remove(s);
				} else {
					cMap.put(s, 1);
					set.add(s);
				}
				mMap.put(name, s);
			}
		for(String name : set)
			cMap.remove(name);
		for(String name : cMap.keySet())
			cMap.put(name, 1);
		sA = new String[sbaRates.size()];
		sbaRates.keySet().toArray(sA);
		Arrays.sort(sA);
		int i;
		clearList.append("clear");
		for(String name : sA) {
			s = mMap.get(name);
			m.append(s).append(" = ");
			clearList.append(" ").append(s);
			s = CompiledRate.toString(sbaRates.get(name));
			s = sanitiseExpression(s, mMap);
			m.append(s);
			m.append(";").append(" % ").append(name).append(term);
		}
		m.append(term).append("% non-zero components").append(term);
		sA = new String[sbaPopulations.size()];
		sbaPopulations.keySet().toArray(sA);
		Arrays.sort(sA);
		double d;
		int i2 = 1;
		Set<String> nonZero = new HashSet<String>();
		for(String name : sA) {
			d = sbaPopulations.get(name).doubleValue();
			if(d != 0.00) {
				s = mMap.get(name);
				if(cMap.containsKey(s)) {
					i = cMap.get(s);
					cMap.put(s, i+1);
					s += Integer.toString(i);
					mMap.put(name, s);
				}
				m.append(s).append(" = ").append(d);
				m.append(";").append(" % ").append(name).append(term);
				nonZero.add(name);
				clearList.append(" ").append(s);
			}
		}
		clearList.append(" ").append(functionName).append(";");
		m.append(term);
		sA = model.getOrderedSpeciesNamesArray();
		Map<String, Integer> speciesIndex = new HashMap<String, Integer>();
		m.append("% ");
		for(i = 0; i < sA.length; i++) {
			speciesIndex.put(sA[i], i);
			m.append(sA[i]).append(" ");
		}
		m.deleteCharAt(m.length()-1);
		m.append(term);
		m.append("y = [");
		for(String name : sA) {
			if(nonZero.contains(name))
				m.append(mMap.get(name));
			else
				m.append("0.0");
			m.append(" ");
			mMap.put(name, "y(" + (i2++) + ")");
		}
		m.deleteCharAt(m.length()-1);
		m.append("];").append(term).append(term);
		
		// ode generation
		StringBuilder[] odesSB = new StringBuilder[sA.length];
		ArrayList<String>[] odesAL = new ArrayList[sA.length];
		for(i = 0; i < sA.length; i++) {
			odesSB[i] = new StringBuilder();
			odesAL[i] = new ArrayList<String>();
		}
		CompiledRate rate;
		String rateString;
		int index;
		for(SBAReaction sbar : sbaReactions) {
			rate = (apparentRate ? getApparentRate(sbar) : getMassActionRate(sbar));
			rateString = rate.toString();
			if(apparentRate && !rate.isExpression()) {
				for(SBAComponent sbac : sbar.reactants)
					if(sbac.stoichiometry > 1)
						rateString += ("*(" + sbac.name + "^" + sbac.stoichiometry + ")"); 
					else
						rateString += ("*" + sbac.name);
			}
			rateString = sanitiseExpression(rateString, mMap);
			s = " + " + rateString;
			for(SBAComponent sbac : sbar.reactants) {
				index = speciesIndex.get(sbac.name);
				i = odesAL[index].indexOf(s);
				if(i == -1)
					odesAL[index].add(" - " + rateString);
				else
					odesAL[index].remove(i);
			}
			s = " - " + rateString;
			for(SBAComponent sbac : sbar.products) {
				index = speciesIndex.get(sbac.name);
				i = odesAL[index].indexOf(s);
				if(i == -1)
					odesAL[index].add(" + " + rateString);
				else
					odesAL[index].remove(i);
			}
		}
		for(i = odesSB.length-1; i >= 0; i--) {
			for(String entry : odesAL[i])
				odesSB[i].append(entry);
			if(odesSB[i].length() == 0)
				odesSB[i].append("0");
		}
		// _ODE.m file
		m.append(functionName).append(" = @(t,y) [");
		if(odesSB[0].length() > 3 && odesSB[0].substring(0, 3).equals(" + "))
			odesSB[0].delete(0, 3);
		else if(odesSB[0].length() > 3 && odesSB[0].substring(0, 3).equals(" - "))
			odesSB[0].replace(0, 3, "-");
		m.append(odesSB[0].toString()).append(";...").append(term);
		for(i = 1; i < odesSB.length; i++) {
			if(odesSB[i].length() > 3 && odesSB[i].substring(0, 3).equals(" + "))
				odesSB[i].delete(0, 3);
			else if(odesSB[i].length() > 3 && odesSB[i].substring(0, 3).equals(" - "))
				odesSB[i].replace(0, 3, "-");
			m.append("                          ").append(odesSB[i].toString());
			if(i == odesSB.length - 1)
				m.append("];").append(term).append(term);
			else
				m.append(";...").append(term);
		}
		m.append("[t,y] = ode15s(").append(functionName).append(",[");
		m.append(startTime).append(",");
		if(stopTime == startTime)
			m.append(OptionsMap.Parameter.Stop_Time.getDefault());
		else
			m.append(stopTime);
		m.append("],y);").append(term).append(term);
		m.append(clearList);
		return m.toString();
	}
	
	private static String matlabName(String name) {
		StringBuilder sb = new StringBuilder(name);
		if(name.startsWith("\"") && name.endsWith("\"")) {
			for(char c : name.substring(1, name.length() - 1).toCharArray())
				if(Character.isLetterOrDigit(c))
					sb.append(c);
				else
					break;
		}
		return sb.insert(0, "P_").toString();
	}
	
	private static final String sanitiseExpression(String expression, Map<String, String> mMap) {
		ArrayList<String> splitString = new ArrayList<String>();
		String[] sA = expression.split("\"");
		String s;
		int i = 0;
		if(!expression.startsWith("\""))
			i++;
		while(i < sA.length) {
			sA[i] = mMap.get("\"" + sA[i] + "\"");
			i += 2;
		}
		for(String s1 : sA)
			splitString.add(s1);
		String delim2 = null;
		for(String delim : new String[] {"\\+", "-", "\\*", "/", "\\(", ",", "\\)", "^"}) {
			if(delim.length() > 1)
				delim2 = delim.substring(delim.length()-1, delim.length());
			else
				delim2 = delim;
			for(i = 0; i < splitString.size(); i++) {
				s = splitString.get(i);
				sA = s.split(delim);
				if(sA.length > 1 || sA[0].length() != s.length()) {
					splitString.remove(i);
					splitString.add(i++, sA[0]);
					for(int i3 = 1; i3 < sA.length; i3++) {
						splitString.add(i++, delim2);
						splitString.add(i++, sA[i3]);
					}
					// consecutive delimiters at the end of a string are lost
					while(s.endsWith(delim2)) {
						splitString.add(i++, delim2);
						s = s.substring(0, s.length() - delim2.length());
					}
					i--;
				}
			}
		}
		for(i = 0; i < splitString.size(); i++) {
			s = splitString.get(i);
			if(mMap.containsKey(s)) {
				splitString.remove(i);
				splitString.add(i, mMap.get(s));
			}
		}
		StringBuilder sb = new StringBuilder();
		for(String s1 : splitString)
			sb.append(s1);
		return sb.toString();
	}
}
