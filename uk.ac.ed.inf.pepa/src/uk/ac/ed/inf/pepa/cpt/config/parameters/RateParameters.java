package uk.ac.ed.inf.pepa.cpt.config.parameters;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.Parser;

public class RateParameters extends Parameters {

	
	public RateParameters(Double rate){
		this.keyValueMap = new HashMap<String,Double>();
		this.keyTypeMap = new HashMap<String,String>();
		
		//default component parameters
		this.keyValueMap.put(Config.LABMIN, rate);
		this.keyValueMap.put(Config.LABMAX, rate + 1);
		this.keyValueMap.put(Config.LABRAN, 2.0);
		this.keyValueMap.put(Config.LABSTE, 0.1);
		this.keyValueMap.put(Config.LABWEI, 1.0);
		
		//type map
		this.keyTypeMap.put(Config.LABMIN, Config.DOUBLE);
		this.keyTypeMap.put(Config.LABMAX, Config.DOUBLE);
		this.keyTypeMap.put(Config.LABRAN, Config.DOUBLE);
		this.keyTypeMap.put(Config.LABSTE, Config.DOUBLE);
		this.keyTypeMap.put(Config.LABWEI, Config.DOUBLE);
	}
	
	@Override
	public boolean valid() {
		Double min, max, ran, wei;
		min = this.keyValueMap.get(Config.LABMIN);
		max = this.keyValueMap.get(Config.LABMAX);
		ran = (max - min) + 1;
		//remove extra range
		if(min == 0){
			ran--;
		}
		this.keyValueMap.put(Config.LABRAN, ran);
		wei = this.keyValueMap.get(Config.LABWEI);
		//no negatives..
		return (min >= 0) && (max >= 0) && (ran >= 0) && (wei >= 0) ;
	}
	
	public boolean setValue(String key, String value) {
		if(this.keyValueMap.containsKey(key)){
			Double d = Parser.parse(this.keyTypeMap.get(key),value);
			if(d >= 0){
				this.keyValueMap.put(key, d);
				
				Double min, max, ran;
				
				min = this.keyValueMap.get(Config.LABMIN);
				max = this.keyValueMap.get(Config.LABMAX);
				
				ran = (max - min) + 1;
				//remove extra range
				if(min == 0){
					ran--;
				}
				this.keyValueMap.put(Config.LABRAN, ran);
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
