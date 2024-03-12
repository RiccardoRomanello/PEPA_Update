package uk.ac.ed.inf.pepa.cpt.config.lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.options.ActionOptions;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;


/**
 * For use with actions, in terms of defining action target variables
 * @author twig
 *
 */
public class ActionList implements IOptionList {
	
	private HashMap<String,ActionOptions> myActionList;
	private HashMap<Short,String> myActionIDToLabelMap;
	private HashMap<String,Short> myLabelToActionIDMap;
	private HashMap<String,Boolean> myActionToSelectedMap;
	
	public ActionList(IParametricDerivationGraph graph){
		
		this.myActionList = new HashMap<String,ActionOptions>();
		this.myActionIDToLabelMap = new HashMap<Short,String>();
		this.myLabelToActionIDMap = new HashMap<String,Short>();
		this.myActionToSelectedMap = new HashMap<String,Boolean>();
		
		
		HashMap<Short,String> idLabelMap = Utils.getActionIds(graph);
		
		for(Entry<Short, String> entry : idLabelMap.entrySet()){
			this.myActionList.put(entry.getValue(), new ActionOptions(entry.getValue(),entry.getKey()));
			this.myActionIDToLabelMap.put(entry.getKey(), entry.getValue());
			this.myLabelToActionIDMap.put(entry.getValue(),entry.getKey());
			this.myActionToSelectedMap.put(entry.getValue(),false);
		}
		
	}
	
	public String toPrint(){
		String output = "";
		for(String s : this.myActionToSelectedMap.keySet()){
			if(this.myActionToSelectedMap.get(s)){
				output = output + s + ";";
			}
		}
		
		if(output.length() > 0){
			return output.substring(0, output.length() - 1);
		} else {
			return "";
		}
	}
	
	public void clearSelection(){
		
		for(Entry<String, ActionOptions> entry : myActionList.entrySet()){
			entry.getValue().setSelected(false);
		}
	}

	public Short[] getSelectedActionIds() {
		
		ArrayList<Short> collector = new ArrayList<Short>();
		
		for(Entry<String, ActionOptions> entry : this.myActionList.entrySet()){
			if(entry.getValue().isSelected()){
				collector.add(entry.getValue().getId());
			}
		}
		
		return collector.toArray(new Short[collector.size()]);
	}
	
	public boolean setSelectedHandler(short actionId, boolean selected){
		if(this.myActionIDToLabelMap.containsKey(actionId)){
			this.setSelected(actionId, selected);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setSelectedHandler(String name, boolean selected) {
		return setSelectedHandler(this.getActionID(name), selected);
	}

	private void setSelected(short actionId, boolean selected) {
		String key = this.myActionIDToLabelMap.get(actionId);
		this.myActionList.get(key).setSelected(selected);
		this.myActionToSelectedMap.put(key, selected);
	}
	
	public boolean getSelectionState(String key){
		return this.myActionToSelectedMap.get(key);
	}
	
	public ArrayList<HashMap<String, Short>> getAllActionIds() {
		
		ArrayList<HashMap<String,Short>> allActionIds = new ArrayList<HashMap<String,Short>>();
		
		allActionIds.add(new HashMap<String,Short>());
		
		for(Entry<Short, String> entry : this.myActionIDToLabelMap.entrySet()){
			allActionIds.get(0).put(entry.getValue(), entry.getKey());
		}
		
		return allActionIds;
	}

	public String getLabel(Short short1) {
		return this.myActionIDToLabelMap.get(short1);
	}
	
	public Short getActionID(String name) {
		return this.myLabelToActionIDMap.get(name);
	}

}
