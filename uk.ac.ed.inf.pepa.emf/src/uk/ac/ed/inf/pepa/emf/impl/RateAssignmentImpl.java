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
import uk.ac.ed.inf.pepa.emf.FiniteRate;
import uk.ac.ed.inf.pepa.emf.RateAssignment;
import uk.ac.ed.inf.pepa.emf.RateIdentifier;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rate Assignment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.RateAssignmentImpl#getRateIdentifier <em>Rate Identifier</em>}</li>
 *   <li>{@link uk.ac.ed.inf.pepa.emf.impl.RateAssignmentImpl#getRate <em>Rate</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RateAssignmentImpl extends EObjectImpl implements RateAssignment {
	/**
	 * The cached value of the '{@link #getRateIdentifier() <em>Rate Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRateIdentifier()
	 * @generated
	 * @ordered
	 */
	protected RateIdentifier rateIdentifier;

	/**
	 * The cached value of the '{@link #getRate() <em>Rate</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRate()
	 * @generated
	 * @ordered
	 */
	protected FiniteRate rate;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RateAssignmentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return EmfPackage.Literals.RATE_ASSIGNMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RateIdentifier getRateIdentifier() {
		return rateIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRateIdentifier(RateIdentifier newRateIdentifier, NotificationChain msgs) {
		RateIdentifier oldRateIdentifier = rateIdentifier;
		rateIdentifier = newRateIdentifier;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER, oldRateIdentifier, newRateIdentifier);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRateIdentifier(RateIdentifier newRateIdentifier) {
		if (newRateIdentifier != rateIdentifier) {
			NotificationChain msgs = null;
			if (rateIdentifier != null)
				msgs = ((InternalEObject)rateIdentifier).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER, null, msgs);
			if (newRateIdentifier != null)
				msgs = ((InternalEObject)newRateIdentifier).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER, null, msgs);
			msgs = basicSetRateIdentifier(newRateIdentifier, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER, newRateIdentifier, newRateIdentifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FiniteRate getRate() {
		return rate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRate(FiniteRate newRate, NotificationChain msgs) {
		FiniteRate oldRate = rate;
		rate = newRate;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EmfPackage.RATE_ASSIGNMENT__RATE, oldRate, newRate);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRate(FiniteRate newRate) {
		if (newRate != rate) {
			NotificationChain msgs = null;
			if (rate != null)
				msgs = ((InternalEObject)rate).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EmfPackage.RATE_ASSIGNMENT__RATE, null, msgs);
			if (newRate != null)
				msgs = ((InternalEObject)newRate).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EmfPackage.RATE_ASSIGNMENT__RATE, null, msgs);
			msgs = basicSetRate(newRate, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmfPackage.RATE_ASSIGNMENT__RATE, newRate, newRate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER:
				return basicSetRateIdentifier(null, msgs);
			case EmfPackage.RATE_ASSIGNMENT__RATE:
				return basicSetRate(null, msgs);
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
			case EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER:
				return getRateIdentifier();
			case EmfPackage.RATE_ASSIGNMENT__RATE:
				return getRate();
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
			case EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER:
				setRateIdentifier((RateIdentifier)newValue);
				return;
			case EmfPackage.RATE_ASSIGNMENT__RATE:
				setRate((FiniteRate)newValue);
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
			case EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER:
				setRateIdentifier((RateIdentifier)null);
				return;
			case EmfPackage.RATE_ASSIGNMENT__RATE:
				setRate((FiniteRate)null);
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
			case EmfPackage.RATE_ASSIGNMENT__RATE_IDENTIFIER:
				return rateIdentifier != null;
			case EmfPackage.RATE_ASSIGNMENT__RATE:
				return rate != null;
		}
		return super.eIsSet(featureID);
	}

} //RateAssignmentImpl