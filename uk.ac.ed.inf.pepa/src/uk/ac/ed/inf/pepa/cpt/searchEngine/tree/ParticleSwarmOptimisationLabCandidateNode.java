package uk.ac.ed.inf.pepa.cpt.searchEngine.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;

public class ParticleSwarmOptimisationLabCandidateNode extends CandidateNode {
	
	HashMap<String,Double> parameters;
	protected ArrayList<PSONode> children;
	private ModelConfigurationCandidateNode fittestNode;
	
	public ParticleSwarmOptimisationLabCandidateNode(String name,
			HashMap<String,Double> parameters,
			MetaHeuristicNode parent){
		super(name, parent);
		
		this.children = new ArrayList<PSONode>();
		this.parameters = parameters;
		setMyMap();
	}
	
	public void setMyMap() {
		this.myMap.put(Config.LABEXP, parameters.get(Config.LABEXP));
	}
	
	public void fillQueue(PriorityQueue<ResultNode> resultsQueue, IProgressMonitor monitor) {
		for(int i = 0; i < this.children.size(); i++){
			this.children.get(i).fillQueue(resultsQueue, monitor);
		}
	}
	
	@Override
	public void setUpUID() {
		this.uid = Utils.getParticleSwarmOptimisationLabCandidateNodeUID();
	}

	@Override
	public void setResultsSize() {
		
		for(int i = 0; i < children.size() ; i ++){
			children.get(i).setResultsSize();
		}
		
	}
	
	public void updateFitness(){
		
		Double top, mean, std, art, fitness;
		
		top = 10000.0;
		mean = 0.0;
		art = 0.0;
		std = 0.0;
		
		for(int i = 0; i < this.children.size(); i++){
			fitness = this.children.get(i).getFittestNode().getFitness();
			if(fitness < top){
				top = fitness;
				this.fittestNode = this.children.get(i).getFittestNode();
			}
			mean += fitness;
			art += ((Long) this.children.get(i).getRunTime()).doubleValue();
		}
		
		mean = mean/this.children.size();
		art = mean/this.children.size();
		
		for(int i = 0; i < this.children.size(); i++){
			fitness = this.children.get(i).getFittestNode().getFitness();
			std += Math.pow(mean - fitness,2);
		}
		
		std = std/this.children.size();
		
		this.fitness = (0.25 * top) + (0.25 * mean) + (0.25 * std) + (0.25 * art); 
	}
	
	public ModelConfigurationCandidateNode getFittestNode() {
		return this.fittestNode;
	}
	
	public void registerChild(PSONode child){
		children.add(child);
		childUIDToIndex.put(child.getUID(), children.size() - 1);
	}
	
}
