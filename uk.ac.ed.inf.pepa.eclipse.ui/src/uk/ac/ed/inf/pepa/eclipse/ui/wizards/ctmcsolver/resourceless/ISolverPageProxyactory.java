/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

/**
 * Retrieves the solver page for a solver which is specified with the integer as
 * defined in PEPAto
 * 
 * @author mtribast
 * 
 */
public class ISolverPageProxyactory {

	/**
	 * Factory method for solver pages.
	 * 
	 * @param id
	 *            the unique identifier for a solver
	 * @return the solver setting page, or null if none is available
	 */
	public static ISolverPageProxy createPageFor(int solverId, int preconditionerId) {
		switch (solverId) {
			case OptionMap.MTJ_BICG:
			case OptionMap.MTJ_BICG_STAB:
			case OptionMap.MTJ_CG:
			case OptionMap.MTJ_CGS:
			case OptionMap.MTJ_IR:
				return new MTJIterativeSolverPage(solverId, preconditionerId);
			case OptionMap.MTJ_GMRES:
				return new GMRESSolverPage(solverId, preconditionerId);
			case OptionMap.MTJ_DIRECT:
				return ISolverPageProxy.EMPTY_SOLVER_PAGE;
			case OptionMap.MTJ_CHEBYSHEV:
				return new ChebyshevSolverPage(solverId, preconditionerId);
			case OptionMap.HYDRA_AIR:
				return new HydraSolverPage();//ISolverPageProxy.EMPTY_SOLVER_PAGE;
		}
		return null;
	}
}
