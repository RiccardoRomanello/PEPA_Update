package uk.ac.ed.inf.pepa.jhydra.driver;

import uk.ac.ed.inf.pepa.jhydra.driver.parser.Parser;
import uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver.PassageTimeResults;
import uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver.PassageTimeSolver;
import uk.ac.ed.inf.pepa.jhydra.driver.steadystatesolver.SteadyStateSolver;
import uk.ac.ed.inf.pepa.jhydra.driver.generator.Generator;
import uk.ac.ed.inf.pepa.jhydra.matrix.Matrix;
import uk.ac.ed.inf.pepa.jhydra.petrinet.PetriNet;
import java.util.Hashtable;


public class Driver {

	private PetriNet myPN;
	private Matrix myMatrix;
	private double[] alphaVector;
	private Hashtable<String,Double> constants;
	
	private String modelFileName;
	
	private Parser myParser;
	private Generator myGenerator;
	private SteadyStateSolver mySteadyStateSolver;
	private PassageTimeSolver myPassageTimeSolver;

	
	public Driver(String model){
		
		modelFileName = model;
		
		myPN = new PetriNet();
		myMatrix = new Matrix();
		
		constants = new Hashtable<String,Double>();
		
	    try {	
	    	myParser = new Parser(new java.io.FileInputStream(modelFileName),myPN,constants);
	    } catch (java.io.FileNotFoundException e) {
	    	System.out.println("Parser:  File " + modelFileName + " not found.");
	    	System.exit(-1);
	    }
		

		
	}
	
	public void go(){
		System.out.println("\nStarting analysis of " + modelFileName + "...");
	
		// parses the input file and generates a Petri net representation of the model from which the underlying state-space
		// is later generated.
		myParser.parse();

		// Generator generates the underlying state space
		// takes in: the Petri net, an uninstantiated matrix, the sourse and target conditions from the model and any constants declared
		myGenerator = new Generator(myPN, myMatrix, myParser.getSourceCondition(), myParser.getTargetCondition(), myParser.getConstants());

		// results in: the Q matrix, a bit bector indicating source states, a bit vector indicating target states 
		myGenerator.generate();	
		
		
		//Solver solves for the steady state solution of the EMC (not the CTMC!) 
		mySteadyStateSolver = new SteadyStateSolver(myMatrix);
		
		myMatrix.print();
		
		//takes in: Q, a bit vector indicating source states
		//returns: the weighting vector alpha from the EMC
		alphaVector = mySteadyStateSolver.solve(myGenerator.getSourceStates());

		
		/*
		for(int i=0; i<myMatrix.getTangible(); i++){
			System.out.println("["+i+"]  " + steadyStateVector[i]);
		}
		*/

		
		//PassageTimeSolver solves for the passage time cdf/pdf
		//takes in: Q matrix (which it uniformises), weighting vector alpha, a bit vector indicating target states, the t values
		myPassageTimeSolver = new PassageTimeSolver(myMatrix, alphaVector, myGenerator.getTargetStates(), 
		                             myParser.getTStart(), myParser.getTStop(), myParser.getTStep());

		
		//perform the passage time calculations using all of the above inputs
		//prints the cdf and pdf to screen and files
		// The results name doesn't matter as it is only stored in the results.
		PassageTimeResults ptResults = myPassageTimeSolver.uniformise(modelFileName, "cdf", "pdf");
		ptResults.printOutResults(modelFileName);
		
		System.out.println("\nFinished analysis of " + modelFileName + "...");
	}
}
