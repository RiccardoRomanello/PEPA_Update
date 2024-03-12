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

public class AMGPreconditionerPage implements ISolverPageProxy {

	public static class AMGWizardPage extends AbstractConfigurationWizardPage {

		public AMGWizardPage(String name) {
			super(name);
			setTitle(WizardMessages.AMG_TITLE);
			setDescription(WizardMessages.AMG_DESCRIPTION);
		}

		private DoubleConfigurationText textOmegaPref;

		private DoubleConfigurationText textOmegaPrer;

		private DoubleConfigurationText textOmegaPostf;

		private DoubleConfigurationText textOmegaPostr;

		private IntegerConfigurationText textNu1;

		private IntegerConfigurationText textNu2;

		private IntegerConfigurationText textGamma;

		private IntegerConfigurationText textMin;

		private DoubleConfigurationText textOmega;

		@Override
		protected void fillSettingPanel() {

			Label labelOmegaPref = new Label(this.settingPanel, labelStyle);
			labelOmegaPref.setText("Forward sweep of the pre-smoothing");
			textOmegaPref.createControl(settingPanel);

			Label labelOmegaPrer = new Label(this.settingPanel, labelStyle);
			labelOmegaPrer.setText("Backwards sweep of the pre-smoothing");
			textOmegaPrer.createControl(settingPanel);

			Label labelOmegaPostf = new Label(this.settingPanel, labelStyle);
			labelOmegaPostf.setText("Forward sweep of the post-smoothing");
			textOmegaPostf.createControl(settingPanel);

			Label labelOmegaPostr = new Label(this.settingPanel, labelStyle);
			labelOmegaPostr.setText("Backwards sweep of the post-smoothing");
			textOmegaPostr.createControl(settingPanel);

			Label labelNu1 = new Label(this.settingPanel, labelStyle);
			labelNu1.setText("Pre-relaxations");
			textNu1.createControl(settingPanel);

			Label labelNu2 = new Label(this.settingPanel, labelStyle);
			labelNu2.setText("Post-relaxations");
			textNu2.createControl(settingPanel);

			Label labelGamma = new Label(this.settingPanel, labelStyle);
			labelGamma.setText("Coarser level parameter");
			textGamma.createControl(settingPanel);

			Label labelMin = new Label(this.settingPanel, labelStyle);
			labelMin.setText("Smallest matrix size");
			textMin.createControl(settingPanel);

			Label labelOmega = new Label(this.settingPanel, labelStyle);
			labelOmega.setText("Jacobi damping parameter");
			textOmega.createControl(settingPanel);
			int horizontal = GridData.FILL_HORIZONTAL
					| GridData.GRAB_HORIZONTAL;

			labelOmegaPref.setLayoutData(new GridData());
			textOmegaPref.control.setLayoutData(new GridData(horizontal));
			labelOmegaPrer.setLayoutData(new GridData());
			textOmegaPrer.control.setLayoutData(new GridData(horizontal));
			labelOmegaPostf.setLayoutData(new GridData());
			textOmegaPostf.control.setLayoutData(new GridData(horizontal));
			labelOmegaPostr.setLayoutData(new GridData());
			textOmegaPostr.control.setLayoutData(new GridData(horizontal));
			labelNu1.setLayoutData(new GridData());
			textNu1.control.setLayoutData(new GridData(horizontal));
			labelNu2.setLayoutData(new GridData());
			textNu2.control.setLayoutData(new GridData(horizontal));
			labelGamma.setLayoutData(new GridData());
			textGamma.control.setLayoutData(new GridData(horizontal));
			labelMin.setLayoutData(new GridData());
			textMin.control.setLayoutData(new GridData(horizontal));
			labelOmega.setLayoutData(new GridData());
			textOmega.control.setLayoutData(new GridData(horizontal));

		}

		@Override
		protected void createConfigurationWidgets() {

			OptionMap map = ((SolverWizard) getWizard()).getOptionMap();
			textOmegaPref = new DoubleConfigurationText(map,
					OptionMap.AMG_OMEGA_PRE_F_KEY,this);
			
			this.configurationWidgets.add(textOmegaPref);

			textOmegaPrer = new DoubleConfigurationText(map,
					OptionMap.AMG_OMEGA_PRE_R_KEY,this);
			this.configurationWidgets.add(textOmegaPrer);

			textOmegaPostf = new DoubleConfigurationText(map,
					OptionMap.AMG_OMEGA_POST_F_KEY,this);
			this.configurationWidgets.add(textOmegaPostf);

			textOmegaPostr = new DoubleConfigurationText(map,
					OptionMap.AMG_OMEGA_POST_R_KEY,this);
			this.configurationWidgets.add(textOmegaPostr);

			textNu1 = new IntegerConfigurationText(map, OptionMap.AMG_NU_1_KEY,this);
			this.configurationWidgets.add(textNu1);

			textNu2 = new IntegerConfigurationText(map, OptionMap.AMG_NU_2_KEY,this);
			this.configurationWidgets.add(textNu2);

			textGamma = new IntegerConfigurationText(map,
					OptionMap.AMG_GAMMA_KEY,this);
			this.configurationWidgets.add(textGamma);

			textMin = new IntegerConfigurationText(map, OptionMap.AMG_MIN_KEY,this);
			this.configurationWidgets.add(textMin);

			textOmega = new DoubleConfigurationText(map,
					OptionMap.AMG_OMEGA_KEY,this);
			this.configurationWidgets.add(textOmega);

		}

	}

	public boolean isNeedPage() {
		return true;
	}

	public AbstractConfigurationWizardPage getPage() {
		AbstractConfigurationWizardPage wp = new AMGWizardPage(
				"AMGPreconditionerPage");
		return wp;
	}

}
