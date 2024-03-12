package uk.ac.ed.inf.pepa.ctmc.solution;

import java.io.IOException;
import java.util.BitSet;


import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.filters.ActionsFilter;
import uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver.PassageTimeResults;
import uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver.PassageTimeSolver;
import uk.ac.ed.inf.pepa.jhydra.driver.steadystatesolver.SteadyStateSolver;
import uk.ac.ed.inf.pepa.jhydra.matrix.Matrix;
import uk.ac.ed.inf.pepa.jhydra.matrix.MatrixElement;
import uk.ac.ed.inf.pepa.jhydra.matrix.MatrixRow;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class PepaJHydra {

	public PepaJHydra(String fileName, ModelNode modelNode){
		this.modelNode     = modelNode ;
		this.modelFileName = fileName ;
	}
	
	private String modelFileName;
	private ModelNode modelNode ;
	private double startTime = 0.1;
	private double timeStep  = 0.1;
	private double stopTime  = 10.0;
	
	public void setStartTime (double startTime){
		this.startTime = startTime ;
	}
	public void setTimeStep (double timeStep){
		this.timeStep = timeStep ;
	}
	public void setStopTime (double stopTime){
		this.stopTime = stopTime ;
	}
	
	private String[] sourceActions ;
	private String[] targetActions ;
	public void setSourceActions (String[] actions){ 
		this.sourceActions = actions ;
	}
	public void setTargetActions (String[] actions){
		this.targetActions = actions;
	}
	
	public PassageTimeResults performPassageTime(String cdfName, String pdfName) 
	            throws IOException, DerivationException{
		// get abstract syntax tree of PEPA
		// ModelNode modelNode = (ModelNode) PepaTools.parse(PepaTools.readText(fileName));
		// get state space
		IStateSpace stateSpace = PepaTools.derive(new OptionMap(), modelNode, null, null);
		// print transitions
		/*
		for (int i = 0; i < stateSpace.size(); i++) {
			String source = getProcessString(stateSpace, i);
			int[] targetStateIndices = stateSpace.getOutgoingStateIndices(i);
			for (int target : targetStateIndices) {
				StringBuffer message = new StringBuffer();
				message.append(source);
				message.append(" --> ");
				message.append(getProcessString(stateSpace, target));
				message.append(" via ");
				for (String action : stateSpace.getAction(i, target)) 
					message.append(action + " ");
				System.out.println(message.toString());
			}
		}
		System.out.println ("The size of the state space is: " + stateSpace.size());
		*/
		
		/* Now we start the jHydra code */
		Matrix myMatrix = new Matrix ();
		
		
		/* For now make up the source states and the target states */
		BitSet sourceStates = new BitSet (stateSpace.size ()) ;
		sourceStates.clear ();
		// sourceStates.set(0);
		BitSet targetStates = new BitSet (stateSpace.size ());
		targetStates.clear();
		//targetStates.set(stateSpace.size() - 1);
		
		// Set up the action based filter
		ActionsFilter sourceActFilter = new ActionsFilter(this.sourceActions, true);
		IFilterRunner srcFilterRunner = sourceActFilter.getRunner(stateSpace);
		ActionsFilter targetActFilter = new ActionsFilter(this.targetActions, true);
		IFilterRunner tgtFilterRunner = targetActFilter.getRunner(stateSpace);
		
		/* Now set up the matrix*/
		for (int i = 0; i < stateSpace.size(); i++){
			int[] targetStateIndices = stateSpace.getOutgoingStateIndices(i);
			MatrixRow newRow = new MatrixRow (i);
			
			/*
			 * NOTE: the else if here, rather than just a second
			 * if means that a state cannot be both a source state
			 * and a target state, this makes sense but also may
			 * cause the odd bit of confusion for some people.
			 */
			if(srcFilterRunner.select(i)){
				sourceStates.set(i);
			} else if (tgtFilterRunner.select(i)){
				targetStates.set(i);
			}
			
			
			for (int target : targetStateIndices){
				double rate = stateSpace.getRate(i, target);
				MatrixElement matrixElement = new MatrixElement (target, rate);
				newRow.add(matrixElement);
			}
			myMatrix.addRow(newRow, i);
		}
		myMatrix.print ();
		myMatrix.columnify();
		
		
		//Solver solves for the steady state solution of the EMC (not the CTMC!) 
		SteadyStateSolver mySteadyStateSolver = new SteadyStateSolver(myMatrix);
		
		
		
		
		//takes in: Q, a bit vector indicating source states
		//returns: the weighting vector alpha from the EMC
		double[] alphaVector = mySteadyStateSolver.solve(sourceStates);

		
		
		/*
		for(int i=0; i<myMatrix.getTangible(); i++){
			System.out.println("["+i+"]  " + steadyStateVector[i]);
		}
		*/

		
		//PassageTimeSolver solves for the passage time cdf/pdf
		//takes in: Q matrix (which it uniformises), weighting vector alpha, a bit vector indicating target states, the t values
		PassageTimeSolver myPassageTimeSolver = 
			new PassageTimeSolver(myMatrix, alphaVector, targetStates, 
		                          startTime, stopTime, timeStep);

		
		//perform the passage time calculations using all of the above inputs
		//prints the cdf and pdf to screen and files
		PassageTimeResults ptResults = myPassageTimeSolver.uniformise(modelFileName, cdfName, pdfName);
		
		// System.out.println("\nFinished analysis of " + modelFileName + "...");
		return ptResults;
	}
	
	/**
	 * Prepares a string representation of a process
	 * @param stateSpace
	 * @param stateIndex
	 * @return
	 */
	private static String getProcessString(IStateSpace stateSpace, int stateIndex) {
		StringBuffer buf = new StringBuffer();
		String separator = " | ";
		for (int i = 0; i < stateSpace.getMaximumNumberOfSequentialComponents(); i++) {
			buf.append(stateSpace.getLabel(stateIndex, i));
			if (i < stateSpace.getMaximumNumberOfSequentialComponents() - 1)
				buf.append(separator);
		}
		return buf.toString();
	}

}
	
