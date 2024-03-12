/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.eclipse.ui.PerspectiveFactory;

public class PEPAEditor extends TextEditor implements IProcessAlgebraEditor {

	public static final String ID = "uk.ac.ed.inf.pepa.eclipse.ui.editor.PEPAEditor";
	
	private IPepaModel fModel;

	private ColorManager fColorManager;

	public PEPAEditor() {
		super();
		fColorManager = new ColorManager();
		setSourceViewerConfiguration(new PepaViewerConfiguration(fColorManager));
		setDocumentProvider(new PepaDocumentProvider());
	}
	
	@Override
	public void dispose() {
		fColorManager.dispose();
		super.dispose();
	}

	/**
	 * Cache the edited PEPA model and register listener for enabling actions.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		/* cache the PEPA model */
		this.fModel = getIResource(input);
		if (fModel == null)
			throw new PartInitException("Model not available for EditorInput:"
					+ input.getName());

		checkPerspective(site.getPage());

	}

	/**
	 * TIP Check current perspective upon editor initialisation.
	 * <p>
	 * If the perspective is not the PEPA one, then a message dialog ask the
	 * user if she wants to switch to the PEPA perspective.
	 * <p>
	 * See documentation for ways to improve this check.
	 * 
	 * @param page
	 *            the current page.
	 */
	public static void checkPerspective(IWorkbenchPage page) {
		IPerspectiveDescriptor descriptor = page.getPerspective();
		if (descriptor == null) {
			// when the workspace is loaded
			return;
		}
		String currentPerspective = descriptor.getId();
		if (!currentPerspective.equals(PerspectiveFactory.PERSPECTIVE_ID)) {
			boolean result = MessageDialog
					.openQuestion(
							page.getWorkbenchWindow().getShell(),
							"Switch to PEPA Perspective",
							"This resource is associated to PEPA perspective. "
									+ "Would you like to switch to PEPA Perspective now?");
			if (result == true) {
				// user wants to switch
				IWorkbench workbench = Activator.getDefault().getWorkbench();
				page.setPerspective(workbench.getPerspectiveRegistry()
						.findPerspectiveWithId(
								PerspectiveFactory.PERSPECTIVE_ID));
			}
		}

	}

	/**
	 * Return the underlying PEPA model for this editor
	 * 
	 * @return the edited PEPA model
	 */
	public IPepaModel getProcessAlgebraModel() {
		return fModel;
	}

	private IPepaModel getIResource(IEditorInput input) {
		IResource resource = (IResource) input.getAdapter(IResource.class);
		return PepaCore.getDefault().getPepaManager().getModel(resource);
	}

}
