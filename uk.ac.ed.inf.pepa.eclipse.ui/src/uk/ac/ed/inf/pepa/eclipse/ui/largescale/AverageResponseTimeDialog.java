package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.AverageResponseTimeCalculation;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.ISequentialComponent;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.simulation.AverageResponseTimeCollector;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;

public class AverageResponseTimeDialog extends PerformanceMetricDialog {

	private class ResponseTimeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ISequentialComponent) {
				Set<Entry<Short, Coordinate>> mapping = ((ISequentialComponent) parentElement)
						.getComponentMapping();
				Integer[] children = new Integer[mapping.size()];
				int i = 0;
				for (Entry<Short, Coordinate> entry : mapping) {
					children[i++] = entry.getValue().getCoordinate();
				}
				return children;
			} else
				return null;
		}

		public Object getParent(Object element) {
			if (element instanceof ISequentialComponent[])
				return null;
			if (element instanceof ISequentialComponent) {
				return fDerivationGraph.getSequentialComponents();
			}
			if (element instanceof Integer) {
				for (ISequentialComponent c : fDerivationGraph
						.getSequentialComponents())
					for (Entry<Short, Coordinate> entry : c
							.getComponentMapping())
						if (entry.getValue().getCoordinate() == ((int) (Integer) element))
							return c;
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof Integer)
				return false;
			else
				return true;
		}

		public Object[] getElements(Object inputElement) {
			return fDerivationGraph.getSequentialComponents();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private CheckboxTreeViewer viewer;

	private ViewerFilter filter;

	public AverageResponseTimeDialog(boolean isFluid, Shell parentShell,
			IParametricDerivationGraph derivationGraph, IPepaModel model) {
		// it is always steady state!
		super(false, isFluid, parentShell, derivationGraph, model);
		filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer filteredViewer, Object parentElement,
					Object element) {
				if (element instanceof ISequentialComponent
						|| viewer.getChecked(element))
					return true;
				String label = null;
				if (element instanceof Integer)
					label = fDerivationGraph
							.getSymbolGenerator()
							.getProcessLabel(
									fDerivationGraph.getProcessMappings()[(Integer) element]);
				return label.contains(filterText.getText());
			}
		};
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		enableOKButton();
		setTitle("Average response time");
		return control;
	}

	protected void configureShell(Shell newShell) {
		/* Set Title */
		super.configureShell(newShell);
		newShell.setText("Average response time");
	}

	protected StructuredViewer createViewer(Composite composite) {
		Label actions = new Label(composite, SWT.NONE);
		actions.setText("Select local state in the passage");
		GridData actionsData = new GridData(GridData.FILL_HORIZONTAL);
		actionsData.horizontalSpan = 2;
		actions.setLayoutData(actionsData);

		GridData checkListData = new GridData(GridData.FILL_BOTH);
		checkListData.horizontalSpan = 2;
		viewer = new CheckboxTreeViewer(composite, SWT.NONE);
		viewer.getTree().setLayoutData(checkListData);
		viewer.setContentProvider(new ResponseTimeContentProvider());
		viewer.setLabelProvider(new LabelProvider() {

			public String getText(Object element) {
				if (element instanceof ISequentialComponent)
					return ((ISequentialComponent) element).getName();
				if (element instanceof Integer)
					return fDerivationGraph
							.getSymbolGenerator()
							.getProcessLabel(
									fDerivationGraph.getProcessMappings()[(Integer) element]);
				return super.getText(element);
			}
		});
		viewer.setInput(fDerivationGraph.getSequentialComponents());
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof ISequentialComponent
						&& event.getChecked() == true)
					viewer.setChecked(event.getElement(), false);
				enableOKButton();
			}

		});
		return viewer;
	}

	protected boolean isOKButtonEnabled() {
		ResponseTimeContentProvider provider = (ResponseTimeContentProvider) viewer
				.getContentProvider();
		ISequentialComponent checkedComponent = null;
		for (ISequentialComponent c : fDerivationGraph
				.getSequentialComponents())
			for (Object child : provider.getChildren(c))
				if (viewer.getChecked(child))
					if (checkedComponent == null)
						checkedComponent = c;
					else if (checkedComponent != c)
						return false;
		boolean isOkEnabled = super.isOKButtonEnabled()
				&& checkedComponent != null;
		return isOkEnabled;
	}

	@Override
	protected String getDialogTitle() {
		return "Average response time";
	}

	protected IPointEstimator[] getPerformanceMetrics() {
		final int[] insystem = new int[viewer.getCheckedElements().length];
		for (int i = 0; i < insystem.length; i++)
			insystem[i] = (Integer) viewer.getCheckedElements()[i];
		ISequentialComponent c = (ISequentialComponent) ((ResponseTimeContentProvider) viewer
				.getContentProvider())
				.getParent(viewer.getCheckedElements()[0]);
		int j = 0;
		for (; j < fDerivationGraph.getSequentialComponents().length; j++)
			if (fDerivationGraph.getSequentialComponents()[j] == c)
				break;
		AverageResponseTimeCalculation art = new AverageResponseTimeCalculation(
				j, insystem, fDerivationGraph);
		return new IPointEstimator[] { art.getUsersInSystemEstimator(),
				art.getIncomingThroughputEstimator() };
	}

	@Override
	protected AnalysisJob getAnalysisJob() {
		if (fSolverOptionsHandler.isTransient())
			throw new IllegalStateException(
					"Cannot request a transient analysis with average response time");
		IStatisticsCollector[] collectors = new IStatisticsCollector[] { new AverageResponseTimeCollector(
				0, 1) };
		String[] labels = new String[] { "Average response time" };
		if (isFluid())
			return new AnalysisJobFluidSteadyState(
					"Average response time",
					fDerivationGraph,
					fOptionMap,
					getPerformanceMetrics(), collectors, labels);
		else
			return new AnalysisJobSimulationSteadyState(
					"Average response time", fDerivationGraph, fOptionMap,
					getPerformanceMetrics(),
					collectors, labels);
	}

	@Override
	protected String getViewerHeader() {
		return "Average response time";
	}

	@Override
	protected ViewerFilter getViewerFilter() {
		return filter;
	}

	@Override
	protected void addOptions(Composite composite) {
	}
}
