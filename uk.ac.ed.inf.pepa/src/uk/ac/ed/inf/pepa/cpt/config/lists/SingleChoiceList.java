package uk.ac.ed.inf.pepa.cpt.config.lists;


/**
 * Provides a superclass for any single choice CPT parameter.
 * @author twig
 *
 */
public class SingleChoiceList {

	/*
	 * label for list
	 */
	protected String label;

	/*
	 * user set value
	 */
	protected String value;

	/*
	 * list of options for the user
	 */
	protected String[] choices;


	public String getLabel(){
		return this.label;
	}

	public String getValue(){
		return this.value;
	}

	public boolean setValue(String s){
		this.value = s;
		return true;
	}

	public String[] getChoices(){
		return this.choices;
	}
	
	public String toPrint(){
		
		String output = "";
		output = this.label + " ; " + this.value;
		
		return output;
	}

}