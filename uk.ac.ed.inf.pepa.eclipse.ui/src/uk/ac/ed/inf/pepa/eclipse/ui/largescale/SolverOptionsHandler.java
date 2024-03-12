package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.ConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.ConfigurationWidget;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.DoubleConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IntegerConfigurationText;

abstract class SolverOptionsHandler {

	private boolean isTransient = false;

	private boolean supportsTransient;

	protected Combo transientCombo;

	protected OptionMap map;

	protected IValidationCallback cb;

	protected ArrayList<ConfigurationWidget> widgets = new ArrayList<ConfigurationWidget>();

	public SolverOptionsHandler(boolean supportsTransient, OptionMap map,
			IValidationCallback cb) {
		this.supportsTransient = supportsTransient;
		this.map = map;
		this.cb = cb;
	}

	public final Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layoutTP = new GridLayout();
		layoutTP.marginWidth = 5;
		layoutTP.numColumns = 2;
		composite.setLayout(layoutTP);
		Label analysisLabel = new Label(composite, SWT.NONE);
		analysisLabel.setText("Kind of analysis");
		analysisLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		transientCombo = new Combo(composite, SWT.READ_ONLY | SWT.NONE);
		transientCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		transientCombo.add("Steady state");
		if (supportsTransient)
			transientCombo.add("Transient");
		transientCombo.select(0);
		transientCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setTransient(transientCombo.getText().equals("Transient"));
				cb.validate();
			}

		});

		fillDialogArea(composite);
		setTransient(false);

		return composite;

	}

	protected void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	protected abstract void fillDialogArea(Composite composite);

	public boolean isConfigurationValid() {
		for (ConfigurationWidget w : widgets)
			if (!w.isValid())
				return false;
		return true;
	}

	// protected abstract void
	public final boolean isTransient() {
		return isTransient;
	}

	protected ConfigurationText configure(Composite composite,
			String labelText, String key, boolean isDouble) {
		return (ConfigurationText) configureComplete(composite, labelText, key,
				isDouble)[0];
	}

	protected Object[] configureComplete(Composite composite, String labelText,
			String key, boolean isDouble) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ConfigurationText configurationText;
		if (isDouble)
			configurationText = new NonNegativeDoubleConfigurationText(map, key, cb);
		else
			configurationText = new PositiveIntegerConfigurationText(map, key, cb);
		configurationText.createControl(composite);
		configurationText.control.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		widgets.add(configurationText);
		return new Object[] { configurationText, label };
	}

	protected OptionMap updateOptionMap() {
		for (ConfigurationWidget w : widgets) {
			map.put(w.getProperty(), w.getValue());
		}
		return map;
	}

}
