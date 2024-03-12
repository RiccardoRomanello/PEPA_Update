package uk.ac.ed.inf.pepa.cpt.config.control.populationControl;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.control.PopulationControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.TargetList;

public class TargetControl extends PopulationControl {
	
	private TargetList myTargetList;
	private String type;

	public TargetControl(TargetList targetList, String type) {
		this.myTargetList = targetList;
		this.type = type;
	}
	
	public String[] getKeys(){
		return this.myTargetList.getYKeys();
	}
	
	public String[] getXKeys(String component){
		return this.myTargetList.getXKeys(component);
	}
	
	public boolean setValue(String component, String key, String value){
		return this.myTargetList.setValue(component, key, value);
	}
	
	public boolean setValue(String component){
		return this.myTargetList.setValue(component);
	}
	
	public void clearMap(){
		this.myTargetList.clear();
	}
	
	public String getValue(String component, String key){
		return this.myTargetList.getValue(component, key);
	}
	
	public boolean validate(){
		return this.myTargetList.valid();
	}

	@Override
	public String getType(String key, String attribute) {
		return this.myTargetList.getType(key, attribute);
	}

	public void update() {
		String[] keys = CPTAPI.getPerformanceControls().getLabels();
		
		for(int i = 0; i < keys.length; i++){
			this.setValue(keys[i]);
		}
		
	}

	@Override
	public String getType(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String key) {
		
		String output = "";
		
		for(String s : this.myTargetList.getYKeys()){
			output = output + s + " = " + this.getValue(s, key) + ", ";
		}
		return output.substring(0,output.length() - 2);
	}

	@Override
	public boolean setValue(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getKeys(String s) {
		return this.myTargetList.getXKeys(s);
	}

	@Override
	public String toPrint() {
		return this.type + ";" + this.myTargetList.toPrint();
	}

}
