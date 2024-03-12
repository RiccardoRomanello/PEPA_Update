/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;

/**
 * Calculates unguarded paths of a constant.
 * <p>
 * The harmfulness of unguarded declarations can be detected as follows:
 * <p>
 * For each process assignment A = P, we check if there is an 'unguarded path'
 * leading back to A. All the constants along that path should be highlighted in
 * the editor as 'horrors'. In this way we can capture situations like
 * 
 * <pre>
 *    A = (a,1).B + C;
 *    B = (c,1).A;
 *    C = D + (b,1).A;
 *    D = A
 * </pre>
 * 
 * The unguarded path being A...C...D...A
 * 
 * @author mtribast
 * 
 */
public class UnguardedPathDetector {

	ModelNode model;

	Set<ConstantProcessNode> constants = new HashSet<ConstantProcessNode>();

	/**
	 * 
	 * @param model
	 * @param constant
	 */
	public UnguardedPathDetector(ModelNode model) {
		this.model = model;
		for (ProcessDefinitionNode node : model.processDefinitions())
			node.accept(new PathVisitor(this));
	}

	public ConstantProcessNode[] getConstantsAffected() {
		return constants.toArray(new ConstantProcessNode[constants.size()]);
	}

}

class PathVisitor extends DefaultVisitor {

	List<ConstantProcessNode> path = new ArrayList<ConstantProcessNode>();

	UnguardedPathDetector detector;

	/* Default constructor with empty path */
	public PathVisitor(UnguardedPathDetector detector) {
		this.detector = detector;
	}

	public PathVisitor(UnguardedPathDetector detector,
			List<ConstantProcessNode> path) {
		this(detector);
		this.path.addAll(path);
	}

	public void visitAggregationNode(AggregationNode aggregation) {
		aggregation.getProcessNode().accept(this);
	}

	public void visitChoiceNode(ChoiceNode choice) {
		PathVisitor lPath = new PathVisitor(detector, path);
		PathVisitor rPath = new PathVisitor(detector, path);
		choice.getLeft().accept(lPath);
		choice.getRight().accept(rPath);
	}

	public void visitConstantProcessNode(ConstantProcessNode constant) {
		/* Iterate through the element of the path to search
		 * for an element with the same name
		 */
		for (ConstantProcessNode element : path) {
			if (constant.getName().equals(element.getName())) {
				/*
				 * Unguarded path detected. All the constants along the path are
				 * recorded and this path is no longer visited
				 */
				for (ConstantProcessNode n : path) {
					detector.constants.add(n);
				}
				return; // finish path
			}
		}
		ASTNode procDef = detector.model.getResolver().getProcessDefinition(
				constant.getName());
		procDef.accept(this);
	}

	public void visitCooperationNode(CooperationNode cooperation) {
	}

	public void visitHidingNode(HidingNode hiding) {
		hiding.getProcess().accept(this);
	}

	public void visitPrefixNode(PrefixNode prefix) {
		/* Does nothing */
	}

	public void visitProcessDefinitionNode(
			ProcessDefinitionNode processDefinition) {
		path.add(processDefinition.getName());
		processDefinition.getNode().accept(this);
	}

}
