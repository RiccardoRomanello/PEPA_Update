package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.CapacityUtilisationCalculation;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.ISequentialComponent;

public class CapacityUtilisationDialog extends ChecklistPerformanceMetricDialog {

	private class CapacityUtilisationProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0)
				if (element instanceof Integer) {
					int index = (Integer) element;
					ISequentialComponent c = fDerivationGraph
							.getSequentialComponents()[index];
					return c.getName();
				} else
					throw new IllegalArgumentException();
			return null;
		}
	}

	public CapacityUtilisationDialog(boolean isFluid, Shell parentShell,
			IParametricDerivationGraph derivationGraph, IPepaModel model) {
		super(true, isFluid, parentShell, derivationGraph, model);
	}

	@Override
	protected
	String getViewerHeader() {
		return "Select sequential component";
	}

	@Override
	protected String getDialogTitle() {
		return "Capacity Utilisation Analysis";
	}

	@Override
	ITableLabelProvider getProvider() {
		return new CapacityUtilisationProvider();
	}

	@Override
	String getShellTitle() {
		return getDialogTitle();
	}

	@Override
	Object getViewerInput() {
		Integer[] values = new Integer[fDerivationGraph
				.getSequentialComponents().length];
		for (int i = 0; i < values.length; i++)
			values[i] = i;
		return values;
	}

	@Override
	protected String[] getLabels() {
		Object[] checkedElements = viewer.getCheckedElements();
		String[] labels = new String[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			labels[i] = fDerivationGraph.getSequentialComponents()[(Integer) checkedElements[i]]
					.getName();
		}
		return labels;

	}

	@Override
	protected IPointEstimator[] getPerformanceMetrics() {
		Object[] checkedElements = viewer.getCheckedElements();
		CapacityUtilisationCalculation[] calculators = new CapacityUtilisationCalculation[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			calculators[i] = new CapacityUtilisationCalculation(
					(Integer) checkedElements[i], fDerivationGraph);
		}
		return calculators;

	}

}
