package uk.ac.ed.inf.pepa.cpt.config.lists;

import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.config.parameters.Parameters;

/**
 * Range list - for use with rate and component ranges
 * @author twig
 *
 */
public abstract class RangeList {
	
	protected HashMap<String,Parameters> myHashMap;
	
	public RangeList(){
		this.myHashMap = new HashMap<String,Parameters>();
	}
	
	public boolean setValue(String target, String key, String value){
		if(this.myHashMap.containsKey(target)){
			return this.myHashMap.get(target).setValue(key, value);
		}
		return false;
	}
	
	public String getValue(String target, String key){
		return this.myHashMap.get(target).getValue(key);
	}
	
	public String getType(String target, String key){
		return this.myHashMap.get(target).getType(key);
	}
	
	public String[] getYKeys(){
		return this.myHashMap.keySet().toArray(new String[this.myHashMap.size()]);
	}
	
	public String[] getXKeys(String target){
		return this.myHashMap.get(target).getKeys();
	}
	
	public String toPrint(){
		String output = "";
		for(Entry<String, Parameters> entry : myHashMap.entrySet()){
			output = output + entry.getKey() + ";" + entry.getValue().toPrint() + ";";
		}
		return output.substring(0, output.length() - 1);
	}
	
	public abstract boolean valid();
	
}
