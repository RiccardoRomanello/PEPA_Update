package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.ConfigurationWidget;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.DoubleConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IntegerConfigurationText;

class SimulationSolverOptionsHandler extends SolverOptionsHandler {

	public SimulationSolverOptionsHandler(boolean supportsTransient,
			OptionMap map, IValidationCallback cb) {
		super(supportsTransient, map, cb);
	}

	private DoubleConfigurationText startText;

	private IntegerConfigurationText stepText;

	private IntegerConfigurationText iterText;

	private DoubleConfigurationText confidenceLevelErrorText;

	private Combo convergenceCombo;

	private Label stopLabel;

	@Override
	protected void fillDialogArea(Composite composite) {
		Label convergenceCriterion = new Label(composite, SWT.NONE);
		convergenceCriterion.setText("Convergence criterion");
		convergenceCriterion.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		convergenceCombo = new Combo(composite, SWT.READ_ONLY | SWT.NONE);
		convergenceCombo.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		convergenceCombo.add("Confidence level");
		convergenceCombo.add("Number of replications");
		convergenceCombo.select(0);
		convergenceCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				cb.validate();
			}

		});

		startText = (DoubleConfigurationText) configure(composite,
				"Start time", OptionMap.SSA_START_TIME, true);

		stopLabel = (Label) configureComplete(composite, "Stop time",
				OptionMap.SSA_STOP_TIME, true)[1];

		stepText = (IntegerConfigurationText) configure(composite,
				"Number of time points", OptionMap.SSA_TIME_POINTS, false);

		iterText = (IntegerConfigurationText) configure(composite,
				"Number of replications", OptionMap.SSA_MAX_ITERATIONS,
				false);

		configure(composite, "Confidence level",
				OptionMap.SSA_CONFIDENCE_LEVEL, true);

		confidenceLevelErrorText = (DoubleConfigurationText) configure(
				composite, "Confidence level percentage error",
				OptionMap.SSA_CONFIDENCE_PERCENT_ERROR, true);

		convergenceCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				confidenceLevelErrorText.control
						.setEnabled(convergenceCombo.getSelectionIndex() == 0);
			}

			public void widgetSelected(SelectionEvent e) {
				confidenceLevelErrorText.control
						.setEnabled(convergenceCombo.getSelectionIndex() == 0);
			}

		});
	}

	protected void setTransient(boolean isTransient) {
		super.setTransient(isTransient);
		startText.control.setEnabled(isTransient);
		stepText.control.setEnabled(isTransient);
		iterText.control.setEnabled(isTransient);
		if (isTransient) {
			convergenceCombo.setEnabled(true);
			confidenceLevelErrorText.control.setEnabled(convergenceCombo
					.getSelectionIndex() == 0);
			stopLabel.setText("Stop time");
		} else {
			convergenceCombo.setEnabled(false);
			convergenceCombo.select(0);
			confidenceLevelErrorText.control.setEnabled(true);
			stopLabel.setText("Transient period");
		}

	}

	public String getConvergenceCriterion() {
		if (convergenceCombo.getSelectionIndex() == 0)
			return OptionMap.SSA_CONFIDENCE_LEVEL_CRITERION;
		else
			return OptionMap.SSA_MAX_ITERATIONS_CRITERION;
	}

	@Override
	protected OptionMap updateOptionMap() {
		OptionMap map = super.updateOptionMap();
		map.put(OptionMap.SSA_CRITERION_OF_CONVERGENCE,
				getConvergenceCriterion());
		for (ConfigurationWidget w : widgets) {
			map.put(w.getProperty(), w.getValue());
		}
		return map;
	}

}