package uk.ac.ed.inf.pepa.cpt.config.control.populationControl;

import uk.ac.ed.inf.pepa.cpt.config.control.PopulationControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.ComponentList;

public class ComponentControl extends PopulationControl {
	
	private ComponentList myComponentList;

	public ComponentControl(ComponentList componentList) {
		this.myComponentList = componentList;
	}
	
	public String[] getKeys(){
		return this.myComponentList.getYKeys();
	}
	
	public String[] getXKeys(String component){
		return this.myComponentList.getXKeys(component);
	}
	
	public boolean setValue(String component, String key, String value){
		return this.myComponentList.setValue(component, key, value);
	}
	
	public String getValue(String component, String key){
		return this.myComponentList.getValue(component, key);
	}
	
	public boolean validate(){
		return this.myComponentList.valid();
	}

	@Override
	public String getType(String key, String attribute) {
		return this.myComponentList.getType(key, attribute);
	}

	@Override
	public String getType(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getKeys(String s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toPrint() {
		return "Component ranges;" + this.myComponentList.toPrint();
	}


}
