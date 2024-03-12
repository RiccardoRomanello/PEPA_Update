package uk.ac.ed.inf.pepa.cpt.config.control;

import uk.ac.ed.inf.pepa.cpt.config.parameters.Parameters;

public class ParameterControl implements Control {
	
	Parameters myParameters;
	String type;
	
	public ParameterControl(Parameters parameters, String type){
		
		this.type = type;
		this.myParameters = parameters;
		
	}
	
	public boolean setValue(String key, String value){
		return this.myParameters.setValue(key, value);
	}
	
	public String getValue(String key){
		return this.myParameters.getValue(key);
	}
	
	public String getType(String key){
		return this.myParameters.getType(key);
	}
	
	public String[] getKeys(){
		return this.myParameters.getKeys();
	}
	
	public boolean validate(){
		return this.myParameters.valid();
	}

	@Override
	public boolean setValue(String component, String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getKeys(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String component, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toPrint() {
		return this.type + ";" + myParameters.toPrint();
	}

}
