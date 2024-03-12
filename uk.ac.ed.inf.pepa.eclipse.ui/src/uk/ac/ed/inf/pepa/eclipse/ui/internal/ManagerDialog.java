/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterModel;

public class ManagerDialog extends Dialog {

	private class ConfigurationNameValidator implements IInputValidator {

		boolean isEditing;

		public String isValid(String newText) {

			if (newText == null || newText.trim().equals(""))
				return "Filter name cannot be empty";
			if (!isEditing) {
				for (IFilterModel configuration : fFilterManager.filterModels)
					if (configuration.getName().equals(newText))
						return "Filter name already existing";
			}
			return null;

		}

	}

	private ConfigurationNameValidator validator = new ConfigurationNameValidator();

	/* Page for dialog settings */
	private final static String DIALOG_SETTINGS_NAME = "FILTER_MANAGER_DIALOG";

	/* ListViewer of IConfiguration */
	private CheckboxTableViewer checkBoxViewer;

	/* Create a new configuration from the current settings */
	private Button createButton;

	/* Save the current configuration under the currently selected one */
	private Button editButton;

	/* Delete the currently selected configuration */
	private Button removeButton;

	private Button okButton;

	private FilterManager fFilterManager;

	private IFilterModel currentConfiguration = null;

	public ManagerDialog(FilterManager filterManager, Shell shell, IFilterModel selected) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.fFilterManager = filterManager;
		currentConfiguration = selected;
	}

	public IFilterModel getSelectedConfiguration() {
		return this.currentConfiguration;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("State Space Filters");
	}

	protected void okPressed() {
		fFilterManager.saveConfigurations();
		Object[] checkedElements = checkBoxViewer.getCheckedElements();
		if (checkedElements.length == 0) {
			currentConfiguration = null;
		} else {
			currentConfiguration = (IFilterModel) checkedElements[0];
		}
		super.okPressed();
	}

	protected void cancelPressed() {
		// configurations are saved anyway
		fFilterManager.saveConfigurations();
		super.cancelPressed();
	}

	/**
	 * A way to remember the dialog settings
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_NAME);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS_NAME);
		}
		return section;
	}

	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout());

		Composite main = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		checkBoxViewer = CheckboxTableViewer.newCheckList(main, SWT.SINGLE
				| SWT.BORDER);

		class CheckBoxLabelProvider extends LabelProvider implements
				ITableLabelProvider {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0) {
					FilterModel configuration = (FilterModel) element;
					return configuration.getName();
				}
				return null;
			}
		}
		/* return the name of the configuration */
		checkBoxViewer.setLabelProvider(new CheckBoxLabelProvider());
		checkBoxViewer.setContentProvider(new ArrayContentProvider());
		checkBoxViewer.setInput(fFilterManager.filterModels);
		checkBoxViewer.getTable().setLayoutData(
				new GridData(GridData.FILL_BOTH));

		Composite buttonComposite = new Composite(main, SWT.NULL);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		RowLayout compLayout = new RowLayout(SWT.VERTICAL);
		compLayout.fill = true;
		buttonComposite.setLayout(compLayout);

		createButton = new Button(buttonComposite, SWT.PUSH);
		createButton.setText("New...");
		createButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				FilterModel config = performCreateConfiguration();
				updateControls();
				/* set the new configuration */
				if (config != null)
					checkBoxViewer
							.setSelection(new StructuredSelection(config));
			}

		});
		createButton.setToolTipText("Save the current configuration");

		editButton = new Button(buttonComposite, SWT.PUSH);
		editButton.setText("Edit...");
		editButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				FilterModel old = getCurrentConfiguration();
				boolean wasChecked = checkBoxViewer.getChecked(old);
				FilterModel config = performEditConfiguration(old);
				updateControls();
				checkBoxViewer.setSelection(new StructuredSelection(config));
				checkBoxViewer.setChecked(config, wasChecked);
			}
		});
		editButton
				.setToolTipText("Update this configuration with the current settings");

		removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Delete");
		removeButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				performRemoveConfiguration();
				updateControls();
			}

		});
		removeButton.setToolTipText("Delete the selected configuration");

		checkBoxViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						changeButtonStatus(selection);

					}

				});

		checkBoxViewer.addCheckStateListener(new ICheckStateListener() {
			/* only one check is possible */
			private boolean ignore = false;

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (ignore)
					return;
				Object element = event.getElement();
				if (event.getChecked()) {
					ignore = true;
					checkBoxViewer.setAllChecked(false);
					checkBoxViewer.setChecked(element, true);
					ignore = false;
				}
				// force the selection to the checked element
				checkBoxViewer.setSelection(new StructuredSelection(element));
			}

		});

		if (currentConfiguration != null)
			checkBoxViewer.setChecked(currentConfiguration, true);

		return main;
	}

	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		// just change the label of the OK button
		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setText("Run");
		return control;
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		changeButtonStatus((IStructuredSelection) checkBoxViewer.getSelection());
		return control;
	}

	private void changeButtonStatus(IStructuredSelection selection) {
		removeButton.setEnabled(!selection.isEmpty());
		editButton.setEnabled(!selection.isEmpty());
	}

	/**
	 * @return the new configuration or <code>null</code>
	 */
	private FilterModel performCreateConfiguration() {
		FilterModel filterModel = new FilterModel(fFilterManager.getProcessAlgebraModel());
		validator.isEditing = false;
		Dialog filterModelDialog = filterModel.createDialogControl(getShell(),
				validator);
		if (filterModelDialog.open() == filterModelDialog.OK) {
			fFilterManager.filterModels.add(filterModel);
			return filterModel;
		} else
			return null;
	}

	private void performRemoveConfiguration() {
		FilterModel current = getCurrentConfiguration();
		Assert
				.isNotNull(current,
						"Configuration cannot be null inside performRemoveConfiguration");
		fFilterManager.filterModels.remove(current);
	}

	private FilterModel performEditConfiguration(FilterModel current) {
		// FilterModel current = getCurrentConfiguration();
		Assert.isNotNull(current,
				"Configuration cannot be null inside performEditConfiguration");
		
		FilterModel filterModel = new FilterModel(this.fFilterManager
				.getProcessAlgebraModel());
		IMemento memento = XMLMemento.createWriteRoot("BASE");
		current.getMemento(memento);
		filterModel.setMemento(memento);
		
		validator.isEditing = true;
		Dialog filterModelDialog = filterModel.createDialogControl(getShell(),
				validator);
		if (filterModelDialog.open() == filterModelDialog.OK) {
			fFilterManager.filterModels.set(fFilterManager.filterModels
					.indexOf(current), filterModel);
			return filterModel;
		} else
			return current;

	}

	private FilterModel getCurrentConfiguration() {
		IStructuredSelection selection = (IStructuredSelection) checkBoxViewer
				.getSelection();
		FilterModel configuration = (FilterModel) selection
				.getFirstElement();
		return configuration;
	}

	private void updateControls() {
		checkBoxViewer.refresh();
	}
}
