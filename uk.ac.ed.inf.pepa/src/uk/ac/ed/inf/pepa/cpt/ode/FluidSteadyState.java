package uk.ac.ed.inf.pepa.cpt.ode;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics.CounterCallBack;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ModelConfigurationCandidateNode;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class FluidSteadyState implements Runnable {
	
	private String[] labels;
	private IParametricDerivationGraph graph;
	private IPointEstimator[] estimators;
	private IStatisticsCollector[] collectors;
	private OptionMap map;
	private SteadyStateRoutine routine;
	private double[] results = null;
	private IProgressMonitor monitor;
	ModelConfigurationCandidateNode node;
	private CounterCallBack cb;
	
	class NullMonitor implements IProgressMonitor {

		@Override
		public void beginTask(int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void done() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isCanceled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setCanceled(boolean state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void worked(int worked) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public FluidSteadyState(String[] labels, 
			IParametricDerivationGraph graph,
			IPointEstimator[] estimators,
			IStatisticsCollector[] collectors,
			OptionMap map,
			IProgressMonitor monitor,
			ModelConfigurationCandidateNode node,
			CounterCallBack cb){
		
		this.labels = labels;
		this.graph = graph;
		this.estimators = estimators;
		this.collectors = collectors;
		this.map = map;
		this.monitor = monitor;
		this.node = node;
		this.cb = cb;
		
	}
	
	private void computeResults(double timePoint, double[] solution)
	throws DifferentialAnalysisException {
		// calculate results anyway
		double[] estimates = new double[estimators.length];
		results = new double[collectors.length];
		for (int i = 0; i < estimates.length; i++) {
			estimates[i] = estimators[i].computeEstimate(timePoint, solution);
		}
		for (int j = 0; j < collectors.length; j++)
			results[j] = collectors[j].computeObservation(estimates);
	}
	

	@Override
	public void run() {
		
		routine = new SteadyStateRoutine(this.map, this.graph);
		
		try{
			if(this.monitor == null){
				this.monitor = new NullMonitor();
			}
			routine.obtainSteadyState(this.monitor);
		} catch (DifferentialAnalysisException e) {
			differentialAnalysisExeceptionHandling();
		} catch (InterruptedException e) {
			interupted();
		}
		
		try{
			computeResults(routine.getTimePoint(), routine.getSolution());
		} catch (DifferentialAnalysisException e) {
			differentialAnalysisExeceptionHandling();
		}
		
		
		this.node.setODEResults(labels, results);
		
		cb.increment();
		
	}
	
	private void differentialAnalysisExeceptionHandling() {
		this.node.switchFlag();
		
	}

	public void interupted(){
		
		for(int i = 0; i < results.length; i++){
			results[i] = 100000.0;
		}
		
	}

}
