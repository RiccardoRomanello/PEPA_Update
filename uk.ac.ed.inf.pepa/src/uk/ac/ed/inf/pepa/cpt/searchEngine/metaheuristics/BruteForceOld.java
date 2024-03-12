package uk.ac.ed.inf.pepa.cpt.searchEngine.metaheuristics;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//
//import uk.ac.ed.inf.pepa.cpt.CPTAPI;
//import uk.ac.ed.inf.pepa.cpt.Utils;
//import uk.ac.ed.inf.pepa.cpt.config.Config;
//import uk.ac.ed.inf.pepa.cpt.ode.FluidSteadyState;
//import uk.ac.ed.inf.pepa.cpt.searchEngine.candidates.ModelConfiguration;
//import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.CandidateNode;
//import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.HCNode;

public class BruteForceOld { //implements MetaHeuristics {
	
//	public class SynchronizedCounter implements CounterCallBack {
//	    
//		private int c = 0;
//		
//		public SynchronizedCounter(){
//		}
//
//	    public synchronized void increment() {
//	        c++;
//	    }
//
//	    public synchronized int value() {
//	        return c;
//	    }
//	}
//	
//	private HCNode myNode;
//	private IProgressMonitor myMonitor;
//	private ArrayList<ModelConfiguration> population;
//	
//	
//	
//	private int threads;
//	private ExecutorService executor;
//	private SynchronizedCounter counter;
//	private int totalCounter;
//
//	public BruteForce(HashMap<String, Double> parameters,
//			CandidateNode resultNode, 
//			IProgressMonitor monitor) {
//		
//		this.myNode = new HCNode("ParticleSwarmOptimisation",
//				resultNode);
//		resultNode.registerChild(this.myNode);
//		
//		this.myMonitor = monitor;
//		
//		this.population = new ArrayList<ModelConfiguration>();
//		
//		this.threads = 10;
//		
//		this.executor = Executors.newFixedThreadPool(threads);
//		
//		startAlgorithm();
//		
//		this.executor.shutdown();
//		
//		this.myNode.updateFinishTime();
//		
//		
//	}
//	
//	/**
//	 * populate a HashMap with the search domain
//	 * @return
//	 */
//	public HashMap<String,Double> getParameters(Double d){
//		
//		HashMap<String,Double> parameters = new HashMap<String,Double>();
//		
//		String[] keys = CPTAPI.getPopulationControls().getKeys();
//		
//		Double a,b,c;
//		
//		a = (d % 5);
//		b = Math.floor(((d % 25)/5));
//		c = Math.floor(((d % 125)/25));
//		
//		for(int i = 0; i < keys.length; i++){
//			if(keys[i].equals("Database")){
//				CPTAPI.getPopulationControls().setValue("Database", Config.LABMIN, "26.0");
//				CPTAPI.getPopulationControls().setValue("Database", Config.LABMAX, "26.0");
//				parameters.put(keys[i], 26.0);
//			}
//			if(keys[i].equals("ValCur")){
//				CPTAPI.getPopulationControls().setValue("ValCur", Config.LABMIN, "15.0");
//				CPTAPI.getPopulationControls().setValue("ValCur", Config.LABMAX, "25.0");
//				parameters.put(keys[i], 15.0);
//			}
//			if(keys[i].equals("ValUni")){
//				CPTAPI.getPopulationControls().setValue("ValUni", Config.LABMIN, "35.0");
//				CPTAPI.getPopulationControls().setValue("ValUni", Config.LABMAX, "45.0");
//				parameters.put(keys[i], 35.0);
//			}
//			if(keys[i].equals("Portal")){
//				CPTAPI.getPopulationControls().setValue("Portal", Config.LABMIN, "75.0");
//				CPTAPI.getPopulationControls().setValue("Portal", Config.LABMAX, "85.0");
//				parameters.put(keys[i], 75.0);
//			}
//			if(keys[i].equals("TTPD_1")){
//				CPTAPI.getPopulationControls().setValue("TTPD_1", Config.LABMIN, "26.0");
//				CPTAPI.getPopulationControls().setValue("TTPD_1", Config.LABMAX, "30.0");
//				parameters.put(keys[i], 26.0 + a);
//			}
//			if(keys[i].equals("PS_1")){
//				CPTAPI.getPopulationControls().setValue("PS_1", Config.LABMIN, "49.0");
//				CPTAPI.getPopulationControls().setValue("PS_1", Config.LABMAX, "53.0");
//				parameters.put(keys[i], 49.0 + b);
//			}
//			if(keys[i].equals("StdThinkTag")){
//				CPTAPI.getPopulationControls().setValue("StdThinkTag", Config.LABMIN, "600.0");
//				CPTAPI.getPopulationControls().setValue("StdThinkTag", Config.LABMAX, "600.0");
//				parameters.put(keys[i], 600.0);
//			}
//			if(keys[i].equals("Logger")){
//				CPTAPI.getPopulationControls().setValue("Logger", Config.LABMIN, "19.0");
//				CPTAPI.getPopulationControls().setValue("Logger", Config.LABMAX, "23.0");
//				parameters.put(keys[i], 19.0 + c);
//			}
//		}
//		
//		return parameters;
//	}
//	
//	public void startAlgorithm(){
//		
//		//set up population
//		setUpPopulation();
//		
//		for(int i = 0; i < 1000; i++){
//			
//			for(int j = 0; j < 125; j++){
//				move(this.population.get(j),(i+1)*(j+1));
//			}
//			
//			evaluateAll();
//			
//		}
//	
//	}
//
//	/**
//	 * create new velocity for particle, move particle
//	 * @param modelConfiguration
//	 */
//	public void move(ModelConfiguration modelConfiguration, int q){
//
//		
//		modelConfiguration.setParameters(nudge(q,modelConfiguration.getNode().getMyMap()),
//				null, 
//				myNode);
//		
//		
//	}
//	
//	public HashMap<String,Double> nudge(int q, HashMap<String,Double> map){
//		
//		Double a,b,c;
//		
//		HashMap<String,Double> temp = Utils.copyHashMap(map);
//		
//		a = Math.floor((q % 10));
//		b = Math.floor(((q % 100)/10));
//		c = Math.floor(((q % 1000)/100));
//		
//		temp.put("ValCur",15.0 + a);
//		temp.put("ValUni",35.0 + b);
//		temp.put("Portal",75.0 + c);
//		
//		return Utils.copyHashMap(temp);
//		
//	}
//	
//	/**
//	 * Create the particle population
//	 */
//	public void setUpPopulation(){
//		for(int i=0; i<125; i++){
//			HashMap<String,Double> temp = getParameters((double) i);
//			this.population.add(new ModelConfiguration(Utils.copyHashMap(temp), 
//					temp,
//					myMonitor, 
//					myNode));
//		}
//	}
//	
//	
//	/**
//	 * Cycle through all particles doing an ODE evaluation
//	 */
//	public void evaluateAll(){
//		
//		this.counter = new SynchronizedCounter();
//		
//		for (int i = 0; i < this.population.size(); i++) {
//			
//			Runnable worker = new FluidSteadyState(CPTAPI.getLabels(), 
//				population.get(i).getGraph(), 
//				CPTAPI.getEstimators(), 
//				CPTAPI.getCollectors(), 
//				CPTAPI.getOptionMap(),
//				null,
//				population.get(i).getNode(),
//				this.counter);
//			this.executor.execute(worker);
//		}
//		
//		
//		//barrier 
//		while(this.counter.value() < population.size());
//		
//		for (int i = 0; i < this.population.size(); i++) {
//				population.get(i).getNode().updateFitness();
//				if(population.get(i).getNode().getFitness() < this.myNode.getFittestNode().getFitness()){
//					this.myNode.setFittestNode(population.get(i).getNode());
//				}
//		}
//		
//		
//	}
	
	

}
