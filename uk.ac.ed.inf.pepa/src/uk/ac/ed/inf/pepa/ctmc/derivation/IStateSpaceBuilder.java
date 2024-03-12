/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation;

import uk.ac.ed.inf.pepa.IProgressMonitor;

/**
 * Interface for object implementing state space derivation services.
 * 
 * @author mtribast
 * 
 */
public interface IStateSpaceBuilder {

	/**
	 * Derives the state space. A boolean controls whether or not passive rates
	 * are allowed in the state space. If <code>true</code> this information
	 * is typically used by analysers. A progress monitor is passed to control
	 * this long-running operation.
	 * 
	 * @param allowPassiveRate
	 * 			 <code>true</code> if the user wants to have transitions
	 * 			occurring at passive rates.
	 * @param monitor
	 *            the monitor passed in for iteration reporting
	 * @return the state space
	 * @throws DerivationException
	 */
	public IStateSpace derive(boolean allowPassiveRates,
			IProgressMonitor monitor) throws DerivationException;
	
	/**
	 * Returns wall-clock execution times for the state
	 * space derivation, or <code>null</code> if this option
	 * is not supported by this builder.
	 * @return
	 */
	public MeasurementData getMeasurementData();
	

}
