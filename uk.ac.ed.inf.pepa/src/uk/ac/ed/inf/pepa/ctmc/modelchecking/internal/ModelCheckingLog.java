/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.ILogListener;

public class ModelCheckingLog {
	
	private ArrayList<ILogListener> listeners;
	
	public ModelCheckingLog() {
		this.listeners = new ArrayList<ILogListener>();
	}
	
	public void addListener(ILogListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void addEntry(String entry) {
		notify(entry);
	}
	
	private void notify(String entry) {
		for (ILogListener listener : listeners) {
			listener.notifyLogEntry(entry);
		}
	}
	
}
