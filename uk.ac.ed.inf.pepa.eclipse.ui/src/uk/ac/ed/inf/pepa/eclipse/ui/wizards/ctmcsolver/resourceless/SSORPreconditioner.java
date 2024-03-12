/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.AbstractConfigurationWizardPage;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.ConfigurationCheckBox;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.DoubleConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

public class SSORPreconditioner implements ISolverPageProxy {

	private static class SSORPage extends AbstractConfigurationWizardPage {

		private DoubleConfigurationText textOmegaf;

		private DoubleConfigurationText textOmegar;

		private ConfigurationCheckBox check;

		SSORPage() {
			super("SSORPreconditionerPage");
			setTitle(WizardMessages.SSOR_TITLE);
		}

		@Override
		protected void fillSettingPanel() {

			Label labelOmegaf = new Label(this.settingPanel, labelStyle);
			labelOmegaf
					.setText("Overrelaxation parameter for forward sweep (between 0 and 2)");
			textOmegaf.createControl(settingPanel);

			Label labelOmegar = new Label(this.settingPanel, labelStyle);
			labelOmegar
					.setText("Overrelaxation parameter for backward sweep (between 0 and 2)");
			textOmegar.createControl(settingPanel);

			Label reverse = new Label(this.settingPanel, labelStyle);
			reverse.setText("Perform both sweeps");
			check.createControl(settingPanel);

			int horizontal = GridData.FILL_HORIZONTAL
					| GridData.GRAB_HORIZONTAL;

			labelOmegaf.setLayoutData(new GridData());
			textOmegaf.control.setLayoutData(new GridData(horizontal));
			labelOmegar.setLayoutData(new GridData());
			textOmegar.control.setLayoutData(new GridData(horizontal));
			reverse.setLayoutData(new GridData());
			check.control.setLayoutData(new GridData(horizontal));

		}

		@Override
		protected void createConfigurationWidgets() {
			OptionMap map = ((SolverWizard) getWizard()).getOptionMap();
			textOmegaf = new DoubleConfigurationText(map,
					OptionMap.SSOR_OMEGA_F, this);
			this.configurationWidgets.add(textOmegaf);

			textOmegar = new DoubleConfigurationText(map,
					OptionMap.SSOR_OMEGA_R, this);
			this.configurationWidgets.add(textOmegar);

			check = new ConfigurationCheckBox(map, OptionMap.SSOR_REVERSE, this);
			this.configurationWidgets.add(check);

		}
	}

	public AbstractConfigurationWizardPage getPage() {
		return new SSORPage();
	}

	public boolean isNeedPage() {
		return true;
	}

}
