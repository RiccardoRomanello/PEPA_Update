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

import uk.ac.ed.inf.pepa.emf.Aggregation;
import uk.ac.ed.inf.pepa.emf.EmfPackage;
import uk.ac.ed.inf.pepa.emf.FiniteRate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Aggregation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.AggregationImpl#getProcess <em>Process</em>}</li>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.AggregationImpl#getCopies <em>Copies</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AggregationImpl extends ProcessImpl implements Aggregation {
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
	 * The cached value of the '{@link #getCopies() <em>Copies</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCopies()
	 * @generated
	 * @ordered
	 */
	protected FiniteRate copies;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AggregationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return EmfPackage.Literals.AGGREGATION;
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.AGGREGATION__PROCESS, oldProcess, newProcess);
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
				msgs = ((InternalEObject)process).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.AGGREGATION__PROCESS, null, msgs);
			if (newProcess != null)
				msgs = ((InternalEObject)newProcess).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.AGGREGATION__PROCESS, null, msgs);
			msgs = basicSetProcess(newProcess, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.AGGREGATION__PROCESS, newProcess, newProcess));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FiniteRate getCopies() {
		return copies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCopies(FiniteRate newCopies, NotificationChain msgs) {
		FiniteRate oldCopies = copies;
		copies = newCopies;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.AGGREGATION__COPIES, oldCopies, newCopies);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCopies(FiniteRate newCopies) {
		if (newCopies != copies) {
			NotificationChain msgs = null;
			if (copies != null)
				msgs = ((InternalEObject)copies).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.AGGREGATION__COPIES, null, msgs);
			if (newCopies != null)
				msgs = ((InternalEObject)newCopies).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.AGGREGATION__COPIES, null, msgs);
			msgs = basicSetCopies(newCopies, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.AGGREGATION__COPIES, newCopies, newCopies));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmfPackage.AGGREGATION__PROCESS:
				return basicSetProcess(null, msgs);
			case EmfPackage.AGGREGATION__COPIES:
				return basicSetCopies(null, msgs);
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
			case EmfPackage.AGGREGATION__PROCESS:
				return getProcess();
			case EmfPackage.AGGREGATION__COPIES:
				return getCopies();
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
			case EmfPackage.AGGREGATION__PROCESS:
				setProcess((uk.ac.ed.inf.pepa.emf.Process)newValue);
				return;
			case EmfPackage.AGGREGATION__COPIES:
				setCopies((FiniteRate)newValue);
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
			case EmfPackage.AGGREGATION__PROCESS:
				setProcess((uk.ac.ed.inf.pepa.emf.Process)null);
				return;
			case EmfPackage.AGGREGATION__COPIES:
				setCopies((FiniteRate)null);
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
			case EmfPackage.AGGREGATION__PROCESS:
				return process != null;
			case EmfPackage.AGGREGATION__COPIES:
				return copies != null;
		}
		return super.eIsSet(featureID);
	}

} //AggregationImpl