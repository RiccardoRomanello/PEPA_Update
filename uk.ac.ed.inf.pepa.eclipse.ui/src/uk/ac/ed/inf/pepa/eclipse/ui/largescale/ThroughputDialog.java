/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.ISequentialComponent;
import uk.ac.ed.inf.pepa.largescale.ThroughputCalculation;

/**
 * Throughput dialog
 * 
 * @author mtribast
 * 
 */
public class ThroughputDialog extends ChecklistPerformanceMetricDialog {

	private class ThroughputTableProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0)
				if (element instanceof Short)
					return fDerivationGraph.getSymbolGenerator()
							.getActionLabel((Short) element);
				else
					throw new IllegalArgumentException();
			return null;
		}
	}

	public ThroughputDialog(boolean isFluid, Shell shell,
			IParametricDerivationGraph derivationGraph, IPepaModel model) {
		super(true, isFluid, shell, derivationGraph, model);
	}

	String getShellTitle() {
		return "Fluid-flow Throughput Calculation";
	}

	protected String getViewerHeader() {
		return "Select actions";
	}

	protected String getDialogTitle() {
		return "Throughput Analysis";
	}

	ITableLabelProvider getProvider() {
		return new ThroughputTableProvider();
	}

	protected Object getViewerInput() {
		return getAlphabet();
	}

	private Short[] getAlphabet() {
		ArrayList<Short> alphabet = new ArrayList<Short>();
		for (ISequentialComponent c : fDerivationGraph
				.getSequentialComponents())
			for (short actionId : c.getActionAlphabet())
				if (!alphabet.contains(actionId))
					alphabet.add(actionId);
		Collections.sort(alphabet, new Comparator<Short>() {

			public int compare(Short arg0, Short arg1) {
				return fDerivationGraph.getSymbolGenerator().getActionLabel(
						arg0).compareTo(
						fDerivationGraph.getSymbolGenerator().getActionLabel(
								arg1));
			}

		});
		return alphabet.toArray(new Short[alphabet.size()]);
	}

	@Override
	protected String[] getLabels() {
		Object[] checkedElements = viewer.getCheckedElements();
		String[] labels = new String[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			labels[i] = fDerivationGraph.getSymbolGenerator().getActionLabel(
					(Short) checkedElements[i]);
		}
		return labels;
	}

	@Override
	protected IPointEstimator[] getPerformanceMetrics() {
		Object[] checkedElements = viewer.getCheckedElements();
		ThroughputCalculation[] calculators = new ThroughputCalculation[checkedElements.length];
		for (int i = 0; i < checkedElements.length; i++) {
			calculators[i] = new ThroughputCalculation(
					(Short) checkedElements[i], fDerivationGraph);
		}
		return calculators;
	}

}
