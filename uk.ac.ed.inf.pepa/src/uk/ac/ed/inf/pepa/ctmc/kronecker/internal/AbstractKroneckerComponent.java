/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions.KroneckerActionManager;

public class AbstractKroneckerComponent {
	
	private SequentialAbstraction abstraction;
	
	private AbstractRateMatrix[] syncModel;
	private AbstractRateMatrix   localModel;
	
	public AbstractKroneckerComponent(int componentID, SequentialAbstraction abstraction, KroneckerActionManager actionManager,
			                          AbstractRateMatrix[] syncModel, AbstractRateMatrix localModel) {
		this.abstraction    = abstraction;
		this.syncModel      = syncModel;
		this.localModel     = localModel;
	}
	
	public SequentialAbstraction getAbstraction() {
		return abstraction;
	}
	
	public double getSyncLowerRate(int action, short state) {
		return syncModel[action].getLowerRate(state);
	}
	
	public double getSyncUpperRate(int action, short state) {
		return syncModel[action].getUpperRate(state);
	}
	
	public NextStateInformation nextSyncStates(int action, short state) {
		return syncModel[action].nextStates(state);
	}
	
	public double getLocalLowerRate(short state) {
		return localModel.getLowerRate(state);
	}
	
	public double getLocalUpperRate(short state) {
		return localModel.getUpperRate(state);
	}
	
	public NextStateInformation nextLocalStates(short state) {
		return localModel.nextStates(state);
	}
	
	public boolean isPassiveLoop(int action) {
		return syncModel[action].isEmpty();
	}
	
}
