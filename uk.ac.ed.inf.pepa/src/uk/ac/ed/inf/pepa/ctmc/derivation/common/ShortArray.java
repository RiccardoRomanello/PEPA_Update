/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

/**
 * Specialised ArrayList of shorts. It should be much faster.
 * 
 * @author msmith
 * 
 */

public class ShortArray {
	
	private short[] elementData;
	
	private int[] reverseLookup = null;
	
	private short minElement = 0;
	private short maxElement = 0;
	
	private int size = 0;
	
	public ShortArray(int initialSize) {
		elementData = new short[initialSize];
	}
	
	public ShortArray(short[] initialArray) {
		size = initialArray.length;
		elementData = new short[size];
		if (size > 0) {
			minElement = initialArray[0];
			maxElement = initialArray[0];
		}
		for (int i = 0; i < size; i++) {
			short element = initialArray[i];
			elementData[i] = element;
			minElement = (short) Math.min(minElement, element);
			maxElement = (short) Math.max(maxElement, element);
		}
	}
	
	/**
	 * Copy constructor - creates a deep copy.
	 */
	public ShortArray(ShortArray copy) {
		elementData = new short[copy.elementData.length];
		reverseLookup = new int[copy.reverseLookup.length];
		System.arraycopy(copy.elementData, 0, elementData, 0, copy.elementData.length);
		System.arraycopy(copy.reverseLookup, 0, reverseLookup, 0, copy.reverseLookup.length);
		minElement = copy.minElement;
		maxElement = copy.maxElement;
		size = copy.size;
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
	
	private void changed() {
		reverseLookup = null;
	}
	
	private short[] copyOf(short[] original, int newLength) {
		short[] newArray = new short[newLength];
		System.arraycopy(original, 0, newArray, 0, size);
		return newArray;
	}
	
	public void add(short element) {
		if (size == 0) {
			minElement = element;
			maxElement = element;
		}
		minElement = (short) Math.min(minElement, element);
		maxElement = (short) Math.max(maxElement, element);
		ensureCapacity(size + 1);
		elementData[size++] = element;
		changed();
	}
	
	public void add(short[] elements) {
		if (elements == null) return;
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}
	}
	
	public void remove(short element) {
		int numFound = 0;
		for (int i = 0; i < size; i++) {
			while (i + numFound < size && elementData[i + numFound] == element) {
				numFound++;
			}
			if (i + numFound >= size) {
				elementData[i] = 0;
			} else {
				elementData[i] = elementData[i + numFound];
			}
		}
		size -= numFound;
		changed();
	}
	
	public void clear() {
		size = 0;
		changed();
	}
	
	public void addNew(short element) {
		if (!contains(element)) add(element);
	}
	
	public short get(int index) {
		if (index >= 0 && index < size) 
			return elementData[index];
		throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
	}
	
    public boolean contains(short x) {
    	for(int i = 0; i < size(); i++) {
        	if (elementData[i] == x) return true;        	
        }
        return false;
    } 
    
    public void optimiseFind() {
    	reverseLookup = new int[maxElement - minElement + 1];
    	for (int i = 0; i < reverseLookup.length; i++) {
    		reverseLookup[i] = -1;
    	}
    	for (int i = 0; i < size(); i++) {
    		reverseLookup[elementData[i] - minElement] = (short)i;
    	}
    }
    
    /**
     * Returns the position of x in the array. If optimiseFind() has been called,
     * this is a constant time operation. Returns -1 if x is not found in the array.
     */
    public int findPosition(short x) {
    	if (reverseLookup == null) {
    		for (int i = 0; i < size(); i++) {
    			if (elementData[i] == x) {
    				return i;
    			}
    		}
    		return -1;
    	} else {
    		if (x < minElement || x > maxElement) return -1;
    		return reverseLookup[x - minElement];
    	}
    }
	
    /**
     * Swaps the element (state id) at index x with the element at index y.
     * In other words, we swap the indices of the states at x and y. 
     */
    public void swap(int index1, int index2) {
    	short state1 = get(index1);
    	short state2 = get(index2);
    	elementData[index1] = state2;
    	elementData[index2] = state1;
    	if (reverseLookup != null) {
    		reverseLookup[state1 - minElement] = index2;
    		reverseLookup[state2 - minElement] = index1;
    	}
    }
    
	public void trimToSize() {
		if (size < elementData.length)
			elementData = copyOf(elementData, size);
	}
	
	public short[] toArray() {
		short[] newArray = new short[size];
		System.arraycopy(elementData, 0, newArray, 0, size);
		return newArray;
	}
	
	public int size() {
		return size;
	}
	
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShortArray([");
		for (short el: elementData) {
			builder.append("" + el + ", ");
		}
		builder.append("]");
		
		return builder.toString();
	}
}