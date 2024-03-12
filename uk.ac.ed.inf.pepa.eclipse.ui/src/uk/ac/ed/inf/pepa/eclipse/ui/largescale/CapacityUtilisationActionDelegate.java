package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;

public class CapacityUtilisationActionDelegate extends
		PerformanceMetricActionDelegate {

	@Override
	PerformanceMetricDialog getDialog(boolean isFluid,
			IParametricDerivationGraph graph, IPepaModel model) {
		return new CapacityUtilisationDialog(isFluid,
				this.activeShell, graph, model);
	}

	@Override
	boolean supportsTransient() {
		return true;
	}

}
