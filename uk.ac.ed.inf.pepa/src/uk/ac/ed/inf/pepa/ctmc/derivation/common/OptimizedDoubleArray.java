package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import java.io.IOException;

public interface OptimizedDoubleArray {
	public void add(double element) throws IOException;
	
	public double get(int index) throws IOException;
	
	/**
	 * Copies the contents of this array into the destination.
	 * For performance reasons, it doesn't do any bound checking.
	 * @param from
	 * @param to
	 * @param dest
	 */
	public void getBulk(int from, int to, double[] dest) throws IOException;
	
	public int size();
	
	public void trimToSize();
}
