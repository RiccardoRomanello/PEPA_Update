/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;


/**
 * Specialised ArrayList of doubles. It should be quite faster.
 * 
 * @author mtribast
 * 
 */
public class DoubleArray {
	
	private double[] elementData;
	
	private int size;
	
	public DoubleArray(int initialSize) {
		elementData = new double[initialSize];
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length; 
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = copyOf(elementData, newCapacity);
		}
	}
	
	private double[] copyOf(double[] original, int newLength) {
		double[] newArray = new double[newLength];
		System.arraycopy(original, 0, newArray, 0, size);
		return newArray;
	}
	
	public void add(double element) {
		ensureCapacity(size + 1);
		elementData[size++] = element;
		
	}
	
	public double get(int index) {
		if (index >= 0 && index < size) 
			return elementData[index];
		throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
	}
	
	public void trimToSize() {
		if (size < elementData.length)
			elementData = copyOf(elementData, size);
	}
	
	public double[] toArray() {
		double[] newArray = new double[size];
		System.arraycopy(elementData, 0, newArray, 0, size);
		return newArray;
	}
	
	public int size() {
		return size;
	}
	
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DoubleArray([");
		for (double el: elementData) {
			builder.append("" + el + ", ");
		}
		builder.append("]");
		
		return builder.toString();
	}
}
