/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import java.util.*;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.*;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;
import uk.ac.ed.inf.pepa.sba.SBAParseException;

/**
 * 
 * @author ajduguid
 *
 */
public class AlgorithmWizardPage extends WizardPage {

	public static final String name = "Algorithm";

	Listener controlListener;

	HashMap<Control, Parameter> controlMap;

	boolean firstUse = true, saveCMDLPossible = true, booleanSaveCMDL;

	HashSet<Parameter> invalidParameters;

	IPepaModel model;

	Group optionsGroup;

	OptionsMap optionsMap;

	Combo solverCombo;

	Composite solverComposite;
	
	Button saveCMDL;

	Solver[] solvers;

	protected AlgorithmWizardPage(IPepaModel model, OptionsMap optionsMap) {
		super(name);
		this.model = model;
		this.optionsMap = optionsMap;
		controlMap = new HashMap<Control, Parameter>();
		invalidParameters = new HashSet<Parameter>();
		setTitle(WizardMessages.ALGORITHM_WIZARD_PAGE_TITLE);
		Solver currentSolver = (Solver) this.optionsMap
				.getValue(OptionsMap.Parameter.Solver);
		setPageComplete(false);
		solvers = model.getValidTimeSeriesSolvers();
		for (Solver solver : solvers)
			if (solver.equals(currentSolver)) {
				setPageComplete(true);
				break;
			}
	}

	public void algorithmChanged() {
		controlMap.clear();
		invalidParameters.clear();
		for (Control child : optionsGroup.getChildren())
			if (!child.isDisposed())
				child.dispose();
		Solver solver = (Solver) optionsMap.getValue(Parameter.Solver);
		setDescription(WizardMessages.ALGORITHM_WIZARD_PAGE_DESCRIPTION + " "
				+ solver.getDescriptiveName());
		Parameter[] parameters = solver.getRequiredParameters();
		Label label;
		Control control;
		for (Parameter parameter : parameters) {
			// ignore these parameters (taken care of elsewhere)
			if (parameter.equals(Parameter.Components))
				continue;
			label = new Label(optionsGroup, SWT.LEFT);
			label.setText(parameter.toString());
			Class<?> c = parameter.getType();
			if (Number.class.isAssignableFrom(c)) {
				control = new Text(optionsGroup, SWT.RIGHT | SWT.SINGLE);
				((Text) control).setText(optionsMap.getValue(parameter)
						.toString());
				control.addListener(SWT.Modify, controlListener);
				control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				controlMap.put(control, parameter);
			}
		}
		optionsGroup.layout();
		solverComposite.layout();
		validatePage();
	}

	public void createControl(Composite parent) {
		solverComposite = new Composite(parent, SWT.NONE);
		solverComposite.setLayout(new FormLayout());
		solverCombo = new Combo(solverComposite, SWT.READ_ONLY);
		saveCMDL = new Button(solverComposite, SWT.CHECK);
		saveCMDL.setText("Save model in CMDL and Matlab format");
		saveCMDL.setToolTipText("Save As Dialog for Matlab file will open when you click on finish");
		saveCMDL.setEnabled(saveCMDLPossible);
		solverCombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				solverSelected();
			}
		});
		optionsGroup = new Group(solverComposite, SWT.NONE);
		optionsGroup.setText("Solver Parameters");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		optionsGroup.setLayout(layout);
		for(int i = 0; i < 16; i++)
			new Label(optionsGroup, SWT.LEFT);
		controlListener = new Listener() {
			public void handleEvent(Event event) {
				Control control = (Control) event.widget;
				Parameter parameter = controlMap.get(control);
				try {
					if (control instanceof Text)
						optionsMap.setValue(parameter, ((Text) control)
								.getText());
					// no error thrown so valid type
					invalidParameters.remove(parameter);
				} catch (IllegalArgumentException e) {
					invalidParameters.add(parameter);
				}
				validatePage();
			}
		};
		saveCMDL.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				booleanSaveCMDL = ((Button) e.widget).getSelection();
			}
		});
		// Layout composites
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		saveCMDL.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(saveCMDL, 20);
		formData.left = new FormAttachment(0);
		solverCombo.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(solverCombo);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		optionsGroup.setLayoutData(formData);
		setControl(solverComposite);
	}
	
	public void setVisible(boolean visible) {
		if (visible) {
			solverCombo.removeAll();
			solvers = model.getValidTimeSeriesSolvers();
			if (solvers.length == 0) {
				PepaLog.logError(new SBAParseException("No valid solvers."));
				getWizard().dispose();
			}
			for (Solver solver : solvers)
				solverCombo.add(solver.getDescriptiveName());
			Solver currentSolver = (Solver) optionsMap
					.getValue(OptionsMap.Parameter.Solver);
			int i;
			for (i = 0; i < solvers.length; i++)
				if (solvers[i].equals(currentSolver)) {
					solverCombo.select(i);
					break;
				}
			if (i == solvers.length)
				solverCombo.select(0);
			solverSelected();
		}
		super.setVisible(visible);
	}

	private void solverSelected() {
		int index = solverCombo.getSelectionIndex();
		if (index != -1) {
			optionsMap.setValue(OptionsMap.Parameter.Solver, solvers[index]);
			algorithmChanged();
		}
	}

	private void validatePage() {
		if (invalidParameters.isEmpty()) {
			setPageComplete(true);
			setErrorMessage(null);
		} else { // ergo there is at least one Parameter in the set
			setPageComplete(false);
			setErrorMessage("Invalid value entered for "
					+ invalidParameters.iterator().next().toString());
		}
	}
	
	public void disableCMDLSave() {
		if (saveCMDL != null)
			saveCMDL.setEnabled(false);
		saveCMDLPossible = false;
	}
	
	/**
	 * 
	 * @return
	 */
	boolean saveCMDL() {
		return (saveCMDL.isEnabled() && booleanSaveCMDL);
	}
}
