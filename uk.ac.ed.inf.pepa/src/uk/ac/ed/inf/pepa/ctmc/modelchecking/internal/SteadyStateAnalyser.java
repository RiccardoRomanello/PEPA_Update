/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;
import java.util.HashSet;

import no.uib.cipr.matrix.MatrixSingularException;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.KroneckerStateSpace;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ModelCheckingException;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ProbabilityInterval;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;

public class SteadyStateAnalyser {

	private KroneckerStateSpace stateSpace;
	private SequentialAbstraction[] abstraction;
	
	private AbstractCTMC abstractCTMC;
	private CSLPropertyManager propertyManager;
	
	private ModelCheckingLog eventLog;
	
	public SteadyStateAnalyser(KroneckerStateSpace stateSpace, AbstractCTMC abstractCTMC, CSLPropertyManager propertyManager,
			                   SequentialAbstraction[] abstraction, ModelCheckingLog eventLog) {
		this.stateSpace = stateSpace;
		this.abstraction = abstraction;
		this.abstractCTMC = abstractCTMC;
		this.propertyManager = propertyManager;
		this.eventLog = eventLog;
	}
	
	public ProbabilityInterval checkSteadyState(CSLAbstractStateProperty property, OptionMap optionMap, IProgressMonitor monitor) throws ModelCheckingException {
		int numComponents = stateSpace.getDisplayModel().getNumComponents();
		if (!isAbstracted()) {
			// We don't construct a compositional property - just directly construct a set of states
			AbstractCTMCProperty trueStates = new AbstractCTMCProperty(abstractCTMC);
			for (AbstractCTMCState state : abstractCTMC) {
				AbstractBoolean value = propertyManager.test(property, state);
				if (value == AbstractBoolean.TRUE || value == AbstractBoolean.MAYBE) {
					trueStates.addState(state);
				} 
			}
			return concreteSteadyStateInterval(trueStates, optionMap, monitor);
		} else {
			ArrayList<HashSet<Short>> maybeTrueSet = new ArrayList<HashSet<Short>>(numComponents);
			ArrayList<HashSet<Short>> maybeFalseSet = new ArrayList<HashSet<Short>>(numComponents);
			for (int i = 0; i < numComponents; i++) {
				maybeTrueSet.add(new HashSet<Short>());
				maybeFalseSet.add(new HashSet<Short>());
			}
			// work out an upper and lower approximation to the states satisfying the property.
			// TODO - we could be cleverer for sets that can't be compositionally specified
			for (AbstractCTMCState state : abstractCTMC) {
				AbstractBoolean value = propertyManager.test(property, state);
				short[] stateID = state.getState();
				//System.out.println(Arrays.toString(stateID) + " = " + value);
				if (value == AbstractBoolean.TRUE) {
					for (int i = 0; i < numComponents; i++) {
						maybeTrueSet.get(i).add(stateID[i]);
					}
				} else if (value == AbstractBoolean.FALSE) {
					for (int i = 0; i < numComponents; i++) {
						maybeFalseSet.get(i).add(stateID[i]);
					}
				} else if (value == AbstractBoolean.MAYBE) {
					for (int i = 0; i < numComponents; i++) {
						maybeTrueSet.get(i).add(stateID[i]);
						maybeFalseSet.get(i).add(stateID[i]);
					}
				}
			}
			short[][] trueProperty = new short[numComponents][];
			short[][] falseProperty = new short[numComponents][];
			boolean isMaybeTrueEmpty = false;
			boolean isMaybeFalseEmpty = false;
			for (int i = 0; i < numComponents; i++) {
				HashSet<Short> maybeTrue = maybeTrueSet.get(i);
				if (maybeTrue.size() == 0) {
					isMaybeTrueEmpty = true;
					continue;
				}
				HashSet<Short> maybeFalse = maybeFalseSet.get(i);
				if (maybeFalse.size() == 0) {
					isMaybeFalseEmpty = true;
					continue;
				}
				ShortArray trueStates = new ShortArray(10);
				ShortArray falseStates = new ShortArray(10);
				for (short j = 0; j < abstraction[i].size(); j++) {
					if (maybeTrue.contains(j)) trueStates.add(j);
					if (maybeFalse.contains(j)) falseStates.add(j);
				}
				trueProperty[i] = trueStates.toArray();
				falseProperty[i] = falseStates.toArray();
			}
			CompositionalProperty upperProperty;
			CompositionalProperty lowerProperty;
			if (isMaybeTrueEmpty) {
				upperProperty = new CompositionalProperty(abstraction, false);
				lowerProperty = upperProperty;
			} else if (isMaybeFalseEmpty) {
				upperProperty = new CompositionalProperty(abstraction, true);
				lowerProperty = upperProperty;
			} else {
				upperProperty = new CompositionalProperty(abstraction, trueProperty);
				lowerProperty = new CompositionalProperty(abstraction, falseProperty);
			}
			return abstractSteadyStateInterval(new CompositionalPropertyList(lowerProperty), new CompositionalPropertyList(upperProperty), optionMap, monitor);
		}
	}
	
	public ProbabilityInterval checkSteadyState(CompositionalPropertyList property, OptionMap optionMap, IProgressMonitor monitor) throws ModelCheckingException {
		if (!isAbstracted()) {
			return concreteSteadyStateInterval(property, optionMap, monitor);
		} else {
			//System.out.println("Property = \n" + property);
			CompositionalPropertyList lowerProperty = property.complement();
			//System.out.println("Lower Propery = \n" + lowerProperty);
			return abstractSteadyStateInterval(lowerProperty, property, optionMap, monitor);
		}
	}
		
