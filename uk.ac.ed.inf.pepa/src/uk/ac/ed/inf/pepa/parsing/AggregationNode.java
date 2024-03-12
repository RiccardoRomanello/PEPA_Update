/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;
/**
 * As of 20/02/2007 (see ChangeLog) Aggregation no longer supports
 * action sets
 * 
 * @author mtribast
 *
 */
public class AggregationNode extends ProcessNode {
	
	private FiniteRateNode expression;

	private ProcessNode process;
	
	AggregationNode() {
	}
	
	
	public ProcessNode getProcessNode() {
		return process;
	}

	public FiniteRateNode getCopies() {
		return expression;
	}

	/**
	 * Set the number of copies for this aggregation
	 * 
	 * @param copies
	 *            the number of copies
	 * @throws IllegalArgumentException
	 *             if <code>copies</code> is less than 1
	 */
	public void setCopies(FiniteRateNode expression) {
		if (expression == null)
			throw new NullPointerException();
		this.expression = expression;
	}
	
	/**
	 * Set the action set for this aggregator.
	 * <p>
	 * Aggregation that does not synchronise over any action set must
	 * be passed an empty action set, not null.
	 * 
	 * @param actionSet 
	 * @throws IllegalArgumentException if the action set is null
	 */
//	public void setActionSet(Actions actionSet) {
//		if (actionSet == null)
//			throw new IllegalArgumentException();
//		this.actionSet = actionSet;
//		
//	}
	
	/**
	 * Set the aggregated process
	 * @param process the aggregated process
	 * @throws IllegalArgumentException if null is passed
	 */
	public void setProcessNode(ProcessNode process) {
		if (process == null)
			throw new IllegalArgumentException();
		this.process = process;
		
	}

	@Override
	protected void accept0(ASTVisitor v) {
		v.visitAggregationNode(this);
	}

}
