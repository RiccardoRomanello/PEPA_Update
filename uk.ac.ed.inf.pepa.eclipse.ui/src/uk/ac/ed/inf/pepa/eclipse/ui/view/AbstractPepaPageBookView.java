/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.*;

import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;

/**
 * Implements common functionality for Eclipse views 
 * for PEPA which requires a PageBook-based implementation.
 * 
 * @author mtribast
 * 
 */
public abstract class AbstractPepaPageBookView extends PageBookView {

	@Override
	/**
	 * The default implementation creates a <code>MessagePage</code> informing
	 * the user the editor is not a PEPA model
	 * <p>
	 * May be reimplemented.
	 * 
	 */
	protected IPage createDefaultPage(PageBook book) {
		Page page = new MessagePage("Not a PEPA model");
		if (page instanceof IPageBookViewPage)
			initPage((IPageBookViewPage) page);
		page.createControl(book);
		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		/*
		 * This is an instance of a part contributing a model Therefore the
		 * casting is safe
		 */
		if (part instanceof IProcessAlgebraEditor) {
			Page page = getPageFor((IProcessAlgebraEditor) part);
			if (page instanceof IPageBookViewPage)
				initPage((IPageBookViewPage) page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		} else
			return null;
	}

	/**
	 * This method is called with {@link #doCreatePage(IWorkbenchPart)} when the
	 * part is an instance of a PEPAEditor.
	 * <p>
	 * Must not return <code>null</code>
	 * @param editor
	 *            the linked PEPAEditor
	 * @return the page for this view
	 */
	protected abstract Page getPageFor(IProcessAlgebraEditor editor);

	@Override
	/**
	 * Does nothing
	 */
	protected abstract void doDestroyPage(IWorkbenchPart part, PageRec pageRecord);
	/**
	 * This implementation return the active editor in the workbench
	 * @return the current active editor
	 */
	protected IWorkbenchPart getBootstrapPart() {

		return AbstractView.getActiveEditor();
	}

	/**
	 * This is important when the part is an editor part
	 */
	protected boolean isImportant(IWorkbenchPart part) {
		return (part instanceof IEditorPart);
	}


}
