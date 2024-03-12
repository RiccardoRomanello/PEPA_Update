/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.ProcessAssignment;
import uk.ac.ed.inf.pepa.emf.ProcessIdentifier;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Process Assignment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.ProcessAssignmentImpl#getProcessIdentifier <em>Process Identifier</em>}</li>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.ProcessAssignmentImpl#getProcess <em>Process</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProcessAssignmentImpl extends EObjectImpl implements ProcessAssignment {
	/**
	 * The cached value of the '{@link #getProcessIdentifier() <em>Process Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessIdentifier()
	 * @generated
	 * @ordered
	 */
	protected ProcessIdentifier processIdentifier;

	/**
	 * The cached value of the '{@link #getProcess() <em>Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcess()
	 * @generated
	 * @ordered
	 */
	protected uk.ac.ed.inf.pepa.emf.Process process;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProcessAssignmentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return EmfPackage.Literals.PROCESS_ASSIGNMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProcessIdentifier getProcessIdentifier() {
		return processIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProcessIdentifier(ProcessIdentifier newProcessIdentifier, NotificationChain msgs) {
		ProcessIdentifier oldProcessIdentifier = processIdentifier;
		processIdentifier = newProcessIdentifier;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER, oldProcessIdentifier, newProcessIdentifier);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProcessIdentifier(ProcessIdentifier newProcessIdentifier) {
		if (newProcessIdentifier != processIdentifier) {
			NotificationChain msgs = null;
			if (processIdentifier != null)
				msgs = ((InternalEObject)processIdentifier).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER, null, msgs);
			if (newProcessIdentifier != null)
				msgs = ((InternalEObject)newProcessIdentifier).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER, null, msgs);
			msgs = basicSetProcessIdentifier(newProcessIdentifier, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER, newProcessIdentifier, newProcessIdentifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public uk.ac.ed.inf.pepa.emf.Process getProcess() {
		return process;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProcess(uk.ac.ed.inf.pepa.emf.Process newProcess, NotificationChain msgs) {
		uk.ac.ed.inf.pepa.emf.Process oldProcess = process;
		process = newProcess;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.PROCESS_ASSIGNMENT__PROCESS, oldProcess, newProcess);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProcess(uk.ac.ed.inf.pepa.emf.Process newProcess) {
		if (newProcess != process) {
			NotificationChain msgs = null;
			if (process != null)
				msgs = ((InternalEObject)process).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PROCESS_ASSIGNMENT__PROCESS, null, msgs);
			if (newProcess != null)
				msgs = ((InternalEObject)newProcess).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PROCESS_ASSIGNMENT__PROCESS, null, msgs);
			msgs = basicSetProcess(newProcess, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.PROCESS_ASSIGNMENT__PROCESS, newProcess, newProcess));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER:
				return basicSetProcessIdentifier(null, msgs);
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS:
				return basicSetProcess(null, msgs);
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
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER:
				return getProcessIdentifier();
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS:
				return getProcess();
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
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER:
				setProcessIdentifier((ProcessIdentifier)newValue);
				return;
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS:
				setProcess((uk.ac.ed.inf.pepa.emf.Process)newValue);
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
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER:
				setProcessIdentifier((ProcessIdentifier)null);
				return;
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS:
				setProcess((uk.ac.ed.inf.pepa.emf.Process)null);
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
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS_IDENTIFIER:
				return processIdentifier != null;
			case EmfPackage.PROCESS_ASSIGNMENT__PROCESS:
				return process != null;
		}
		return super.eIsSet(featureID);
	}

} //ProcessAssignmentImpl