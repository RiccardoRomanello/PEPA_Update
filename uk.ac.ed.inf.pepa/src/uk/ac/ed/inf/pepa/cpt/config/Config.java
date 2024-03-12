package uk.ac.ed.inf.pepa.cpt.config;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.cpt.Utils;
import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.cpt.config.control.EListControl;
import uk.ac.ed.inf.pepa.cpt.config.control.ListControl;
import uk.ac.ed.inf.pepa.cpt.config.control.ParameterControl;
import uk.ac.ed.inf.pepa.cpt.config.control.PerformanceControl;
import uk.ac.ed.inf.pepa.cpt.config.control.PopulationControl;
import uk.ac.ed.inf.pepa.cpt.config.control.RListControl;
import uk.ac.ed.inf.pepa.cpt.config.control.performanceControl.AverageResponseTimeControl;
import uk.ac.ed.inf.pepa.cpt.config.control.performanceControl.CapacityUtilisationControl;
import uk.ac.ed.inf.pepa.cpt.config.control.performanceControl.PopulationLevelControl;
import uk.ac.ed.inf.pepa.cpt.config.control.performanceControl.ThroughputControl;
import uk.ac.ed.inf.pepa.cpt.config.control.populationControl.ComponentControl;
import uk.ac.ed.inf.pepa.cpt.config.control.populationControl.PSOControl;
import uk.ac.ed.inf.pepa.cpt.config.control.populationControl.RateControl;
import uk.ac.ed.inf.pepa.cpt.config.control.populationControl.TargetControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.ActionList;
import uk.ac.ed.inf.pepa.cpt.config.lists.ComponentList;
import uk.ac.ed.inf.pepa.cpt.config.lists.DomainChoiceList;
import uk.ac.ed.inf.pepa.cpt.config.lists.EvaluatorChoiceList;
import uk.ac.ed.inf.pepa.cpt.config.lists.PSOList;
import uk.ac.ed.inf.pepa.cpt.config.lists.ProcessList;
import uk.ac.ed.inf.pepa.cpt.config.lists.RateList;
import uk.ac.ed.inf.pepa.cpt.config.lists.SearchChoiceList;
import uk.ac.ed.inf.pepa.cpt.config.lists.TargetList;
import uk.ac.ed.inf.pepa.cpt.config.parameters.CostFunctionParameters;
import uk.ac.ed.inf.pepa.cpt.config.parameters.HillClimbingParameters;
import uk.ac.ed.inf.pepa.cpt.config.parameters.OptionMapParameters;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer.Node;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer.PACS;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;
import uk.ac.ed.inf.pepa.parsing.ModelNode;



/**
 * Configuration for a capacity planning job. 
 * Also contains hard written strings used in Capacity Planning.
 * 
 * @author twig
 *
 */
public class Config implements ConfigCallBack{
	
	//List labels
	public static final String SEARCH = "Search type choice";
	public static final String EVALUATOR = "Evaluator choice";
	public static final String DOMAIN = "Search domain";
	
	//Input Types
	public static final String NATURAL = "NATURAL"; // >= 1
	public static final String INTEGER = "INTEGER"; // >= 0
	public static final String PERCENT = "PERCENT"; // 0.0 <= x <= 1.0
	public static final String DOUBLE  = "DOUBLE "; // 0.0 <= x 

	//Evaluator related hard coded strings
	public static String EVALARPT = "Average response time";
	public static String EVALTHRO = "Throughput";
	public static String EVALUTIL = "Capacity Utilisation";
	public static String EVALPOPU = "Population Level";
	
	//Domain related
	public static String DOMRAR = "Search on rates";
	public static String DOMCOM = "Search on components";
	public static String DOMBOT = "Search on both";
	
	//Search type related hard coded strings
	public static String SEARCHSINGLE = "Particle Swarm Optimisation";
	public static String SEARCHDRIVEN = "Driven Particle Swarm Optimisation";
	public static String SEARCHBRUTE = "Brute-force search";
	
