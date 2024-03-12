/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.utilisationview;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import uk.ac.ed.inf.pepa.ctmc.LocalState;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;

public class UtilisationTreeLabelProvider extends LabelProvider {
	
	public Image getImage(Object element) {
		return null;
	}
	
	public String getText(Object element) {
		if (element instanceof SequentialComponent)
			return ((SequentialComponent) element).getName() + " (" +
			((SequentialComponent) element).getLocalStates().length + " local states)";
		if (element instanceof LocalState) {
			LocalState state = (LocalState) element;
			return state.getName() + " = " + state.getUtilisation();
		}
		return "? [UtilisationTreeLabelProvider]";
	}

}
