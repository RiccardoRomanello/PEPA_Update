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
import uk.ac.ed.inf.pepa.parsing.ASTNode;

/**
 * This experiment produces X-Y plots.
 * <p>
 * This object is a children of {@link ExperimentPage} and a parent of
 * {@link IPerformanceMetric}.
 * 
 * @author mtribast
 * 
 */
public class BasicXYExperiment extends AbstractExperiment {

	/**
	 * ASTSetting's available within the wizard context
	 */
	protected ISetting[] settings = new ISetting[0];

	/**
	 * ASTSetting for the X Axis
	 */
	protected ISetting xAxisSetting = null;

	/* GUI Related Stuff */
	private Composite composite = null;

	private Combo xAxis;

	private ExperimentPage experimentPage;

	public BasicXYExperiment(String description) {
		super(description);
	}

	protected Composite createASTSettings(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginHeight = compositeLayout.marginWidth = 0;
		composite.setLayout(compositeLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite main = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		// layout.verticalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		/* Second row */

		/* Label */
		Label xAxisLabel = new Label(main, SWT.NULL);
		xAxisLabel.setText("X Axis");
		xAxisLabel.setLayoutData(new GridData());

		/* Combo */
		xAxis = new Combo(main, SWT.READ_ONLY);
		xAxis.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		xAxis.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				/*
				 * Update the model object
				 */
				Object o = xAxis.getData(xAxis.getText());
				if ((o == null) || !(o instanceof ISetting)) {
					return;
				}
				xAxisSetting = (ISetting) o;
				validate();
			}

		});

		populateXAxis();

		return composite;

	}

	private void populateXAxis() {
		xAxis.removeAll();
		for (ISetting setting : settings) {
			xAxis.add(setting.getDescription());
			xAxis.setData(setting.getDescription(), setting);
		}
		/* restore last selected object if any */
		if (xAxisSetting != null) {
			for (int i = 0; i < xAxis.getItemCount(); i++) {
				if (xAxis.getData(xAxis.getItem(i)) == xAxisSetting) {
					xAxis.select(i);
					break;
				}
			}
		}
	}

	/**
	 * TIP No SWT-related checks here, the widget may be disposed
	 */
	public boolean isCanRun() {
		return super.isCanRun() && xAxisSetting != null;
	}

	public void run(IProgressMonitor monitor, boolean showAsYouGo)
			throws EvaluationException {

		Assert.isTrue(isCanRun(), "Making sure that the experiment can be run");
		int numSettings = xAxisSetting.getSettingCount();
		monitor.beginTask(getName(), numSettings);
		monitor.subTask(getName() + " Generating graph");
		
		InfoWithAxes info = new InfoWithAxes();
		info.setGraphTitle(getName());
		info.setHas3DEffect(false);
		info.setShowLegend(false);
		double[] xValues = new double[numSettings];
		double[] yValues = new double[numSettings];
		ISetting[] settings = new ISetting[] { xAxisSetting };
		int[] currentIndex = new int[1];
		boolean isCanceled = false;
		for (int i = 0; i < numSettings; i++) {
			if (monitor.isCanceled()) {
				isCanceled = true;
				break;
			}
			monitor.subTask(getName() + " [" + (i + 1) + "/" + numSettings
					+ "]");
			currentIndex[0] = i;
			xValues[i] = xAxisSetting.getSetting(i);
			yValues[i] = currentMetric.evaluate(settings, currentIndex);
			
			monitor.worked(1);
		}
		info.setXSeries(Series.create(xValues, xAxisSetting.getDescription()));
		info.getYSeries().add(Series.create(yValues, currentMetric.getLabel()));
		info.setYLabel(currentMetric.getLabel());
		
		if (!isCanceled) {
			uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
					.getPlottingTools().createTimeSeriesChart(info);
			display(chart);

		}

		monitor.done();
	}

	

	public void setAvailableNodes(Object[] astNodes) {

		if (xAxisSetting == null)
			return;
		boolean found = false;
		for (Object astNode : astNodes) {
			ASTNode node = (ASTNode) astNode;
			if (xAxisSetting.getSensibleNode() == node) {
				found = true;
				break;
			}
		}
		currentSettingFoundAfterUpdate(found);
	}

	public void setAvailableSettings(ISetting[] settings) {

		Assert.isNotNull(settings, "At least one ASTSetting needed");

		this.settings = settings;
		/*
		 * If the available settings don't include the currently selected th the
		 * current one is not available and has to be discarded
		 */
		boolean found = false;
		for (ISetting newSetting : settings)
			if (newSetting == xAxisSetting) {
				found = true;
				break;
			}
		currentSettingFoundAfterUpdate(found);
	}

	private void currentSettingFoundAfterUpdate(boolean found) {
		/* Update the model */
		if (!found)
			xAxisSetting = null;

		/* Update the GUI if it not disposed */
		if (composite != null && !composite.isDisposed())
			populateXAxis();

		/* Trigger button update */
		validate();

	}

	public void dispose() {
		composite.dispose();
	}

	public ExperimentPage getExperimentPage() {
		return this.experimentPage;
	}

	public void setExperimentPage(ExperimentPage page) {
		this.experimentPage = page;
	}

}