	private ProbabilityInterval concreteSteadyStateInterval(AbstractCTMCProperty property, OptionMap optionMap, IProgressMonitor monitor) throws ModelCheckingException {
		double[] solution = solve(stateSpace, optionMap, monitor);
		double probability = steadyStateProbability(stateSpace, solution, property);
		return new ProbabilityInterval(probability, probability);
	}
	
	private ProbabilityInterval concreteSteadyStateInterval(CompositionalPropertyList properties, OptionMap optionMap, IProgressMonitor monitor) throws ModelCheckingException {
		double[] solution = solve(stateSpace, optionMap, monitor);
		double probability = 0.0;
		for (CompositionalProperty property : properties) {
			if (property.isTrue()) {
				probability = 1;
				break;
			} else if (!property.isFalse()) {
				probability += steadyStateProbability(stateSpace, solution, property.getConcreteProperty());
			}
		}
		properties.unregister();
		//probability = Math.max(1.0, probability);
		return new ProbabilityInterval(probability, probability);
	}
	
	private ProbabilityInterval abstractSteadyStateInterval(CompositionalPropertyList lowerProperty,
			                                                CompositionalPropertyList upperProperty,
			                                                OptionMap optionMap, IProgressMonitor monitor) throws ModelCheckingException {
		double upperProbability = 0.0;
		double lowerProbability = 0.0;
		if (upperProperty.isFalse()) {
			lowerProbability = 0.0;
			upperProbability = 0.0;
		} else if (lowerProperty.isTrue()) {
			lowerProbability = 1.0;
			upperProbability = 1.0;
		} else {
			// We have to separate the property so that _every_ property is a single
			// element (the top of the state space), in order for the bound to be correct
			// TODO - is there a better solution to this? Also the complement can get pretty big...
			if (!upperProperty.isSingleComponent()) {
				upperProperty.unregister();
				upperProperty = upperProperty.split();
			}
			if (!lowerProperty.isSingleComponent()) {
				lowerProperty.unregister();
				lowerProperty = lowerProperty.split();
			}
			
			for (CompositionalProperty property : upperProperty) {
				if (property.isTrue()) {
					upperProbability += 1;
				} else if (!property.isFalse()) {
					KroneckerStateSpace upperStateSpace = stateSpace.getUpperBoundingStateSpace(property);
					double[] upperSolution = solve(upperStateSpace, optionMap, monitor);
					//System.out.println("Upper solution = " + Arrays.toString(upperSolution));
					upperProbability += steadyStateProbability(upperStateSpace, upperSolution, property.getAbstractProperty());
				}
			}
			upperProbability = Math.min(1, upperProbability);
			for (CompositionalProperty property : lowerProperty) {
				if (property.isTrue()) {
					lowerProbability += 1;
				} else if (!property.isFalse()) {
					KroneckerStateSpace lowerStateSpace = stateSpace.getUpperBoundingStateSpace(property);
					double[] lowerSolution = solve(lowerStateSpace, optionMap, monitor);
					//System.out.println("Lower solution = " + Arrays.toString(lowerSolution));
					lowerProbability += steadyStateProbability(lowerStateSpace, lowerSolution, property.getAbstractProperty());
				}
			}
			lowerProbability = Math.max(0, 1 - lowerProbability);
		}
		upperProperty.unregister();
		lowerProperty.unregister();
		return new ProbabilityInterval(lowerProbability, upperProbability);
	}

	private double steadyStateProbability(KroneckerStateSpace stateSpace, double[] solution, short[][] property) throws ModelCheckingException {
		double probability = 0.0;
		for (int i = 0; i < solution.length; i++) {
			short[] state = stateSpace.getSystemState(i);
			//System.out.println("Solution state " + Arrays.toString(state) + " = " + solution[i]);
			boolean isOK = true;
			for (int j = 0; j < state.length; j++) {
				boolean isComponentOK = false;
				for (int k = 0; k < property[j].length; k++) {
					if (property[j][k] == state[j]) {
						isComponentOK = true;
						break;
					}
				}
				isOK = isOK && isComponentOK;
			}
			if (isOK) probability += solution[i];	
		}
		return probability;	
	}
	
	private double steadyStateProbability(KroneckerStateSpace stateSpace, double[] solution, AbstractCTMCProperty property) throws ModelCheckingException {
		double probability = 0.0;
		for (int i = 0; i < solution.length; i++) {
			AbstractCTMCState state = new AbstractCTMCState(stateSpace.getSystemState(i));
			if (property.containsState(state)) {
				probability += solution[i];
			}
			if (probability > 1.0) break;
		}
		return Math.max(1.0, probability);	
	}
	
	private double[] solve(KroneckerStateSpace stateSpace, OptionMap optionMap, IProgressMonitor monitor) throws ModelCheckingException {
		ISolver solver = SolverFactory.createSolver(stateSpace, optionMap);
		try {
			double[] solution = solver.solve(monitor);
			eventLog.addEntry("Solved a CTMC of size " + solution.length);
			return solution;
		} catch (SolverException e) {
			e.printStackTrace();
			throw new ModelCheckingException("Unable to solve the steady state distribution.");
		} catch (MatrixSingularException e) {
			e.printStackTrace();
			throw new ModelCheckingException("Unable to solve the steady state distribution (size: " + stateSpace.size() + ").");
		}
	}
	
	private boolean isAbstracted() {
		boolean isAbstracted = false;
		for (int i = 0; i < abstraction.length; i++) {
			if (abstraction[i].isAbstracted()) {
				isAbstracted = true;
				break;
			}
		}
		return isAbstracted;
	}
	
}
