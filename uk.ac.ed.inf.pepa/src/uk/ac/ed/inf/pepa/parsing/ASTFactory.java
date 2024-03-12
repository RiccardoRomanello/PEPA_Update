/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.parsing;

/**
 * This is the factory class for PEPA AST nodes.
 * 
 * @author mtribast
 *
 */
public class ASTFactory {
	
	/**
	 * Create a PEPA model
	 * @return the PEPA model
	 */
	public static ModelNode createModel() {
		return new ModelNode();
	}
	
	/**
	 * Create an action type. 
	 * @param actionType the action type to be created
	 * @return the AST Node representing the action type
	 */
	public static ActionTypeNode createActionType() {
		return new ActionTypeNode();
	}
	
	public static UnknownActionTypeNode createUnknownActionType() {
		return new UnknownActionTypeNode();
	}
	
	/**
	 * Create an activity AST node
	 * @return the AST node for the activity
	 */
	public static ActivityNode createActivity() {
		return new ActivityNode();
	}
	
	/**
	 * Create an aggregation AST node
	 * @return
	 */
	public static AggregationNode createAggregation() {
		return new AggregationNode();
	}
	
	/**
	 * Create a rate defined as a binary operator (+,-,/,*)
	 * @return
	 */
	public static BinaryOperatorRateNode createBinaryOperationRate() {
		return new BinaryOperatorRateNode();
	}
	
	/**
	 * Create the AST node for the PEPA choice operator
	 * @return
	 */
	public static ChoiceNode createChoice() {
		return new ChoiceNode();
	}
	
	/**
	 * Create the AST node for a process defined as constant
	 * @return
	 */
	public static ConstantProcessNode createConstant() {
		return new ConstantProcessNode();
	}
	
	/**
	 * Create the AST node for a PEPA cooperation operator
	 * @return
	 */
	public static CooperationNode createCooperation() {
		return new CooperationNode();
	}
	
	/**
	 * Create the AST node for hiding
	 * @return
	 */
	public static HidingNode createHiding() {
		return new HidingNode();
	}
	
	/**
	 * Create a new passive rate.
	 * @return the passive rate
	 */
	public static PassiveRateNode createPassiveRate() {
		return new PassiveRateNode();
	}
	
	/** 
	 * Create the AST for a PEPA prefix
	 * @return
	 */
	public static PrefixNode createPrefix() {
		return new PrefixNode();
	}
	
	/**
	 * Create a process definition
	 * @return
	 */
	public static ProcessDefinitionNode createProcessDefinition() {
		return new ProcessDefinitionNode();
	}
	
	/**
	 * Create a rate definition
	 * @return
	 */
	public static RateDefinitionNode createRateDefinition() {
		return new RateDefinitionNode();
	}
	
	/**
	 * Create a finite rate
	 * @return
	 */
	public static RateDoubleNode createRate() {
		return new RateDoubleNode();
	}
	
	/**
	 * Create a variable for a rate
	 * @return
	 */
	public static VariableRateNode createRateVariable() {
		return new VariableRateNode();
	}
	
	public static WildcardCooperationNode createWildcardCooperation() {
		return new WildcardCooperationNode();
	}
}
