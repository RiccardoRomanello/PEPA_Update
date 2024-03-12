package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.ui.actions.BasicProcessAlgebraModelActionDelegate;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

public abstract class PerformanceMetricActionDelegate extends
		BasicProcessAlgebraModelActionDelegate {

	@Override
	protected final void checkStatus() {
		this.action.setEnabled(this.model.isDerivable());
	}

	@Override
	public final void run(IAction action) {
		MessageDialog messageDialog = new MessageDialog(activeShell,
                "Select kind of analysis",
                null,
                "What analysis would you like to do?",
                MessageDialog.INFORMATION,
                new String[] { "Simulation", "ODE" },
                0);
		int messageKind = messageDialog.open();
		IPepaModel model = (IPepaModel) this.model;
		final ModelNode node = model.getAST();
		try {
			RunnableGraphProvider gp = new RunnableGraphProvider(node);
			new ProgressMonitorDialog(activeShell).run(true, true, gp);
			PerformanceMetricDialog d = getDialog(messageKind == 1, gp.getGraph(), model);
			d.open();
		} catch (InvocationTargetException e) {
			MessageDialog.openError(this.activeShell, "ODE Generation Error", e
					.getTargetException().getMessage());
		} catch (InterruptedException e) {
			MessageDialog.openInformation(this.activeShell,
					"Cancel Acknowledgement",
					"The ODE generation process has been cancelled");
		}

	}

	abstract PerformanceMetricDialog getDialog(
			boolean isFluid, IParametricDerivationGraph graph, IPepaModel model);

	abstract boolean supportsTransient();

}
