/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

/**
 * Implementations of this interface are notified when a change occurs in
 * a PEPA model (<code>IPepaModel</code>). Events can be notified by
 * requests of state space derivation, steady-state probability
 * calculation and so on.
 * 
 * @author mtribast
 * @see IPepaModel
 */
public interface IProcessAlgebraModelChangedListener {

	/**
	 * This method is called when a PEPA model notifies a change in its
	 * state
	 * 
	 * @param event
	 *            event that generated the change
	 */
	public void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event);

}
