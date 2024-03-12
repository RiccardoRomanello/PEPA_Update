/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.editor;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class PepaScanner extends RuleBasedScanner {

	public PepaScanner(ColorManager manager) {
		Token other = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.DEFAULT)));
		Token keyword = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.KEYWORD)/* , null */));
		
		Token quotedName = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.QUOTED_NAME)));
		Token processName = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.PROCESS_NAME)));
		Token lowerCaseName = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.LOWER_NAME)));
		Token symbolName = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.SYMBOL)));

		Token numberLiteral = new Token(new TextAttribute(manager
				.getColor(IPepaColorConstants.NUMBER)));

		IRule[] rules = new IRule[6];
		// Add rule for keywords
		WordRule wordRule = new WordRule(new PepaWordDetector(), lowerCaseName);
		wordRule.addWord("tau", keyword);
		wordRule.addWord("infty", keyword);
		// wordRule.addWord("T", processName);

		WordRule numberLiteralRule = new WordRule(new IWordDetector() {

			public boolean isWordStart(char c) {
				return Character.isDigit(c);
			}

			public boolean isWordPart(char c) {
				return Character.isDigit(c) || c == '.';
			}

		}, numberLiteral);

		WordRule symbolRule = new WordRule(new IWordDetector() {

			/**
			 * Always false - symbols are one-character tokens
			 */
			public boolean isWordPart(char c) {
				return false;
			}

			public boolean isWordStart(char c) {
				return c == '<' || c == '>' || c == '|' || c == ',' || c == ';'
						|| c == '=' || c == '.' || c == '(' || c == ')'
						|| c == '+';
			}

		}, symbolName);

		WordRule processRule = new WordRule(new IWordDetector() {

			public boolean isWordStart(char c) {
				return Character.isUpperCase(c);
			}

			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}

		}, processName);

		rules[0] = processRule;

		rules[1] = wordRule;

		rules[2] = symbolRule;

		rules[3] = numberLiteralRule;
		
		rules[4] = new SingleLineRule("\"", "\"", quotedName);
		
		rules[5] = new WhitespaceRule(new PepaWhitespaceDetector());

		setRules(rules);

	}
}
