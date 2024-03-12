/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.view.AbstractPepaPageBookView;

/**
 * The state space explorer shows information about Markovian analysis of PEPA
 * models. It tracks the active editor and provides a page if it is an instance
 * of <code>uk.ac.ed.inf.pepa.eclipse.ui.editor.PEPAEditor</code>.
 * <p>
 * The state space can be filtered, exported into other formats, etc.
 * <p>
 * The view is updated when the steady-state distribution is calculated.
 * 
 * @author mtribast
 * 
 */
public class StateSpaceExplorer extends AbstractPepaPageBookView {
	
	@Override
	protected Page getPageFor(IProcessAlgebraEditor editor) {
		// TODO Should use Eclipse extension points here
		IProcessAlgebraModel model = editor.getProcessAlgebraModel();
		if (model instanceof IPepaModel) {
			return new PepaModelPage(model);
		} else { 
			return new GenericPAModelPage(model);
		}
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		if (pageRecord != null)
			pageRecord.page.dispose();
		
	}

}