	//lab parameter related hard coded strings
	public static String LABEXP ="Experiments";
	public static String LABGEN ="Generations";
	
	//lab parameter related hard coded strings - metaheuristic
	public static String LABMUT ="Mutation rate";
	public static String LABPOP ="Population count";
	public static String LABLOC ="Personal best weight";
	public static String LABGLO ="Global best weight";
	public static String LABORG ="Original velocity weight";
	
	//lab parameter (ranges) related hard coded strings - metaheuristic
	public static String LABMIN ="Minimum";
	public static String LABMAX ="Maximum";
	public static String LABRAN ="Range";
	public static String LABWEI ="Weight";
	public static String LABSTE ="Step";
	public static String LABTAR ="Target";
	
	//fitnessfunction population vs performance vs .... (maybe ODE fitness later?)
	public static String FITRES = "Population weight";
	public static String FITPER = "Performance weight";
	public static String FITPEN = "Performance penalty";
	
	//models
	private ModelNode node = null;
	private IParametricDerivationGraph graph = null;
	
	//configuration objects
	/**
	 * These are the configuration objects required in order to
	 * run a capacity search.
	 */
	//choose type of performance evaluation
	private EvaluatorChoiceList evaluatorChoiceList;
	//choose type of search
	private SearchChoiceList searchChoiceList;
	//choose search domain
	private DomainChoiceList domainChoiceList;
	//set up HillClimbing parameters
	private HillClimbingParameters hillClimbingParameters;
	//set up fitness balance
	private CostFunctionParameters costFunctionParameters;
	//set up pso range list
	private PSOList psoList;
	//set up component population ranges
	private ComponentList componentList;
	//set up rate population ranges
	private RateList rateList;
	//choose actions for performance evaluation
	private ActionList actionList;
	//choose processes for performance evaluation
	private ProcessList processList;
	//choose targets for action/process
	private TargetList targetList;
	
	//for ODE solutions
	private IPointEstimator[] estimators;
	private IStatisticsCollector[] collectors;
	private OptionMapParameters optionMapParameters;
	
	//for the filename
	private String fileName;
	
	/*
	 * Controllers
	 */
	
	//lists
	public ListControl evaluationController;
	public ListControl searchController;
	public ListControl domainController;
	
	//parameters
	public ParameterControl hillController;
	public ParameterControl costFunctionController;
	
	//pso range related
	public PSOControl psoRangeController;
	
	//population related
	public PopulationControl rateAndComponentRangeAndWeightController;
	
	//target related 
	public TargetControl targetControl;
	
	//performance related
	public PerformanceControl actionAndProcessSelectionController;
	
	//option map related
	public ParameterControl optionMapController;
	public long startTime;
	private String folderName;
	public static final String EXTENSION = "csv";
	
	private ArrayList<Control> controls;
	public int resultSize;
	
	public Node results;
	public PACS pacs;
	
	/**
	 * Configuration object, required for a cpt run.
	 * @param node
	 */
	public Config(ModelNode node){
		this.node = node;
		this.graph = Utils.getDevGraphFromAST(node);
		
		this.evaluatorChoiceList = new EvaluatorChoiceList();
		this.searchChoiceList = new SearchChoiceList();
		this.domainChoiceList = new DomainChoiceList();
		this.hillClimbingParameters = new HillClimbingParameters();
		this.psoList = new PSOList();
		this.costFunctionParameters = new CostFunctionParameters();
		this.componentList = new ComponentList(this.graph);
		this.rateList = new RateList(this.node);
		this.actionList = new ActionList(this.graph);
		this.processList = new ProcessList(this.graph);
		this.targetList = new TargetList();
		this.optionMapParameters = new OptionMapParameters();
		
		/*
		 * set up controllers - so only use controllers to change underlying data
		 */
		
		this.controls = new ArrayList<Control>();
		
		this.evaluationController = new EListControl(this.evaluatorChoiceList, this);
		
		this.controls.add(evaluationController);
		
		this.searchController = new ListControl(this.searchChoiceList);
		
		this.controls.add(searchController);
		
		this.domainController = new RListControl(this.domainChoiceList, this);
		
		this.controls.add(domainController);
		
		this.hillController = new ParameterControl(this.hillClimbingParameters, "Hill Climbing");
		
		this.controls.add(hillController);
		
		this.optionMapController = new ParameterControl(this.optionMapParameters, "ODE parameters");
		
		this.controls.add(optionMapController);
		
		this.costFunctionController = new ParameterControl(this.costFunctionParameters, "Cost function parameters");
		
		this.controls.add(costFunctionController);
		
		this.psoRangeController = new PSOControl(psoList, "Particle Swarm Optimisation");
		
		this.controls.add(psoRangeController);
		
		this.actionAndProcessSelectionController = new ThroughputControl(actionList, this.graph);
		
		this.rateAndComponentRangeAndWeightController = new ComponentControl(this.componentList);
		
		this.controls.add(rateAndComponentRangeAndWeightController);
		
		this.targetControl = new TargetControl(targetList, "Targets");
		
		this.controls.add(targetControl);
		
		this.resultSize = 0;
		
		this.results = new Node("Results",null);
		
		this.pacs = new PACS();
		
	}

