package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;

public class ResultNode implements Comparator<ResultNode>, Comparable<ResultNode> {

//	private MetaHeuristicNode mhConfiguration;
//	private ModelConfigurationCandidateNode modelConfiguration;
	private Double cost, performanceCost, populationCost, totalPopulation, runTime;
	private HashMap<String,Double> componentPopulationMap;
	private HashMap<String,Double> performanceMap;
	private HashMap<String,Double> metaheuristicParameterMap;
	private String name;
	private boolean converged;
	
	public String COMPONENT,MEASURED, PSO, TOTAL, POP, PER, TPOP, CONVERGED, RUNTIME;
	
	public ResultNode(MetaHeuristicNode mhConfiguration,
			ModelConfigurationCandidateNode modelConfiguration){
		
		this.cost = modelConfiguration.getFitness();
		this.performanceCost = modelConfiguration.getPerformanceResult();
		this.populationCost = modelConfiguration.getPopulationResult();
		this.totalPopulation = modelConfiguration.getTotalComponents();
		this.componentPopulationMap = Utils.copyHashMap(modelConfiguration.getMyMap());
		this.performanceMap = Utils.copyHashMap(modelConfiguration.getPerformanceMap());
		this.metaheuristicParameterMap = Utils.copyHashMap(mhConfiguration.getMyMap());
		this.name = modelConfiguration.getName();
		this.converged = modelConfiguration.hasConverged();
		this.runTime = (double) modelConfiguration.getRunTime();
		
		//required strings
		this.COMPONENT = "Component population";
		this.MEASURED = "Measured Performance";
		this.PSO = "PSO parameters";
		this.TOTAL = "Total cost";
		this.POP = "Population cost";
		this.PER = "Performance cost";
		this.TPOP = "Total population";
		this.CONVERGED = "Converged";
		this.RUNTIME = "Run time";
		
	}
	
	private String mapAsCSVString(HashMap<String,Double> map){
		String output = "";
		
		for(String s : map.keySet()){
			output = output + s + ";" + map.get(s) + ";";
		}
		
		if(output.length() != 0)
			return output.substring(0,output.length() - 1);
		else
			return "";
	}
	
//	private String mapAsNodeString(HashMap<String,Double> map){
//		String output = "";
//		
//		for(String s : map.keySet()){
//			output = output + s + "[" + map.get(s) + "] ";
//		}
//		
//		if(output.length() != 0)
//			return output.substring(0,output.length() - 1);
//		else
//			return "";
//	}
	
	private String mapAsNodeStringInt(HashMap<String,Double> map){
		String output = "";
		
		for(String s : map.keySet()){
			output = output + s + "[" + (map.get(s)).intValue() + "] ";
		}
		
		if(output.length() != 0)
			return output.substring(0,output.length() - 1);
		else
			return "";
	}
	
	private String mapAsNodeStringEquals(HashMap<String,Double> map){
		String output = "";
		
		for(String s : map.keySet()){
			output = output + s + " = " + map.get(s);
		}
		
		if(output.length() != 0)
			return output.substring(0,output.length() - 1);
		else
			return "";
	}
	
	private String mapAsNodeStringEquals4SF(HashMap<String,Double> map){
		String output = "";
		DecimalFormat myFormat = new DecimalFormat("0.000");
		
		for(String s : map.keySet()){
			output = output + s + " = " + myFormat.format(map.get(s));
		}
		
		if(output.length() != 0)
			return output.substring(0,output.length() - 1);
		else
			return "";
	}
	

	public String populationMapAsCSVString(){
		return mapAsCSVString(componentPopulationMap);
	}
	
	public String peformanceMapAsCSVString(){
		return  mapAsCSVString(performanceMap);
	}
	
	public String psoMapAsCSVString(){
		return  mapAsCSVString(metaheuristicParameterMap);
	}
	
	public String getPopulationMapAsNodeString(){
		return mapAsNodeStringInt(componentPopulationMap);
	}
	
	public String peformanceMapAsNodeString(){
		return mapAsNodeStringEquals(performanceMap);
	}
	
	public String peformanceMapAsNodeString4SF(){
		return mapAsNodeStringEquals4SF(performanceMap);
	}
	
	public String psoMapAsNodeString(){
		return mapAsNodeStringInt(metaheuristicParameterMap);
	}
	
	public String getTotalCostString(){
		return "" + this.cost;
	}
	
