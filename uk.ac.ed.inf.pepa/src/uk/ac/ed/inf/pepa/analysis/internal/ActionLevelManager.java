/*******************************************************************************
 * Copyright (c) 2024 Alberto Casagrande <alberto.casagrande@uniud.it>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.parsing.LevelDeclarations;


public class ActionLevelManager {

	private HashMap<String, HashMap<Integer, Integer>> decl_level = new HashMap<String, HashMap<Integer, Integer>>();
	private ProblemManager problemManager;

	public ActionLevelManager(ProblemManager problemManager) {
		this.problemManager = problemManager;
	}
	
	
	public void declare(String name, Integer level) {
		HashMap<Integer, Integer> level_map;
		if (decl_level.containsKey(name)) {
			level_map = decl_level.get(name);
		} else {
			level_map = new HashMap<Integer, Integer>();
			decl_level.put(name, level_map);
		}
		level_map.put(level, level_map.getOrDefault(level, 0)+1);
	}

	public void declare_high(String name) {
		declare(name, LevelDeclarations.HIGH_LEVEL);
	}

	public void declare_low(String name) {
		declare(name, LevelDeclarations.LOW_LEVEL);
	}

	/*
	 * Reports warnings to the model. This method is called after the visitor
	 * object has finished to visit the model
	 */
	public void warn() {

		for (Entry<String, HashMap<Integer, Integer>> mapEntry : decl_level.entrySet()) {
			HashMap<Integer, Integer> level_map = mapEntry.getValue();
			String name = mapEntry.getKey();
			if (level_map.size() > 1) {
				problemManager.actionLevelConflict(name);
			}
			for (Entry<Integer, Integer> levelEntry : level_map.entrySet()) {
				if (levelEntry.getValue()>1) {
					problemManager.actionLevelMultipleDeclaration(name);
				}
			}
		}

	}

}
