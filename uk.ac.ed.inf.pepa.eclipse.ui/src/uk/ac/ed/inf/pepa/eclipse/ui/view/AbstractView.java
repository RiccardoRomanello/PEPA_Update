/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

import uk.ac.ed.inf.pepa.eclipse.core.*;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;

/**
 * This abstract Eclipse UI View provides common functionalities for
 * read-only views that have to be linked to the active editor.
 *
 * @author mtribast
 * 
 */
public abstract class AbstractView extends ViewPart {

	private ListenerMix fListener;

	protected IProcessAlgebraEditor fEditor;

	/**
	 * Utility method returning the currently active editor
	 * 
	 * @return the currently active editor, null if none
	 */
	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = PepaCore.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}

	private static class ListenerMix implements IPartListener2,
			IProcessAlgebraModelChangedListener {

		private AbstractView fView;

		public ListenerMix(AbstractView view) {
			fView = view;
		}

		public void partActivated(IWorkbenchPartReference partRef) {
			// we have to track the active editor
			if (partRef instanceof IEditorReference) {
				IEditorReference editorRef = (IEditorReference) partRef;
				IEditorPart part = editorRef.getEditor(true);
				fView.setInput(part);
			}
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// no interest
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			if (partRef instanceof IEditorReference) {
				fView.setInput(getActiveEditor());
			}

		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
			// no interest
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			// no interest
		}

		public void partHidden(IWorkbenchPartReference partRef) {
			// no interest
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			// no interest

		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			// no interest
		}

		/*
		 * Listen for changes to the underlying PEPA Model
		 */
		public void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event) {
			fView.handleModelChanged(event);
		}
	}

	public void init(IViewSite site) throws PartInitException {
		super.setSite(site);
		fListener = new ListenerMix(this);
		site.getPage().addPartListener(fListener);
	}
	
	@Override
	public final void createPartControl(Composite parent) {
		internalCreatePartControl(parent);
		IEditorPart part = AbstractView.getActiveEditor();
		setInput(part);
	}
	
	/**
	 * Create controls for this view.
	 * @param parent
	 */
	protected abstract void internalCreatePartControl(Composite parent);

	private void setInput(IEditorPart editor) {
		if (editor == fEditor) {
			return;
		}
		if (fEditor != null)
			unRegisterPEPAListener();
		if (editor instanceof IProcessAlgebraEditor) {
			fEditor = (IProcessAlgebraEditor) editor;
			registerPEPAListener();
		} else {
			fEditor = null;
		}
		updateView(fEditor);
	}

	/**
	 * This method is called when the given editor becomes the new
	 * currently active editor.
	 * <p>
	 * This method can be implemented in order to update the information
	 * this view is containing
	 * 
	 * @param editor
	 *            the current active editor, or null if none
	 */
	protected abstract void updateView(IProcessAlgebraEditor editor);

	/**
	 * This method is called when the underlying PEPA model of the active
	 * editor is affected by some event (parsing, state space derivation,
	 * etc.).
	 * <p>
	 * This method can be implemented in order to update the information
	 * this view is containg
	 * 
	 * @param event
	 *            the event that changed the underlying model
	 */
	protected abstract void handleModelChanged(ProcessAlgebraModelChangedEvent event);

	private void registerPEPAListener() {
		fEditor.getProcessAlgebraModel().addModelChangedListener(fListener);
	}

	private void unRegisterPEPAListener() {
		if (fEditor != null)
			fEditor.getProcessAlgebraModel().removeModelChangedListener(fListener);
	}

	@Override
	public abstract void setFocus();

	public void dispose() {
		getSite().getPage().removePartListener(fListener);
		unRegisterPEPAListener();
		super.dispose();
	}

}
