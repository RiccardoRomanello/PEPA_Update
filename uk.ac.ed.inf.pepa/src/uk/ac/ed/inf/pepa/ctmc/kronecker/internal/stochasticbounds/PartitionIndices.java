/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.stochasticbounds;

import uk.ac.ed.inf.pepa.ctmc.abstraction.AbstractState;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;

public class PartitionIndices {

	private int[] indices;

	public PartitionIndices(SequentialAbstraction abstraction) {
		AbstractState[] partitions = abstraction.getAbstractStateSpace();
		this.indices = new int[partitions.length + 1];
		indices[0] = 0;
		for (int k = 0; k < partitions.length; k++) {
			indices[k+1] = indices[k] + partitions[k].size();
		}
	}

	public int getStart(int partition) {
		return indices[partition];
	}
	
	public int getEnd(int partition) {
		return indices[partition+1] - 1;
	}
	
	public int getLength(int partition) {
		return indices[partition+1] - indices[partition]; 
	}
	
	public int size() {
		return indices.length - 1;
	}
	
}
