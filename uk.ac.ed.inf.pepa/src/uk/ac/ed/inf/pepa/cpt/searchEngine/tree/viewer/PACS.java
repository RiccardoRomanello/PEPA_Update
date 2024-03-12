package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

import java.util.ArrayList;
import java.util.List;

public class PACS {
	
	private List<PopulationAndCost> pacs;
	
	public PACS() {
		pacs = new ArrayList<PopulationAndCost>();
	}
	
	public void addPAC(String population, String cost, String performance, String total){
		pacs.add(new PopulationAndCost(population, cost, performance, total));
	}
	
	public List<PopulationAndCost> getpacs() {
		return pacs;
	}

}
