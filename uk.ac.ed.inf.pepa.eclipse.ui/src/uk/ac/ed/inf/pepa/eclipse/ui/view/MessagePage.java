/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.Page;

/**
 * Default message page for resources that do not contribute any 
 * <code>IPrepaModel</code> reference.
 * 
 * @author mtribast
 *
 */
public class MessagePage extends Page {

	private Composite pgComp;

	private Label msgLabel;

	private String message = "";//$NON-NLS-1$

	/**
	 * Creates a new page. The message is the empty string.
	 */
	public MessagePage(String message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc) Method declared on IPage.
	 */
	public void createControl(Composite parent) {
		// Message in default page of Outline should have margins
		pgComp = new Composite(parent, SWT.NULL);
		pgComp.setLayout(new FillLayout());

		msgLabel = new Label(pgComp, SWT.LEFT | SWT.TOP | SWT.WRAP);
		msgLabel.setText(message);
	}

	/*
	 * (non-Javadoc) Method declared on IPage.
	 */
	public Control getControl() {
		return pgComp;
	}

	/**
	 * Sets focus to a part in the page.
	 */
	public void setFocus() {
		// important to give focus to the composite rather than the label
		// as the composite will actually take focus (though hidden),
		// but setFocus on a Label is a no-op
		pgComp.setFocus();
	}

	/**
	 * Sets the message to the given string.
	 * 
	 * @param message
	 *            the message text
	 */
	public void setMessage(String message) {
		this.message = message;
		if (msgLabel != null)
			msgLabel.setText(message);
	}
}

