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
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.common.ui.plotview.views.PlotView;

public abstract class AbstractExperiment implements IExperiment, IDynamicParent {

	protected Composite performanceMetricContent;

	protected String description;

	protected Combo performanceMetrics;

	protected Text nameText;

	private AbstractPerformanceMetricFactory performanceFactory;

	private IDynamicParent dynamicParent;

	protected IPerformanceMetric currentMetric;

	private Font boldFont;

	public AbstractExperiment(String description) {
		Assert.isNotNull(description);
		setName(description);

	}

	static void display(final uk.ac.ed.inf.common.ui.plotting.IChart chart) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				final IWorkbenchPage page = window.getActivePage();
				final Shell shell = window.getShell();
				try {
					PlotView plotView = (PlotView) page.showView(PlotView.ID);
					plotView.reveal(chart);

				} catch (PartInitException e) {
					ErrorDialog.openError(shell, "Error",
							"Error displaying graph", e.getStatus());
				}
			}

		});
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				// disposes of the bold font
				if (boldFont != null && !boldFont.isDisposed())
					boldFont.dispose();
			}

		});
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		/* First row with span */

		Label debug = new Label(main, SWT.WRAP);
		debug.setText("Experiment Settings");
		FontData data = debug.getFont().getFontData()[0];
		data.setStyle(SWT.BOLD);
		boldFont = new Font(debug.getFont().getDevice(),
				new FontData[] { data });
		debug.setFont(boldFont);
		debug.setLayoutData(new GridData());

		Composite nameComp = new Composite(main, SWT.NONE);
		nameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout nameCompLayout = new GridLayout();
		nameCompLayout.numColumns = 1;
		nameComp.setLayout(nameCompLayout);

		Label nameLabel = new Label(nameComp, SWT.WRAP);
		nameLabel.setText("Name");
		nameLabel.setLayoutData(new GridData());

		nameText = new Text(nameComp, SWT.LEFT | SWT.BORDER);
		nameText.setText(description);
		nameText.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				setName(nameText.getText());
				validate();

			}

		});
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite astSettings = new Composite(main, SWT.NONE);
		astSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		astSettings.setLayout(new GridLayout());
		createASTSettings(astSettings);

		Composite performanceMetric = new Composite(main, SWT.NONE);
		performanceMetric.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		performanceMetric.setLayout(new GridLayout());

		createPerformanceMetric(performanceMetric);

	}

	/**
	 * Create the AST setting widget
	 * 
	 * @param parent
	 * @return
	 */
	protected abstract Composite createASTSettings(Composite parent);

	/**
	 * Create performance metric widgets
	 * 
	 * @param parent
	 * @return
	 */
	protected Composite createPerformanceMetric(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		// layout.verticalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label performanceLabel = new Label(main, SWT.NULL);
		performanceLabel.setText("Performance metric");
		performanceLabel.setLayoutData(new GridData());

		/* Combo */
		performanceMetrics = new Combo(main, SWT.READ_ONLY);
		performanceMetrics
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		performanceMetrics.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				createNewPerformanceMetric();
			}

		});

		/* Fourth row */

		performanceMetricContent = new Composite(main, SWT.NULL);
		GridData metricContent = new GridData(GridData.FILL_BOTH);
		performanceMetricContent.setLayoutData(metricContent);
		GridLayout performanceMetricContentLayout = new GridLayout();
		performanceMetricContentLayout.marginHeight = 0;
		performanceMetricContentLayout.marginWidth = 0;
		performanceMetricContentLayout.verticalSpacing = 0;
		performanceMetricContent.setLayout(performanceMetricContentLayout);

		populatePerformanceMetrics();

		return main;

	}

	private void populatePerformanceMetrics() {
		for (String desc : performanceFactory.getDescriptions())
			performanceMetrics.add(desc);
		/* restore last selected object if any */
		if (currentMetric != null) {
			performanceMetrics.setText(currentMetric.getDescription());
			showPerformanceMetricContent(currentMetric);
		}

	}

	private void createNewPerformanceMetric() {
		String selectedText = performanceMetrics.getText();

		IPerformanceMetric newMetric = performanceFactory
				.createPerformanceMetric(selectedText,
						((ExperimentationWizard) getExperimentPage()
								.getWizard()).getEvaluator());
		newMetric.setDynamicParent(this);
		newMetric.setExperiment(this);
		showPerformanceMetricContent(newMetric);
	}

	private void showPerformanceMetricContent(IPerformanceMetric metric) {

		currentMetric = metric;
		Assert.isNotNull(currentMetric);

		for (Control child : this.performanceMetricContent.getChildren()) {
			if (!child.isDisposed())
				child.dispose();
		}

		currentMetric.createControl(this.performanceMetricContent);

		/*
		 * TIP To dinamically change the content of a composite See Snippet98
		 * http://www.eclipse.org/swt/snippets/
		 */
		this.performanceMetricContent.layout(true);
		validate();

	}

	protected void validate() {
		updateParentState();
	}

	public void setName(String name) {
		description = name;
	}

	public String getName() {
		return description;
	}

	public boolean isCanRun() {
		return (currentMetric != null) && currentMetric.isCanEvaluate();
	}

	public abstract void run(IProgressMonitor monitor, boolean showAsYouGo)
			throws EvaluationException;

	public abstract void setAvailableNodes(Object[] astNodes);

	public abstract void setAvailableSettings(ISetting[] settings);

	public void setPerformanceMetricFactory(
			AbstractPerformanceMetricFactory factory) {

		this.performanceFactory = factory;
		Assert.isNotNull(factory);

	}

	public IDynamicParent getDynamicParent() {
		return dynamicParent;
	}

	public void setDynamicParent(IDynamicParent parent) {
		this.dynamicParent = parent;

	}

	public void updateParentState() {
		/*
		 * Ask my parent to update its state
		 */
		this.dynamicParent.updateParentState();

	}

}
