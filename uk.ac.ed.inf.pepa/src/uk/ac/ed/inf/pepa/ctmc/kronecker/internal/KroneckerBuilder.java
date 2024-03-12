/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.MeasurementData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerActionManager;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerApparentRateVisitor;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerSynchronisationVisitor;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerSystemEquationVisitor;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.SystemComponentInfo;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;

/**
 * This derives a Kronecker representation of the state space.
 * 
 * @author msmith
 *
 */
public class KroneckerBuilder implements IStateSpaceBuilder {

	/**
	 * A copy of the original model
	 */
	private final ISymbolGenerator generator;

	private IStateExplorer explorer;
	
	/**
	 * The system equation - used to determine that the model satisfies the structural requirements
	 * for the Kronecker representation, and to work out which actions are synchronised.
	 */
	private ProcessNode systemEquation;
	
	/**
	 * Maintains mapping between actions used in the model, and internal action indices.
	 */
	private KroneckerActionManager actionManager;
	
	public KroneckerBuilder(IStateExplorer explorer, ISymbolGenerator generator,
			                ProcessNode systemEquation, int productId, IResourceManager manager) {
		this.explorer = explorer;
		this.generator = generator;
		this.systemEquation = systemEquation;
		this.actionManager = new KroneckerActionManager(generator);
	}
	
	private ArrayList<Transition> getTransitions(short processId) {
		return explorer.getData(processId).fFirstStepDerivative;
	}
	
	
	/**
	 *  Explore the system equation, to work out whether the model has the required structure,
	 *  and if so construct information regarding which actions are synchronised
	 *  
	 *  Throws an exception if we are able to build a Kronecker representation.
	 *  For now, we don't deal with hiding or aggregation (TODO).
	 */
	private void exploreSystemEquation() throws DerivationException{
		// (1) Generate labelling of components
		SystemComponentInfo componentInfo = new SystemComponentInfo();
		KroneckerSystemEquationVisitor componentVisitor = new KroneckerSystemEquationVisitor(componentInfo);
		systemEquation.accept(componentVisitor);
		
		if (!componentVisitor.canMakeKronecker()) {
			throw new DerivationException("Cannot construct a Kronecker representation for a model with aggregation or hiding.");
		}
		
		// (2) Generate action information
		KroneckerSynchronisationVisitor choiceVisitor = new KroneckerSynchronisationVisitor(generator, actionManager);
		systemEquation.accept(choiceVisitor);
		
		// (3) Build an apparent rate calculator for every external action type
		for (Short actionID : actionManager.getSyncActions()) {
			KroneckerApparentRateVisitor rateVisitor = new KroneckerApparentRateVisitor(systemEquation, actionID, generator);
			short actionIndex = actionManager.getActionIndex(actionID);
			actionManager.addApparentRateCalculator(actionIndex, rateVisitor.getCalculator());
		}
	}
	
	/**
	 * Explore the state space of a sequential component.
	 */
	private SequentialStateSpace exploreComponent(int component) {		
		SequentialStateSpace componentStates = new SequentialStateSpace();
		
		// Stack of unexplored states
		short[] initialState = generator.getInitialState();
		Queue<Short> queue = new LinkedList<Short>();
		queue.add(initialState[component]);
		componentStates.addState(initialState[component]);
		
		// Explore the local state space
		while (!queue.isEmpty()) {
			short state = queue.remove();
			ArrayList<Transition> found = getTransitions(state);
			for (Transition t : found) {
				short s = t.fTargetProcess[component];
				if (!componentStates.containsState(s)) {
					componentStates.addState(s);
					queue.add(s);
				}
			}
		}
		return componentStates;
	}
	
	/**
	 * Generates a KroneckerStateSpace
	 */
	public IStateSpace derive(boolean allowPassiveRates, IProgressMonitor monitor) throws DerivationException {
		short[] initialState = generator.getInitialState();
		int numComponents = initialState.length;
		
		// Create a new Kronecker model
		//System.out.println("[Kronecker] Deriving Kronecker Model for " + numComponents + " components...");
		KroneckerModel model = new KroneckerModel(numComponents, actionManager, generator);
		
		// Generate the state space of each component
		SequentialStateSpace[] componentStates = new SequentialStateSpace[numComponents];
		for (int component = 0; component < numComponents; component++) {
			componentStates[component] = exploreComponent(component);
		}
		
		// Explore the system equation, to work out what actions are used
		exploreSystemEquation();
		
		// Generate the transition matrix for each component
		for (int component = 0; component < numComponents; component++) {
			// Add a new RateMatrix to the model for each action
			model.initialiseComponent(component, initialState[component], componentStates[component]);
			
			// Add the transitions for each state
			for (int j = 0; j < componentStates[component].size(); j++) {
				short state = componentStates[component].getState(j);
				ArrayList<Transition> found = getTransitions(state);
				for (Transition t : found) {
					model.addTransition(t.fActionId, component, state, t.fTargetProcess[component], t.fRate);
					//System.out.println("Transition: component " + component);
					//System.out.println(state + "===(" + generator.getActionLabel(t.fActionId) + "(" + actionManager.getActionID(action) + ")," + t.fRate + ")==> " + t.fTargetProcess[component]);
				}
			}
		}
		model.normaliseRateMatrices();
		
		//System.out.println("[Kronecker] Generated Kronecker Model");
		return new KroneckerStateSpace(generator, model);
	}

	public MeasurementData getMeasurementData() {
		return null;
	}

}
