package uk.ac.ed.inf.pepa.cpt.config.control;

import uk.ac.ed.inf.pepa.cpt.config.ConfigCallBack;
import uk.ac.ed.inf.pepa.cpt.config.lists.SingleChoiceList;

public class EListControl extends ListControl {

	private ConfigCallBack cb;
	
	public EListControl(SingleChoiceList list, ConfigCallBack cb) {
		super(list);
		this.cb = cb;
	}
	
	public void setValue(String s){
		this.myList.setValue(s);
		this.cb.initialiseAndUpdateActionsAndProcesses();
	}

}
