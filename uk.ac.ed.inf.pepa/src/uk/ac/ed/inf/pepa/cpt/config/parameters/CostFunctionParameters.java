package uk.ac.ed.inf.pepa.cpt.config.parameters;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class CostFunctionParameters extends Parameters {

	public CostFunctionParameters() {
		this.keyValueMap = new HashMap<String,Double>();
		this.keyTypeMap = new HashMap<String,String>();
		
		//default fitness weights
		this.keyValueMap.put(Config.FITRES, 1.0);
		this.keyValueMap.put(Config.FITPER, 1.0);
		//this.keyValueMap.put(Config.FITPEN, 0.0);
		
		//type map
		this.keyTypeMap.put(Config.FITRES, Config.NATURAL);
		this.keyTypeMap.put(Config.FITPER, Config.NATURAL);
		//this.keyTypeMap.put(Config.FITPEN, Config.INTEGER);
		
	}

	@Override
	public boolean valid() {
		return (this.keyValueMap.get(Config.FITRES) > 0.0) && 
		(this.keyValueMap.get(Config.FITPER) > 0.0);
	}

}
