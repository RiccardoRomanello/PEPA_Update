/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.util.Collection;
import java.util.Map;

import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Process;

public interface ISymbolGenerator {

	public static final short TAU_ACTION = 0;
	
	public static final short NOT_FOUND = -1;
	
	public static final String TAU = "tau";

	public abstract Collection<String> getSequentialComponentNames();

	/**
	 * Get the action label corresponding to the action id.
	 * 
	 * @return null if no association is found.
	 * 
	 */
	public abstract NamedAction getAction(short actionId);

	public abstract String getActionLabel(short actionId);

	public abstract short getActionId(String action);

	public abstract short getIndex(Process process);

	public abstract short[] getInitialState();

	/**
	 * Get the number of copies of processId in state.
	 * 
	 * @param processId
	 * @param state
	 * @return
	 */
	public abstract int getNumOfCopies(short processId, short[] state);

	/**
	 * Get the process id corresponding to the sequential component passed as a
	 * string.
	 * 
	 * @param process
	 * @return
	 * @see #getProcessLabel(short) for the opposite operation
	 */
	public abstract short getProcessId(String process);

	/**
	 * Get the label corresponding to the given process id
	 * 
	 * @param processId
	 * @return
	 */
	public abstract String getProcessLabel(short processId);

	public abstract Map<Short, Process> getSequentialComponentMap();

}