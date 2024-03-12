package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

import org.eclipse.swt.widgets.Composite;

import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;

public abstract class CapacityPlanningWidget {
	
	protected IValidationCallback cb;
	protected Composite container;
	protected Control control;
	
	public CapacityPlanningWidget(IValidationCallback cb, Composite container, Control control) {
		this.cb = cb;
		this.container = container;
		this.control = control;
	
	}
	
	public abstract Response isValid();

}