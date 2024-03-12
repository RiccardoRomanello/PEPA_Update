/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;

public class DeriveActionDelegate extends BasicProcessAlgebraModelActionDelegate {
	
	public DeriveActionDelegate() {
		super();
	}
	
	/**
	 * This method is called to update the state of the action either
	 * when the model broadcasts new events or when the active editor changes.
	 * 
	 *
	 */
	public void checkStatus() {
		this.action.setEnabled(this.model.isDerivable());
	}

	public  void run(IAction action) {
		
		//long tic = System.nanoTime();
		ActionCommands.derive(model);
		//long toc = System.nanoTime();
		//PepaLog.logInfo("State space derivation: " + (toc - tic));

	}
	
}
