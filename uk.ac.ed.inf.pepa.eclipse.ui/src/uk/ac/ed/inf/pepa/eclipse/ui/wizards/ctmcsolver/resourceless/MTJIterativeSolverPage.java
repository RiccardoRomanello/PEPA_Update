/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.ctmcsolver.resourceless;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.AbstractConfigurationWizardPage;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.DoubleConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IntegerConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

public class MTJIterativeSolverPage implements ISolverPageProxy {

	protected static class MTJPage extends AbstractConfigurationWizardPage {

		private Combo comboPreconditioners;

		private IntegerConfigurationText maxIterText;

		private DoubleConfigurationText rTolText;

		private DoubleConfigurationText aTolText;

		private DoubleConfigurationText dTolText;

		private String currentSelectedPreconditioner = null;

		private int fPreconditionerId;

		protected MTJPage(int solverId, int preconditionerId) {
			super("MTJIterativeSolverPage");
			fPreconditionerId = preconditionerId;
			setTitle(WizardMessages.MTJ_ITERATION_TITLE);
			setDescription("Set solver parameters");
		}

		@Override
		public void setOptions(OptionMap map) {
			super.setOptions(map);
			map.put(OptionMap.PRECONDITIONER, fPreconditionerId);
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

			Label labelRTol = new Label(settingPanel, labelStyle);
			labelRTol.setText("Relative tolerance");
			rTolText.createControl(settingPanel);

			Label labelATol = new Label(settingPanel, labelStyle);
			labelATol.setText("Absolute tolerance");
			aTolText.createControl(settingPanel);

			Label labelDTol = new Label(settingPanel, labelStyle);
			labelDTol.setText("Divergence tolerance");
			dTolText.createControl(settingPanel);

			labelMaxIter.setLayoutData(new GridData());

			maxIterText.control.setLayoutData(new GridData(gridDataStyle));
			labelRTol.setLayoutData(new GridData());
			rTolText.control.setLayoutData(new GridData(gridDataStyle));
			labelATol.setLayoutData(new GridData());
			aTolText.control.setLayoutData(new GridData(gridDataStyle));
			labelDTol.setLayoutData(new GridData());
			dTolText.control.setLayoutData(new GridData(gridDataStyle));

			Label precondLabel = new Label(settingPanel, SWT.NONE);
			precondLabel.setText("Select the preconditioner");
			precondLabel.setLayoutData(new GridData());

			comboPreconditioners = new Combo(settingPanel, SWT.READ_ONLY);
			populatePreconditionerCombo();
			comboPreconditioners.setLayoutData(new GridData(gridDataStyle));

			setPreconditioner(fPreconditionerId);

		}

		private void setPreconditioner(int id) {
			comboPreconditioners.setText(Preconditioners.getInstance()
					.getPreconditionerName(id));
			newPreconditionerSelected(comboPreconditioners.getText());
		}

		@Override
		protected void resetToDefaults() {
			super.resetToDefaults();
			setDefaultPreconditioner();
		}

		private void setDefaultPreconditioner() {
			setPreconditioner(PepaCore.getDefault().getPluginPreferences()
					.getDefaultInt(OptionMap.PRECONDITIONER));
		}

		@Override
		protected void createConfigurationWidgets() {
			OptionMap map = ((SolverWizard) getWizard()).getOptionMap();
			
			maxIterText = new IntegerConfigurationText(
					map, OptionMap.ITER_MON_MAX_ITER,this);

			rTolText = new DoubleConfigurationText(
					map, OptionMap.ITER_MON_RTOL,this);

			aTolText = new DoubleConfigurationText(
					map, OptionMap.ITER_MON_ATOL,this);

			dTolText = new DoubleConfigurationText(map, OptionMap.ITER_MON_DTOL,this);
			this.configurationWidgets.add(maxIterText);
			this.configurationWidgets.add(rTolText);
			this.configurationWidgets.add(aTolText);
			this.configurationWidgets.add(dTolText);

		}

		private void populatePreconditionerCombo() {

			Collection<String> precondStrings = Preconditioners.getInstance()
					.getAvailablePreconditioners();

			for (String element : precondStrings)
				comboPreconditioners.add(element);

			comboPreconditioners.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					newPreconditionerSelected(comboPreconditioners.getText());
				}
			});
		}

		private void newPreconditionerSelected(String textPreconditioner) {
			setPageComplete(false);
			this.fPreconditionerId = Preconditioners.getInstance()
					.getPreconditionerId(textPreconditioner);
			ISolverPageProxy preconditionerPage = PreconditionerPageFactory
					.createPreconditionerPageFor(fPreconditionerId);

			if (preconditionerPage == null) {
				/* An unexpected error occurred */
				setMessage(null);
				String error = "No Preconditioner available";
				setErrorMessage(error);
				return;
			}

			if (textPreconditioner.equals(currentSelectedPreconditioner)) {
				// do nothing
			} else {
				AbstractConfigurationWizardPage newPage = preconditionerPage
						.getPage();
				((SolverWizard) getWizard()).fPreconditionerPage = newPage;
				if (newPage != null) {
					newPage.setWizard(getWizard());
				}

			}
			this.currentSelectedPreconditioner = textPreconditioner;
			setMessage(null);
			setErrorMessage(null);
			setPageComplete(true);

		}
	}

	protected int fSolverId;
	
	protected int fPreconditionerId;

	public MTJIterativeSolverPage(int solverId, int preconditionerId) {
		this.fSolverId = solverId;
		this.fPreconditionerId = preconditionerId;
	}

	public boolean isNeedPage() {
		return true;
	}

	public AbstractConfigurationWizardPage getPage() {
		return new MTJPage(fSolverId, fPreconditionerId);
	}

}
