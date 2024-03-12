package uk.ac.ed.inf.pepa.cpt.config.lists;

import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.parameters.ComponentParameters;
import uk.ac.ed.inf.pepa.cpt.config.parameters.Parameters;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;

public class ComponentList extends RangeList {

	public ComponentList(IParametricDerivationGraph graph) {
		
		super();
		
		if(graph != null){
			String[] componentLabels = Utils.getSystemEquation(graph);
			Integer[] componentPopulation = Utils.getInitialPopulation(graph);
			for(int i = 0; i < componentLabels.length; i++){
				this.myHashMap.put(componentLabels[i], new ComponentParameters(componentPopulation[i].doubleValue()));
			}
			//Config has not been initialised
		} else {
			this.myHashMap.put("init", new ComponentParameters(0.0));
		}
		
	}

	@Override
	public boolean valid() {
		
		boolean valid = true;
		
		for(Entry<String, Parameters> entry : this.myHashMap.entrySet()){
			valid = valid && entry.getValue().valid();
		}
		
		return valid;
	}

}
