package uk.ac.ed.inf.pepa.cpt.searchEngine.fitnessFunctions;

import java.util.HashMap;

public interface FitnessFunction {
	
	public Double assessFitness(HashMap<String,Double> domain, HashMap<String,Double> secondDomain);

}
