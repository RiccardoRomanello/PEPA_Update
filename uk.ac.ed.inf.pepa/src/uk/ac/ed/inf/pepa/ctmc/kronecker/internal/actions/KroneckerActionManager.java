/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;

/**
 * Kronecker interface for action identifiers - we use abstract identifies
 * in the Kronecker representation, which map onto the action identifiers
 * of the parsed model.
 * 
 * @author msmith
 */
public class KroneckerActionManager {

	private ISymbolGenerator generator;
	
	private ArrayList<Short> syncActions;
	private HashMap<Short, Short> actionIndices;
	private HashMap<Short, ApparentRateCalculator> apparentRateCalculators;
	
	public KroneckerActionManager(ISymbolGenerator generator) {
		this.generator = generator;
		this.syncActions = new ArrayList<Short>();
		this.actionIndices = new HashMap<Short, Short>();
		this.apparentRateCalculators = new HashMap<Short, ApparentRateCalculator>(20);
	}
	
	public short getActionID(short actionIndex) {
		Short actionID = syncActions.get(actionIndex);
		if (actionID == null) {
			return -1;
		} else {
			return actionID;
		}
	}
	
	// TEMP for debugging
	public String getActionName(short actionIndex) {
		if (actionIndex < syncActions.size()) {
			return generator.getActionLabel(syncActions.get(actionIndex)) + "(" + actionIndex + ")";
		} else {
			return "Don't know";
		}
	}
	
	public short getActionIndex(short actionID) {
		Short index = actionIndices.get(actionID);
		if (index == null) {
			return -1;
		} else {
			return index;
		}
	}
	
	// Sets the fact that actionID occurs in some synchronisation set
	public void addSyncAction(short actionID) {
		short actionIndex = (short)syncActions.size();
		syncActions.add(actionID);
		actionIndices.put(actionID, actionIndex);
	}
	
	public ArrayList<Short> getSyncActions() {
		return syncActions;
	}
	
	public void addApparentRateCalculator(short actionIndex, ApparentRateCalculator calculator) {
		apparentRateCalculators.put(actionIndex, calculator);
	}
	
	public ApparentRateCalculator getApparentRateCalculator(short actionIndex) {
		return apparentRateCalculators.get(actionIndex);
	}
	
	public int getNumSyncActions() {
		return syncActions.size();
	}
	
}
