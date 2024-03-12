/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.eclipse.ui.IFilterManager;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterModel;
import uk.ac.ed.inf.pepa.eclipse.ui.internal.FilterManager;
import uk.ac.ed.inf.pepa.eclipse.ui.internal.ManagerDialog;

/**
 * Utility class for opening dialog boxes that change the filter sets of a PEPA
 * model.
 * 
 * @author mtribast
 * 
 */
public class FilterDialogs {

	/**
	 * Opens a dialog for filter managers. The dialog updates the state of the
	 * filter manager. Filter rules can be added, removed, or edited. The method
	 * returns the current configuration selected by the user.
	 * 
	 * 
	 * @param manager
	 *            the filter manager.
	 * @param shell
	 *            the shell to use to display the dialog.
	 * @return the selected configuration, or null if no filters are wanted.
	 */
	public static IFilterModel openFilterManagerDialog(IFilterManager manager,
			Shell shell, IFilterModel selection) {
		FilterManager filterManager = (FilterManager) manager;
		ManagerDialog dialog = new ManagerDialog(filterManager, shell,
				selection);
		dialog.open();
		return dialog.getSelectedConfiguration();
	}

}
