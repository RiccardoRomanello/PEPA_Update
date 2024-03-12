/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.util.ArrayList;

public class MemoryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long before = Runtime.getRuntime().totalMemory() -
			Runtime.getRuntime().freeMemory();
		ArrayList<Integer> list = new ArrayList<Integer>(10000);
		
		long after = Runtime.getRuntime().totalMemory() -
		Runtime.getRuntime().freeMemory();
		
		System.out.println(after - before);

	}

}
