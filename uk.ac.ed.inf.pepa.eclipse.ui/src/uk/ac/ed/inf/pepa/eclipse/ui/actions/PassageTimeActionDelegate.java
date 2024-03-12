package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;

public class PassageTimeActionDelegate extends
		BasicProcessAlgebraModelActionDelegate implements IEditorActionDelegate {

	@Override
	protected void checkStatus() {
		this.action.setEnabled(model.isDerivable());
	}

	@Override
	public void run(IAction action) {
		ActionCommands.passageTime((IPepaModel)model);
	}

}
