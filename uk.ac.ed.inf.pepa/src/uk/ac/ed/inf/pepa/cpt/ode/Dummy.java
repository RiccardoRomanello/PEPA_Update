package uk.ac.ed.inf.pepa.cpt.ode;

import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics.CounterCallBack;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ModelConfigurationCandidateNode;

public class Dummy implements Runnable {

	ModelConfigurationCandidateNode node;
	private CounterCallBack cb;
	private String[] labels;
	
	public Dummy(String[] labels, ModelConfigurationCandidateNode node, CounterCallBack cb){
		
		this.labels = labels;
		this.node = node;
		this.cb = cb;
		
	}
	

	@Override
	public void run() {
		
		double[] results = new double[labels.length];
		           
		for(int i = 0; i < results.length; i++){
			results[i] = Utils.returnRandom();
		}
		
		this.node.setODEResults(labels, results);
		
		cb.increment();
		
	}

}
