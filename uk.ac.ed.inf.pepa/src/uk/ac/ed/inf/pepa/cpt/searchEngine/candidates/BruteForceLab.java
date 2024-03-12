package uk.ac.ed.inf.pepa.cpt.searchEngine.candidates;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;


import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics.BruteForce;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.HCNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ModelConfigurationCandidateNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ParticleSwarmOptimisationLabCandidateNode;

public class BruteForceLab extends ParticleSwarmOptimisationLab {
	
	public BruteForceLab(HashMap<String,Double> parameters, 
			IProgressMonitor monitor, 
			HCNode hcNode){
		super(parameters,monitor,hcNode);
		
	}

	public BruteForceLab(ParticleSwarmOptimisationLabCandidateNode node, IProgressMonitor myMonitor) {
		super(node,myMonitor);
	}
	
	public void setParameters(HashMap<String,Double> parameters, HCNode resultNode){
		
		
		
	}
	
	public void startExperiment(HashMap<String,Double> parameters){
		
		try{
			
			this.myMonitor.beginTask("Searching", CPTAPI.totalPSOLabWork()*CPTAPI.totalPSOWork());
			this.myMonitor.subTask(this.myNode.getName());
		
				
			if(this.myMonitor.isCanceled()){
				throw new OperationCanceledException();
			}
				
			new BruteForce(Utils.copyHashMap(parameters), 
				this.myNode, 
				new SubProgressMonitor(this.myMonitor, (CPTAPI.totalPSOLabWork())));
				
			
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
