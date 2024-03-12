/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.util.Hashtable;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

/**
 * Tests the new implementation for
 * @author mtribast
 *
 */
public class OptionMapTest {
	
	public static void main(String[] args) {
		OptionMap options = new OptionMap();
		options.put(OptionMap.AGGREGATE_ARRAYS, false);
		System.out.println(options.prettyPrint());
		options.put(OptionMap.AGGREGATE_ARRAYS, true);
		System.out.println(options.prettyPrint());
		options.put("Not exists", true);
		System.out.println(options.prettyPrint());
		options.put(OptionMap.PRECONDITIONER, 1000);
		options.put(OptionMap.AGGREGATE_ARRAYS, false);
		System.out.println(options.prettyPrint());
		
		Hashtable ht1 = new Hashtable();
		ht1.put("Test", "FirstValue");
		Hashtable ht2 = new Hashtable(ht1);
		ht2.put("Test", "Second value");
		System.out.println(ht1.get("Test"));
		System.out.println(ht2.get("Test"));
	
	}

}
