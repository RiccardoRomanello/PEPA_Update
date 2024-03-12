package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;

import java.util.HashMap;


public abstract class CandidateNode implements Node {
	
	protected int uid;
	private int generation;
	protected HashMap<String,Double> myMap;
	private String name;
	private long timeCreated;
	protected long timeFinished;
	protected Double fitness;
	private MetaHeuristicNode parent;
	protected HashMap<Integer,Integer> childUIDToIndex;
	private CandidateNode sister;
	private String familyUID;
	
	public CandidateNode(String name, 
			MetaHeuristicNode parent){
		
		this.myMap = new HashMap<String,Double>();
		this.childUIDToIndex = new HashMap<Integer, Integer>();
		
		setUpUID();
		
		this.setName(name + "-" + uid);
		this.setGeneration(0);
		this.setParent(parent);
		this.timeCreated = System.currentTimeMillis();
		this.fitness = 1000000.0;
		this.sister = null;
		this.familyUID = this.getName().split("-")[1];
		
	}
	
	public abstract void setUpUID();

	
	public abstract void setMyMap();

	public HashMap<String,Double> getMyMap() {
		return myMap;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public int getGeneration() {
		return generation;
	}
	

	public long getRunTime(){
		
		return this.timeFinished - this.timeCreated;
		
	}

	public void setParent(MetaHeuristicNode parent) {
		this.parent = parent;
	}

	public MetaHeuristicNode getParent() {
		return parent;
	}

	public Integer getUID() {
		return this.uid;
	}

	public Double getFitness() {
		return this.fitness;
	}

	public void setSister(CandidateNode node) {
		this.sister = node;
		this.setFamilyUID(node.familyUID);
		this.setGeneration(this.sister.generation + 1);
	}
	
	private void setFamilyUID(String familyUID) {
		this.familyUID = familyUID;
	}

	public boolean hasSister(){
		
		if(this.sister != null){
			return true;
		} else {
			return false;
		}
	}
	
	public void updateFinishTime(){
		this.timeFinished = System.currentTimeMillis();
	}


	public abstract void setResultsSize(); 

	
}
