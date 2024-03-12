package uk.ac.ed.inf.pepa.cpt.config.parameters;

import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.config.control.Parser;

public abstract class Parameters {
	
	protected HashMap<String,Double> keyValueMap;
	protected HashMap<String,String> keyTypeMap;
	
	
	/**
	 * Enter value into keyValueMap - returns true if successful, false otherwise
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setValue(String key, String value) {
		if(this.keyValueMap.containsKey(key)){
			Double d = Parser.parse(this.keyTypeMap.get(key),value);
			if(d >= 0){
				this.keyValueMap.put(key, d);
				return valid();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public abstract boolean valid();
	
	public String getValue(String key){
		String type = this.keyTypeMap.get(key);
		Double value = this.keyValueMap.get(key);
		return Parser.revert(type, value);
	}
	
	public String getType(String key){
		return this.keyTypeMap.get(key);
	}
	
	public String[] getKeys(){
		String[] tempArray = new String[this.keyValueMap.size()];		
		this.keyValueMap.keySet().toArray(tempArray);
		return tempArray;
	}
	
	public String toPrint(){
		String output = "";
		for(Entry<String, Double> entry : this.keyValueMap.entrySet()){
			output = output + entry.getKey() + " ; " + entry.getValue() + ";";
		}
		return output;
	}

}
