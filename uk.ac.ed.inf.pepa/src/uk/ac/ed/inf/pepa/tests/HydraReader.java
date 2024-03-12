/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.mindprod.ledatastream.LEDataInputStream;

public class HydraReader {
	
	public static void main(String[] args) throws IOException {
		String fileName = args[0];
		LEDataInputStream is = new LEDataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		double data = 0.0;
		while( data == is.readDouble())
			System.out.println(data);
		System.out.println("Result");
		is.close();
	}
}
