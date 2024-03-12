/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.filters;

import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;

public class ActionFilter implements IStateSpaceFilter {

	private boolean fIncoming;

	private String fActionType;
	
	public ActionFilter(String actionType,
			boolean incoming) {
		fActionType = actionType;
		fIncoming = incoming;
	}
	
	public IFilterRunner getRunner(final IStateSpace ss) {
		
		return new IFilterRunner() {

			public boolean select(int state) {
				int[] indices;
				indices = fIncoming ? ss.getIncomingStateIndices(state)
						: ss.getOutgoingStateIndices(state);
				for (int i : indices) {
					String[] actions = null;
					if (fIncoming)
						actions = ss.getAction(i, state);
					else
						actions = ss.getAction(state, i);
					for (String action : actions)
						if (fActionType.equals(action))
							return true;
				}
				return false;
			}
			
		};
	}

}
