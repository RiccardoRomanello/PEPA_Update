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

import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.Hiding;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hiding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.HidingImpl#getHiddenProcess <em>Hidden Process</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HidingImpl extends ProcessWithSetImpl implements Hiding {
	/**
	 * The cached value of the '{@link #getHiddenProcess() <em>Hidden Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHiddenProcess()
	 * @generated
	 * @ordered
	 */
	protected uk.ac.ed.inf.pepa.emf.Process hiddenProcess;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HidingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return EmfPackage.Literals.HIDING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public uk.ac.ed.inf.pepa.emf.Process getHiddenProcess() {
		return hiddenProcess;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetHiddenProcess(uk.ac.ed.inf.pepa.emf.Process newHiddenProcess, NotificationChain msgs) {
		uk.ac.ed.inf.pepa.emf.Process oldHiddenProcess = hiddenProcess;
		hiddenProcess = newHiddenProcess;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.HIDING__HIDDEN_PROCESS, oldHiddenProcess, newHiddenProcess);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHiddenProcess(uk.ac.ed.inf.pepa.emf.Process newHiddenProcess) {
		if (newHiddenProcess != hiddenProcess) {
			NotificationChain msgs = null;
			if (hiddenProcess != null)
				msgs = ((InternalEObject)hiddenProcess).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.HIDING__HIDDEN_PROCESS, null, msgs);
			if (newHiddenProcess != null)
				msgs = ((InternalEObject)newHiddenProcess).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.HIDING__HIDDEN_PROCESS, null, msgs);
			msgs = basicSetHiddenProcess(newHiddenProcess, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.HIDING__HIDDEN_PROCESS, newHiddenProcess, newHiddenProcess));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmfPackage.HIDING__HIDDEN_PROCESS:
				return basicSetHiddenProcess(null, msgs);
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
			case EmfPackage.HIDING__HIDDEN_PROCESS:
				return getHiddenProcess();
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
			case EmfPackage.HIDING__HIDDEN_PROCESS:
				setHiddenProcess((uk.ac.ed.inf.pepa.emf.Process)newValue);
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
			case EmfPackage.HIDING__HIDDEN_PROCESS:
				setHiddenProcess((uk.ac.ed.inf.pepa.emf.Process)null);
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
			case EmfPackage.HIDING__HIDDEN_PROCESS:
				return hiddenProcess != null;
		}
		return super.eIsSet(featureID);
	}

} //HidingImpl