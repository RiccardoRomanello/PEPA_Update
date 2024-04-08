/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.model.NamedAction;

public class TransitionLabelProvider extends StateLabelProvider {

	private NavigationDialog dialog;

	private boolean outgoing;

	private boolean displayAction;

	private static final String EMPTY = "";

	public TransitionLabelProvider(LazyContentProvider provider,
			NavigationDialog dialog, boolean displayAction, boolean outgoing) {
		super(provider);
		this.dialog = dialog;
		this.outgoing = outgoing;
		this.displayAction = displayAction;
	}

	public String getColumnText(Object element, int columnIndex) {

		if (columnIndex == 0) {
			if (!displayAction)
				return EMPTY;
			IStateSpace ss = provider.getStateSpace();
			int state = (Integer) element;
			NamedAction[] actions = null;
			if (outgoing)
				actions = ss.getAction(dialog.currentState, state);
			else
				actions = ss.getAction(state, dialog.currentState);
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < actions.length; i++) {
				buf.append(actions[i].prettyPrint());
				if (i != actions.length - 1)
					buf.append(", ");
			}
			return buf.toString();
		} else
			return super.getColumnText(element, columnIndex - 1);
	}

}
