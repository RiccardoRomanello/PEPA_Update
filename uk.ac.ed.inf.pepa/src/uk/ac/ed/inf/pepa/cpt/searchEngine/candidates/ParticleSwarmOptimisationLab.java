package uk.ac.ed.inf.pepa.cpt.searchEngine.candidates;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;


import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics.ParticleSwarmOptimisation;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.HCNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ModelConfigurationCandidateNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ParticleSwarmOptimisationLabCandidateNode;

public class ParticleSwarmOptimisationLab extends Lab {
	
	protected ParticleSwarmOptimisationLabCandidateNode myNode;
	protected IProgressMonitor myMonitor;
	
	public ParticleSwarmOptimisationLab(HashMap<String,Double> parameters, 
			IProgressMonitor monitor, 
			HCNode hcNode){
		
		this.myNode = new ParticleSwarmOptimisationLabCandidateNode("ModelConfigurationLab",
				parameters,
				hcNode);
		hcNode.registerChild(this.myNode);
		
		this.myMonitor = monitor;
		
		startExperiment(parameters);
		
	}

	public ParticleSwarmOptimisationLab(ParticleSwarmOptimisationLabCandidateNode node, IProgressMonitor myMonitor) {
		this.myNode = node;
		this.myMonitor = myMonitor;
	}
	
	public void setParameters(HashMap<String,Double> parameters,
			HCNode resultNode){
		
		ParticleSwarmOptimisationLabCandidateNode newNode = new ParticleSwarmOptimisationLabCandidateNode("ParticleSwarmOptimisationLab",
				parameters,
				resultNode);
		
		newNode.setSister(this.myNode);
		
		resultNode.registerChild(this.myNode);
		
		this.myNode = newNode;
		
		startExperiment(parameters);
		
	}
	
	public void startExperiment(HashMap<String,Double> parameters){
		
		try{
			
			this.myMonitor.beginTask("Searching", CPTAPI.totalPSOLabWork()*CPTAPI.totalPSOWork());
			this.myMonitor.subTask(this.myNode.getName());
		
			for(int i = 0; i < myNode.getMyMap().get(Config.LABEXP); i++){
				
				if(this.myMonitor.isCanceled()){
					throw new OperationCanceledException();
				}
				
				new ParticleSwarmOptimisation(Utils.copyHashMap(parameters), 
					this.myNode, 
					new SubProgressMonitor(this.myMonitor, (CPTAPI.totalPSOLabWork())));
				
			}
			
			this.myNode.updateFitness();
		
		}
		finally {
			this.myNode.updateFinishTime();
			this.myMonitor.done();
		}
		
	}

	public ParticleSwarmOptimisationLabCandidateNode getNode() {
		return myNode;
	}

	public void setNode(ParticleSwarmOptimisationLabCandidateNode node) {
		this.myNode = node;
	}
	
	public Double getFitness(){
		return this.myNode.getFitness();
		
	}

	public ModelConfigurationCandidateNode getFitnessNode() {
		return myNode.getFittestNode();
	}
	
}
