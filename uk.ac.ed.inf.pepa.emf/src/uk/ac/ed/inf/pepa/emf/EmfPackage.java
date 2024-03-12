/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see uk.ac.ed.inf.pepa.emf.EmfFactory
 * @model kind="package"
 * @generated
 */
public interface EmfPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "emf";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http:///uk/ac/ed/inf/pepa/emf.ecore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "uk.ac.ed.inf.pepa.emf";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EmfPackage eINSTANCE = uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl.init();

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ActionImpl <em>Action</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ActionImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getAction()
	 * @generated
	 */
	int ACTION = 0;

	/**
	 * The number of structural features of the '<em>Action</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ActivityImpl <em>Activity</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ActivityImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getActivity()
	 * @generated
	 */
	int ACTIVITY = 1;

	/**
	 * The feature id for the '<em><b>Rate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__RATE = 0;

	/**
	 * The feature id for the '<em><b>Action</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY__ACTION = 1;

	/**
	 * The number of structural features of the '<em>Activity</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTIVITY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessImpl <em>Process</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcess()
	 * @generated
	 */
	int PROCESS = 11;

	/**
	 * The number of structural features of the '<em>Process</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessWithSetImpl <em>Process With Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessWithSetImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcessWithSet()
	 * @generated
	 */
	int PROCESS_WITH_SET = 12;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.AggregationImpl <em>Aggregation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.AggregationImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getAggregation()
	 * @generated
	 */
	int AGGREGATION = 2;

	/**
	 * The feature id for the '<em><b>Process</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATION__PROCESS = PROCESS_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Copies</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATION__COPIES = PROCESS_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Aggregation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AGGREGATION_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.BinaryOperatorImpl <em>Binary Operator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.BinaryOperatorImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getBinaryOperator()
	 * @generated
	 */
	int BINARY_OPERATOR = 3;

	/**
	 * The feature id for the '<em><b>Right Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_OPERATOR__RIGHT_HAND_SIDE = PROCESS_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Left Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_OPERATOR__LEFT_HAND_SIDE = PROCESS_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Binary Operator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_OPERATOR_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ChoiceImpl <em>Choice</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ChoiceImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getChoice()
	 * @generated
	 */
	int CHOICE = 4;

	/**
	 * The feature id for the '<em><b>Right Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHOICE__RIGHT_HAND_SIDE = BINARY_OPERATOR__RIGHT_HAND_SIDE;

	/**
	 * The feature id for the '<em><b>Left Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHOICE__LEFT_HAND_SIDE = BINARY_OPERATOR__LEFT_HAND_SIDE;

	/**
	 * The number of structural features of the '<em>Choice</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHOICE_FEATURE_COUNT = BINARY_OPERATOR_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.CooperationImpl <em>Cooperation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.CooperationImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getCooperation()
	 * @generated
	 */
	int COOPERATION = 5;

	/**
	 * The feature id for the '<em><b>Right Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COOPERATION__RIGHT_HAND_SIDE = BINARY_OPERATOR__RIGHT_HAND_SIDE;

	/**
	 * The feature id for the '<em><b>Left Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COOPERATION__LEFT_HAND_SIDE = BINARY_OPERATOR__LEFT_HAND_SIDE;

	/**
	 * The feature id for the '<em><b>Actions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COOPERATION__ACTIONS = BINARY_OPERATOR_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Cooperation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COOPERATION_FEATURE_COUNT = BINARY_OPERATOR_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateImpl <em>Rate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.RateImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRate()
	 * @generated
	 */
	int RATE = 13;

	/**
	 * The number of structural features of the '<em>Rate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.FiniteRateImpl <em>Finite Rate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.FiniteRateImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getFiniteRate()
	 * @generated
	 */
	int FINITE_RATE = 6;

	/**
	 * The number of structural features of the '<em>Finite Rate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FINITE_RATE_FEATURE_COUNT = RATE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Actions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_WITH_SET__ACTIONS = PROCESS_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Process With Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_WITH_SET_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.HidingImpl <em>Hiding</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.HidingImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getHiding()
	 * @generated
	 */
	int HIDING = 7;

	/**
	 * The feature id for the '<em><b>Actions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIDING__ACTIONS = PROCESS_WITH_SET__ACTIONS;

	/**
	 * The feature id for the '<em><b>Hidden Process</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIDING__HIDDEN_PROCESS = PROCESS_WITH_SET_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Hiding</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HIDING_FEATURE_COUNT = PROCESS_WITH_SET_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ModelImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getModel()
	 * @generated
	 */
	int MODEL = 8;

	/**
	 * The feature id for the '<em><b>System Equation</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__SYSTEM_EQUATION = 0;

	/**
	 * The feature id for the '<em><b>Process Assignments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__PROCESS_ASSIGNMENTS = 1;

	/**
	 * The feature id for the '<em><b>Rate Assignments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__RATE_ASSIGNMENTS = 2;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.PassiveRateImpl <em>Passive Rate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.PassiveRateImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getPassiveRate()
	 * @generated
	 */
	int PASSIVE_RATE = 9;

	/**
	 * The feature id for the '<em><b>Weight</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PASSIVE_RATE__WEIGHT = RATE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Passive Rate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PASSIVE_RATE_FEATURE_COUNT = RATE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.PrefixImpl <em>Prefix</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.PrefixImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getPrefix()
	 * @generated
	 */
	int PREFIX = 10;

	/**
	 * The feature id for the '<em><b>Target Process</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFIX__TARGET_PROCESS = PROCESS_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Activity</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFIX__ACTIVITY = PROCESS_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Prefix</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFIX_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.SilentActionImpl <em>Silent Action</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.SilentActionImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getSilentAction()
	 * @generated
	 */
	int SILENT_ACTION = 14;

	/**
	 * The number of structural features of the '<em>Silent Action</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SILENT_ACTION_FEATURE_COUNT = ACTION_FEATURE_COUNT + 0;


	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ActionIdentifierImpl <em>Action Identifier</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ActionIdentifierImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getActionIdentifier()
	 * @generated
	 */
	int ACTION_IDENTIFIER = 15;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_IDENTIFIER__NAME = ACTION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Action Identifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_IDENTIFIER_FEATURE_COUNT = ACTION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.NumberLiteralImpl <em>Number Literal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.NumberLiteralImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getNumberLiteral()
	 * @generated
	 */
	int NUMBER_LITERAL = 16;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMBER_LITERAL__VALUE = FINITE_RATE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Number Literal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMBER_LITERAL_FEATURE_COUNT = FINITE_RATE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessAssignmentImpl <em>Process Assignment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessAssignmentImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcessAssignment()
	 * @generated
	 */
	int PROCESS_ASSIGNMENT = 17;

	/**
	 * The feature id for the '<em><b>Process Identifier</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER = 0;

	/**
	 * The feature id for the '<em><b>Process</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_ASSIGNMENT__PROCESS = 1;

	/**
	 * The number of structural features of the '<em>Process Assignment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_ASSIGNMENT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessIdentifierImpl <em>Process Identifier</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessIdentifierImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcessIdentifier()
	 * @generated
	 */
	int PROCESS_IDENTIFIER = 18;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_IDENTIFIER__NAME = PROCESS_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Process Identifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_IDENTIFIER_FEATURE_COUNT = PROCESS_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateAssignmentImpl <em>Rate Assignment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.RateAssignmentImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateAssignment()
	 * @generated
	 */
	int RATE_ASSIGNMENT = 19;

	/**
	 * The feature id for the '<em><b>Rate Identifier</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_ASSIGNMENT__RATE_IDENTIFIER = 0;

	/**
	 * The feature id for the '<em><b>Rate</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_ASSIGNMENT__RATE = 1;

	/**
	 * The number of structural features of the '<em>Rate Assignment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_ASSIGNMENT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateExpressionImpl <em>Rate Expression</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.RateExpressionImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateExpression()
	 * @generated
	 */
	int RATE_EXPRESSION = 20;

	/**
	 * The feature id for the '<em><b>Left Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_EXPRESSION__LEFT_HAND_SIDE = FINITE_RATE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Right Hand Side</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_EXPRESSION__RIGHT_HAND_SIDE = FINITE_RATE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Operator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_EXPRESSION__OPERATOR = FINITE_RATE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Rate Expression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_EXPRESSION_FEATURE_COUNT = FINITE_RATE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateIdentifierImpl <em>Rate Identifier</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.impl.RateIdentifierImpl
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateIdentifier()
	 * @generated
	 */
	int RATE_IDENTIFIER = 21;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_IDENTIFIER__NAME = FINITE_RATE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Rate Identifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RATE_IDENTIFIER_FEATURE_COUNT = FINITE_RATE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link uk.ac.ed.inf.pepa.emf.RateOperator <em>Rate Operator</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see uk.ac.ed.inf.pepa.emf.RateOperator
	 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateOperator()
	 * @generated
	 */
	int RATE_OPERATOR = 22;


	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Action <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Action
	 * @generated
	 */
	EClass getAction();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Activity <em>Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Activity</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Activity
	 * @generated
	 */
	EClass getActivity();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Activity#getRate <em>Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Rate</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Activity#getRate()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_Rate();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Activity#getAction <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Action</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Activity#getAction()
	 * @see #getActivity()
	 * @generated
	 */
	EReference getActivity_Action();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Aggregation <em>Aggregation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Aggregation</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Aggregation
	 * @generated
	 */
	EClass getAggregation();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Aggregation#getProcess <em>Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Process</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Aggregation#getProcess()
	 * @see #getAggregation()
	 * @generated
	 */
	EReference getAggregation_Process();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Aggregation#getCopies <em>Copies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Copies</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Aggregation#getCopies()
	 * @see #getAggregation()
	 * @generated
	 */
	EReference getAggregation_Copies();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.BinaryOperator <em>Binary Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binary Operator</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.BinaryOperator
	 * @generated
	 */
	EClass getBinaryOperator();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.BinaryOperator#getRightHandSide <em>Right Hand Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Right Hand Side</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.BinaryOperator#getRightHandSide()
	 * @see #getBinaryOperator()
	 * @generated
	 */
	EReference getBinaryOperator_RightHandSide();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.BinaryOperator#getLeftHandSide <em>Left Hand Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Left Hand Side</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.BinaryOperator#getLeftHandSide()
	 * @see #getBinaryOperator()
	 * @generated
	 */
	EReference getBinaryOperator_LeftHandSide();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Choice <em>Choice</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Choice</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Choice
	 * @generated
	 */
	EClass getChoice();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Cooperation <em>Cooperation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cooperation</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Cooperation
	 * @generated
	 */
	EClass getCooperation();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.FiniteRate <em>Finite Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Finite Rate</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.FiniteRate
	 * @generated
	 */
	EClass getFiniteRate();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Hiding <em>Hiding</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Hiding</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Hiding
	 * @generated
	 */
	EClass getHiding();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Hiding#getHiddenProcess <em>Hidden Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Hidden Process</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Hiding#getHiddenProcess()
	 * @see #getHiding()
	 * @generated
	 */
	EReference getHiding_HiddenProcess();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Model <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Model
	 * @generated
	 */
	EClass getModel();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Model#getSystemEquation <em>System Equation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>System Equation</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Model#getSystemEquation()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_SystemEquation();

	/**
	 * Returns the meta object for the containment reference list '{@link uk.ac.ed.inf.pepa.emf.Model#getProcessAssignments <em>Process Assignments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Process Assignments</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Model#getProcessAssignments()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_ProcessAssignments();

	/**
	 * Returns the meta object for the containment reference list '{@link uk.ac.ed.inf.pepa.emf.Model#getRateAssignments <em>Rate Assignments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rate Assignments</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Model#getRateAssignments()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_RateAssignments();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.PassiveRate <em>Passive Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Passive Rate</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.PassiveRate
	 * @generated
	 */
	EClass getPassiveRate();

	/**
	 * Returns the meta object for the attribute '{@link uk.ac.ed.inf.pepa.emf.PassiveRate#getWeight <em>Weight</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Weight</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.PassiveRate#getWeight()
	 * @see #getPassiveRate()
	 * @generated
	 */
	EAttribute getPassiveRate_Weight();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Prefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Prefix</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Prefix
	 * @generated
	 */
	EClass getPrefix();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Prefix#getTargetProcess <em>Target Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Target Process</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Prefix#getTargetProcess()
	 * @see #getPrefix()
	 * @generated
	 */
	EReference getPrefix_TargetProcess();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.Prefix#getActivity <em>Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Activity</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Prefix#getActivity()
	 * @see #getPrefix()
	 * @generated
	 */
	EReference getPrefix_Activity();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Process <em>Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Process</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Process
	 * @generated
	 */
	EClass getProcess();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.ProcessWithSet <em>Process With Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Process With Set</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessWithSet
	 * @generated
	 */
	EClass getProcessWithSet();

	/**
	 * Returns the meta object for the containment reference list '{@link uk.ac.ed.inf.pepa.emf.ProcessWithSet#getActions <em>Actions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Actions</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessWithSet#getActions()
	 * @see #getProcessWithSet()
	 * @generated
	 */
	EReference getProcessWithSet_Actions();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.Rate <em>Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rate</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.Rate
	 * @generated
	 */
	EClass getRate();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.SilentAction <em>Silent Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Silent Action</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.SilentAction
	 * @generated
	 */
	EClass getSilentAction();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.ActionIdentifier <em>Action Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action Identifier</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ActionIdentifier
	 * @generated
	 */
	EClass getActionIdentifier();

	/**
	 * Returns the meta object for the attribute '{@link uk.ac.ed.inf.pepa.emf.ActionIdentifier#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ActionIdentifier#getName()
	 * @see #getActionIdentifier()
	 * @generated
	 */
	EAttribute getActionIdentifier_Name();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.NumberLiteral <em>Number Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Number Literal</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.NumberLiteral
	 * @generated
	 */
	EClass getNumberLiteral();

	/**
	 * Returns the meta object for the attribute '{@link uk.ac.ed.inf.pepa.emf.NumberLiteral#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.NumberLiteral#getValue()
	 * @see #getNumberLiteral()
	 * @generated
	 */
	EAttribute getNumberLiteral_Value();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.ProcessAssignment <em>Process Assignment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Process Assignment</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessAssignment
	 * @generated
	 */
	EClass getProcessAssignment();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.ProcessAssignment#getProcessIdentifier <em>Process Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Process Identifier</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessAssignment#getProcessIdentifier()
	 * @see #getProcessAssignment()
	 * @generated
	 */
	EReference getProcessAssignment_ProcessIdentifier();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.ProcessAssignment#getProcess <em>Process</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Process</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessAssignment#getProcess()
	 * @see #getProcessAssignment()
	 * @generated
	 */
	EReference getProcessAssignment_Process();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.ProcessIdentifier <em>Process Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Process Identifier</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessIdentifier
	 * @generated
	 */
	EClass getProcessIdentifier();

	/**
	 * Returns the meta object for the attribute '{@link uk.ac.ed.inf.pepa.emf.ProcessIdentifier#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessIdentifier#getName()
	 * @see #getProcessIdentifier()
	 * @generated
	 */
	EAttribute getProcessIdentifier_Name();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.RateAssignment <em>Rate Assignment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rate Assignment</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateAssignment
	 * @generated
	 */
	EClass getRateAssignment();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.RateAssignment#getRateIdentifier <em>Rate Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Rate Identifier</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateAssignment#getRateIdentifier()
	 * @see #getRateAssignment()
	 * @generated
	 */
	EReference getRateAssignment_RateIdentifier();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.RateAssignment#getRate <em>Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Rate</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateAssignment#getRate()
	 * @see #getRateAssignment()
	 * @generated
	 */
	EReference getRateAssignment_Rate();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.RateExpression <em>Rate Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rate Expression</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateExpression
	 * @generated
	 */
	EClass getRateExpression();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.RateExpression#getLeftHandSide <em>Left Hand Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Left Hand Side</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateExpression#getLeftHandSide()
	 * @see #getRateExpression()
	 * @generated
	 */
	EReference getRateExpression_LeftHandSide();

	/**
	 * Returns the meta object for the containment reference '{@link uk.ac.ed.inf.pepa.emf.RateExpression#getRightHandSide <em>Right Hand Side</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Right Hand Side</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateExpression#getRightHandSide()
	 * @see #getRateExpression()
	 * @generated
	 */
	EReference getRateExpression_RightHandSide();

	/**
	 * Returns the meta object for the attribute '{@link uk.ac.ed.inf.pepa.emf.RateExpression#getOperator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operator</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateExpression#getOperator()
	 * @see #getRateExpression()
	 * @generated
	 */
	EAttribute getRateExpression_Operator();

	/**
	 * Returns the meta object for class '{@link uk.ac.ed.inf.pepa.emf.RateIdentifier <em>Rate Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rate Identifier</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateIdentifier
	 * @generated
	 */
	EClass getRateIdentifier();

	/**
	 * Returns the meta object for the attribute '{@link uk.ac.ed.inf.pepa.emf.RateIdentifier#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateIdentifier#getName()
	 * @see #getRateIdentifier()
	 * @generated
	 */
	EAttribute getRateIdentifier_Name();

	/**
	 * Returns the meta object for enum '{@link uk.ac.ed.inf.pepa.emf.RateOperator <em>Rate Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Rate Operator</em>'.
	 * @see uk.ac.ed.inf.pepa.emf.RateOperator
	 * @generated
	 */
	EEnum getRateOperator();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EmfFactory getEmfFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals  {
		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ActionImpl <em>Action</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ActionImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getAction()
		 * @generated
		 */
		EClass ACTION = eINSTANCE.getAction();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ActivityImpl <em>Activity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ActivityImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getActivity()
		 * @generated
		 */
		EClass ACTIVITY = eINSTANCE.getActivity();

		/**
		 * The meta object literal for the '<em><b>Rate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__RATE = eINSTANCE.getActivity_Rate();

		/**
		 * The meta object literal for the '<em><b>Action</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTIVITY__ACTION = eINSTANCE.getActivity_Action();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.AggregationImpl <em>Aggregation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.AggregationImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getAggregation()
		 * @generated
		 */
		EClass AGGREGATION = eINSTANCE.getAggregation();

		/**
		 * The meta object literal for the '<em><b>Process</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference AGGREGATION__PROCESS = eINSTANCE.getAggregation_Process();

		/**
		 * The meta object literal for the '<em><b>Copies</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference AGGREGATION__COPIES = eINSTANCE.getAggregation_Copies();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.BinaryOperatorImpl <em>Binary Operator</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.BinaryOperatorImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getBinaryOperator()
		 * @generated
		 */
		EClass BINARY_OPERATOR = eINSTANCE.getBinaryOperator();

		/**
		 * The meta object literal for the '<em><b>Right Hand Side</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINARY_OPERATOR__RIGHT_HAND_SIDE = eINSTANCE.getBinaryOperator_RightHandSide();

		/**
		 * The meta object literal for the '<em><b>Left Hand Side</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINARY_OPERATOR__LEFT_HAND_SIDE = eINSTANCE.getBinaryOperator_LeftHandSide();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ChoiceImpl <em>Choice</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ChoiceImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getChoice()
		 * @generated
		 */
		EClass CHOICE = eINSTANCE.getChoice();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.CooperationImpl <em>Cooperation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.CooperationImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getCooperation()
		 * @generated
		 */
		EClass COOPERATION = eINSTANCE.getCooperation();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.FiniteRateImpl <em>Finite Rate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.FiniteRateImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getFiniteRate()
		 * @generated
		 */
		EClass FINITE_RATE = eINSTANCE.getFiniteRate();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.HidingImpl <em>Hiding</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.HidingImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getHiding()
		 * @generated
		 */
		EClass HIDING = eINSTANCE.getHiding();

		/**
		 * The meta object literal for the '<em><b>Hidden Process</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HIDING__HIDDEN_PROCESS = eINSTANCE.getHiding_HiddenProcess();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ModelImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getModel()
		 * @generated
		 */
		EClass MODEL = eINSTANCE.getModel();

		/**
		 * The meta object literal for the '<em><b>System Equation</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__SYSTEM_EQUATION = eINSTANCE.getModel_SystemEquation();

		/**
		 * The meta object literal for the '<em><b>Process Assignments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__PROCESS_ASSIGNMENTS = eINSTANCE.getModel_ProcessAssignments();

		/**
		 * The meta object literal for the '<em><b>Rate Assignments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__RATE_ASSIGNMENTS = eINSTANCE.getModel_RateAssignments();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.PassiveRateImpl <em>Passive Rate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.PassiveRateImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getPassiveRate()
		 * @generated
		 */
		EClass PASSIVE_RATE = eINSTANCE.getPassiveRate();

		/**
		 * The meta object literal for the '<em><b>Weight</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PASSIVE_RATE__WEIGHT = eINSTANCE.getPassiveRate_Weight();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.PrefixImpl <em>Prefix</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.PrefixImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getPrefix()
		 * @generated
		 */
		EClass PREFIX = eINSTANCE.getPrefix();

		/**
		 * The meta object literal for the '<em><b>Target Process</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PREFIX__TARGET_PROCESS = eINSTANCE.getPrefix_TargetProcess();

		/**
		 * The meta object literal for the '<em><b>Activity</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PREFIX__ACTIVITY = eINSTANCE.getPrefix_Activity();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessImpl <em>Process</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcess()
		 * @generated
		 */
		EClass PROCESS = eINSTANCE.getProcess();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessWithSetImpl <em>Process With Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessWithSetImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcessWithSet()
		 * @generated
		 */
		EClass PROCESS_WITH_SET = eINSTANCE.getProcessWithSet();

		/**
		 * The meta object literal for the '<em><b>Actions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROCESS_WITH_SET__ACTIONS = eINSTANCE.getProcessWithSet_Actions();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateImpl <em>Rate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.RateImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRate()
		 * @generated
		 */
		EClass RATE = eINSTANCE.getRate();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.SilentActionImpl <em>Silent Action</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.SilentActionImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getSilentAction()
		 * @generated
		 */
		EClass SILENT_ACTION = eINSTANCE.getSilentAction();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ActionIdentifierImpl <em>Action Identifier</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ActionIdentifierImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getActionIdentifier()
		 * @generated
		 */
		EClass ACTION_IDENTIFIER = eINSTANCE.getActionIdentifier();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTION_IDENTIFIER__NAME = eINSTANCE.getActionIdentifier_Name();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.NumberLiteralImpl <em>Number Literal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.NumberLiteralImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getNumberLiteral()
		 * @generated
		 */
		EClass NUMBER_LITERAL = eINSTANCE.getNumberLiteral();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMBER_LITERAL__VALUE = eINSTANCE.getNumberLiteral_Value();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessAssignmentImpl <em>Process Assignment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessAssignmentImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcessAssignment()
		 * @generated
		 */
		EClass PROCESS_ASSIGNMENT = eINSTANCE.getProcessAssignment();

		/**
		 * The meta object literal for the '<em><b>Process Identifier</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER = eINSTANCE.getProcessAssignment_ProcessIdentifier();

		/**
		 * The meta object literal for the '<em><b>Process</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROCESS_ASSIGNMENT__PROCESS = eINSTANCE.getProcessAssignment_Process();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.ProcessIdentifierImpl <em>Process Identifier</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.ProcessIdentifierImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getProcessIdentifier()
		 * @generated
		 */
		EClass PROCESS_IDENTIFIER = eINSTANCE.getProcessIdentifier();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROCESS_IDENTIFIER__NAME = eINSTANCE.getProcessIdentifier_Name();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateAssignmentImpl <em>Rate Assignment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.RateAssignmentImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateAssignment()
		 * @generated
		 */
		EClass RATE_ASSIGNMENT = eINSTANCE.getRateAssignment();

		/**
		 * The meta object literal for the '<em><b>Rate Identifier</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RATE_ASSIGNMENT__RATE_IDENTIFIER = eINSTANCE.getRateAssignment_RateIdentifier();

		/**
		 * The meta object literal for the '<em><b>Rate</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RATE_ASSIGNMENT__RATE = eINSTANCE.getRateAssignment_Rate();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateExpressionImpl <em>Rate Expression</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.RateExpressionImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateExpression()
		 * @generated
		 */
		EClass RATE_EXPRESSION = eINSTANCE.getRateExpression();

		/**
		 * The meta object literal for the '<em><b>Left Hand Side</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RATE_EXPRESSION__LEFT_HAND_SIDE = eINSTANCE.getRateExpression_LeftHandSide();

		/**
		 * The meta object literal for the '<em><b>Right Hand Side</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RATE_EXPRESSION__RIGHT_HAND_SIDE = eINSTANCE.getRateExpression_RightHandSide();

		/**
		 * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RATE_EXPRESSION__OPERATOR = eINSTANCE.getRateExpression_Operator();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.impl.RateIdentifierImpl <em>Rate Identifier</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.impl.RateIdentifierImpl
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateIdentifier()
		 * @generated
		 */
		EClass RATE_IDENTIFIER = eINSTANCE.getRateIdentifier();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RATE_IDENTIFIER__NAME = eINSTANCE.getRateIdentifier_Name();

		/**
		 * The meta object literal for the '{@link uk.ac.ed.inf.pepa.emf.RateOperator <em>Rate Operator</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see uk.ac.ed.inf.pepa.emf.RateOperator
		 * @see uk.ac.ed.inf.pepa.emf.impl.EmfPackageImpl#getRateOperator()
		 * @generated
		 */
		EEnum RATE_OPERATOR = eINSTANCE.getRateOperator();

	}

} //EmfPackage
