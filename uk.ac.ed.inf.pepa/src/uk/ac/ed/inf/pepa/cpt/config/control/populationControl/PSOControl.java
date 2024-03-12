package uk.ac.ed.inf.pepa.cpt.config.control.populationControl;

import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.PopulationControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.PSOList;

public class PSOControl extends PopulationControl {
	
	private PSOList myPSOList;
	private String type;

	public PSOControl(PSOList componentList, String type) {
		this.myPSOList = componentList;
		this.type = type;
	}
	
	public String[] getKeys(){
		return this.myPSOList.getYKeys();
	}
	
	public String[] getXKeys(String component){
		return this.myPSOList.getXKeys(component);
	}
	
	public boolean setValue(String component, String key, String value){
		return this.myPSOList.setValue(component, key, value);
	}
	
	public String getValue(String component, String key){
		return this.myPSOList.getValue(component, key);
	}
	
	public String getType(String component, String key){
		return this.myPSOList.getType(component, key);
	}
	
	public boolean validate(){
		return this.myPSOList.valid();
	}

	@Override
	public String getType(String key) {
		return this.myPSOList.getType(key, Config.LABMAX);
	}

	@Override
	public String getValue(String key) {
		return this.myPSOList.getValue(key, Config.LABMAX);
	}

	@Override
	public boolean setValue(String key, String value) {
		boolean min, max;
		min = this.myPSOList.setValue(key, Config.LABMIN, value);
		max = this.myPSOList.setValue(key, Config.LABMAX, value);
		return min || max;
	}

	@Override
	public String[] getKeys(String s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toPrint() {
		return this.type + ";" + this.myPSOList.toPrint();
	}

}
