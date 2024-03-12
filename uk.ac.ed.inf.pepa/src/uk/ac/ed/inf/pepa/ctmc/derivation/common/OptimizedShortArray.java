package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.io.IOException;

public interface OptimizedShortArray {
	public void add(short element) throws IOException;
	
	public short get(int index) throws IOException;
	
	/**
	 * Copies the contents of this array into the destination.
	 * For performance reasons, it doesn't do any bound checking.
	 * @param from
	 * @param to
	 * @param dest
	 */
	public void getBulk(int from, int to, short[] dest) throws IOException;
	
	public int size();
	
	public void trimToSize();
}
