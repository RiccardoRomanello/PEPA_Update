/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;


/**
 * Specialised ArrayList of ints. It should be quite faster.
 * 
 * @author mtribast
 * 
 */
public class IntegerArray {
	
	private int[] elementData;
	
	private int size;
	
	public IntegerArray(int initialSize) {
		elementData = new int[initialSize];
	}
	
	public void ensureCapacity(int minCapacity) {
		//System.err.println("Ensuring capacity:" + minCapacity);
		int oldCapacity = elementData.length; 
		//System.err.println("Old capacity: " + oldCapacity);
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			//System.err.println("New capacity: " + newCapacity);
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = copyOf(elementData, newCapacity);
		}
	}
	
	private int[] copyOf(int[] original, int newCapacity) {
		int[] newArray = new int[newCapacity];
		System.arraycopy(original, 0, newArray, 0, size);
		return newArray;
	}
	
	public void add(int element) {
		ensureCapacity(size + 1);
		elementData[size++] = element;
	}
	
	public void addNew(int element) {
		if (!contains(element)) add(element);
	}
	
	public boolean contains(int element) {
		for (int i = 0; i < size; i++) {
			if (elementData[i] == element) return true;
		}
		return false;
	}
	
	public int get(int index) {
		if (index >= 0 && index < size) 
			return elementData[index];
		throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
	}
	
	public void trimToSize() {
		if (size < elementData.length)
			elementData = copyOf(elementData, size);
	}
	
	public int[] toArray() {
		int[] newArray = new int[size];
		System.arraycopy(elementData, 0, newArray, 0, size);
		return newArray;
	}

	
	public int size() {
		return size;
	}

	public void set(int index, int element) {
		elementData[index] = element;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IntegerArray([");
		for (int el: elementData) {
			builder.append("" + el + ", ");
		}
		builder.append("]");
		
		return builder.toString();
	}
}
