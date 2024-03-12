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

public class HydraSolverPage implements ISolverPageProxy {

	protected static class HydraPage extends AbstractConfigurationWizardPage {

		private IntegerConfigurationText maxIterText;

		private DoubleConfigurationText accuracyText;

		protected HydraPage() {
			super("HydraPage");
			setTitle(WizardMessages.HYDRA_AIR);
			setDescription("Set solver parameters");
		}

		@Override
		public void setOptions(OptionMap map) {
			super.setOptions(map);
		}

		@Override
		protected void fillSettingPanel() {

			Label iterLabel = new Label(settingPanel, labelStyle);
			iterLabel.setText("");
			GridData iterData = new GridData();
			iterData.horizontalSpan = 2;
			iterLabel.setLayoutData(iterData);

			Label labelMaxIter = new Label(settingPanel, labelStyle);
			labelMaxIter.setText("Maximum number of iterations");
			maxIterText.createControl(settingPanel);
			labelMaxIter.setLayoutData(new GridData());
			maxIterText.control.setLayoutData(new GridData(gridDataStyle));

			Label labelRTol = new Label(settingPanel, labelStyle);
			labelRTol.setText("Accuracy");
			accuracyText.createControl(settingPanel);
			labelRTol.setLayoutData(new GridData());
			accuracyText.control.setLayoutData(new GridData(gridDataStyle));

		}

		@Override
		protected void createConfigurationWidgets() {
			OptionMap map = ((SolverWizard) getWizard()).getOptionMap();

			maxIterText = new IntegerConfigurationText(map,
					OptionMap.HYDRA_MAX_ITERATIONS,this);

			accuracyText = new DoubleConfigurationText(map,
					OptionMap.HYDRA_ACCURACY,this);

			this.configurationWidgets.add(maxIterText);
			this.configurationWidgets.add(accuracyText);

		}

	}

	public HydraSolverPage() {
	}

	public boolean isNeedPage() {
		return true;
	}

	public AbstractConfigurationWizardPage getPage() {
		return new HydraPage();
	}

}
