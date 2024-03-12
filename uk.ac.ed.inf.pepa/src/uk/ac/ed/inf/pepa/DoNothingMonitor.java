/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa;

/**
 * A progress monitor which does nothing. It can be used by long-running
 * operations to avoid checking the nullity of the passed monitor. If
 * the passed monitor is null, a {@link DoNothingMonitor} can be created
 * for this purpose.
 * 
 * @author mtribast
 *
 */
public class DoNothingMonitor implements IProgressMonitor {
	
	public void beginTask(int amount) {
	}

	public void done() {
	}

	public boolean isCanceled() {
		return false;
	}

	public void setCanceled(boolean state) {
	}

	public void worked(int worked) {
	}

}
