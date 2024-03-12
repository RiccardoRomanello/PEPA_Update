/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.io.IOException;

import uk.ac.ed.inf.pepa.parsing.ASTSupport;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class AggregationExpressionCopyTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String modelName = args[0];
		String modelText = TestFilter.readText(modelName);
		ModelNode modelNode = (ModelNode) PepaTools.parse(modelText);
		ModelNode copy = (ModelNode) ASTSupport.copy(modelNode);
		String s1 = ASTSupport.toString(modelNode);
		String s2 = ASTSupport.toString(copy);
		System.out.println(s1);
		System.out.println("******");
		System.out.println(s2);
		System.out.println(s1.equals(s2));
		
	}

}
