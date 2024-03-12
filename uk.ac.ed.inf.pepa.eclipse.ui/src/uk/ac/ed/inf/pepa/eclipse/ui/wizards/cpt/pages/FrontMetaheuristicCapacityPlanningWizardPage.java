package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.KeySingleValueWidget;

public class FrontMetaheuristicCapacityPlanningWizardPage extends CapacityPlanningWizardPage {
	
	

	public FrontMetaheuristicCapacityPlanningWizardPage(String pageName) {
		super();
		this.setDescription(pageName);
	}

	@Override
	protected void constructPage(IValidationCallback cb, Composite container) {
		
		Control control = CPTAPI.getMHParameterControls();
		String[] keys;
		String[] hc = {Config.LABEXP,Config.LABGEN};
		String[] pso = {Config.LABEXP,Config.LABGEN,Config.LABPOP,Config.LABORG,Config.LABLOC,Config.LABGLO};
		
		if(CPTAPI.getSearchControls().getValue().equals(Config.SEARCHSINGLE))
			keys = pso;
		else
			keys = hc;
		
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
		
		String root, context;
		root = "uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt";
		context = "";
		
		if(CPTAPI.getSearchControls().getValue().equals(Config.SEARCHSINGLE))
			context = root + ".single";
		else{
			context = root + ".driven";
		}
			
			PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),context);;
		
	}

	@Override
	public void setOwnTitle() {
		String title = "";
	
		if(CPTAPI.getSearchControls().getValue().equals(Config.SEARCHSINGLE))
			title = "Capacity planning: Single Particle Swarm Optimisation (PSO)";
		else
			title = "Capacity planning: Driven Particle Swarm Optimisation (dPSO)";
			
		setTitle(title);
		
	}
	


}
