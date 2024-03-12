/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
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
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see uk.ac.ed.inf.pepa.emf.EmfPackage
 * @generated
 */
public class EmfSwitch {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EmfPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EmfSwitch() {
		if (modelPackage == null) {
			modelPackage = EmfPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public Object doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch((EClass)eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case EmfPackage.ACTION: {
				Action action = (Action)theEObject;
				Object result = caseAction(action);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.ACTIVITY: {
				Activity activity = (Activity)theEObject;
				Object result = caseActivity(activity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.AGGREGATION: {
				Aggregation aggregation = (Aggregation)theEObject;
				Object result = caseAggregation(aggregation);
				if (result == null) result = caseProcess(aggregation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.BINARY_OPERATOR: {
				BinaryOperator binaryOperator = (BinaryOperator)theEObject;
				Object result = caseBinaryOperator(binaryOperator);
				if (result == null) result = caseProcess(binaryOperator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.CHOICE: {
				Choice choice = (Choice)theEObject;
				Object result = caseChoice(choice);
				if (result == null) result = caseBinaryOperator(choice);
				if (result == null) result = caseProcess(choice);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.COOPERATION: {
				Cooperation cooperation = (Cooperation)theEObject;
				Object result = caseCooperation(cooperation);
				if (result == null) result = caseBinaryOperator(cooperation);
				if (result == null) result = caseProcessWithSet(cooperation);
				if (result == null) result = caseProcess(cooperation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.FINITE_RATE: {
				FiniteRate finiteRate = (FiniteRate)theEObject;
				Object result = caseFiniteRate(finiteRate);
				if (result == null) result = caseRate(finiteRate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.HIDING: {
				Hiding hiding = (Hiding)theEObject;
				Object result = caseHiding(hiding);
				if (result == null) result = caseProcessWithSet(hiding);
				if (result == null) result = caseProcess(hiding);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.MODEL: {
				Model model = (Model)theEObject;
				Object result = caseModel(model);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.PASSIVE_RATE: {
				PassiveRate passiveRate = (PassiveRate)theEObject;
				Object result = casePassiveRate(passiveRate);
				if (result == null) result = caseRate(passiveRate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.PREFIX: {
				Prefix prefix = (Prefix)theEObject;
				Object result = casePrefix(prefix);
				if (result == null) result = caseProcess(prefix);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.PROCESS: {
				uk.ac.ed.inf.pepa.emf.Process process = (uk.ac.ed.inf.pepa.emf.Process)theEObject;
				Object result = caseProcess(process);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.PROCESS_WITH_SET: {
				ProcessWithSet processWithSet = (ProcessWithSet)theEObject;
				Object result = caseProcessWithSet(processWithSet);
				if (result == null) result = caseProcess(processWithSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.RATE: {
				Rate rate = (Rate)theEObject;
				Object result = caseRate(rate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.SILENT_ACTION: {
				SilentAction silentAction = (SilentAction)theEObject;
				Object result = caseSilentAction(silentAction);
				if (result == null) result = caseAction(silentAction);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.ACTION_IDENTIFIER: {
				ActionIdentifier actionIdentifier = (ActionIdentifier)theEObject;
				Object result = caseActionIdentifier(actionIdentifier);
				if (result == null) result = caseAction(actionIdentifier);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.NUMBER_LITERAL: {
				NumberLiteral numberLiteral = (NumberLiteral)theEObject;
				Object result = caseNumberLiteral(numberLiteral);
				if (result == null) result = caseFiniteRate(numberLiteral);
				if (result == null) result = caseRate(numberLiteral);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.PROCESS_ASSIGNMENT: {
				ProcessAssignment processAssignment = (ProcessAssignment)theEObject;
				Object result = caseProcessAssignment(processAssignment);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.PROCESS_IDENTIFIER: {
				ProcessIdentifier processIdentifier = (ProcessIdentifier)theEObject;
				Object result = caseProcessIdentifier(processIdentifier);
				if (result == null) result = caseProcess(processIdentifier);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.RATE_ASSIGNMENT: {
				RateAssignment rateAssignment = (RateAssignment)theEObject;
				Object result = caseRateAssignment(rateAssignment);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.RATE_EXPRESSION: {
				RateExpression rateExpression = (RateExpression)theEObject;
				Object result = caseRateExpression(rateExpression);
				if (result == null) result = caseFiniteRate(rateExpression);
				if (result == null) result = caseRate(rateExpression);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case EmfPackage.RATE_IDENTIFIER: {
				RateIdentifier rateIdentifier = (RateIdentifier)theEObject;
				Object result = caseRateIdentifier(rateIdentifier);
				if (result == null) result = caseFiniteRate(rateIdentifier);
				if (result == null) result = caseRate(rateIdentifier);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseAction(Action object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Activity</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Activity</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseActivity(Activity object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Aggregation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Aggregation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseAggregation(Aggregation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Binary Operator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Binary Operator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseBinaryOperator(BinaryOperator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Choice</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Choice</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseChoice(Choice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cooperation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cooperation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseCooperation(Cooperation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Finite Rate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Finite Rate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseFiniteRate(FiniteRate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Hiding</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Hiding</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseHiding(Hiding object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseModel(Model object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Passive Rate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Passive Rate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object casePassiveRate(PassiveRate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Prefix</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Prefix</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object casePrefix(Prefix object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Process</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Process</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProcess(uk.ac.ed.inf.pepa.emf.Process object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Process With Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Process With Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProcessWithSet(ProcessWithSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseRate(Rate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Silent Action</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Silent Action</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseSilentAction(SilentAction object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Identifier</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Identifier</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseActionIdentifier(ActionIdentifier object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Number Literal</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Number Literal</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseNumberLiteral(NumberLiteral object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Process Assignment</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Process Assignment</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProcessAssignment(ProcessAssignment object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Process Identifier</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Process Identifier</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProcessIdentifier(ProcessIdentifier object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rate Assignment</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rate Assignment</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseRateAssignment(RateAssignment object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rate Expression</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rate Expression</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseRateExpression(RateExpression object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rate Identifier</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rate Identifier</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseRateIdentifier(RateIdentifier object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public Object defaultCase(EObject object) {
		return null;
	}

} //EmfSwitch
