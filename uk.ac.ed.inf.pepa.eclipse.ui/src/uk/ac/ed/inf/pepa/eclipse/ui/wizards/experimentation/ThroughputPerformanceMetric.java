/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;

public abstract class ThroughputPerformanceMetric extends AbstractPerformanceMetric {

	private Combo combo;

	private String lastValue = null;
	
	public ThroughputPerformanceMetric(String description, IEvaluator evaluator) {
		super(description, evaluator);
	}

	protected abstract Collection<String> fillActionTypes();
	
	public void createControl(Composite parent) {

		Collection<String> actionTypes = fillActionTypes();

		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		// layout.marginHeight = 0;
		layout.marginWidth = 0;
		// layout.verticalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(main, SWT.WRAP);
		label.setText("Action");
		label.setLayoutData(new GridData());

		combo = new Combo(main, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				handleSelection();
			}

		});

		populateCombo(actionTypes);

	}

	private void populateCombo(Collection<String> actionTypes) {
		for (String action : actionTypes)
			combo.add(action);
		/* remember the last value from the model object */
		if (lastValue != null)
			combo.setText(lastValue);

	}

	private void handleSelection() {
		lastValue = combo.getText();
		this.parent.updateParentState();

	}

	public double evaluate(ISetting[] settings, int[] currentIndex)
		throws EvaluationException {
		double result = Double.NaN;

		try {
			IStateSpace ss = evaluator.doEvaluate(settings, currentIndex);
			ThroughputResult[] results = ss.getThroughput();
			for (ThroughputResult r : results) {
				if (r.getActionType().equals(lastValue)) {
					result = r.getThroughput();
					break;
				}
			}
			Assert.isTrue(!Double.isNaN(result));
		} catch (Exception e) {
			PepaLog.logError(e);
			e.printStackTrace();
			throw new EvaluationException(e);
		}
		return result;
	}

	public boolean isCanEvaluate() {
		return lastValue != null;
	}

	@Override
	public String getLabel() {
		return getDescription() + ": " + lastValue;
	}

}
