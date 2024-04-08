/*******************************************************************************
 * Copyright (c) 2024. Alberto Casagrande <alberto.casagrande@uniud.it>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

public class LevelDeclarations {
	public Actions[] levelDefinitions;
	
	public static int HIGH_LEVEL = 0;
	public static int LOW_LEVEL = 1;
	
	public Integer default_level = null;
	
	public LevelDeclarations() {
		levelDefinitions = new Actions[2];

		levelDefinitions[LevelDeclarations.HIGH_LEVEL] = new Actions();
		levelDefinitions[LevelDeclarations.LOW_LEVEL] = new Actions();
	}
	
	public Actions getHigh() {
		return levelDefinitions[LevelDeclarations.HIGH_LEVEL];
	}

	public Actions getLow() {
		return levelDefinitions[LevelDeclarations.LOW_LEVEL];
	}
}
