/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.utilisationview;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import uk.ac.ed.inf.pepa.ctmc.LocalState;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;

public class UtilisationTreeContentProvider extends ArrayContentProvider
		implements ITreeContentProvider {

	private static final Comparator<LocalState> LOCAL_STATE_COMPARATOR = 
		new Comparator<LocalState>() {

			public int compare(LocalState arg0, LocalState arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		
	};
	
	public static Object[] NOTHING = new Object[0];
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SequentialComponent) {
			LocalState[] children = ((SequentialComponent) parentElement).getLocalStates();
			Arrays.sort(children, LOCAL_STATE_COMPARATOR);
			return children;
		} else
			return NOTHING;
	}

	public Object getParent(Object element) {
		if (element instanceof LocalState)
			return ((LocalState) element).getSequentialComponent();
		return null;
	}

	public boolean hasChildren(Object element) {
		return (element instanceof SequentialComponent) &&
		 ((SequentialComponent) element).getLocalStates().length > 0;
		 
	}
	
}
