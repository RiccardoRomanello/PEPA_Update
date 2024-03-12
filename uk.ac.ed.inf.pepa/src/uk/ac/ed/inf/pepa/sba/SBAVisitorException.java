/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

/**
 * 
 * @author ajduguid
 * 
 */
public class SBAVisitorException extends RuntimeException {

	private static final long serialVersionUID = -6692900486788939350L;

	public SBAVisitorException(StackTraceElement method,
			StackTraceElement declaringClass) {
		super(method.getMethodName()
				+ " method should not be called from within "
				+ declaringClass.getClassName() + "."
				+ declaringClass.getMethodName());
	}

	public SBAVisitorException(StackTraceElement[] stackTraceElement) {
		super(stackTraceElement[2].getMethodName()
				+ " method should not be called from within "
				+ stackTraceElement[2].getClassName());
	}
	
	public SBAVisitorException(String reason) {
		super(reason);
	}
}
