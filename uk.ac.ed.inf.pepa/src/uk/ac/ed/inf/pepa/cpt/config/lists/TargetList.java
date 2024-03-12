package uk.ac.ed.inf.pepa.cpt.config.lists;

import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.config.parameters.Parameters;
import uk.ac.ed.inf.pepa.cpt.config.parameters.TargetParameters;

public class TargetList extends RangeList {

	
	public TargetList(){
		super();
		
		this.myHashMap.put("temp", new TargetParameters());
	}
	
	@Override
	public boolean valid() {
		
		boolean valid = true;
		
		for(Entry<String, Parameters> entry : this.myHashMap.entrySet()){
			valid = valid && entry.getValue().valid();
		}
		
		return valid;
	}
	
	public boolean setValue(String target){
		if(!this.myHashMap.containsKey(target)){
			this.myHashMap.put(target, new TargetParameters());
		}
		return true;
	}

	public void clear() {
		this.myHashMap = new HashMap<String, Parameters>();
		
	}
}
