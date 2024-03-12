package uk.ac.ed.inf.pepa.cpt.config.parameters;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class MetaheuristicSearchParameters extends Parameters {

	public MetaheuristicSearchParameters() {
		
		this.keyValueMap = new HashMap<String,Double>();
		this.keyTypeMap = new HashMap<String,String>();
		
		//default component parameters
		this.keyValueMap.put(Config.LABMIN, 1.0);
		this.keyValueMap.put(Config.LABMAX, 10.0);
		this.keyValueMap.put(Config.LABRAN, 11.0);
		
		//type map
		this.keyTypeMap.put(Config.LABMIN, Config.INTEGER);
		this.keyTypeMap.put(Config.LABMAX, Config.INTEGER);
		this.keyTypeMap.put(Config.LABRAN, Config.INTEGER);
		
	}
	
	public MetaheuristicSearchParameters(Double min, Double max, Double ran) {
		
		this.keyValueMap = new HashMap<String,Double>();
		this.keyTypeMap = new HashMap<String,String>();
		
		//default component parameters
		this.keyValueMap.put(Config.LABMIN, min);
		this.keyValueMap.put(Config.LABMAX, max);
		this.keyValueMap.put(Config.LABRAN, ran);
		
		//type map
		this.keyTypeMap.put(Config.LABMIN, Config.INTEGER);
		this.keyTypeMap.put(Config.LABMAX, Config.INTEGER);
		this.keyTypeMap.put(Config.LABRAN, Config.INTEGER);
		
	}

	@Override
	public boolean valid() {
		Double min, max, ran;
		boolean isValid = true;
		min = this.keyValueMap.get(Config.LABMIN);
		max = this.keyValueMap.get(Config.LABMAX);
		ran = (max - min);
		
		//remove extra range
		if(min == 0){
			ran--;
		}
		this.keyValueMap.put(Config.LABRAN, ran);
		
		//no negatives..
		isValid = (min >= 0) && (max >= 0) && (ran >= 0);
		
		return isValid;
	}
}
