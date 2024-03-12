/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.editor;

import org.eclipse.jface.text.rules.IWordDetector;

public class PepaWordDetector implements IWordDetector {

	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}

	public boolean isWordStart(char c) {
		return Character.isLetter(c);
	}

}
