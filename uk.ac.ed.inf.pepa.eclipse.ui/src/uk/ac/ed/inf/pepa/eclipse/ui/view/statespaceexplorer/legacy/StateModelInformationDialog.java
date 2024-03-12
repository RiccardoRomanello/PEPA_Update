/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer.legacy;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog showing information about the state.
 * 
 * @author mtribast
 * 
 */
public class StateModelInformationDialog extends Dialog {

	private IStateModel state;

	private Composite main;

	protected StateModelInformationDialog(Shell parentShell, IStateModel state) {
		super(parentShell);
		this.state = state;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("State Properties");
	}

	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		main = new Composite(composite, SWT.NULL);
		main.setLayout(new GridLayout(2, false));
		createField("State Number");
		createValue(state.getStateNumber() + 1 + "");

		createField("Problem");
		createValue((state.getProblem() == null) ? "" : state.getProblem());

		createField("Steady-state Probability");
		double solution = state.getSolution();
		if (solution == state.SOLUTION_NOT_AVAILABLE)
			createValue("");
		else
			createValue(solution + "");
		return composite;

	}

	private void createField(String text) {
		Label label = new Label(main, SWT.NULL);
		label.setText(text);

	}

	private void createValue(String text) {
		Text label = new Text(main, SWT.READ_ONLY | SWT.BORDER);
		label.setText(text);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

}
