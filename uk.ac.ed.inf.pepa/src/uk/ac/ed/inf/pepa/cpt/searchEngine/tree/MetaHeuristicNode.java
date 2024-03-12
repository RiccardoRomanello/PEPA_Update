package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;

import java.util.HashMap;


public abstract class MetaHeuristicNode implements Node {
	
	protected int uid;
	protected HashMap<String,Double> myMap;
	protected String name;
	protected long timeCreated;
	private long timeFinished;
	protected CandidateNode parent;
	protected HashMap<Integer,Integer> childUIDToIndex;
	protected ModelConfigurationCandidateNode fittestNode;
	public boolean isPSO;
	
	public MetaHeuristicNode(String name, 
			CandidateNode parent,
			boolean isPSO){
		
		this.myMap = new HashMap<String,Double>();
		this.childUIDToIndex = new HashMap<Integer, Integer>();
		setUpUID();
		
		
		this.name = name + "-" + uid;
		this.parent = parent;
		this.timeCreated = System.currentTimeMillis();
		this.fittestNode = new ModelConfigurationCandidateNode();
		this.isPSO = isPSO;
	}
	
	public abstract void setUpUID();
	
	public String getName() {
		return this.name;
	}

	public Integer getUID() {
		return this.uid;
	}
	
	public void updateFinishTime(){
		this.timeFinished = System.currentTimeMillis();
	}
	
	public long getRunTime(){
		
		return this.timeFinished - this.timeCreated;
		
	}

	public CandidateNode getParent() {
		return parent;
	}

	@Override
	public HashMap<String, Double> getMyMap() {
		return this.myMap;
	}

	public ModelConfigurationCandidateNode getFittestNode() {
		return this.fittestNode;
	}

	public void setFittestNode(ModelConfigurationCandidateNode node) {
		if(node != null){
			this.fittestNode = node;
		} 
	}
	
	public abstract void setMyMap();

	public abstract void setResultsSize();
	
}
