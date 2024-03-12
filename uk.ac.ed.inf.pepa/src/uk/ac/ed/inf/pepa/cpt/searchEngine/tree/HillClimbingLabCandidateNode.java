package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;

public class HillClimbingLabCandidateNode extends CandidateNode {

	protected ArrayList<HCNode> children;
	
	public HillClimbingLabCandidateNode(String name, 
			MetaHeuristicNode parent){
		super(name, parent);
		
		this.children = new ArrayList<HCNode>();
		
		setMyMap();
		
	}
	
	public void setMyMap() {
		
		boolean isSingleSearch = (CPTAPI.getSearchControls().getValue().equals(Config.SEARCHSINGLE) || CPTAPI.getSearchControls().getValue().equals(Config.SEARCHBRUTE));
		if(isSingleSearch){
			this.myMap.put(Config.SEARCH, 1.0);
			this.myMap.put(Config.LABEXP, 1.0);
		} else {
			this.myMap.put(Config.SEARCH, 0.0);
			this.myMap.put(Config.LABEXP, Double.parseDouble(CPTAPI.getMHParameterControls().getValue(Config.LABEXP)));
		}
	}

	public void fillQueue(PriorityQueue<ResultNode> resultsQueue, IProgressMonitor monitor) {
		for(int i = 0; i < this.children.size(); i++){
			this.children.get(i).fillQueue(resultsQueue,monitor);
		}
	}

	@Override
	public void setUpUID() {
		this.uid = Utils.getHillClimbingLabCandidateNodeUID();
	}


	@Override
	public void setResultsSize() {
		
		for(int i = 0; i < children.size() ; i ++){
			children.get(i).setResultsSize();
		}
		
	}
	
	public void registerChild(HCNode child){
		children.add(child);
		childUIDToIndex.put(child.getUID(), children.size() - 1);
	}
	
	
}
