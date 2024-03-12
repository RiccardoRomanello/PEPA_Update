package uk.ac.ed.inf.pepa.cpt.searchEngine.candidates;

import java.util.PriorityQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics.HillClimbing;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.HillClimbingLabCandidateNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ResultNode;

public class HillClimbingLab extends Lab {

	private HillClimbingLabCandidateNode myNode;
	private IProgressMonitor myMonitor;
	
	/**
	 * A lab runs many experiments (each meta heuristic run is an experiment)
	 * @param api
	 * @param monitor
	 */
	public HillClimbingLab(IProgressMonitor monitor) {
		
		this.myMonitor = monitor;
		
		this.myNode = new HillClimbingLabCandidateNode("HillClimbingConfigurationLab", null);
		
		try{
			
			this.myMonitor.beginTask("Searching", CPTAPI.totalHCLabWork() * CPTAPI.totalHCWork() * CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork());
			start();
			
		}
		finally {
			this.myNode.updateFinishTime();
			this.myMonitor.done();
		}
		
	}
	
	private void start(){
		for(int i = 0; i < myNode.getMyMap().get(Config.LABEXP); i++){
			
			if(this.myMonitor.isCanceled()){
				throw new OperationCanceledException();
			}
			
			new HillClimbing(myNode, new SubProgressMonitor(this.myMonitor, CPTAPI.totalHCWork() * CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork()));
		}
	}
	
	public void fillQueue(PriorityQueue<ResultNode> resultsQueue, IProgressMonitor monitor) {
		
		this.myNode.setResultsSize();
		
		this.myNode.fillQueue(resultsQueue,monitor);
		
	}

}
