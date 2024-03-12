/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import java.util.ArrayList;

public class ProcessAlgebraManager implements IProcessAlgebraManager {
	
	private ArrayList<IProcessAlgebraManagerListener> listeners = 
		new ArrayList<IProcessAlgebraManagerListener>();
	
	protected void notifyAdded(IProcessAlgebraModel model) {
		for (IProcessAlgebraManagerListener l : listeners)
			l.modelAdded(model);
	}
	
	protected void notifyRemoved(IProcessAlgebraModel model) {
		for (IProcessAlgebraManagerListener l : listeners)
			l.modelRemoved(model);

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraManager#addListener(uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraManagerListener)
	 */
	public void addListener(IProcessAlgebraManagerListener listener) {
		if (listener == null || listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraManager#removeListener(uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraManagerListener)
	 */
	public void removeListener(IProcessAlgebraManagerListener listener) {
		if (listener == null)
			return;
		listeners.remove(listener);
	}

}
