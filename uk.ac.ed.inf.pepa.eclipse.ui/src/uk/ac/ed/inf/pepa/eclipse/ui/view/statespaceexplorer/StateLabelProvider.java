/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;

/**
 * Label provider for state space tables.
 * @author mtribast
 *
 */
public class StateLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	// private final Image error =
	// ImageManager.getInstance().getImage(ImageManager.WARNING_SMALL);
	private static final String EMPTY = "";
	
	private static final String NA = "-";
	protected LazyContentProvider provider;
	
	/*
	 * State label provider is initialised with the lazy content
	 * provider of the viewer.
	 *  
	 * @param provider
	 */
	public StateLabelProvider(LazyContentProvider provider) {
		this.provider = provider;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		/*
		 * IStateModel e = (IStateModel) element; if (columnIndex == 0 &&
		 * e.getProblem() != null) return error;
		 */
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		int state = (Integer) element;
		if (columnIndex == 0)
			return (state + 1) + EMPTY;
		IStateSpace input = provider.getStateSpace();
		int nsc = input.getNumberOfSequentialComponents(state);
		if (columnIndex > 0
				&& columnIndex <= nsc)
			return input.getLabel(state, columnIndex - 1);
		if (columnIndex > nsc && columnIndex < input.getMaximumNumberOfSequentialComponents() + 1)
			return NA;
		if (columnIndex == input.getMaximumNumberOfSequentialComponents() + 1)
			return EMPTY + input.getSolution(state);
		return null;
	}

}
