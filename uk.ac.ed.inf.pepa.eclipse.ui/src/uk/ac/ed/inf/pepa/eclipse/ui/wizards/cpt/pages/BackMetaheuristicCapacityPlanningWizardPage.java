package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.KeyDoubleValueWidgetForMinimumAndMaximum;

public class BackMetaheuristicCapacityPlanningWizardPage extends CapacityPlanningWizardPage {

	public BackMetaheuristicCapacityPlanningWizardPage(String pageName) {
		super();
		this.setDescription(pageName);
	}

	@Override
	protected void constructPage(IValidationCallback cb, Composite container) {
		
		Control control = CPTAPI.getPSORangeParameterControls();
		String[] keys = {Config.LABEXP,Config.LABGEN,Config.LABORG,Config.LABLOC,Config.LABGLO};
		
		//left pad
		pad(container);
		
		Composite child = center(container);
		
		String[] titles = {"Setting","Minimum", "Maximum"};
		
		header(titles,child,2);
		
		for(int i = 0; i < keys.length; i++){
			String value1 = CPTAPI.getPSORangeParameterControls().getValue(keys[i], Config.LABMIN);
			String value2 = CPTAPI.getPSORangeParameterControls().getValue(keys[i], Config.LABMAX);
			widgets.add(new KeyDoubleValueWidgetForMinimumAndMaximum(cb, child, keys[i],
					value1,
					value2,
					control));
		}
		
		footer(child);
		
		//Left pad
		pad(container);

	}
	

	@Override
	protected void setHelp() {
		String root = "uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt";
		String context = root + ".metaheuristic";
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),context);;
		
	}
	
	@Override
	public void setOwnTitle() {
		String title = "none";
			
		setTitle(title);
		
	}

}
