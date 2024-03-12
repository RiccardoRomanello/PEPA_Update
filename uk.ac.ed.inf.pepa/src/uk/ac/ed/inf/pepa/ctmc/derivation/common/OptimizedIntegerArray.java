package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.io.IOException;

public interface OptimizedIntegerArray {

	public void add(int element) throws IOException;
	
	public int get(int index) throws IOException;
	
	/**
	 * Copies the contents of this array into the destination.
	 * For performance reasons, it doesn't do any bound checking.
	 * @param from
	 * @param to
	 * @param dest
	 */
	public void getBulk(int from, int to, int[] dest) throws IOException;
	
	public int size();
	
	public void trimToSize();
}
