package uk.ac.ed.inf.pepa.jhydra;

import uk.ac.ed.inf.pepa.jhydra.driver.*;


public class JHydra {

	static Driver mainDriver;
	
	
	public static void main(String[] args) {
		// TODO parse
		// TODO gen
		// TODO func
		// TODO steady
		// TODO target
		// TODO uniform
		
		System.out.println("Starting JHydra...");

		mainDriver = new Driver(args[0]);
		mainDriver.go();
		
		System.out.println("JHydra execution complete...");

	}
		
}
