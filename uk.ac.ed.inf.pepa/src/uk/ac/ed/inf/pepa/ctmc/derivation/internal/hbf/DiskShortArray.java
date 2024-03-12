/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskShortArray {
	
	private static final int BYTE_SIZE = 1;
	
	private RandomAccessFile file;
	
	private int size;
	
	public DiskShortArray(String file) throws IOException {
		this.file = new RandomAccessFile(file, "r");
		long longSize = this.file.length() >> BYTE_SIZE; 
		//System.err.println("Size: " + longSize);
		if (longSize > Integer.MAX_VALUE)
			throw new IllegalStateException("File too long");
		this.size = (int) longSize;
		//System.err.println("Number of elements: " + this.size);
	}
	
	public short get(int index) throws IOException {
		//System.err.println("Index: " + index);
		file.seek(index << BYTE_SIZE);
		short s = file.readShort();
		//System.err.println("Short: " + s);
		return s;
	}
	
	/**
	 * Copies the contents of this array into the destination.
	 * For performance reasons, it doesn't do any bound checking.
	 * @param from
	 * @param to
	 * @param dest
	 */
	public void getBulk(int from, int to, short[] dest) throws IOException {
		file.seek(from << BYTE_SIZE);
		int range = to-from;
		for (int i = 0; i <range; i++) {
			dest[i] = file.readShort();
		}
	}
	
	public int size() {
		return size;
	}
}
