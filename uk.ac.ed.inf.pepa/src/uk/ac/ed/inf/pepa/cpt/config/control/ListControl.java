package uk.ac.ed.inf.pepa.cpt.config.control;

import java.util.Arrays;

import uk.ac.ed.inf.pepa.cpt.config.lists.SingleChoiceList;

public class ListControl implements Control {
	
	protected SingleChoiceList myList;
	
	public ListControl(SingleChoiceList list){
		
		this.myList = list;
		
	}
	
	public String getLabel(){
		return this.myList.getLabel();
	}
	
	public String[] getChoices(){
		return this.myList.getChoices();
	}
	
	public void setValue(String s){
		this.myList.setValue(s);
	}
	
	public String getValue(){
		return this.myList.getValue();
	}
	
	public boolean validate(){
		return Arrays.asList(this.myList.getChoices()).contains(this.myList.getValue());
	}

	@Override
	public String[] getKeys() {
		// TODO Auto-generated method stub
		return null;
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
		return this.myList.toPrint();
	}
	
	

}
