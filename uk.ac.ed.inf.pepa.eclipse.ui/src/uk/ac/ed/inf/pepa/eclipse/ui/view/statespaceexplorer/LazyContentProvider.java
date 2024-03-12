/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class LazyContentProvider implements ILazyContentProvider {

	private ArrayList<Integer> filteredStates;

	private TableViewer viewer;

	private IProcessAlgebraModel model;
	
	/*
	 * Content provider is initialised with the underlying PEPA
	 * model and the table viewer it is providing contents to.
	 */
	public LazyContentProvider(IProcessAlgebraModel model, TableViewer viewer) {
		this.viewer = viewer;
		this.model = model;
		this.filteredStates = null;
	}
	
	/**
	 * Returns the underlying state space
	 * @return
	 */
	public IStateSpace getStateSpace() {
		return model.getStateSpace();
	}
	
	public boolean isFiltered(int state) {
		if (filteredStates == null) 
			return false;
		else
			return filteredStates.contains(state);
	}

	public void updateElement(int index) {
		if (filteredStates == null)
			viewer.replace(index, index);
		else {
			viewer.replace(filteredStates.get(index), index);
		}
	}

	public void dispose() {
	}
	
	/* 
	 * Input is an arraylist of integer representing state numbers
	 * of filtered element or null if no filter is applied.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// caches the current input
		IStateSpace currentStateSpace = model.getStateSpace();
		filteredStates = (ArrayList<Integer>) newInput;
		if (currentStateSpace != null)
			if (filteredStates == null)
				this.viewer.setItemCount(currentStateSpace.size());
			else
				this.viewer.setItemCount(filteredStates.size());
		else
			this.viewer.setItemCount(0);
	}

}
