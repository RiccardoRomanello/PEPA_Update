package uk.ac.ed.inf.pepa.cpt.config.parameters;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class HillClimbingParameters extends Parameters {

	public HillClimbingParameters() {
		this.keyValueMap = new HashMap<String,Double>();
		this.keyTypeMap = new HashMap<String,String>();
		
		//default mutation rate
		this.keyValueMap.put(Config.LABMUT, 1.0);
		this.keyValueMap.put(Config.LABEXP, 2.0);
		this.keyValueMap.put(Config.LABGEN, 10.0);
		
		//type map
		this.keyTypeMap.put(Config.LABMUT, Config.PERCENT);
		this.keyTypeMap.put(Config.LABEXP, Config.NATURAL);
		this.keyTypeMap.put(Config.LABGEN, Config.NATURAL);
	}

	@Override
	public boolean valid() {
		return (this.keyValueMap.get(Config.LABMUT) >= 0.0) && 
			(this.keyValueMap.get(Config.LABMUT) <= 1.0);
	}

}
