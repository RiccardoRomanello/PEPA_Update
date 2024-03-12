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
import java.util.Map;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class Preconditioners {

	private HashMap<String, Integer> availablePreconditioners;

	private static final Preconditioners instance = new Preconditioners();

	private Preconditioners() {
		availablePreconditioners = new HashMap<String, Integer>(5);
		availablePreconditioners
				.put("Algebraic Multigrid (AMG)", OptionMap.AMG);
		availablePreconditioners
				.put("Incomplete Cholesky (ICC)", OptionMap.ICC);
		availablePreconditioners.put("Incomplete LU Decomposition (ILU)",
				OptionMap.ILU);
		availablePreconditioners.put("Incomplete LU with fill-in (ILUT)",
				OptionMap.ILUT);
		availablePreconditioners.put(
				"Symmetrical Sucessive Overrelaxation (SSOR)", OptionMap.SSOR);
		availablePreconditioners.put("-", OptionMap.NO_PRECONDITIONER);
	}

	public static Preconditioners getInstance() {
		return instance;
	}

	public Collection<String> getAvailablePreconditioners() {
		ArrayList<String> l = new ArrayList<String>(availablePreconditioners
				.keySet());
		Collections.sort(l);
		return l;
	}

	public Integer getPreconditionerId(String key) {
		return availablePreconditioners.get(key);
	}

	public String getPreconditionerName(Integer value) {
		for (Map.Entry<String, Integer> entry : availablePreconditioners
				.entrySet())
			if (entry.getValue().equals(value))
				return entry.getKey();
		return null;
	}

}
