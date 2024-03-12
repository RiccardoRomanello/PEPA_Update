/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import uk.ac.ed.inf.pepa.emf.Activity;
import uk.ac.ed.inf.pepa.emf.Aggregation;
import uk.ac.ed.inf.pepa.emf.Choice;
import uk.ac.ed.inf.pepa.emf.ProcessIdentifier;
import uk.ac.ed.inf.pepa.emf.RateAssignment;
import uk.ac.ed.inf.pepa.emf.RateExpression;
import uk.ac.ed.inf.pepa.emf.Cooperation;
import uk.ac.ed.inf.pepa.emf.EmfFactory;
import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.FiniteRate;
import uk.ac.ed.inf.pepa.emf.Hiding;
import uk.ac.ed.inf.pepa.emf.Model;
import uk.ac.ed.inf.pepa.emf.NumberLiteral;
import uk.ac.ed.inf.pepa.emf.ActionIdentifier;
import uk.ac.ed.inf.pepa.emf.RateIdentifier;
import uk.ac.ed.inf.pepa.emf.RateOperator;
import uk.ac.ed.inf.pepa.emf.PassiveRate;
import uk.ac.ed.inf.pepa.emf.Prefix;
import uk.ac.ed.inf.pepa.emf.ProcessAssignment;
import uk.ac.ed.inf.pepa.emf.SilentAction;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EmfFactoryImpl extends EFactoryImpl implements EmfFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EmfFactory init() {
		try {
			EmfFactory theEmfFactory = (EmfFactory)EPackage.Registry.INSTANCE.getEFactory("http:///uk/ac/ed/inf/pepa/emf.ecore"); 
			if (theEmfFactory != null) {
				return theEmfFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EmfFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EmfFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case EmfPackage.ACTIVITY: return createActivity();
			case EmfPackage.AGGREGATION: return createAggregation();
			case EmfPackage.CHOICE: return createChoice();
			case EmfPackage.COOPERATION: return createCooperation();
			case EmfPackage.HIDING: return createHiding();
			case EmfPackage.MODEL: return createModel();
			case EmfPackage.PASSIVE_RATE: return createPassiveRate();
			case EmfPackage.PREFIX: return createPrefix();
			case EmfPackage.SILENT_ACTION: return createSilentAction();
			case EmfPackage.ACTION_IDENTIFIER: return createActionIdentifier();
			case EmfPackage.NUMBER_LITERAL: return createNumberLiteral();
			case EmfPackage.PROCESS_ASSIGNMENT: return createProcessAssignment();
			case EmfPackage.PROCESS_IDENTIFIER: return createProcessIdentifier();
			case EmfPackage.RATE_ASSIGNMENT: return createRateAssignment();
			case EmfPackage.RATE_EXPRESSION: return createRateExpression();
			case EmfPackage.RATE_IDENTIFIER: return createRateIdentifier();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case EmfPackage.RATE_OPERATOR:
				return createRateOperatorFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case EmfPackage.RATE_OPERATOR:
				return convertRateOperatorToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Activity createActivity() {
		ActivityImpl activity = new ActivityImpl();
		return activity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Aggregation createAggregation() {
		AggregationImpl aggregation = new AggregationImpl();
		return aggregation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Choice createChoice() {
		ChoiceImpl choice = new ChoiceImpl();
		return choice;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Cooperation createCooperation() {
		CooperationImpl cooperation = new CooperationImpl();
		return cooperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Hiding createHiding() {
		HidingImpl hiding = new HidingImpl();
		return hiding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Model createModel() {
		ModelImpl model = new ModelImpl();
		return model;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PassiveRate createPassiveRate() {
		PassiveRateImpl passiveRate = new PassiveRateImpl();
		return passiveRate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Prefix createPrefix() {
		PrefixImpl prefix = new PrefixImpl();
		return prefix;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SilentAction createSilentAction() {
		SilentActionImpl silentAction = new SilentActionImpl();
		return silentAction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ActionIdentifier createActionIdentifier() {
		ActionIdentifierImpl actionIdentifier = new ActionIdentifierImpl();
		return actionIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumberLiteral createNumberLiteral() {
		NumberLiteralImpl numberLiteral = new NumberLiteralImpl();
		return numberLiteral;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProcessAssignment createProcessAssignment() {
		ProcessAssignmentImpl processAssignment = new ProcessAssignmentImpl();
		return processAssignment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProcessIdentifier createProcessIdentifier() {
		ProcessIdentifierImpl processIdentifier = new ProcessIdentifierImpl();
		return processIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RateAssignment createRateAssignment() {
		RateAssignmentImpl rateAssignment = new RateAssignmentImpl();
		return rateAssignment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RateExpression createRateExpression() {
		RateExpressionImpl rateExpression = new RateExpressionImpl();
		return rateExpression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RateIdentifier createRateIdentifier() {
		RateIdentifierImpl rateIdentifier = new RateIdentifierImpl();
		return rateIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RateOperator createRateOperatorFromString(EDataType eDataType, String initialValue) {
		RateOperator result = RateOperator.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertRateOperatorToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EmfPackage getEmfPackage() {
		return (EmfPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static EmfPackage getPackage() {
		return EmfPackage.eINSTANCE;
	}

} //EmfFactoryImpl
