package uk.ac.ed.inf.pepa.cpt.config.lists;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class DomainChoiceList extends SingleChoiceList {
	
	public DomainChoiceList() {
		this.label = Config.DOMAIN;
		this.value = Config.DOMCOM;
		this.choices = new String[] {Config.DOMCOM,Config.DOMRAR};
	}

}
