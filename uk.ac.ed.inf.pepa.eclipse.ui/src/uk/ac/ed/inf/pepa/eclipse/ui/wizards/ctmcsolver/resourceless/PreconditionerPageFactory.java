/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class PreconditionerPageFactory {

	/**
	 * Factory for MTJ preconditioner
	 * 
	 * @param id
	 *            preconditioner unique identifier
	 * @return the preconditioner setting page, or null if none is available
	 */
	public static ISolverPageProxy createPreconditionerPageFor(int id) {
		switch (id) {
		case OptionMap.AMG:
			return new AMGPreconditionerPage();
		case OptionMap.SSOR:
			return new SSORPreconditioner();
		case OptionMap.DIAGONAL:
			return ISolverPageProxy.EMPTY_SOLVER_PAGE;
		case OptionMap.NO_PRECONDITIONER:
			return ISolverPageProxy.EMPTY_SOLVER_PAGE;
		case OptionMap.ICC:
			return ISolverPageProxy.EMPTY_SOLVER_PAGE;
		case OptionMap.ILU:
			return ISolverPageProxy.EMPTY_SOLVER_PAGE;
		case OptionMap.ILUT:
			return new ILUTPreconditionerPage();

		}
		return null;
	}

}
