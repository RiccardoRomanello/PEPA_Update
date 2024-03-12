package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.listen;

import java.util.Observable;
import java.util.Observer;

import uk.ac.ed.inf.pepa.eclipse.ui.view.cptview.CapacityPlanningListView;
import uk.ac.ed.inf.pepa.eclipse.ui.view.cptview.CapacityPlanningView;

public class CapacityPlanningObserver implements Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		CapacityPlanningListView.update();
		CapacityPlanningView.update();
		
	}
	

}
