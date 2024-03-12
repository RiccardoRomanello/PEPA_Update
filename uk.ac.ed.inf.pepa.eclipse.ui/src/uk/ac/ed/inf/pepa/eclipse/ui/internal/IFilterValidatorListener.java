/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

/**
 * This interface is implemented by object who want to be notified
 * of changes to the validation state of state space filters.
 * 
 * @author mtribast
 *
 */
public interface IFilterValidatorListener {
	/**
	 * This method is called when a change occur in the GUI related
	 * to the state space filter. If the filter is valid, <code>message</code> is
	 * null, otherwise the message is a human-readable description of what
	 * went wrong.
	 * 
	 * @param message an error message, or <code>null</code> of the filter is OK.
	 */
	public void filterValidated(String message);

}