	public void setOptionMap(OptionMap optionMap) {
		this.optionMapParameters.setOptionMap(optionMap);
	}

	public OptionMap getOptionMap() {
		return this.optionMapParameters.getOptionMap();
	}

	public ModelNode getNode() {
		return node;
	}
	
	public IParametricDerivationGraph getGraph(){
		return Utils.getDevGraphFromAST(node);
	}

	@Override
	public void initialiseAndUpdateActionsAndProcesses() {
		
		this.actionList.clearSelection();
		this.processList.clearSelection();
		
		if(this.evaluatorChoiceList.getValue().equals(Config.EVALARPT)){
			
			this.actionAndProcessSelectionController = new AverageResponseTimeControl(processList, this.graph);
			
		} else if (this.evaluatorChoiceList.getValue().equals(Config.EVALTHRO)){
			
			this.actionAndProcessSelectionController = new ThroughputControl(actionList, this.graph);
			
		} else if (this.evaluatorChoiceList.getValue().equals(Config.EVALUTIL)){
			
			this.actionAndProcessSelectionController = new CapacityUtilisationControl(processList, this.graph);
			
		} else {
			
			this.actionAndProcessSelectionController = new PopulationLevelControl(processList);
		}
		
		this.controls.add(actionAndProcessSelectionController);
	}

	@Override
	public void initialiseAndUpdateComponentsAndRates() {
		
		
		if(domainChoiceList.getValue().equals(Config.DOMCOM)){
			this.rateAndComponentRangeAndWeightController = new ComponentControl(this.componentList);
		}
		if(domainChoiceList.getValue().equals(Config.DOMRAR)){
			this.rateAndComponentRangeAndWeightController = new RateControl(this.rateList);
		}
		if(domainChoiceList.getValue().equals(Config.DOMBOT)){
			this.rateAndComponentRangeAndWeightController = new ComponentControl(this.componentList);
			//and then I guess make a call to change this over to DOMRAR
		}
		
	}

	public IPointEstimator[] getEstimators() {
		this.estimators = this.actionAndProcessSelectionController.getEstimators();
		return this.estimators;
	}

	public IStatisticsCollector[] getCollectors() {
		if(this.estimators != null){
			this.collectors = this.actionAndProcessSelectionController.getCollectors(this.estimators);
			return this.collectors;
		} else {
			return null;
		}
	}
	
	public String[] getLabels() {
		return this.actionAndProcessSelectionController.getLabels();
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		
	}
	
	public String getFileName() {
		return this.fileName;
	}

	public void setFolderName(String osString) {
		this.folderName = osString;
		
	}

	public String getFolderName() {
		return this.folderName;
	}
	
	public String[] toPrint(){
		String[] output = new String[this.controls.size()];
		int i = 0;
		
		for(Control c : this.controls){
			output[i] = c.toPrint();
			i++;
		}
		
		return output;
	}

}
