package uk.ac.ed.inf.pepa.ctmc.derivation.filters;

import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;

public class ActionsFilter implements IStateSpaceFilter {
	private boolean fIncoming;

	private String[] fActionTypes;
	
	public ActionsFilter(String[] actionTypes, boolean incoming) {
		fActionTypes = actionTypes;
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
					for (String action : actions){
						for (String filterAction : fActionTypes){
						  if (filterAction.equals(action))
						     return true;
					    }
					}
				}
				return false;
			}
			
		};
	}

}
