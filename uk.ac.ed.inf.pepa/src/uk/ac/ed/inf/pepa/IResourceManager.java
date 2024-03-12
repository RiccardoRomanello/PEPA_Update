/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa;

/**
 * A manager capable of assigning OS resources, such as files to PEPA classes
 * 
 * @author mtribast
 * 
 */
public interface IResourceManager {
	
	public static final IResourceManager TEMP = new IResourceManager() {

		public String acquirePath(Object o) {
			return "c:/tmp/hbf/";
		}

		public void releasePath(Object o) {
		}

	};
	
	/**
	 * Acquires a file system path for an object. It is responsibility of the
	 * caller to release such a path
	 * 
	 * @param o
	 *            the object which wants to acquire a path.
	 * @return the absolute file system path.
	 * @throws IllegalStateException
	 *             if the object has already acquired a path.
	 * 
	 */
	public String acquirePath(Object o);

	/**
	 * Releases the path which was acquired by o. Has no effect if o is null, or
	 * o has never acquired any path.
	 * 
	 */
	public void releasePath(Object o);
}
