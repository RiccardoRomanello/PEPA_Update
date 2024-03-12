package uk.ac.ed.inf.pepa.cpt.config.control;

public abstract class PopulationControl implements Control{
	
	
	public abstract String[] getKeys();
	
	public abstract String[] getXKeys(String key);
	
	public abstract boolean setValue(String key, String attribute, String value);
	
	public abstract String getValue(String key, String attribute);
	
	public abstract String getType(String key, String attribute);
	
	public abstract boolean validate();
	
}
