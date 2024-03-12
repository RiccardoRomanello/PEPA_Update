/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterManager;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterModel;

/**
 * Calculate the overall probability of a filtered state space.
 * 
 * @author mtribast
 * 
 */
public class FilteredStateSpaceOverallProbability extends
		AbstractPerformanceMetric {

	private Combo combo;

	private IFilterManager filterManager;

	private IFilterModel selectedFilterSet = null;

	private IStateSpaceFilter[] filters;
	
	public FilteredStateSpaceOverallProbability(String description, IEvaluator evaluator) {
		super(description, evaluator);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		// layout.marginHeight = 0;
		layout.marginWidth = 0;
		// layout.verticalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(main, SWT.WRAP);
		label.setText("Filter name");
		label.setLayoutData(new GridData());

		combo = new Combo(main, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				handleSelection();
			}

		});

		populateCombo();

	}

	private void populateCombo() {
		for (IFilterModel filterModel : filterManager.getFilterModels()) {
			combo.add(filterModel.getName());
			combo.setData(filterModel.getName(), filterModel);
		}
		/* select the last one */
		if (selectedFilterSet != null) {
			for (int i = 0; i < combo.getItemCount(); i++)
				if (combo.getItem(i).equals(selectedFilterSet.getName())) {
					combo.select(i);
					break;
				}
		}

	}

	private void handleSelection() {
		Object data = combo.getData(combo.getText());
		selectedFilterSet = (data != null) ? (IFilterModel) data : null;
		if (selectedFilterSet != null) {
			filters = selectedFilterSet.getFilters();
		}
		this.parent.updateParentState();

	}

	public double evaluate(ISetting[] settings, int[] currentIndex)
			throws EvaluationException {

		double result = 0d;

		try {
			IStateSpace ss = evaluator.doEvaluate(settings, currentIndex);
			// no computation of transient states
			boolean makesAll;
			IFilterRunner[] runners = new IFilterRunner[filters.length];
			for (int j = 0; j < runners.length; j++) {
				runners[j] = filters[j].getRunner(ss);
			}
			for (int s = 0; s < ss.size(); s++) {
				makesAll = true;
				for (IFilterRunner runner : runners) {
					if (!runner.select(s)) {
						makesAll = false;
						break;
					}
				}
				if (makesAll)
					result += ss.getSolution(s);
			}

		} catch (Exception e) {
			PepaLog.logError(e);
			throw new EvaluationException(e);
		}
		return result;
	}

	public boolean isCanEvaluate() {
		return filters != null && filters.length != 0;
	}

	public void setExperiment(IExperiment experiment) {
		super.setExperiment(experiment);
		filterManager = Activator.getDefault().getFilterManagerProvider()
				.getFilterManager(getModel());
	}

	@Override
	public String getLabel() {
		return getDescription() + ": " + selectedFilterSet.getName();
	}

}
