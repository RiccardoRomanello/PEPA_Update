package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.searchEngine.fitnessFunctions.FitnessFunction;
import uk.ac.ed.inf.pepa.cpt.searchEngine.fitnessFunctions.ModelConfigurationFitnessFunction;


public class ModelConfigurationCandidateNode extends CandidateNode 
implements Comparator<ModelConfigurationCandidateNode>, Comparable<ModelConfigurationCandidateNode> {
	
	private ModelConfigurationCandidateNode sister;
	private int generation;
	private HashMap<String,Double> performanceMap;
	private HashMap<String,Double> velocity;
	private FitnessFunction fitnessFunction;
	private Double performanceResult;
	private Double populationResult;
	private String familyUID;
	private boolean evaluatedSuccessFully;
	private HashMap<String,Double> parameters;
	private MetaHeuristicNode parentMetaHeuristicNode;

	public ModelConfigurationCandidateNode(String name,
			HashMap<String,Double> parameters,
			HashMap<String,Double> velocity,
			IProgressMonitor monitor,
			MetaHeuristicNode parentMetaHeuristicNode) {
		super(name, parentMetaHeuristicNode);
		
		this.parameters = parameters;
		setMyMap();
		this.performanceMap = new HashMap<String, Double>();
		this.performanceResult = 100000.0;
		this.populationResult = 100000.0;
		this.sister = null;
		this.generation = 0;
		this.velocity = velocity;
		this.fitnessFunction = new ModelConfigurationFitnessFunction();
		this.familyUID = this.getName().split("-")[1];
		this.evaluatedSuccessFully = true;
		this.parentMetaHeuristicNode = parentMetaHeuristicNode;
	}
	
	public Double getTotalComponents(){
		
		String[] keys = this.myMap.keySet().toArray(new String[this.myMap.keySet().size()]);
		Double count = 0.0;
		
		for(int i = 0; i < keys.length; i++){
			count += this.myMap.get(keys[i]);
		}
		
		return count;
	}
	
	public ModelConfigurationCandidateNode(){
		super("temp",null);
	}
	
	@Override
	public void setUpUID(){
		this.uid = Utils.getModelCandidateNodeUID();
	}
	
	
	public void setMyMap(HashMap<String,Double> myMap) {
		this.myMap = Utils.copyHashMap(myMap);
	}
	
	public void setSister(ModelConfigurationCandidateNode node){
		this.sister = node;
		this.setFamilyUID(node.familyUID);
		this.setGeneration(this.sister.generation + 1);
	}
	
	private void setFamilyUID(String familyUID) {
		this.familyUID = familyUID;
		
	}

	@Override
	public int getGeneration(){
		return this.generation;
	}
	
	@Override
	public void setGeneration(int generation){
		this.generation = generation;
	}

	public void setODEResults(String[] labels, double[] results) {
		
		for(int i = 0; i < labels.length;i++){
			this.performanceMap.put(labels[i], results[i]);
		}
		
	}
	
	@Override
	public HashMap<String,Double> getMyMap() {
		return myMap;
	}
	
	
	/**
	 * reads from local maps, uses fitness function object, returns results... 
	 */
	public void updateFitness(){
		
		if(this.evaluatedSuccessFully){
			this.fitness = this.fitnessFunction.assessFitness(this.myMap, this.performanceMap);
		} else {
			this.fitness = 100000.0;
		}
		this.updateFinishTime();
		this.performanceResult = ((ModelConfigurationFitnessFunction)this.fitnessFunction).performance;
		this.populationResult = ((ModelConfigurationFitnessFunction)this.fitnessFunction).population;
	}
	
	public HashMap<String, Double> getVelocity() {
		return velocity;
	}

	public void setVelocity(HashMap<String, Double> velocity) {
		this.velocity = velocity;
	}
	
	
	public ModelConfigurationCandidateNode getSister(){
		return this.sister;
	}
	
	
	/**
//	 * has the particle moved in the last two turns?
	 * @return
	 */
	public boolean hasStopped(){
		
		HashMap<String,Double> third,second,first;
		Double t;
		
		if(this.hasSister()){
			first = getMyMap();
		} else {
			return false;
		}
		
		if(this.sister.hasSister()){
			second = this.sister.getMyMap();
		} else {
			return false;
		}
		
		if(this.sister.getSister().hasSister()){
			third = this.sister.getSister().getMyMap();
			
		} else {
			return false;
		}
		
		String[] keys = first.keySet().toArray(new String[first.keySet().size()]);
		
		t = 0.0; 
		
		for(int i = 0; i < keys.length; i++){
			t += Math.abs(first.get(keys[i]) - second.get(keys[i]));
			t += Math.abs(first.get(keys[i]) - third.get(keys[i]));

		}
		
		return (t < 3);
		
	}

	public void switchFlag() {
		this.evaluatedSuccessFully = !this.evaluatedSuccessFully;
		
	}
	
	public boolean hasConverged(){
		return this.evaluatedSuccessFully;
	}
	
	public HashMap<String,Double> getPerformanceMap(){
		return this.performanceMap;
	}
	
	public Double getPerformanceResult() {
		return performanceResult;
	}

	public Double getPopulationResult() {
		return populationResult;
	}

	@Override
	public void setMyMap() {
		this.myMap = Utils.copyHashMap(this.parameters);
	}

	public void fillQueue(PriorityQueue<ResultNode> resultsQueue, IProgressMonitor monitor) {
		
		if(monitor.isCanceled()){
			throw new OperationCanceledException();
		}
		
		resultsQueue.add(new ResultNode(this.parentMetaHeuristicNode,this));
		
		monitor.subTask("Compiling results: " + resultsQueue.size() + " of " + CPTAPI.getResultSize());
	}
	
	@Override
	public void setResultsSize() {

		CPTAPI.setTotalResults();
		
	}
	
	public int compare(ModelConfigurationCandidateNode c1, ModelConfigurationCandidateNode c2){
		
		if(c1.getFitness() > c2.getFitness()){
			return 1;
		} else if (c1.getFitness() < c2.getFitness()){
			return -1;
		} else {
			return 0;
		}
		
	}
	
	@Override
	public int compareTo(ModelConfigurationCandidateNode arg0) {
		
		if(this.fitness > arg0.getFitness()){
			return 1;
		} else if (this.fitness < arg0.getFitness()){
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object obj){
		return false;
	}

}
