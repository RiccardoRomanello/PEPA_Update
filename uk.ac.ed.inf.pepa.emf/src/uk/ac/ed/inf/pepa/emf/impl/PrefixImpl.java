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

import uk.ac.ed.inf.pepa.emf.Activity;
import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.Prefix;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Prefix</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.PrefixImpl#getTargetProcess <em>Target Process</em>}</li>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.PrefixImpl#getActivity <em>Activity</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PrefixImpl extends ProcessImpl implements Prefix {
	/**
	 * The cached value of the '{@link #getTargetProcess() <em>Target Process</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetProcess()
	 * @generated
	 * @ordered
	 */
	protected uk.ac.ed.inf.pepa.emf.Process targetProcess;

	/**
	 * The cached value of the '{@link #getActivity() <em>Activity</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivity()
	 * @generated
	 * @ordered
	 */
	protected Activity activity;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PrefixImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return EmfPackage.Literals.PREFIX;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public uk.ac.ed.inf.pepa.emf.Process getTargetProcess() {
		return targetProcess;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTargetProcess(uk.ac.ed.inf.pepa.emf.Process newTargetProcess, NotificationChain msgs) {
		uk.ac.ed.inf.pepa.emf.Process oldTargetProcess = targetProcess;
		targetProcess = newTargetProcess;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.PREFIX__TARGET_PROCESS, oldTargetProcess, newTargetProcess);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetProcess(uk.ac.ed.inf.pepa.emf.Process newTargetProcess) {
		if (newTargetProcess != targetProcess) {
			NotificationChain msgs = null;
			if (targetProcess != null)
				msgs = ((InternalEObject)targetProcess).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PREFIX__TARGET_PROCESS, null, msgs);
			if (newTargetProcess != null)
				msgs = ((InternalEObject)newTargetProcess).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PREFIX__TARGET_PROCESS, null, msgs);
			msgs = basicSetTargetProcess(newTargetProcess, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.PREFIX__TARGET_PROCESS, newTargetProcess, newTargetProcess));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetActivity(Activity newActivity, NotificationChain msgs) {
		Activity oldActivity = activity;
		activity = newActivity;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.PREFIX__ACTIVITY, oldActivity, newActivity);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setActivity(Activity newActivity) {
		if (newActivity != activity) {
			NotificationChain msgs = null;
			if (activity != null)
				msgs = ((InternalEObject)activity).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PREFIX__ACTIVITY, null, msgs);
			if (newActivity != null)
				msgs = ((InternalEObject)newActivity).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.PREFIX__ACTIVITY, null, msgs);
			msgs = basicSetActivity(newActivity, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.PREFIX__ACTIVITY, newActivity, newActivity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmfPackage.PREFIX__TARGET_PROCESS:
				return basicSetTargetProcess(null, msgs);
			case EmfPackage.PREFIX__ACTIVITY:
				return basicSetActivity(null, msgs);
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
			case EmfPackage.PREFIX__TARGET_PROCESS:
				return getTargetProcess();
			case EmfPackage.PREFIX__ACTIVITY:
				return getActivity();
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
			case EmfPackage.PREFIX__TARGET_PROCESS:
				setTargetProcess((uk.ac.ed.inf.pepa.emf.Process)newValue);
				return;
			case EmfPackage.PREFIX__ACTIVITY:
				setActivity((Activity)newValue);
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
			case EmfPackage.PREFIX__TARGET_PROCESS:
				setTargetProcess((uk.ac.ed.inf.pepa.emf.Process)null);
				return;
			case EmfPackage.PREFIX__ACTIVITY:
				setActivity((Activity)null);
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
			case EmfPackage.PREFIX__TARGET_PROCESS:
				return targetProcess != null;
			case EmfPackage.PREFIX__ACTIVITY:
				return activity != null;
		}
		return super.eIsSet(featureID);
	}

} //PrefixImpl