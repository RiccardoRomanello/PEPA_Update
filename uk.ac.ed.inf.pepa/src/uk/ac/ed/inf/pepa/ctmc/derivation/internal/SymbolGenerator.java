/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.internal.NamedActionImpl;

public class SymbolGenerator implements ISymbolGenerator {

	private Map<Short, Process> processMap;

	private Map<Short, NamedAction> actionMap;

	private short[] initialState;

	public SymbolGenerator(short[] initialState,
			Map<Short, Process> processMap, Map<Short, NamedAction> actionMap) {
		this.initialState = initialState;
		this.processMap = processMap;
		this.actionMap = actionMap;
	}

	public String getActionLabel(short actionId) {
		NamedAction action = actionMap.get(actionId);
		return (action == null) ? 
				((actionId == ISymbolGenerator.TAU_ACTION) ?
						ISymbolGenerator.TAU : null)
				: action.prettyPrint();
	}


	public NamedAction getAction(short actionId) {
		NamedAction action = actionMap.get(actionId);
		return (action == null) ? 
				((actionId == ISymbolGenerator.TAU_ACTION) ?
						new NamedActionImpl(ISymbolGenerator.TAU) : null)
				: action;
	}

	public short getActionId(String action) {
		for (Map.Entry<Short, NamedAction> entry : actionMap.entrySet()) {
			if (action.equals(entry.getValue().prettyPrint()))
				return entry.getKey();
		}
		return NOT_FOUND;
	}

	public short getIndex(Process process) {
		for (Map.Entry<Short, Process> entry : processMap.entrySet()) {
			if (process.equals(entry.getValue()))
				return entry.getKey();
		}
		return NOT_FOUND;
	}

	public short[] getInitialState() {
		return initialState;
	}

	public int getNumOfCopies(short processId, short[] state) {
		int copies = 0;
		for (short id : state) {
			if (processId == id)
				copies++;
		}
		return copies;
	}

	public short getProcessId(String process) {
		for (Map.Entry<Short, Process> entry : processMap.entrySet()) {
			if (process.equals(entry.getValue().prettyPrint()))
				return entry.getKey();
		}
		return NOT_FOUND;
	}

	public String getProcessLabel(short processId) {
		Process process = processMap.get(processId);
		return process == null ? null : process.prettyPrint();
	}

	public Map<Short, Process> getSequentialComponentMap() {
		return processMap;
	}
	
	public Map<Short, NamedAction> getActionMap() {
		return actionMap;
	}

	public Collection<String> getSequentialComponentNames() {
		SortedSet<String> set = new TreeSet<String>();
		Collection<Process> processes = processMap.values();
		for (Process p : processes)
			set.add(p.prettyPrint());
		return set;

	}

}
