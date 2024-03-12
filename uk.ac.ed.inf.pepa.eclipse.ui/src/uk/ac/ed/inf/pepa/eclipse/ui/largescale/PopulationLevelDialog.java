package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class PopulationLevelDialog extends ChecklistPerformanceMetricDialog {

	private class PopulationLevelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0)
				if (element instanceof Integer)
					return fDerivationGraph
							.getSymbolGenerator()
							.getProcessLabel(
									fDerivationGraph.getProcessMappings()[(Integer) element]);
				else
					throw new IllegalArgumentException();
			return null;
		}
	}
	
	public PopulationLevelDialog(boolean isFluid, Shell parentShell,
			IParametricDerivationGraph derivationGraph, IPepaModel model) {
		super(true, isFluid, parentShell, derivationGraph, model);
	}

	@Override
	protected
	String getViewerHeader() {
		return "Select population levels";
	}

	@Override
	protected String getDialogTitle() {
		return "Population Level Analysis";
	}

	@Override
	ITableLabelProvider getProvider() {
		return new PopulationLevelProvider();
	}

	@Override
	String getShellTitle() {
		return getDialogTitle();
	}

	@Override
	Object getViewerInput() {
		Integer[] values = new Integer[fDerivationGraph.getProcessMappings().length];
		for (int i = 0; i < values.length; i++)
			values[i] = i;
		return values;
	}

	class PopulationEstimator implements IPointEstimator {

		private int index;

		public PopulationEstimator(int index) {
			this.index = index;

		}

		public double computeEstimate(double timePoint, double[] solution)
				throws DifferentialAnalysisException {
			return solution[index];
		}
	}

	@Override
	protected String[] getLabels() {
		Object[] checkedElements = viewer.getCheckedElements();
		String[] labels = new String[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			labels[i] = fDerivationGraph
					.getSymbolGenerator()
					.getProcessLabel(
							fDerivationGraph.getProcessMappings()[(Integer) checkedElements[i]]);
		}
		return labels;
	}

	@Override
	protected IPointEstimator[] getPerformanceMetrics() {
		Object[] checkedElements = viewer.getCheckedElements();
		IPointEstimator[] estimators = new IPointEstimator[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			estimators[i] = new PopulationEstimator(
					(Integer) checkedElements[i]);
			System.err.println("Population: " + (Integer) checkedElements[i]);
		}
		return estimators;
	}

	
}
