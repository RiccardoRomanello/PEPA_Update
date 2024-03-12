package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.KeySingleValueWidget;

public class CostFunctionCapacityPlanningWizardPage extends
		CapacityPlanningWizardPage {

	public CostFunctionCapacityPlanningWizardPage(String pageName) {
		super();
		this.setDescription(pageName);
	}

	@Override
	protected void constructPage(IValidationCallback cb, Composite container) {
		
		Control control = CPTAPI.getCostFunctionControls();
		String[] keys = {Config.FITPER,Config.FITRES};
		
		//left pad
		pad(container);
		
		Composite child = center(container);
		
		String[] titles = {"Setting","Value"};
		
		header(titles,child,4);
		
		for(int i = 0; i < keys.length; i++)
			widgets.add(new KeySingleValueWidget(cb, child,keys[i],control.getValue(keys[i]),control));
		
		footer(child);
		
		//Left pad
		pad(container);

	}
	

	@Override
	protected void setHelp() {
		String root = "uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt";
		String context = root + ".costFunction";
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),context);;
		
	}
	

	@Override
	public void setOwnTitle() {
		String title = "Cost function";
			
		setTitle(title);
		
	}

}
