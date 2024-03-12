/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import org.eclipse.core.runtime.IProgressMonitor;

public class PepatoProgressMonitorAdapter implements
			uk.ac.ed.inf.pepa.IProgressMonitor {

		IProgressMonitor eclispeMonitor;

		String name;

		public PepatoProgressMonitorAdapter(IProgressMonitor monitor, String name) {
			eclispeMonitor = monitor;
			this.name = name;

		}

		public void beginTask(int amount) {
			final int eclipseAmount = (amount == uk.ac.ed.inf.pepa.IProgressMonitor.UNKNOWN) ? IProgressMonitor.UNKNOWN
					: amount;
			eclispeMonitor.beginTask(name, eclipseAmount);

		}

		public void setCanceled(boolean state) {
			/*
			 * Set canceled is done by the eclipse monitor, which changes the
			 * return value of its isCanceled to true. This adapter doesn't
			 * cancel anything if it is not done by the wrapped eclipse monitor.
			 */
			// do nothing here
		}

		public boolean isCanceled() {
			return this.eclispeMonitor.isCanceled();
		}

		public void worked(final int worked) {

			eclispeMonitor.worked(worked);

		}

		public void done() {
			eclispeMonitor.done();
		}
	}
