package uk.ac.ed.inf.pepa.cpt.config.parameters;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.Parser;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class OptionMapParameters extends Parameters {

	private OptionMap map;
	
	public OptionMapParameters() {
		
		this.map = new OptionMap();
		
		OptionMap.getDefaultValue(OptionMap.ODE_START_TIME);
		OptionMap.getDefaultValue(OptionMap.ODE_STOP_TIME);
		map.put(OptionMap.ODE_STOP_TIME,500.0);
		OptionMap.getDefaultValue(OptionMap.ODE_STEP);
		OptionMap.getDefaultValue(OptionMap.ODE_ATOL);
		OptionMap.getDefaultValue(OptionMap.ODE_RTOL);
		OptionMap.getDefaultValue(OptionMap.ODE_STEADY_STATE_NORM);
		map.put(OptionMap.ODE_INTERPOLATION,OptionMap.ODE_INTERPOLATION_OFF);
		
		this.keyValueMap = new HashMap<String,Double>();
		this.keyTypeMap = new HashMap<String,String>();
		
		this.keyValueMap.put(OptionMap.ODE_START_TIME, 0.0);
		this.keyValueMap.put(OptionMap.ODE_STOP_TIME, 500.0);
		this.keyValueMap.put(OptionMap.ODE_STEP, 100.0);
		this.keyValueMap.put(OptionMap.ODE_ATOL, 1E-8);
		this.keyValueMap.put(OptionMap.ODE_RTOL, 1E-4);
		this.keyValueMap.put(OptionMap.ODE_STEADY_STATE_NORM, 1e-6);
		
		
		this.keyTypeMap.put(OptionMap.ODE_START_TIME, Config.INTEGER);
		this.keyTypeMap.put(OptionMap.ODE_STOP_TIME, Config.NATURAL);
		this.keyTypeMap.put(OptionMap.ODE_STEP, Config.NATURAL);
		this.keyTypeMap.put(OptionMap.ODE_ATOL, Config.DOUBLE);
		this.keyTypeMap.put(OptionMap.ODE_RTOL, Config.DOUBLE);
		this.keyTypeMap.put(OptionMap.ODE_STEADY_STATE_NORM, Config.DOUBLE);
		
		
	}
	
	@Override
	public boolean setValue(String key, String value) {
		if(this.keyValueMap.containsKey(key)){
			Double d = Parser.parse(this.keyTypeMap.get(key),value);
			if(d >= 0){
				this.keyValueMap.put(key, d);
				this.map.put(key, d);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean valid() {
		return true;
	}

	public void setOptionMap(OptionMap optionMap) {
		this.map = optionMap;
		
	}

	public OptionMap getOptionMap() {
		return this.map;
	}
	
	public void interpolationOff(){
		map.put(OptionMap.ODE_INTERPOLATION,OptionMap.ODE_INTERPOLATION_OFF);
	}
	
	public void interpolationOn(){
		map.put(OptionMap.ODE_INTERPOLATION,OptionMap.ODE_INTERPOLATION_ON);
	}

}
