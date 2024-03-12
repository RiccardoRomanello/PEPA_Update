package uk.ac.ed.inf.pepa.cpt.config.lists;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class EvaluatorChoiceList extends SingleChoiceList {

	public EvaluatorChoiceList() {
		this.label = Config.EVALUATOR;
		this.value = Config.EVALTHRO;
		this.choices = new String[] {Config.EVALTHRO,Config.EVALARPT,Config.EVALUTIL,Config.EVALPOPU};
	}

}
