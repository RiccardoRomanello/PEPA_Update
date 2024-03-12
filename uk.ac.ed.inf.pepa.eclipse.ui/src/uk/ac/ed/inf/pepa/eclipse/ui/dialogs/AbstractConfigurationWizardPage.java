/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.IResourceProvider;

/**
 * This is a wizard page contributing basic behaviour for managing persistency
 * for Pepato options. The page keeps track of {@link ConfigurationWidget},
 * which are objects containing SWT Widgets manipulating persistent option.
 * These objects expose methods for initialising their status from a
 * {@link IResource}'s persistent settings or, if none, from the plug-in's
 * default preferences.
 * <p>
 * The resource this pages uses must be provided by the containing wizard, which
 * must implement the {@link IResourceProvider} interface. If no resource is
 * provided, the configuration widgets will be initialised using the plug-in
 * preferences.
 * <p>
 * The page provides a basic GridLayout with 2 columns, which is expected to be
 * filled as pairs [label, configuration widget], the label describing the
 * option which can be set. Changes to the configuration widget are notified to
 * a unique listener which validates the form and set the status of the
 * completion of this page.
 * 
 * @author mtribast
 * 
 */
public abstract class AbstractConfigurationWizardPage extends WizardPage implements IValidationCallback {

	/**
	 * The list of configuration widgets managed by this page
	 */
	protected final List<ConfigurationWidget> configurationWidgets = new ArrayList<ConfigurationWidget>();

	/**
	 * The Composite with the GridLayout which accepts new configuration
	 * widgets.
	 */
	protected Composite settingPanel;

	protected int textStyle = SWT.BORDER | SWT.RIGHT;

	protected int labelStyle = SWT.RIGHT;

	protected int gridDataStyle = GridData.FILL_HORIZONTAL
			| GridData.GRAB_HORIZONTAL;
	
	/**
	 * Create a Wizard Page containing widgets for modifying configuration
	 * settings. The default values are taken from the plugin preference store.
	 * The widgets are initialised using the resource persistent values, if a
	 * resource is passed. If no resource is passed in, then the widgets are
	 * initialised using the preference store's current values.
	 * 
	 * @param pageName
	 *            page name
	 * @param resource
	 *            resource to look up for initialisation values. It may be null,
	 *            meaning that the preference store's current settings will be
	 *            used at initialisation.
	 */
	protected AbstractConfigurationWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void setWizard(IWizard wizard) {
		super.setWizard(wizard);
		createConfigurationWidgets();

	}

	public final void createControl(Composite parent) {

		settingPanel = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		settingPanel.setLayout(layout);
		setControl(settingPanel);

		fillSettingPanel();

		Button reset = new Button(settingPanel, SWT.NONE);
		reset.setText("Reset to defaults");

		GridData resetData = new GridData();
		resetData.horizontalSpan = 2;
		resetData.verticalSpan = 1;
		resetData.horizontalAlignment = SWT.RIGHT;
		reset.setLayoutData(resetData);
		reset.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				resetToDefaults();
			}
		});

		validate();

	}

	/**
	 * The standard implementation calls
	 * {@link ConfigurationWidget#resetToDefault()} on all the managed widgets
	 * 
	 */
	protected void resetToDefaults() {
		for (ConfigurationWidget widget : configurationWidgets) {
			widget.resetToDefault();
			widget.updateControl();
		}
	}

	/**
	 * Implementors add configuration widgets to the setting panel. Each
	 * configuration widget must be esplicitely added to the list by calling
	 * <code>add</code> on {@link #configurationWidgets}
	 * 
	 */
	abstract protected void fillSettingPanel();

	abstract protected void createConfigurationWidgets();
	
	public void setOptions(OptionMap map) {
		for (ConfigurationWidget widget : configurationWidgets) {
			map.put(widget.getProperty(), widget.getValue());
		}
	}
	
	
	/**
	 * This method is called when one of the configuration texts is changed. It
	 * calls <code>ConfigurationText#isValid()</code> and sets the error
	 * message of the page when one of these checks fails.
	 * <p>
	 * This method can be extended by users.
	 * 
	 */
	public void validate() {
		setPageComplete(false);
		setErrorMessage(null);

		boolean validation = true;
		for (ConfigurationWidget widget : configurationWidgets) {
			validation = widget.isValid();
			if (!validation) {
				setErrorMessage("Value not allowed");
				break;
			}
		}
		setPageComplete(validation);
	}

}
