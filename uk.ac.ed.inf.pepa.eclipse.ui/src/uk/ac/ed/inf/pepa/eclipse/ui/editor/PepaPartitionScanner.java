/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.editor;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class PepaPartitionScanner extends RuleBasedPartitionScanner {
	public final static String PEPA_COMMENT = "__srmc_comment";
	//public final static String SRMC_TAG = "__srmc_tag";

	public PepaPartitionScanner() {

		IToken srmcComment = new Token(PEPA_COMMENT);
		//IToken tag = new Token(SRMC_TAG);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new MultiLineRule("/*", "*/", srmcComment);
		rules[1] = new EndOfLineRule("//", srmcComment);
		
		setPredicateRules(rules);
	}
}
