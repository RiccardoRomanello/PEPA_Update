/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialOrder;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.ApparentRateCalculator;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerActionManager;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds.LocalComponentRateContext;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds.RateContext;

public class KroneckerComponent {

	private SequentialStateSpace concreteStateSpace;
	private SequentialAbstraction abstractStateSpace;
	
	private int numSyncActions;
	private int componentID;
	
	private RateMatrix[] syncModel;
	private RateMatrix   localModel;
	
	private KroneckerActionManager actionManager;
	
	public KroneckerComponent(int componentID, SequentialStateSpace stateSpace, SequentialAbstraction abstraction, KroneckerActionManager actionManager) {
		this.componentID        = componentID;
		this.concreteStateSpace = stateSpace;
		this.abstractStateSpace = abstraction;
		this.actionManager      = actionManager;
		this.numSyncActions     = actionManager.getNumSyncActions();
		this.syncModel          = new RateMatrix[numSyncActions];
	}
	
	private KroneckerComponent(KroneckerComponent copy) {
		this.concreteStateSpace = copy.concreteStateSpace;
		this.abstractStateSpace = copy.abstractStateSpace;
		this.numSyncActions     = copy.numSyncActions;
		this.componentID        = copy.componentID;
		this.syncModel          = new RateMatrix[numSyncActions];
		this.localModel         = null;
		this.actionManager      = copy.actionManager;
	}
	
	public void initRateMatrices() {
		for (int i = 0; i < numSyncActions; i++) {
			syncModel[i] = new RateMatrix(componentID, concreteStateSpace);
		}
		localModel = new RateMatrix(componentID, concreteStateSpace);
		// Make sure that the local model can do no transitions initially,
		// rather than allowing passive loops.
		localModel.disableTransitions();
	}
	
	/**
	 * The maximum exit rate of any state, assuming a worst case where
	 * every state can perform every action at its maximum rate.
	 * Used to construct a uniformisation constant.
	 */
	public double getMaximumRate() {
		double maxRate = localModel.getMaximumRate();
		for (int i = 0; i < numSyncActions; i++) {
			maxRate += syncModel[i].getMaximumRate();
		}
		return maxRate;
	}
	
	public void normaliseRateMatrices() {
		for (int i = 0; i < numSyncActions; i++) {
			syncModel[i].normalise();
		}
		localModel.normalise();
	}
	
	public void addTransition(short actionIndex, short state1, short state2, double rate) throws DerivationException {
		if (actionIndex >= 0) {
			syncModel[actionIndex].addTransition(state1, state2, actionIndex, rate);
		} else {
			localModel.addTransition(state1, state2, actionIndex, rate);
		}
	}
	
	public void addRateContext(SequentialAbstraction abstraction, SequentialOrder order, RateContext[] context) {
		for (int i = 0; i < numSyncActions; i++) {
			syncModel[i].addRateContext(abstraction, order, context[i]);
		}
	}
	
	public void addEmptyRateContext(RateContext[] context) {
		for (int i = 0; i < numSyncActions; i++) {
			syncModel[i].addEmptyRateContext(context[i]);
		}
	}
	
	/**
	 * Returns a copy of the Kronecker Component, with all rate matrices empty (i.e. passive self loops for all states and actions),
	 * and with isAbstracted set to true.
	 */
	public KroneckerComponent getAbstractCopy(boolean[] boundedComponents) {
		KroneckerComponent copy = new KroneckerComponent(this);
		for (int i = 0; i < numSyncActions; i++) {
			ApparentRateCalculator calculator = actionManager.getApparentRateCalculator((short)i);
			boolean isPassive = calculator.syncWithBound(componentID, boundedComponents);
			copy.syncModel[i] = syncModel[i].getAbstractCopy(isPassive);
			//System.out.println("Component " + componentID + " action " + actionManager.getActionName((short)i) + ": " + isPassive + ", " +  copy.syncModel[i].getRate((short)0));
		}
		copy.localModel = localModel.getAbstractCopy(false);
		return copy;
	}
	
	public KroneckerComponent upperBound(RateContext[] context, SequentialOrder order) {
		return boundComponent(context, order, true);
	}
	
	public KroneckerComponent lowerBound(RateContext[] context, SequentialOrder order) {
		return boundComponent(context, order, false);
	}
	
	private KroneckerComponent boundComponent(RateContext[] context, SequentialOrder order, boolean isUpper) {
		KroneckerComponent bound = new KroneckerComponent(this);
		for (int i = 0; i < numSyncActions; i++) {
			RateMatrix new_matrix;
			if (isUpper) {
				new_matrix = syncModel[i].upperBound(abstractStateSpace, context[i].getRateContext(componentID), order);
			} else {
				new_matrix = syncModel[i].lowerBound(abstractStateSpace, context[i].getRateContext(componentID), order);
			}
			//assert new_matrix.isLumpable(abstractStateSpace);
			bound.syncModel[i] = new_matrix.getLumpedMatrix(abstractStateSpace);
		}
		RateMatrix new_local_matrix;
		LocalComponentRateContext localContext = new LocalComponentRateContext(concreteStateSpace.size(), localModel.getMaximumRate());
		if (isUpper) {
			new_local_matrix = localModel.uniformiseRates().upperBound(abstractStateSpace, localContext, order);
		} else {
			new_local_matrix = localModel.uniformiseRates().lowerBound(abstractStateSpace, localContext, order);
		}
		//assert new_local_matrix.isLumpable(abstractStateSpace);
		bound.localModel = new_local_matrix.getLumpedMatrix(abstractStateSpace);
		//System.out.println(bound.toString());
		return bound;
	}
	
	public AbstractKroneckerComponent abstractComponent() {
		AbstractRateMatrix[] abstractSyncModel = new AbstractRateMatrix[numSyncActions];
		for (int i = 0; i < numSyncActions; i++) {
			abstractSyncModel[i] = syncModel[i].getAbstractMatrix(abstractStateSpace);
		}
		AbstractRateMatrix abstractLocalModel = localModel.getAbstractMatrix(abstractStateSpace);
		return new AbstractKroneckerComponent(componentID, abstractStateSpace, actionManager, abstractSyncModel, abstractLocalModel);
	}
	
	private RateMatrix getSyncMatrix(int action) {
		assert action >= 0 && action < numSyncActions;
		return syncModel[action];
	}

	private RateMatrix getLocalMatrix() {
		return localModel;
	}
	
	public boolean isPassiveLoop(int action) {
		return getSyncMatrix(action).isEmpty();
	}
	
	public double getSyncRate(int action, short state) {
		return getSyncMatrix(action).getRate(state);
	}
	
	public double getLocalRate(short state) {
		return getLocalMatrix().getRate(state);
	}
	
	public StateDistribution nextSyncStates(int action, short state) {
		return getSyncMatrix(action).nextStates(state);
	}
	
	public StateDistribution nextLocalStates(short state) {
		return getLocalMatrix().nextStates(state);
	}
	
	public StateDistribution prevSyncStates(int action, short state) {
		return getSyncMatrix(action).prevStates(state);
	}
	
	public StateDistribution prevLocalStates(short state) {
		return getLocalMatrix().prevStates(state);
	}
	
	public SequentialAbstraction getAbstraction() {
		return abstractStateSpace;
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < numSyncActions; i++) {
			s += "Action " + actionManager.getActionName((short)i) + ":\n";
			s += syncModel[i].toString() + "\n";
		}
		s += "Local Actions:\n" + localModel.toString() + "\n";
		return s;
	}
	
}
