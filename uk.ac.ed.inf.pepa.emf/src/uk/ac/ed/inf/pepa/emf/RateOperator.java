/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * @author mtribast
 * @model
 *
 */
public final class RateOperator extends AbstractEnumerator {
	/**
	 * @model name="Plus"
	 */
	public static final int PLUS = 0;
	
	/**
	 * @model name="Minus"
	 */
	public static final int MINUS = 1;
	
	/**
	 * @model name="Divide"
	 */
	public static final int DIVIDE = 2;
	
	/**
	 * @model name="Times"
	 */
	public static final int TIMES = 3;
	/**
	 * The '<em><b>Plus</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PLUS
	 * @generated
	 * @ordered
	 */
	public static final RateOperator PLUS_LITERAL = new RateOperator(PLUS, "Plus", "Plus");

	/**
	 * The '<em><b>Minus</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MINUS
	 * @generated
	 * @ordered
	 */
	public static final RateOperator MINUS_LITERAL = new RateOperator(MINUS, "Minus", "Minus");

	/**
	 * The '<em><b>Divide</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #DIVIDE
	 * @generated
	 * @ordered
	 */
	public static final RateOperator DIVIDE_LITERAL = new RateOperator(DIVIDE, "Divide", "Divide");

	/**
	 * The '<em><b>Times</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TIMES
	 * @generated
	 * @ordered
	 */
	public static final RateOperator TIMES_LITERAL = new RateOperator(TIMES, "Times", "Times");

	/**
	 * An array of all the '<em><b>Rate Operator</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final RateOperator[] VALUES_ARRAY =
		new RateOperator[] {
			PLUS_LITERAL,
			MINUS_LITERAL,
			DIVIDE_LITERAL,
			TIMES_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Rate Operator</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Rate Operator</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RateOperator get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			RateOperator result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Rate Operator</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RateOperator getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			RateOperator result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Rate Operator</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RateOperator get(int value) {
		switch (value) {
			case PLUS: return PLUS_LITERAL;
			case MINUS: return MINUS_LITERAL;
			case DIVIDE: return DIVIDE_LITERAL;
			case TIMES: return TIMES_LITERAL;
		}
		return null;
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private RateOperator(int value, String name, String literal) {
		super(value, name, literal);
	}

}
