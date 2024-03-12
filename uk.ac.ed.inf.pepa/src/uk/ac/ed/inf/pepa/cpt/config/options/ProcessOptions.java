package uk.ac.ed.inf.pepa.cpt.config.options;


public class ProcessOptions {

	private String myLabel;
	private boolean selected;
	private short myProcessId;
	private int myCoordinate;
	
	public ProcessOptions(String label, short processId, int coordinate){
		
		this.myLabel = label;
		this.selected = false;
		this.myProcessId = processId;
		this.myCoordinate = coordinate;
	}
	
	public String getLabel(){
		return myLabel;
	}
	
	public short getProcessId(){
		return this.myProcessId;
	}
	
	public int getCoordinate(){
		return this.myCoordinate;
	}
	
	public void setSelected(boolean select){
		this.selected = select;
	}
	
	public boolean isSelected(){
		return this.selected;
	}
	
	public String toPrint(){
		return "";
	}
}
