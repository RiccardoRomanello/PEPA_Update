/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view;

import java.text.NumberFormat;

/**
 * Utility class containing static methods that can be useful
 * to most views
 * 
 * @author mtribast
 *
 */
public class Tools {
	
	// TODO Make these options accessible
	private static final int MINIMUM_FRACTION_DIGITS = 0;
	
	private static final int MAXIMUM_FRACTION_DIGITS = 2;
	
	private static final NumberFormat FORMATTER = NumberFormat.getInstance();
	
	static {
		FORMATTER.setMinimumFractionDigits(MINIMUM_FRACTION_DIGITS);
		FORMATTER.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
	}
	
	public static String format(double value) {
		
		return FORMATTER.format(value);
		
	}
}
