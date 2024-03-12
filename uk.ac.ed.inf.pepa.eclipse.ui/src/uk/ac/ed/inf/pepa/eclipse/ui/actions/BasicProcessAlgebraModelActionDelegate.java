/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModelChangedListener;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;


public abstract class BasicProcessAlgebraModelActionDelegate implements IEditorActionDelegate,
		IProcessAlgebraModelChangedListener {

	/**
	 * The underlying PEPA model of the linked editor
	 */
	protected IProcessAlgebraModel model = null;
	
	/**
	 * The reference to the action delegated by this class
	 */
	protected IAction action = null;
	
	protected Shell activeShell = null;
	
	public final void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (model != null)
			model.removeModelChangedListener(this);
		if (action == null || targetEditor == null)
			return;
		
		try {
			model = ((IProcessAlgebraEditor) targetEditor).getProcessAlgebraModel();
		} catch (ClassCastException e) {
			// This may happen if there is an error and Eclipse calls this method
			// with targetEditor an instance of ErrorEditorPart
			return;
		}

		model.addModelChangedListener(this);
		this.action = action;
		// FIXME Check against null
		this.activeShell = targetEditor.getEditorSite().getShell();
		checkStatus();

	}

	public abstract void run(IAction action);
	
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public final void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event) {
		checkStatus();
	}
	
	/**
	 * This is called when the underlying model changes state
	 */
	protected abstract void checkStatus();

}
