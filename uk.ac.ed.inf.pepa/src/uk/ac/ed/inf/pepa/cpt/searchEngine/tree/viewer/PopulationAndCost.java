package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

public class PopulationAndCost {
	
	private String population, cost, performance, totalPopulation;
	
	public PopulationAndCost (String population, String cost, String performance, String totalPopulation){
		this.setPopulation(population);
		this.setCost(cost);
		this.setPerformance(performance);
		this.setTotalPopulation(totalPopulation);
	}
	
	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String toString() {
		return cost + " : " + population;
	}

	public void setPerformance(String performance) {
		this.performance = performance;
	}

	public String getPerformance() {
		return performance;
	}

	public void setTotalPopulation(String totalPopulation) {
		this.totalPopulation = totalPopulation;
	}

	public String getTotalPopulation() {
		return totalPopulation;
	}
	
	

}
