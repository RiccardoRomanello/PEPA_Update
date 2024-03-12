package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.jface.action.Action;


public class DisplayAction extends Action {
	
	private boolean showInProgress = false;
	
	public DisplayAction(String message, boolean showInProgress) {
		super(message);
		this.showInProgress = showInProgress;
	}
	
	public boolean showInProgress() {
		return showInProgress;
	}
}
