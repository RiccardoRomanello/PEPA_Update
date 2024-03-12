/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter;

public class RuleLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		AbstractConfigurableStateSpaceFilter model = (AbstractConfigurableStateSpaceFilter) element;
		return model.getLabel();
	}
}