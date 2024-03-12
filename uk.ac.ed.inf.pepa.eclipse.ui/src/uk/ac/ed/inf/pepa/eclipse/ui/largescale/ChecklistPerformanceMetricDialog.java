package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.DefaultCollector;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;

public abstract class ChecklistPerformanceMetricDialog extends
		PerformanceMetricDialog {

	protected CheckboxTableViewer viewer;

	private ViewerFilter filter;

	public ChecklistPerformanceMetricDialog(boolean supportsTransient,
			boolean isFluid, Shell parentShell,
			IParametricDerivationGraph derivationGraph, IPepaModel model) {
		super(supportsTransient, isFluid, parentShell, derivationGraph, model);
		filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer filteredViewer, Object parentElement,
					Object element) {
				if (viewer.getChecked(element))
					return true;
				String label = getProvider().getColumnText(element, 0);
				return label.contains(filterText.getText());
			}
		};

	}

	abstract String getShellTitle();

	abstract ITableLabelProvider getProvider();

	abstract Object getViewerInput();

	protected AnalysisJob getAnalysisJob() {
		String[] labels = getLabels();
		IPointEstimator[] performanceMetrics = getPerformanceMetrics();
		IStatisticsCollector[] collectors = DefaultCollector
				.create(performanceMetrics);
		AnalysisJob job = null;
		if (isFluid() && fSolverOptionsHandler.isTransient())
			job = new AnalysisJobFluidTransient(getDialogTitle(),
					fDerivationGraph, fOptionMap, performanceMetrics,
					collectors, labels);
		else if (isFluid() && !fSolverOptionsHandler.isTransient())
			job = new AnalysisJobFluidSteadyState(getDialogTitle(),
					fDerivationGraph, fOptionMap, performanceMetrics,
					collectors, labels);
		else if (!isFluid() && fSolverOptionsHandler.isTransient())
			job = new AnalysisJobSimulationTransient(getDialogTitle(),
					fDerivationGraph, fOptionMap, performanceMetrics,
					collectors, labels);
		else if (!isFluid() && !fSolverOptionsHandler.isTransient())
			job = new AnalysisJobSimulationSteadyState(getDialogTitle(),
					fDerivationGraph, fOptionMap, performanceMetrics,
					collectors, labels);
		return job;

	}

	protected void addOptions(Composite composite) {
		// do nothing;
	}

	protected abstract String[] getLabels();

	protected abstract IPointEstimator[] getPerformanceMetrics();

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		enableOKButton();
		setTitle(getDialogTitle());
		return control;
	}

	protected void configureShell(Shell newShell) {
		/* Set Title */
		super.configureShell(newShell);
		newShell.setText(getShellTitle());
	}

	protected StructuredViewer createViewer(Composite composite) {

		GridData checkListData = new GridData(GridData.FILL_BOTH);
		checkListData.horizontalSpan = 2;
		viewer = CheckboxTableViewer.newCheckList(composite, SWT.NONE);
		viewer.getTable().setLayoutData(checkListData);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(getProvider());
		viewer.setInput(getViewerInput());
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				enableOKButton();
			}

		});

		return viewer;
	}

	@Override
	protected final ViewerFilter getViewerFilter() {
		return filter;
	}

	protected boolean isOKButtonEnabled() {
		return super.isOKButtonEnabled()
				&& viewer.getCheckedElements().length != 0;
	}

}
