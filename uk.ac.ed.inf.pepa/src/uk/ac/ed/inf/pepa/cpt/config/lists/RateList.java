package uk.ac.ed.inf.pepa.cpt.config.lists;

import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.config.parameters.Parameters;
import uk.ac.ed.inf.pepa.cpt.config.parameters.RateParameters;
import uk.ac.ed.inf.pepa.cpt.nodeHandling.RatesVisitorHandler;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class RateList extends RangeList {
	
	public RateList (ModelNode model){
		super();
		RatesVisitorHandler rvh = new RatesVisitorHandler(model);
		String[] keys = rvh.get().keySet().toArray(new String[rvh.get().keySet().size()]);
		for(int i = 0; i < keys.length;i++){
			this.myHashMap.put(keys[i],new RateParameters(rvh.get().get(keys[i])));
		}
	}
	
	
	@Override
	public boolean valid() {
		
		boolean valid = true;
		
		for(Entry<String, Parameters> entry : this.myHashMap.entrySet()){
			valid = valid && entry.getValue().valid();
		}
		
		return valid;
	}

}
