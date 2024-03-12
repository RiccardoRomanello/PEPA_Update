/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class OptionWizardPage extends WizardPage {

	private IProcessAlgebraModel model;

	private Text separatorText;

	private Button includeStateNumberButton;

	private Button includeCurrentSolutionButton;

	//private Button printCompleteStateButton;

	//private Button printSeparatedTopLevelComponentsButton;

	protected OptionWizardPage(IProcessAlgebraModel processAlgebraModel) {
		super("optionWizardPage");
		this.model = processAlgebraModel;
		setTitle("Exporter Options");
		setDescription("Export the state space data "
				+ "model using the following settings");
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 10;
		main.setLayout(layout);
		setControl(main);

		Group commonOptions = new Group(main, SWT.NONE);
		commonOptions.setText("Common Options");
		commonOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout col = new GridLayout(2, false);
		col.marginTop = col.marginBottom = 10;
		commonOptions.setLayout(col);
		Label l = new Label(commonOptions, SWT.NONE);
		l.setText("Separator");
		l.setLayoutData(new GridData());
		separatorText = new Text(commonOptions, SWT.BORDER);
		separatorText.setTextLimit(5);
		separatorText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				if (separatorText.getText().equals(""))
					setPageComplete(false);
				else
					setPageComplete(true);
			}

		});
		separatorText.setText(",");
		separatorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group stateSpaceOptions = new Group(main, SWT.NONE);
		stateSpaceOptions.setText("State Space Options");
		GridLayout sol = new GridLayout(1, true);
		sol.marginBottom = sol.marginTop = sol.marginHeight = 10;
		stateSpaceOptions.setLayout(sol);
		stateSpaceOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		includeStateNumberButton = new Button(stateSpaceOptions, SWT.CHECK);
		includeStateNumberButton.setText("Include state number");
		includeStateNumberButton.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		//Group stateGroup = new Group(stateSpaceOptions, SWT.NONE);
		//stateGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//stateGroup.setLayout(new GridLayout(1, true));

		/*printCompleteStateButton = new Button(stateGroup, SWT.RADIO);
		printCompleteStateButton.setText("Complete state");
		printCompleteStateButton.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		printSeparatedTopLevelComponentsButton = new Button(stateGroup,
				SWT.RADIO);
		printSeparatedTopLevelComponentsButton
				.setText("Separated top level components");
		printSeparatedTopLevelComponentsButton.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		printCompleteStateButton.setSelection(true);*/

		includeCurrentSolutionButton = new Button(stateSpaceOptions, SWT.CHECK);
		includeCurrentSolutionButton
				.setText("Include current steady-state solution");
		includeCurrentSolutionButton.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		includeCurrentSolutionButton.setEnabled(model.isSolved());
		

	}
	
	public boolean isIncludeStateNumber() {
		return includeStateNumberButton.getSelection();
	}
	
	public boolean isIncludeCurrentSolution() {
		if (includeCurrentSolutionButton.getEnabled())
			return includeCurrentSolutionButton.getSelection();
		else
			return false;
	}
	
	public String getSeparator() {
		return separatorText.getText();
	}

}
