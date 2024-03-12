package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

public class Response {
	
	public boolean valid;
	public String complaint;
	
	public Response(boolean valid){
		
		this.valid = valid;
		this.complaint = "";
	}
	
	public void setComplaint(String complaint){
		this.complaint = complaint;
	}

}
