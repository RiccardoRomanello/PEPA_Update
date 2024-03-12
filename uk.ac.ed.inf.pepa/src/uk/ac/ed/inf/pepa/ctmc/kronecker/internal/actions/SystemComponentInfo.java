/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions;

import java.awt.Point;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * Keeps track of the index associated with each process node in the system
 * equation, and also an array of indices corresponding to the processes in
 * each cooperation term.
 * 
 * @author msmith
 */
public class SystemComponentInfo {

	private int nextProcess = 0;
	
	private HashMap<ProcessNode, Point> indices;
	
	public SystemComponentInfo() {
		this.indices = new HashMap<ProcessNode, Point>(10);
	}
	
	public Point getIndex(ProcessNode node) {
		return indices.get(node);
	}
	
	public void addProcess(ConstantProcessNode node) {
		int index = nextProcess++;
		indices.put(node, new Point(index, index));
	}
	
	public void addCooperation(CooperationNode node) {
		Point indices1 = indices.get(node.getLeft());
		Point indices2 = indices.get(node.getRight());
		Point newIndices = new Point(indices1.x, indices2.y);
		indices.put(node, newIndices);
	}
	
	public void addCooperation(WildcardCooperationNode node) {
		Point indices1 = indices.get(node.getLeft());
		Point indices2 = indices.get(node.getRight());
		Point newIndices = new Point(indices1.x, indices2.y);
		indices.put(node, newIndices);
	}
	
}
