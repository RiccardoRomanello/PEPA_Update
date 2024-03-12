/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.Arrays;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialStateSpace;

public class AtomicProperty {

	private int numComponents;
	private SequentialStateSpace[] stateSpaces;
	private boolean[][] property;
	
	public AtomicProperty(int numComponents) {
		this.numComponents = numComponents;
		this.property = new boolean[numComponents][];
		this.stateSpaces = new SequentialStateSpace[numComponents];
	}
	
	public void addPropertyMap(int component, SequentialStateSpace stateSpace, boolean initialValue) {
		boolean[] propertyMap = new boolean[stateSpace.size()];
		Arrays.fill(propertyMap, initialValue);
		property[component] = propertyMap;
		stateSpaces[component] = stateSpace;
		stateSpace.register(this);
	}
	
	public boolean getProperty(int component, int index) {
		return property[component][index];
	}
	
	public void setProperty(int component, int index, boolean isTrue) {
		property[component][index] = isTrue;
	}
	
	public void setPropertyAll(int component, boolean isTrue) {
		boolean[] propertyMap = property[component];
		for (int i = 0; i < propertyMap.length; i++) {
			propertyMap[i] = isTrue;
		}
	}
	
	public boolean checkProperty(short[] state) {
		assert state.length == numComponents;
		boolean isTrue = true;
		for (int i = 0; i < numComponents; i++) {
			int index = stateSpaces[i].getIndex(state[i]);
			isTrue = isTrue && property[i][index];
		}
		return isTrue;
	}
	
	public void notifySwap(SequentialStateSpace stateSpace, int index1, int index2) {
		for (int i = 0; i < numComponents; i++) {
			if (stateSpaces[i] == stateSpace) {
				boolean value1 = property[i][index1];
				boolean value2 = property[i][index2];
				property[i][index1] = value2;
				property[i][index2] = value1;
			}
		}
	}
	
}
