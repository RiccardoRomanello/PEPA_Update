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
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.DoubleConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IntegerConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

public class ILUTPreconditionerPage implements ISolverPageProxy {

	public static class ILUTPage extends AbstractConfigurationWizardPage {
		private DoubleConfigurationText tau;
		private IntegerConfigurationText p;

		protected ILUTPage() {
			super("ILUT");
			setTitle(WizardMessages.ILUT_TITLE);
			setDescription(WizardMessages.ILUT_DESCRIPTION);
		}

		@Override
		protected void fillSettingPanel() {
			Label tauLabel = new Label(this.settingPanel, labelStyle);
			tauLabel.setText("Drop tolerance");
			tau.createControl(settingPanel);

			Label pLabel = new Label(this.settingPanel, labelStyle);
			pLabel.setText("Number of entries to keep on each row");
			p.createControl(settingPanel);
			this.configurationWidgets.add(p);

			int horizontal = GridData.FILL_HORIZONTAL
					| GridData.GRAB_HORIZONTAL;

			tauLabel.setLayoutData(new GridData());
			tau.control.setLayoutData(new GridData(horizontal));
			pLabel.setLayoutData(new GridData());
			p.control.setLayoutData(new GridData(horizontal));

		}

		@Override
		protected void createConfigurationWidgets() {
			OptionMap map = ((SolverWizard) getWizard()).getOptionMap();

			tau = new DoubleConfigurationText(map, OptionMap.ILUT_TAU,this);
			this.configurationWidgets.add(tau);

			p = new IntegerConfigurationText(map, OptionMap.ILUT_P,this);
			this.configurationWidgets.add(p);

		}

	}

	public boolean isNeedPage() {
		return true;
	}

	public AbstractConfigurationWizardPage getPage() {
		return new ILUTPage();
	}

}
