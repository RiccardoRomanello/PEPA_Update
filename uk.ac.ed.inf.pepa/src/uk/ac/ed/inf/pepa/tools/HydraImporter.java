/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.mindprod.ledatastream.LEDataInputStream;

/**
 * Imports a hydra solution vector.
 * 
 * @author mtribast
 *
 */
public class HydraImporter {
	
	private String fileName;
	
	public HydraImporter(String fileName) {
		if (fileName == null)
			throw new NullPointerException();
		this.fileName = fileName;
	}
	
	public double[] importSolution() throws IOException {
		double[] solution;
		LEDataInputStream is = new LEDataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		int size = is.readInt();
		solution = new double[size];
		for (int i = 0; i < size; i++)
			solution[i] = is.readDouble();
		is.close();
		return solution;
	}
	
}
