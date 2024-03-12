/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer.legacy;

import java.util.LinkedList;


/**
 * Collect information about user's state space walks.
 * <p>
 * The history is reset when either the state space changes or the user chooses
 * to clear it.
 * 
 * @author mtribast
 * 
 */
public class NavigationHistory {

	private LinkedList<IStateModel> visitedStates = new LinkedList<IStateModel>();

	public void reset() {
		visitedStates.clear();
	}

	public void stateVisited(IStateModel state) {
		visitedStates.add(state);
		//System.err.println("Navigation History: " + visitedStates.size());
	}

	public IStateModel[] getHistory() {
		return visitedStates.toArray(new IStateModel[visitedStates.size()]);
	}

}
