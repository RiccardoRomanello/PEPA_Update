package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;


import java.util.ArrayList;
import java.util.PriorityQueue;

import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;

public class HCNode extends MetaHeuristicNode {
	
	protected ArrayList<ParticleSwarmOptimisationLabCandidateNode> children;
	
	public HCNode(String name, 
			CandidateNode hillClimbingCandidateNode){
		super(name, hillClimbingCandidateNode, false);
		this.children = new ArrayList<ParticleSwarmOptimisationLabCandidateNode>();
		setMyMap();
	}
	
	public void setMyMap() {
		
		this.myMap.put(Config.LABMUT, Double.parseDouble(CPTAPI.getHCControls().getValue(Config.LABMUT)));
		this.myMap.put(Config.LABGEN, Double.parseDouble(CPTAPI.getHCControls().getValue(Config.LABGEN)));
		
	}

	public void fillQueue(PriorityQueue<ResultNode> resultsQueue, IProgressMonitor monitor) {
	
		for(int i = 0; i < this.children.size(); i++){
			this.children.get(i).fillQueue(resultsQueue, monitor);
		}
	
	}

	@Override
	public void setUpUID() {
		this.uid = Utils.getHCNodeUID();
		
	}
	
	public void setResultsSize() {
		
		for(int i = 0; i < children.size() ; i ++){
			children.get(i).setResultsSize();
		}
	}
	
	public void registerChild(ParticleSwarmOptimisationLabCandidateNode child){
		children.add(child);
		childUIDToIndex.put(child.getUID(), children.size() - 1);
	}

}
