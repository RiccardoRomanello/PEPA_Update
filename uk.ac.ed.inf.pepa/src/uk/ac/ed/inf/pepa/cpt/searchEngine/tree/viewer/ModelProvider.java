package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

import java.util.ArrayList;
import java.util.List;

public enum ModelProvider {
	INSTANCE;
	
	private List<PopulationAndCost> pacs;
	
	private ModelProvider() {
		pacs = new ArrayList<PopulationAndCost>();
		pacs.add(new PopulationAndCost("no data", "no data", "no data", "no data"));
	}
	
	public List<PopulationAndCost> getpacs() {
		return pacs;
	}
}