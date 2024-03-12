/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import uk.ac.ed.inf.pepa.emf.Action;
import uk.ac.ed.inf.pepa.emf.Activity;
import uk.ac.ed.inf.pepa.emf.Aggregation;
import uk.ac.ed.inf.pepa.emf.BinaryOperator;
import uk.ac.ed.inf.pepa.emf.Choice;
import uk.ac.ed.inf.pepa.emf.ProcessIdentifier;
import uk.ac.ed.inf.pepa.emf.Cooperation;
import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.FiniteRate;
import uk.ac.ed.inf.pepa.emf.Hiding;
import uk.ac.ed.inf.pepa.emf.Model;
import uk.ac.ed.inf.pepa.emf.NumberLiteral;
import uk.ac.ed.inf.pepa.emf.ActionIdentifier;
import uk.ac.ed.inf.pepa.emf.RateIdentifier;
import uk.ac.ed.inf.pepa.emf.PassiveRate;
import uk.ac.ed.inf.pepa.emf.Prefix;
import uk.ac.ed.inf.pepa.emf.ProcessAssignment;
import uk.ac.ed.inf.pepa.emf.ProcessWithSet;
import uk.ac.ed.inf.pepa.emf.Rate;
import uk.ac.ed.inf.pepa.emf.RateAssignment;
import uk.ac.ed.inf.pepa.emf.RateExpression;
import uk.ac.ed.inf.pepa.emf.SilentAction;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see uk.ac.ed.inf.pepa.emf.EmfPackage
 * @generated
 */
public class EmfAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EmfPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EmfAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = EmfPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EmfSwitch modelSwitch =
		new EmfSwitch() {
			public Object caseAction(Action object) {
				return createActionAdapter();
			}
			public Object caseActivity(Activity object) {
				return createActivityAdapter();
			}
			public Object caseAggregation(Aggregation object) {
				return createAggregationAdapter();
			}
			public Object caseBinaryOperator(BinaryOperator object) {
				return createBinaryOperatorAdapter();
			}
			public Object caseChoice(Choice object) {
				return createChoiceAdapter();
			}
			public Object caseCooperation(Cooperation object) {
				return createCooperationAdapter();
			}
			public Object caseFiniteRate(FiniteRate object) {
				return createFiniteRateAdapter();
			}
			public Object caseHiding(Hiding object) {
				return createHidingAdapter();
			}
			public Object caseModel(Model object) {
				return createModelAdapter();
			}
			public Object casePassiveRate(PassiveRate object) {
				return createPassiveRateAdapter();
			}
			public Object casePrefix(Prefix object) {
				return createPrefixAdapter();
			}
			public Object caseProcess(uk.ac.ed.inf.pepa.emf.Process object) {
				return createProcessAdapter();
			}
			public Object caseProcessWithSet(ProcessWithSet object) {
				return createProcessWithSetAdapter();
			}
			public Object caseRate(Rate object) {
				return createRateAdapter();
			}
			public Object caseSilentAction(SilentAction object) {
				return createSilentActionAdapter();
			}
			public Object caseActionIdentifier(ActionIdentifier object) {
				return createActionIdentifierAdapter();
			}
			public Object caseNumberLiteral(NumberLiteral object) {
				return createNumberLiteralAdapter();
			}
			public Object caseProcessAssignment(ProcessAssignment object) {
				return createProcessAssignmentAdapter();
			}
			public Object caseProcessIdentifier(ProcessIdentifier object) {
				return createProcessIdentifierAdapter();
			}
			public Object caseRateAssignment(RateAssignment object) {
				return createRateAssignmentAdapter();
			}
			public Object caseRateExpression(RateExpression object) {
				return createRateExpressionAdapter();
			}
			public Object caseRateIdentifier(RateIdentifier object) {
				return createRateIdentifierAdapter();
			}
			public Object defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	public Adapter createAdapter(Notifier target) {
		return (Adapter)modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Action <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Action
	 * @generated
	 */
	public Adapter createActionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Activity <em>Activity</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Activity
	 * @generated
	 */
	public Adapter createActivityAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Aggregation <em>Aggregation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Aggregation
	 * @generated
	 */
	public Adapter createAggregationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.BinaryOperator <em>Binary Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.BinaryOperator
	 * @generated
	 */
	public Adapter createBinaryOperatorAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Choice <em>Choice</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Choice
	 * @generated
	 */
	public Adapter createChoiceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Cooperation <em>Cooperation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Cooperation
	 * @generated
	 */
	public Adapter createCooperationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.FiniteRate <em>Finite Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.FiniteRate
	 * @generated
	 */
	public Adapter createFiniteRateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Hiding <em>Hiding</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Hiding
	 * @generated
	 */
	public Adapter createHidingAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Model <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Model
	 * @generated
	 */
	public Adapter createModelAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.PassiveRate <em>Passive Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.PassiveRate
	 * @generated
	 */
	public Adapter createPassiveRateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Prefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Prefix
	 * @generated
	 */
	public Adapter createPrefixAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Process <em>Process</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Process
	 * @generated
	 */
	public Adapter createProcessAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.ProcessWithSet <em>Process With Set</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessWithSet
	 * @generated
	 */
	public Adapter createProcessWithSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.Rate <em>Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.Rate
	 * @generated
	 */
	public Adapter createRateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.SilentAction <em>Silent Action</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.SilentAction
	 * @generated
	 */
	public Adapter createSilentActionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.ActionIdentifier <em>Action Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.ActionIdentifier
	 * @generated
	 */
	public Adapter createActionIdentifierAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.NumberLiteral <em>Number Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.NumberLiteral
	 * @generated
	 */
	public Adapter createNumberLiteralAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.ProcessAssignment <em>Process Assignment</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessAssignment
	 * @generated
	 */
	public Adapter createProcessAssignmentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.ProcessIdentifier <em>Process Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.ProcessIdentifier
	 * @generated
	 */
	public Adapter createProcessIdentifierAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.RateAssignment <em>Rate Assignment</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.RateAssignment
	 * @generated
	 */
	public Adapter createRateAssignmentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.RateExpression <em>Rate Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.RateExpression
	 * @generated
	 */
	public Adapter createRateExpressionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link uk.ac.ed.inf.pepa.emf.RateIdentifier <em>Rate Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see uk.ac.ed.inf.pepa.emf.RateIdentifier
	 * @generated
	 */
	public Adapter createRateIdentifierAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //EmfAdapterFactory
