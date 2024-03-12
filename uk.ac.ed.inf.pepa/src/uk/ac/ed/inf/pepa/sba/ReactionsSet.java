/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.*;

/**
 * Seriously, all I need is the ability to pass the occassional tuple in code
 * that nobody else should be playing with but no, I need to create additional
 * classes in Java and what for? can you see any method calls? any need for
 * anybody bar me to be using this? Some days it takes all my willpower not to
 * come up with more 'appropriate' variable names.
 * 
 * @author ajduguid
 * 
 */
class ReactionsSet {

	List<SBAReaction> reactions;

	/*
	 * In database terms, this should be a weak key with no real value. It's a
	 * dirty hack but I'll concatenate the two Strings using : as it's an
	 * illegal character in PEPA to make up the weak key and separate them
	 * later. This is required when you need something like (C + D) to go to
	 * both C and D i.e. two named constants without prefixes.
	 */
	HashMap<String, String> reactionsToIterate;

	ReactionsSet() {
		reactions = new LinkedList<SBAReaction>();
		reactionsToIterate = new HashMap<String, String>();
	}

}
