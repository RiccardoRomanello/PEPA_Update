package uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.searchEngine.candidates.ParticleSwarmOptimisationLab;
import uk.ac.ed.inf.pepa.cpt.searchEngine.candidates.BruteForceLab;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.HCNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.HillClimbingLabCandidateNode;

public class HillClimbing implements MetaHeuristics {
	
	private HCNode myNode;
	private IProgressMonitor myMonitor;
	private ParticleSwarmOptimisationLab candidate;
	private ParticleSwarmOptimisationLab possibleCandidate;

	public HillClimbing(HillClimbingLabCandidateNode hillClimbingCandidateNode, 
			IProgressMonitor monitor) {
		
		this.myMonitor = monitor;
		
		this.myNode = new HCNode("HillClimbingAlgorithm", hillClimbingCandidateNode);
		
		hillClimbingCandidateNode.registerChild(this.myNode);
		
		try{
			
			this.myMonitor.beginTask("Searching", CPTAPI.totalHCWork() * CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork());
			this.myMonitor.subTask(this.myNode.getName());
			
			if(CPTAPI.getSearchControls().getValue().equals(Config.SEARCHBRUTE))
				startBrute();
			else
				startAlgorithm();
		
		}
		finally {
			this.myNode.updateFinishTime();
			this.myMonitor.done();
		}
		
		
		
	}
	
	public void startAlgorithm(){
		
		HashMap<String,Double> parameters = getPSOParameters();
		
		this.candidate = new ParticleSwarmOptimisationLab(parameters,
				new SubProgressMonitor(this.myMonitor, CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork()), 
				this.myNode);
		
		if(CPTAPI.getSearchControls().getValue().equals(Config.SEARCHDRIVEN)){
			
			HashMap<String,Double> possibleParameters = mutateModelConfigurationLabParameters(parameters);
		
			this.possibleCandidate = new ParticleSwarmOptimisationLab(this.candidate.getNode(), 
					new SubProgressMonitor(this.myMonitor, CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork()));
			
			
			int generations = this.myNode.getMyMap().get(Config.LABGEN).intValue();
			
			for(int i = 1; i < generations; i++){
				
				if(this.myMonitor.isCanceled()){
					throw new OperationCanceledException();
				}
				
				this.possibleCandidate.setParameters(possibleParameters, 
						this.myNode);
				
				if(possibleCandidate.getFitness() < candidate.getFitness()){
					parameters = Utils.copyHashMap(possibleParameters);
					candidate.setNode(possibleCandidate.getNode());
				} 
				possibleParameters = mutateModelConfigurationLabParameters(parameters);
			}
		}
		
	}
	
	public void startBrute(){
		
		HashMap<String,Double> brute = getPSOParameters();
		
		brute.put(Config.LABEXP, 1.0);
		brute.put(Config.LABGEN, 1.0);
		brute.put("Brute", 1.0);
		
		this.candidate = new BruteForceLab(brute,
				new SubProgressMonitor(this.myMonitor, CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork()), 
				this.myNode);
	}
	
	/*
	 * mutate existing parameters
	 */
	public HashMap<String,Double> mutateModelConfigurationLabParameters(HashMap<String,Double> parameters){
		
		HashMap<String,Double> mutatedParameters = Utils.copyHashMap(parameters);
		String[] keys = mutatedParameters.keySet().toArray(new String[mutatedParameters.keySet().size()]);
		Double min, max, value;
		String type;
		
		for(int i = 0; i < keys.length; i++){
			
			if(Utils.rollDice(this.myNode.getMyMap().get(Config.LABMUT))){
				min = Double.parseDouble(CPTAPI.getPSORangeParameterControls().getValue(keys[i], Config.LABMIN));
				max = Double.parseDouble(CPTAPI.getPSORangeParameterControls().getValue(keys[i], Config.LABMAX));
				type = CPTAPI.getPSORangeParameterControls().getType(keys[i],Config.LABMIN);
				value = Utils.returnRandomInRange(min, max, type);
				mutatedParameters.put(keys[i], value);
			}
			
		}
		
		return Utils.copyHashMap(mutatedParameters);
	}
	
	/*
	 * Get some randomised PSO parameters
	 */
	public HashMap<String,Double> getPSOParameters(){
		
		HashMap<String,Double> parameters = new HashMap<String,Double>();
		
		String[] keys = CPTAPI.getPSORangeParameterControls().getKeys();
		
		Double min, max, value;
		
		String type;
		
		if(CPTAPI.getSearchControls().getValue().equals(Config.SEARCHDRIVEN)){
			for(int i = 0; i < keys.length; i++){
				
				min = Double.parseDouble(CPTAPI.getPSORangeParameterControls().getValue(keys[i], Config.LABMIN));
				max = Double.parseDouble(CPTAPI.getPSORangeParameterControls().getValue(keys[i], Config.LABMAX));
				type = CPTAPI.getPSORangeParameterControls().getType(keys[i],Config.LABMIN);
				value = Utils.returnRandomInRange(min, max, type);
				parameters.put(keys[i], value);
			}
		} else {
			for(int i = 0; i < keys.length; i++){
				parameters.put(keys[i], Double.parseDouble(CPTAPI.getPSORangeParameterControls().getValue(keys[i])));
			}
		}

		return Utils.copyHashMap(parameters);
	}
	
}
