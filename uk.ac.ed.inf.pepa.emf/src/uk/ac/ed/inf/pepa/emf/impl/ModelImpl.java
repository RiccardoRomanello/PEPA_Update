/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import uk.ac.ed.inf.pepa.emf.ProcessIdentifier;
import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.Model;
import uk.ac.ed.inf.pepa.emf.ProcessAssignment;

import uk.ac.ed.inf.pepa.emf.RateAssignment;

import uk.ac.ed.inf.pepa.emf.RateIdentifier;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.ModelImpl#getSystemEquation <em>System Equation</em>}</li>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.ModelImpl#getProcessAssignments <em>Process Assignments</em>}</li>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.ModelImpl#getRateAssignments <em>Rate Assignments</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelImpl extends EObjectImpl implements Model {
	/**
	 * The cached value of the '{@link #getSystemEquation() <em>System Equation</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSystemEquation()
	 * @generated
	 * @ordered
	 */
	protected uk.ac.ed.inf.pepa.emf.Process systemEquation;

	/**
	 * The cached value of the '{@link #getProcessAssignments() <em>Process Assignments</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessAssignments()
	 * @generated
	 * @ordered
	 */
	protected EList processAssignments;

	/**
	 * The cached value of the '{@link #getRateAssignments() <em>Rate Assignments</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRateAssignments()
	 * @generated
	 * @ordered
	 */
	protected EList rateAssignments;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return EmfPackage.Literals.MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public uk.ac.ed.inf.pepa.emf.Process getSystemEquation() {
		return systemEquation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSystemEquation(uk.ac.ed.inf.pepa.emf.Process newSystemEquation, NotificationChain msgs) {
		uk.ac.ed.inf.pepa.emf.Process oldSystemEquation = systemEquation;
		systemEquation = newSystemEquation;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.MODEL__SYSTEM_EQUATION, oldSystemEquation, newSystemEquation);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSystemEquation(uk.ac.ed.inf.pepa.emf.Process newSystemEquation) {
		if (newSystemEquation != systemEquation) {
			NotificationChain msgs = null;
			if (systemEquation != null)
				msgs = ((InternalEObject)systemEquation).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.MODEL__SYSTEM_EQUATION, null, msgs);
			if (newSystemEquation != null)
				msgs = ((InternalEObject)newSystemEquation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.MODEL__SYSTEM_EQUATION, null, msgs);
			msgs = basicSetSystemEquation(newSystemEquation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.MODEL__SYSTEM_EQUATION, newSystemEquation, newSystemEquation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getProcessAssignments() {
		if (processAssignments == null) {
			processAssignments = new EObjectContainmentEList(ProcessAssignment.class, this, EmfPackage.MODEL__PROCESS_ASSIGNMENTS);
		}
		return processAssignments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getRateAssignments() {
		if (rateAssignments == null) {
			rateAssignments = new EObjectContainmentEList(RateAssignment.class, this, EmfPackage.MODEL__RATE_ASSIGNMENTS);
		}
		return rateAssignments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmfPackage.MODEL__SYSTEM_EQUATION:
				return basicSetSystemEquation(null, msgs);
			case EmfPackage.MODEL__PROCESS_ASSIGNMENTS:
				return ((InternalEList)getProcessAssignments()).basicRemove(otherEnd, msgs);
			case EmfPackage.MODEL__RATE_ASSIGNMENTS:
				return ((InternalEList)getRateAssignments()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EmfPackage.MODEL__SYSTEM_EQUATION:
				return getSystemEquation();
			case EmfPackage.MODEL__PROCESS_ASSIGNMENTS:
				return getProcessAssignments();
			case EmfPackage.MODEL__RATE_ASSIGNMENTS:
				return getRateAssignments();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EmfPackage.MODEL__SYSTEM_EQUATION:
				setSystemEquation((uk.ac.ed.inf.pepa.emf.Process)newValue);
				return;
			case EmfPackage.MODEL__PROCESS_ASSIGNMENTS:
				getProcessAssignments().clear();
				getProcessAssignments().addAll((Collection)newValue);
				return;
			case EmfPackage.MODEL__RATE_ASSIGNMENTS:
				getRateAssignments().clear();
				getRateAssignments().addAll((Collection)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case EmfPackage.MODEL__SYSTEM_EQUATION:
				setSystemEquation((uk.ac.ed.inf.pepa.emf.Process)null);
				return;
			case EmfPackage.MODEL__PROCESS_ASSIGNMENTS:
				getProcessAssignments().clear();
				return;
			case EmfPackage.MODEL__RATE_ASSIGNMENTS:
				getRateAssignments().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EmfPackage.MODEL__SYSTEM_EQUATION:
				return systemEquation != null;
			case EmfPackage.MODEL__PROCESS_ASSIGNMENTS:
				return processAssignments != null && !processAssignments.isEmpty();
			case EmfPackage.MODEL__RATE_ASSIGNMENTS:
				return rateAssignments != null && !rateAssignments.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ModelImpl