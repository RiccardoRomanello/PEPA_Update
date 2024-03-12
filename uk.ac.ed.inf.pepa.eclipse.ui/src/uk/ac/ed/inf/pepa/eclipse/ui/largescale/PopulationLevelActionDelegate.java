package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;

public class PopulationLevelActionDelegate extends
		PerformanceMetricActionDelegate {

	@Override
	PerformanceMetricDialog getDialog(boolean isFluid,
			IParametricDerivationGraph graph, IPepaModel model) {
		return new PopulationLevelDialog(isFluid, this.activeShell, graph, model);
	}

	@Override
	boolean supportsTransient() {
		return true;
	}

}
