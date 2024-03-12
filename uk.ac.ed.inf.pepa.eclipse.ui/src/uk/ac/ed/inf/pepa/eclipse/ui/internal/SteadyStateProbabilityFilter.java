/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class SteadyStateProbabilityFilter extends
		AbstractConfigurableStateSpaceFilter {

	public SteadyStateProbabilityFilter(IProcessAlgebraModel model) {
		super(model);
	}

	private static final String AFFIRMED = "is greater than";

	private static final String NEGATED = "is less or equal than";

	/* Memento top element */
	private static final String TAG_THRESHOLD_FILTER = "thresholdFilter";

	/* Negation */
	private static final String TAG_NEGATION = "negation";

	/* Threshold value */
	private static final String TAG_THRESHOLD_VALUE = "threshold";

	private Text thresholdText;

	private double thresholdValue = 0.0d; // set by the text

	private IFilterValidatorListener validatorListener;

	public void createGUI(Composite parent) {

		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(main, SWT.NULL);
		label.setText("States whose steady-state probability");
		label.setLayoutData(new GridData());

		Control negationCombo = createNegationCombo(main);
		negationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label stateSelection = new Label(main, SWT.NULL);
		stateSelection.setText("Enter steady-state probability:");

		stateSelection.setLayoutData(new GridData());
		Control other = createControl(main);
		other.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	protected Control createControl(Composite parent) {
		if (thresholdText == null || thresholdText.isDisposed())
			thresholdText = _createThresholdText(parent);
		return thresholdText;
	}

	private Text _createThresholdText(Composite parent) {
		final Text text = new Text(parent, SWT.BORDER);
		text.setText("" + thresholdValue);
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				validate();
			}

		});
		return text;
	}

	private void validate() {
		String message = null;
		try {
			thresholdValue = Double.parseDouble(thresholdText.getText());
		} catch (NumberFormatException ne) {
			message = "Insert a valid number";
		}
		if (thresholdValue < 0)
			message = "Negative values not allowed";
		if (thresholdValue > 1)
			message = "Values greater than 1 not allowed";

		validatorListener.filterValidated(message);
	}

	@Override
	protected String getAffirmedString() {
		return AFFIRMED;
	}

	@Override
	protected void getMemento(IMemento memento) {
		IMemento mem = memento.createChild(TAG_THRESHOLD_FILTER);
		mem.putString(TAG_THRESHOLD_VALUE, "" + thresholdValue);
		mem.putString(TAG_NEGATION, "" + negation);
	}

	@Override
	protected String getNegatedString() {
		return NEGATED;
	}

	@Override
	protected void setMemento(IMemento memento) {
		IMemento mem = memento.getChild(TAG_THRESHOLD_FILTER);
		try {
			thresholdValue = Double.parseDouble(mem
					.getString(TAG_THRESHOLD_VALUE));
		} catch (NumberFormatException e) {
			throw new IllegalStateException(
					"Threshold value error when recovering from a memento");
		}
		negation = Boolean.parseBoolean(mem.getString(TAG_NEGATION));
	}

	@Override
	public String getParameter() {
		return thresholdValue + "";
	}

	public String getLabel() {
		return "States whose steady-state probability " + getDescription()
				+ " " + getParameter();
	}

	@Override
	public void setFilterValidatorListener(IFilterValidatorListener listener) {
		this.validatorListener = listener;
		validate();
	}

	@Override
	protected IStateSpaceFilter doCreateFilter() {
		return FilterFactory.createSteadyStateThreshold(
				FilterFactory.Operator.GT, this.thresholdValue);
	}

}
