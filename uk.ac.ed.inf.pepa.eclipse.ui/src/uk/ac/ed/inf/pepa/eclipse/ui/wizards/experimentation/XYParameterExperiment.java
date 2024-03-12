/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.common.ui.plotting.Plotting;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.Series;
import uk.ac.ed.inf.pepa.eclipse.ui.view.Tools;
import uk.ac.ed.inf.pepa.parsing.ASTNode;

public class XYParameterExperiment extends BasicXYExperiment {

	private Combo parameterCombo;

	private Composite main;

	private ISetting parameterSetting;

	public XYParameterExperiment(String description) {
		super(description);
	}

	protected Composite createASTSettings(Composite parent) {
		Composite composite = super.createASTSettings(parent);

		main = new Composite(composite, SWT.NULL);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);

		Label label = new Label(main, SWT.NULL);
		label.setText("Parameter");
		label.setLayoutData(new GridData());

		parameterCombo = new Combo(main, SWT.READ_ONLY);
		parameterCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		parameterCombo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				/*
				 * Update the model object
				 */
				Object o = parameterCombo.getData(parameterCombo.getText());
				if ((o == null) || !(o instanceof ISetting)) {
					return;
				}
				parameterSetting = (ISetting) o;
				validate();
			}

		});

		populateParameter();
		return composite;
	}

	private void populateParameter() {
		parameterCombo.removeAll();
		for (ISetting setting : settings) {
			parameterCombo.add(setting.getDescription());
			parameterCombo.setData(setting.getDescription(), setting);
		}
		/* restore last selected object if any */
		if (parameterSetting != null) {
			for (int i = 0; i < parameterCombo.getItemCount(); i++) {
				if (parameterCombo.getData(parameterCombo.getItem(i)) == parameterSetting) {
					parameterCombo.select(i);
					break;
				}
			}
		}
	}

	public boolean isCanRun() {
		return super.isCanRun() && parameterSetting != null
				&& parameterSetting != xAxisSetting;
	}

	public void run(IProgressMonitor monitor, boolean showAsYouGo)
			throws EvaluationException {

		Assert.isTrue(isCanRun());

		int xAxisSettings = xAxisSetting.getSettingCount();
		int parameterSettings = parameterSetting.getSettingCount();
		int totalSettings = xAxisSettings * parameterSettings;

		final String[] parameterLabels = generateLabelsForParameter();

		monitor.beginTask(getName(), xAxisSettings * parameterSettings);
		monitor.subTask(getName() + " Generating graph");

		InfoWithAxes info = new InfoWithAxes();
		info.setGraphTitle(getName());
		info.setHas3DEffect(false);
		info.setShowLegend(true);
		info.setShowMarkers(false);
		double[] xValues = new double[xAxisSettings];
		double[][] yValues = new double[parameterSettings][xAxisSettings];
		
		boolean isCanceled = false;
		ISetting[] sensibleNodes = new ISetting[] {xAxisSetting, parameterSetting};
		int[] values = new int[2];
		
		for (int j = 0; j < xAxisSettings; j++) {

			xValues[j] = xAxisSetting.getSetting(j);
			values[0] = j;
			for (int i = 0; i < parameterSettings; i++) {

				if (monitor.isCanceled()) {
					isCanceled = true;
					break;
				}

				monitor.subTask(getName() + " [" + (i + j + 1) + "/"
						+ totalSettings + "]");

				values[1] = i;
				yValues[i][j] = currentMetric.evaluate(sensibleNodes, values);
				monitor.worked(1);
			}

			if (isCanceled) {
				break;
			}
		}
		
		if (!isCanceled) {
			info.setXSeries(Series.create(xValues, xAxisSetting.getDescription()));
			for (int p = 0; p < parameterSettings; p++) {
				Series series = Series.create(yValues[p], parameterLabels[p]);
				info.getYSeries().add(series);
			}
			uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
			.getPlottingTools().createTimeSeriesChart(info);
			display(chart);
		}
		monitor.done();

	}

	private String[] generateLabelsForParameter() {
		String[] labels = new String[parameterSetting.getSettingCount()];
		for (int i = 0; i < labels.length; i++)
			labels[i] = parameterSetting.getDescription() + " "
					+ Tools.format(parameterSetting.getSetting(i));
		return labels;
	}

	public void setAvailableNodes(Object[] astNodes) {
		super.setAvailableNodes(astNodes);

		if (parameterSetting == null)
			return;
		boolean found = false;
		for (Object astNode : astNodes) {
			ASTNode node = (ASTNode) astNode;
			if (parameterSetting.getSensibleNode() == node) {
				found = true;
				break;
			}
		}
		currentSettingFoundAfterUpdate(found);
	}

	public void setAvailableSettings(ISetting[] settings) {
		super.setAvailableSettings(settings);
		/*
		 * If the available settings don't include the currently selected th the
		 * current one is not available and has to be discarded
		 */
		boolean found = false;
		for (ISetting newSetting : settings)
			if (newSetting == parameterSetting) {
				found = true;
				break;
			}
		currentSettingFoundAfterUpdate(found);
	}

	private void currentSettingFoundAfterUpdate(boolean found) {
		/* Update the model */
		if (!found)
			parameterSetting = null;

		/* Update the GUI if it not disposed */
		if (main != null && !main.isDisposed())
			populateParameter();

		/* Trigger button update */
		validate();

	}

}