	public String getTotalCostString4SF(){
		DecimalFormat myFormat = new DecimalFormat("0.000");
		if(this.cost > 10000.0)
			return "Did not converge";
		else
			return myFormat.format(this.cost);
	}
	
	public String getPopulationCostString(){
		return "" + this.populationCost;
	}
	
	public String getPerformanceCostString(){
		return "" + this.performanceCost;
	}
	
	public String getTotalPopulationString(){
		return "" + this.totalPopulation;
	}
	
	public String gapper(HashMap<String,Double> map){
		String output = "";
		for(int i = 0; i < map.size()*2; i++){
			output = output + ";";
		}
		return output;
	}
	
	public String componentPopulationGap(){
		return gapper(this.componentPopulationMap);	
	}
	
	public String performanceGap(){
		return gapper(performanceMap);
	}
	
	public String psoGap(){
		return gapper(metaheuristicParameterMap);
	}
	
	public String heading(){
		return COMPONENT + componentPopulationGap() + ";"
		+ TOTAL + ";"
		+ PER + ";"
		+ POP + ";"
		+ MEASURED + performanceGap()
		+ TPOP + ";;"
		+ PSO + psoGap();
	}
	
	public String toString(){
		return populationMapAsCSVString() + ";;" 
		+ getTotalCostString() + ";" 
		+ getPerformanceCostString() + ";"
		+ getPopulationCostString() + ";"
		+ peformanceMapAsCSVString() + ";"
		+ getTotalPopulationString() + ";;"
		+ psoMapAsCSVString() + ";"
		+ getRunTime();
	}
	
	public int compare(ResultNode c1, ResultNode c2){
		
		if(c1.getCost() > c2.getCost()){
			return 1;
		} else if (c1.getCost() < c2.getCost()){
			return -1;
		} else {
			return 0;
		}
		
	}

	private Double getCost() {
		return this.cost;
	}

	@Override
	public int compareTo(ResultNode arg0) {
		
		if(this.cost > arg0.getCost()){
			return 1;
		} else if (this.cost < arg0.getCost()){
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof ResultNode)
			return this.getPopulationMapAsNodeString().equals(((ResultNode) obj).getPopulationMapAsNodeString()); 
		else
			return false;
	}
	
	
	public String getName(){
		
		Double met;
		
		if(CPTAPI.getEvaluationControls().getValue().equals(Config.EVALARPT))
			met = getPercentageOfMetPerformanceTargets(false);
		else 
			met = getPercentageOfMetPerformanceTargets(true);
		if(met >= 100.0)
			if(!this.converged)
				return this.name + ": MET PERFORMANCE TARGET (Inaccurate)";
			else 
				return this.name + ": MET PERFORMANCE TARGET";
		else
			return this.name + ": FAILED PERFORMANCE TARGET";
	}
	
	public String hasConverged(){
		if(this.converged)
			return "True";
		else
			return "False";
	}
	
	public Double getTotalPopulation(){
		return totalPopulation;
	}
	
	public Double getRunTime(){
		return runTime;
	}
	
	public Double getPercentageOfMetPerformanceTargets(boolean higherIsGood){
		int low, high;
		Double output;
		low = 0;
		high = 0;
		for(String s: this.performanceMap.keySet()){
			if(this.performanceMap.get(s) < Double.parseDouble(CPTAPI.getTargetControl().getValue(s, Config.LABTAR)))
				low++;
			else if (this.performanceMap.get(s) > Double.parseDouble(CPTAPI.getTargetControl().getValue(s, Config.LABTAR)))
				high++;
			else{
				high++;
				low++;
			}
		}
		
		if(higherIsGood)
			output = ((double) high*100)/this.performanceMap.size();
		else
			output = ((double) low* 100)/this.performanceMap.size();
		
		return output;
	}
	
	public HashMap<String,Double> getTargetMet(boolean higherIsGood){
		int low, high;
		HashMap<String,Double> output = new HashMap<String, Double>();
		low = 0;
		high = 0;
		for(String s: this.performanceMap.keySet()){
			if(this.performanceMap.get(s) <= Double.parseDouble(CPTAPI.getTargetControl().getValue(s, Config.LABTAR)))
				low++;
			if (this.performanceMap.get(s) >= Double.parseDouble(CPTAPI.getTargetControl().getValue(s, Config.LABTAR)))
				high++;
			if(higherIsGood)
				output.put(s, (double) high);
			else
				output.put(s, (double) low);
		}
		
		return output;
	}

}
