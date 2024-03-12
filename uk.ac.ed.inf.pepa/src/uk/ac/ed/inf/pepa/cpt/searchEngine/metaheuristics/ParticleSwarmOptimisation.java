package uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.Config;
//import uk.ac.ed.inf.pepa.cpt.ode.Dummy;
import uk.ac.ed.inf.pepa.cpt.ode.FluidSteadyState;
import uk.ac.ed.inf.pepa.cpt.searchEngine.candidates.ModelConfiguration;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.PSONode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ParticleSwarmOptimisationLabCandidateNode;

public class ParticleSwarmOptimisation implements MetaHeuristics {
	
	public class SynchronizedCounter implements CounterCallBack {
	    
		private int c = 0;
		
		public SynchronizedCounter(){
		}

	    public synchronized void increment() {
	        c++;
	    }

	    public synchronized int value() {
	        return c;
	    }
	}
	
	private PSONode myNode;
	private IProgressMonitor myMonitor;
	private ArrayList<ModelConfiguration> population;
	private int threads;
	private ExecutorService executor;
	private SynchronizedCounter counter;

	public ParticleSwarmOptimisation(HashMap<String, Double> parameters,
			ParticleSwarmOptimisationLabCandidateNode particleSwarmOptimisationLabCandidateNode, 
			IProgressMonitor monitor) {
		
		this.myNode = new PSONode("ParticleSwarmOptimisation", 
				parameters, 
				particleSwarmOptimisationLabCandidateNode);
		
		particleSwarmOptimisationLabCandidateNode.registerChild(this.myNode);
		
		this.myMonitor = monitor;
		
		this.population = new ArrayList<ModelConfiguration>();
		
		this.threads = this.myNode.getMyMap().get(Config.LABPOP).intValue();
	
		this.executor = Executors.newFixedThreadPool(threads);
		
		startAlgorithm();

		this.executor.shutdown();
		
		this.myNode.updateFinishTime();
		
		
	}
	
	public void startAlgorithm(){
		
		try{
			
			this.myMonitor.beginTask("Searching" + this.myNode.getName(), CPTAPI.totalPSOWork());
			this.myMonitor.subTask("PSO " + this.myNode.getName() + " starting...");
			
			//set up population
			setUpPopulation();
			
			//find current best
			evaluateAll();
			
			int i = 1;
			boolean anyParticlesStillMoving = true;

			while((i < this.myNode.getMyMap().get(Config.LABGEN) && anyParticlesStillMoving)){
				
				this.myMonitor.subTask("Working... please wait: " + this.myNode.getName() + " is evaluating generation " + (i+1) + "." );
				
				boolean allParticlesHaveStopped = true;
				
				for(int j = 0; j < this.myNode.getMyMap().get(Config.LABPOP); j++){
					move(population.get(j));
					allParticlesHaveStopped = allParticlesHaveStopped && population.get(j).getNode().hasStopped();
				}
				
				anyParticlesStillMoving = !allParticlesHaveStopped;
				
				evaluateAll();
				
				i++;
				
				if(this.myMonitor.isCanceled()){
					throw new OperationCanceledException();
				}
			}
			
			this.myMonitor.subTask("Finished PSO run. " + this.myNode.getName());
			
		
		}
		finally {
			this.myNode.updateFinishTime();
			this.myMonitor.done();
		}
	
	}
	
	/**
	 * Create the particle population
	 */
	public void setUpPopulation(){
		
		HashMap<String, Double> parameters = getParameters();
		HashMap<String, Double> velocity = getVelocity(parameters);
		
		for(int i = 0; i < this.myNode.getMyMap().get(Config.LABPOP);i++){
			this.population.add(new ModelConfiguration(Utils.copyHashMap(parameters), 
					Utils.copyHashMap(velocity),
					null, 
					myNode));
			
			parameters = getParameters();
			velocity = getVelocity(parameters);
		}
	}
	
	/**
	 * Cycle through all particles doing an ODE evaluation
	 */
	public void evaluateAll(){
		
//		System.out.println("Before evaluation: " + (System.currentTimeMillis() - CPTAPI.getTime()));
//		CPTAPI.setTime(System.currentTimeMillis());
		
		this.counter = new SynchronizedCounter();
		
		for (int i = 0; i < this.population.size(); i++) {
			
			Runnable worker = new FluidSteadyState(CPTAPI.getLabels(), 
				population.get(i).getGraph(), 
				CPTAPI.getEstimators(), 
				CPTAPI.getCollectors(), 
				CPTAPI.getOptionMap(),
				null,
				population.get(i).getNode(),
				this.counter);

//			Runnable worker = new Dummy(CPTAPI.getLabels(), 
//					population.get(i).getNode(),
//					this.counter);
				this.executor.execute(worker);
		}
		
		
		//barrier 
		while(this.counter.value() < population.size());
		
//		System.out.println("After evaluation: " + (System.currentTimeMillis() - CPTAPI.getTime()));
//		CPTAPI.setTime(System.currentTimeMillis());
		
		for (int i = 0; i < this.population.size(); i++) {
			
			population.get(i).getNode().updateFitness();
			if(this.myNode.getFittestNode().getMyMap().size() == 0){
				this.myNode.setFittestNode(population.get(i).getNode());
			} else if (population.get(i).getNode().getFitness() < this.myNode.getFittestNode().getFitness()){
				this.myNode.setFittestNode(population.get(i).getNode());
			}
			
			this.myMonitor.worked(1);
		}
		
		
	}
	
