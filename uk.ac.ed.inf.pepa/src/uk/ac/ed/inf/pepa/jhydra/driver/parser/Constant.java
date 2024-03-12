package uk.ac.ed.inf.pepa.jhydra.driver.parser;

public class Constant {

	private String name;
	private double value;
	
	public Constant(String s, double d){
		name = s;
		value = d;
		System.out.println("Created new constant <" + name + "> <" + value + "> ...");
	}
	
	public String getName(){ return name; }
	
	public double getValue(){ return value; }
}
