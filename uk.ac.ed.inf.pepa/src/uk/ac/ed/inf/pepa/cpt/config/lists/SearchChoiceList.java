package uk.ac.ed.inf.pepa.cpt.config.lists;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class SearchChoiceList extends SingleChoiceList {

	public SearchChoiceList() {
		this.label = Config.SEARCH;
		this.value = Config.SEARCHSINGLE;
		this.choices = new String[] {Config.SEARCHSINGLE,Config.SEARCHDRIVEN, Config.SEARCHBRUTE};
	}

}
