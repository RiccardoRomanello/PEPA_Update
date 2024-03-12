package uk.ac.ed.inf.pepa.cpt.config.options;


public class ActionOptions {
	
	private String myLabel;
	private Short myActionId;
	private boolean selected;
	
	public ActionOptions(String label, Short myActionId){
		this.myLabel = label;
		this.myActionId = myActionId;
		this.selected = false;
	}
	
	public void setSelected(boolean select){
		this.selected = select;
	}
	
	public boolean isSelected(){
		return this.selected;
	}
	
	public String getLabel(){
		return this.myLabel;
	}
	
	public short getId(){
		return this.myActionId;
	}
	
	public String toPrint(){
		return "";
	}

}
