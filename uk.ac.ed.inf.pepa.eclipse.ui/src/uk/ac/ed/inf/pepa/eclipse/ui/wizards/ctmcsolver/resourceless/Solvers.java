/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

/**
 * Temporary class holding information on the CTMC steady-state probability
 * distribution function solvers available under this Eclipse Plug-in.
 * 
 * 
 * @author mtribast
 * 
 */
// TODO Store this class in a more convenient package
// TODO We may want to include SolverPageFactory methods in this class
public final class Solvers {

	private HashMap<String, Integer> availableSolvers;

	private static final Solvers instance = new Solvers();

	private Solvers() {
		// TODO longer names!
		availableSolvers = new HashMap<String, Integer>(5);
		availableSolvers.put("BiConjugate Gradient (BiCG)", OptionMap.MTJ_BICG);
		availableSolvers.put("BiConjugate Gradient Stabilised (BiCGstab)",
				OptionMap.MTJ_BICG_STAB);
		availableSolvers.put("Conjugate Gradient (CG)", OptionMap.MTJ_CG);
		availableSolvers.put("Conjugate Gradient Squared (CGS)",
				OptionMap.MTJ_CGS);
		availableSolvers.put("Iterative Refinement (IR)", OptionMap.MTJ_IR);
		availableSolvers.put("Direct solver", OptionMap.MTJ_DIRECT);
		availableSolvers.put("Generalized Minimum Residual (GMRES)",
				OptionMap.MTJ_GMRES);
		availableSolvers.put("Chebyshev", OptionMap.MTJ_CHEBYSHEV);
		availableSolvers.put("Hydra AIR", OptionMap.HYDRA_AIR);
	}

	public static Solvers getInstance() {
		return instance;
	}

	public Collection<String> getAvailableSolvers() {
		ArrayList<String> list = new ArrayList<String>(availableSolvers
				.keySet());
		Collections.sort(list);
		return list;
	}

	public Integer getSolverId(String key) {
		return availableSolvers.get(key);
	}

	public String getSolverName(Integer id) {
		for (java.util.Map.Entry<String, Integer> entry : availableSolvers
				.entrySet()) {
			if (id.equals(entry.getValue()))
				return entry.getKey();
		}
		return null;

	}
}