	/**
	 * populate a HashMap with the search domain
	 * @return
	 */
	public HashMap<String,Double> getParameters(){
		
		HashMap<String,Double> parameters = new HashMap<String,Double>();
		
		String[] keys = CPTAPI.getPopulationControls().getKeys();
		
		Double min, max, value;
		String type;
		
		for(int i = 0; i < keys.length; i++){
			min = Double.parseDouble(CPTAPI.getPopulationControls().getValue(keys[i], Config.LABMIN));
			max = Double.parseDouble(CPTAPI.getPopulationControls().getValue(keys[i], Config.LABMAX));
			type = CPTAPI.getPopulationControls().getType(keys[i], Config.LABMIN);
			value = Utils.returnRandomInRange(min, max, type);
			parameters.put(keys[i], value);
		}
		
		return parameters;
	}
	
	/**
	 * return HashMap representing the velocity of a particle
	 * @param parameters
	 * @return
	 */
	public HashMap<String,Double> getVelocity(HashMap<String,Double> parameters){
		
		HashMap<String,Double> velocity = new HashMap<String,Double>();
		
		String[] keys = CPTAPI.getPopulationControls().getKeys();
		
		Double min, max, value, pvalue;
		String type;
		
		for(int i = 0; i < keys.length; i++){
			pvalue = parameters.get(keys[i]);
			min = Double.parseDouble(CPTAPI.getPopulationControls().getValue(keys[i], Config.LABMIN));
			max = Double.parseDouble(CPTAPI.getPopulationControls().getValue(keys[i], Config.LABMAX));
			type = CPTAPI.getPopulationControls().getType(keys[i], Config.LABMIN);
			value = (Utils.returnRandomInRange(min, max, type) - pvalue) / 2;
			velocity.put(keys[i], value);
		}
		
		
		return velocity;
		
	}

	/**
	 * create new velocity for particle, move particle
	 * @param modelConfiguration
	 */
	public void move(ModelConfiguration modelConfiguration){
		HashMap<String, Double> originalVelocity, 
		localBestPosition, 
		globalBestPosition, 
		newVelocity,
		newPosition;
		
		originalVelocity = Utils.copyHashMap(modelConfiguration.getNode().getVelocity());
		globalBestPosition = this.myNode.getFittestNode().getMyMap();
		
		localBestPosition = Utils.copyHashMap(modelConfiguration.getLocalBest().getMyMap());
		newVelocity = new HashMap<String, Double>();
		newPosition = new HashMap<String, Double>();
		
		Double orig, local, global, totalWeight;
		
		orig = this.myNode.getMyMap().get(Config.LABORG);
		local = this.myNode.getMyMap().get(Config.LABLOC);
		global = this.myNode.getMyMap().get(Config.LABGLO);
		
		totalWeight = orig + local + global;
		
		orig = orig/totalWeight;
		local = local/totalWeight;
		global = global/totalWeight;
		
		int size = modelConfiguration.getNode().getMyMap().keySet().size();
		String[] keys = modelConfiguration.getNode().getMyMap().keySet().toArray(new String[size]);
		
		Double o,l,g,p,v,np,step;
		
//		f1 = modelConfiguration.getNode().getFitness();
//		f2 = this.fittestNode.getFitness();
		
		
		//I thought perhaps the step value could have a positive effect on the speed of the algorithm.
		//with a step size proportional to the difference in fitness. That the least fit candidates could
		//benefit with a larger velocity.
		//This did not have a positive effect on the algorithm at all. The particles would go outside of the range
		//too often, and end up converging in one of the 'corners' of the search space.
//		if(f2 == 0){
//			step = 1.0;
//		} else {
//			step = f1/f2;
//			//step = 1.0;
//		}
		
		step = 1.0;
		
		for(int i = 0; i < keys.length; i ++){
			
			p = modelConfiguration.getNode().getMyMap().get(keys[i]);
			
			o = (originalVelocity.get(keys[i])*orig);
			
			l = (localBestPosition.get(keys[i]) - p)*local;
			
			g = (globalBestPosition.get(keys[i]) - p)*global;
			
			v = Math.rint((o+l+g) * step);
			
			newVelocity.put(keys[i], v);
			
			np = p+v;
			
			//reflection
			if(np < 0){
				np = -np;
			}
			
			//sticky
			if(np > Double.parseDouble(CPTAPI.getPopulationControls().getValue(keys[i], Config.LABMAX))){
				np = Double.parseDouble(CPTAPI.getPopulationControls().getValue(keys[i], Config.LABMAX));
			}

			newPosition.put(keys[i], np);
		}
		
		//Can not search on a model with no population at all...
		boolean totalNotZero = true;
		
		for(int i = 0; i < keys.length; i++){
			totalNotZero = totalNotZero && (newPosition.get(keys[i]) != 0);
		}
		
		if(!totalNotZero){
			newPosition = modelConfiguration.getNode().getMyMap();
//			for(int i = 0; i < keys.length; i++){
//				newVelocity.put(keys[i], 0.0);
//			}
		}
		
		modelConfiguration.setParameters(Utils.copyHashMap(newPosition),
				Utils.copyHashMap(newVelocity), 
				myNode);
		
		
	}
	

}
