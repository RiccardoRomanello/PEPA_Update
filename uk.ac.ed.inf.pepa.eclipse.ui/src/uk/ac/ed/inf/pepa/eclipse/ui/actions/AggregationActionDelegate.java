/**
 * 
 */
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import uk.ac.ed.inf.pepa.eclipse.ui.wizards.AggregationWizard;

/**
 * @author Giacomo Alzetta
 *
 */
public class AggregationActionDelegate
	extends BasicProcessAlgebraModelActionDelegate {
	
	public static final String ID = "uk.ac.ed.inf.pepa.eclipse.ui.aggregation";

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.actions.BasicProcessAlgebraModelActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		ActionCommands.aggregation(model);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.actions.BasicProcessAlgebraModelActionDelegate#checkStatus()
	 */
	@Override
	protected void checkStatus() {
		if (this.action != null) {
			action.setEnabled(true);
		}
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

}
