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
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IntegerConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

public class GMRESSolverPage extends MTJIterativeSolverPage {

	protected GMRESSolverPage(int id1, int id2) {
		super(id1, id2);
	}

	@Override
	public AbstractConfigurationWizardPage getPage() {

		return new MTJPage(fSolverId, fPreconditionerId) {

			private IntegerConfigurationText restartText;

			@Override
			protected void fillSettingPanel() {
				super.fillSettingPanel();
				Label label = new Label(settingPanel, labelStyle);
				label.setText("Restart");
				label.setLayoutData(new GridData());

				restartText.createControl(settingPanel);
				restartText.control.setLayoutData(new GridData(gridDataStyle));
			}

			@Override
			protected void createConfigurationWidgets() {
				super.createConfigurationWidgets();
				OptionMap map = ((SolverWizard) getWizard()).getOptionMap();
				
				restartText = new IntegerConfigurationText(
						map, OptionMap.GMRES_RESTART,this);
				this.configurationWidgets.add(restartText);
				setDescription(WizardMessages.GMRES_DESCRIPTION);

			}

		};
	}
}
