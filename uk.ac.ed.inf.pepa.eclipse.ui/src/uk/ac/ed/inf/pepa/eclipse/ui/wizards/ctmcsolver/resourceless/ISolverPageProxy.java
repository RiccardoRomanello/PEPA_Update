/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.AbstractConfigurationWizardPage;

/**
 * This is an interface that any solver page has to implement.
 * <p>
 * If the implemented solver doesn't have a page to show, the
 * {@link #isNeedPage()} must return <code>false</code>. This will cause the
 * wizard no to add the page to the solver. If it returns <code>true</code>
 * then an {@link AbstractConfigurationWizardPage} is returned.
 * 
 * @author mtribast
 * 
 */
public interface ISolverPageProxy {

	public static final ISolverPageProxy EMPTY_SOLVER_PAGE = new ISolverPageProxy() {

		public AbstractConfigurationWizardPage getPage() {
			return null;
		}

		public boolean isNeedPage() {
			return false;
		}

	};

	/**
	 * Returns <code>true</code> when this solver contains widgets.
	 * 
	 * @return
	 */
	public boolean isNeedPage();

	/**
	 * Returns a wizard page that lets modify the settings for the solver.
	 * 
	 * @return
	 */
	public AbstractConfigurationWizardPage getPage();

}
