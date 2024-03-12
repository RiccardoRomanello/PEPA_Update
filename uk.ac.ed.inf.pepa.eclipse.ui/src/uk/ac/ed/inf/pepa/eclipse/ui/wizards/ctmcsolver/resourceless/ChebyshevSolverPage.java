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
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

public class ChebyshevSolverPage extends MTJIterativeSolverPage {

	protected ChebyshevSolverPage(int id1, int id2) {
		super(id1, id2);

	}

	@Override
	public AbstractConfigurationWizardPage getPage() {

		return new MTJPage(fSolverId, fPreconditionerId) {

			private DoubleConfigurationText eigMin;

			private DoubleConfigurationText eigMax;

			@Override
			protected void createConfigurationWidgets() {
				super.createConfigurationWidgets();
				OptionMap map = ((SolverWizard) getWizard()).getOptionMap();
				eigMin = new DoubleConfigurationText(map, OptionMap.CHEB_MIN,this);

				eigMax = new DoubleConfigurationText(map, OptionMap.CHEB_MAX,this);
				this.configurationWidgets.add(eigMin);
				this.configurationWidgets.add(eigMax);

			}

			@Override
			protected void fillSettingPanel() {

				super.fillSettingPanel();

				Label minLabel = new Label(settingPanel, labelStyle);
				minLabel.setText("Minimum eigenvalue estimate");
				eigMin.createControl(settingPanel);

				minLabel.setLayoutData(new GridData());
				eigMin.control.setLayoutData(new GridData(gridDataStyle));

				Label maxLabel = new Label(settingPanel, labelStyle);
				maxLabel.setText("Maximum eigenvalue estimate");
				eigMax.createControl(settingPanel);
				maxLabel.setLayoutData(new GridData());
				eigMax.control.setLayoutData(new GridData(gridDataStyle));

				setDescription(WizardMessages.CHEB_DESCRIPTION);
				/*
				 * Temporary: force the user to set eigenvalue estimates
				 */
				setPageComplete(false);

			}

		};
	}

}
