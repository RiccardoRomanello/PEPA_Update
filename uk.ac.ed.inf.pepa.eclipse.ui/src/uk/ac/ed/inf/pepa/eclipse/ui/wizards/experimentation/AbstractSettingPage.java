/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractSettingPage extends WizardPage implements
		ISettingPage {

	protected ISensibleNode node;
	
	protected Text fromText;

	protected Text toText;

	protected Text stepText;

	protected Text listText;

	protected Button optionStepButton;

	protected Button optionListButton;

	private Label fromLabel, toLabel, stepLabel, listLabel;

	private Group listValuedGroup;

	private Group fromToStepGroup;

	protected AbstractSettingPage(ISensibleNode node) {
		super(node.toString());
		this.node = node;
		this.setPageComplete(false);

	}

	public abstract ISetting getASTSetting();

	public ISensibleNode getNode() {
		return node;
	}

	protected boolean validate() {
		if (optionListButton.getSelection()) {
			return validateListSection();
		} else if (optionStepButton.getSelection()) {
			return validateStepSection();
		}
		return false;
	}
	
	protected abstract boolean validateStepSection();
	
	protected abstract boolean validateListSection();
	
	private Listener listener = new Listener() {

		public void handleEvent(Event event) {
			AbstractSettingPage.this.setPageComplete(validate());
		}

	};

	public void createControl(Composite parent) {

		int labelStyle = SWT.NULL;
		int textStyle = SWT.BORDER;
		int marginLeft = 20;

		Composite main = new Composite(parent, SWT.NONE);
		//GridLayout mainLayout = new GridLayout();
		//mainLayout.numColumns = 1;
		main.setLayout(new FillLayout());
		setControl(main);

		Group optionsGroup = new Group(main, SWT.SHADOW_NONE);
		GridLayout optionsGroupLayout = new GridLayout();
		optionsGroupLayout.numColumns = 1;
		optionsGroup.setLayout(optionsGroupLayout);

		optionStepButton = new Button(optionsGroup, SWT.RADIO);
		optionStepButton.setText("Specify an interval");
		optionStepButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fromToStepGroup = new Group(optionsGroup, SWT.SHADOW_NONE);
		fromToStepGroup.setText("Interval Settings");
		fromToStepGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginLeft = marginLeft;
		fromToStepGroup.setLayout(layout);
		fromToStepGroup.setEnabled(false);

		
		fromLabel = new Label(fromToStepGroup, labelStyle);
		fromLabel.setText("From:");
		fromLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fromLabel.setEnabled(false);
		
		fromText = new Text(fromToStepGroup, textStyle);
		fromText.addListener(SWT.Modify, listener);
		fromText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fromText.setEnabled(false);
		

		toLabel = new Label(fromToStepGroup, labelStyle);
		toLabel.setText("To:");
		toLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toLabel.setEnabled(false);
		toText = new Text(fromToStepGroup, textStyle);
		toText.addListener(SWT.Modify, listener);
		toText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toText.setEnabled(false);
		
		stepLabel = new Label(fromToStepGroup, labelStyle);
		stepLabel.setText("Step:");
		stepLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stepLabel.setEnabled(false);
		stepText = new Text(fromToStepGroup, textStyle);
		stepText.addListener(SWT.Modify, listener);
		stepText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stepText.setEnabled(false);

		optionListButton = new Button(optionsGroup, SWT.RADIO);
		optionListButton.setText("Specify comma-separated list");
		optionListButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		listValuedGroup = new Group(optionsGroup, SWT.SHADOW_ETCHED_OUT);
		listValuedGroup.setText("List Settings");
		GridLayout listLayout = new GridLayout();
		listLayout.numColumns = 2;
		listLayout.marginLeft = marginLeft;
		listValuedGroup.setLayout(listLayout);
		listValuedGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		listValuedGroup.setEnabled(false);

		
		listLabel = new Label(listValuedGroup, labelStyle);
		listLabel.setText("Insert list:");
		listLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		listLabel.setEnabled(false);
		listText = new Text(listValuedGroup, textStyle);
		listText.addListener(SWT.Modify, listener);
		listText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		listText.setEnabled(false);
		
		initialiseValues();
		
		optionStepButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				enableWidgets();
				validate();
				if (optionStepButton.getSelection())
					fromText.setFocus();

			}

		});
		
		optionListButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				enableWidgets();
				validate();
				if (optionListButton.getSelection())
					listText.setFocus();
			}

		});

		this.setPageComplete(validate());
	}
	
	private void enableWidgets() {
		boolean stepOpt = optionStepButton.getSelection();
		fromText.setEnabled(stepOpt);
		fromLabel.setEnabled(stepOpt);
		toText.setEnabled(stepOpt);
		toLabel.setEnabled(stepOpt);
		stepText.setEnabled(stepOpt);
		stepLabel.setEnabled(stepOpt);
		fromToStepGroup.setEnabled(stepOpt);
		
		listValuedGroup.setEnabled(!stepOpt);
		listText.setEnabled(!stepOpt);
		listLabel.setEnabled(!stepOpt);
		
	}

	protected abstract void initialiseValues();

}
