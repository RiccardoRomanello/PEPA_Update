package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.ODEKeySingleValueWidget;

public class ODEOptionCapacityPlanningWizardPage extends
		CapacityPlanningWizardPage {

	public ODEOptionCapacityPlanningWizardPage(String pageName) {
		super();
		this.setDescription(pageName);
	}

	@Override
	protected void constructPage(IValidationCallback cb, Composite container) {
		
		Control control = CPTAPI.getOptionMapControls();
		
		//left pad
		pad(container);
		
		Composite child = center(container);
		
		String[] titles = {"Setting","Value"};
		
		header(titles,child,4);
		
		//widgets.add(new ODEKeySingleValueWidget(cb, container, "Start time", OptionMap.ODE_START_TIME, control.getValue(OptionMap.ODE_START_TIME),control));
		widgets.add(new ODEKeySingleValueWidget(cb, child, "Stop time", OptionMap.ODE_STOP_TIME, control.getValue(OptionMap.ODE_STOP_TIME),control));
		widgets.add(new ODEKeySingleValueWidget(cb, child, "Step", OptionMap.ODE_STEP, control.getValue(OptionMap.ODE_STEP),control));
		widgets.add(new ODEKeySingleValueWidget(cb, child, "Absolute tolerance", OptionMap.ODE_ATOL, control.getValue(OptionMap.ODE_ATOL),control));
		widgets.add(new ODEKeySingleValueWidget(cb, child, "Relative tolderance", OptionMap.ODE_RTOL, control.getValue(OptionMap.ODE_RTOL),control));
		widgets.add(new ODEKeySingleValueWidget(cb, child, "Steady-state convergence norm", OptionMap.ODE_STEADY_STATE_NORM, control.getValue(OptionMap.ODE_STEADY_STATE_NORM),control));
		
		
		footer(child);
		
		//Left pad
		pad(container);

	}
	
	@Override
	protected void setHelp() {
		String root = "uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt";
		String context = root + ".ode";
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),context);;
		
	}
	
	@Override
	public void setOwnTitle() {
		String title = "Capacity Planning: ODE";
			
		setTitle(title);
		
	}


}
