package uk.ac.ed.inf.pepa.jhydra.driver.generator;

import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import uk.ac.ed.inf.pepa.jhydra.matrix.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import java.util.BitSet;


public class Generator {

	private PetriNet myPN;
	private Matrix myMatrix;
	private Integer tangible;
	private String sourceCondition, targetCondition;
	private BitSet sourceStates, targetStates;
	private int sources, targets;

	
	public Generator(PetriNet net, Matrix mat, String sC, String tC, Hashtable<String,Double> c){
		myPN = net;
		myMatrix = mat;
		tangible = new Integer(0);
		sourceCondition = sC;
		targetCondition = tC;
//		System.out.println(sourceCondition);
//		System.out.println(targetCondition);
		myPN.setConstants(c);
		sources = 0;
		targets = 0;
	}
	
	public void generate(){
		System.out.println("\n\nGenerating underlying state space...");

		Vector<Integer> sourceMarkings = new Vector<Integer>(0,1);
		Vector<Integer> targetMarkings = new Vector<Integer>(0,1);
		
		Integer stateNumber = new Integer(0);
		int row = 0;
		
		Hashtable<Marking,Integer> explored = new Hashtable<Marking,Integer>();
		LinkedList<Marking> pending = new LinkedList<Marking>();
	
		Marking initialMarking = myPN.getCurrentMarking();
		
		explored.put(initialMarking,stateNumber);
		pending.addLast(initialMarking);
		
		Marking currentMarking;
		
		Integer currStateNo;
		
		MatrixRow matrixRow;
		MatrixElement child;
		
		while(pending.size()>0){
			
			currentMarking = pending.removeFirst();
			
			matrixRow = new MatrixRow(row);

//			System.out.print("[" + explored.get(currentMarking) + "] ");

			if(myPN.conditionHolds(sourceCondition, currentMarking)){
				++sources;
				currStateNo = explored.get(currentMarking);
				sourceMarkings.add(currStateNo);
			}
				
			if(myPN.conditionHolds(targetCondition, currentMarking)){
				++targets;
				currStateNo = explored.get(currentMarking);
				targetMarkings.add(currStateNo);
			}
			
				
			for(int i=0;i<myPN.getNumberOfTimedTransitions();i++){
				//for every successor of current marking
				TimedTransition t = myPN.getTimedTransition(i);
				
				if(myPN.isEnabled(currentMarking,t)){
					Marking nextMarking = myPN.fire(currentMarking,t);

				
					//  if not in explored
					if(!(explored.containsKey(nextMarking))){				
						
						++stateNumber;
						//    add to tail of pending
						pending.addLast(nextMarking);
						//    add to explored						
						explored.put(nextMarking,stateNumber);
					
						Double rate = new Double(myPN.conditionValue(t.getRate(), currentMarking));
						
//						child = new MatrixElement(stateNumber.intValue(),t.getRate());
						child = new MatrixElement(stateNumber.intValue(),rate.doubleValue());
						matrixRow.add(child);
//						myMatrix.addElement(row, stateNumber, t.getRate());
						
						if((stateNumber+1)%10000==0)
							System.out.println(stateNumber+1 + " states generated (" + pending.size() + " unexplored) ...");
					} else{
						Integer col = explored.get(nextMarking);
	
						Double rate = new Double(myPN.conditionValue(t.getRate(), currentMarking));					
						
//						child = new MatrixElement(col.intValue(),t.getRate());
						child = new MatrixElement(col.intValue(), rate.doubleValue());
						matrixRow.add(child);
//						myMatrix.addElement(row,col.longValue(),t.getRate());
					}
				}
			}
			
			myMatrix.addRow(matrixRow, row);
			row++;			
		}
		
		tangible = row;
//		tangible = stateNumber + 1;
		
		System.out.println("\nFinished generating underlying state space of " + tangible + " states...");

//		myMatrix.print();
		myMatrix.columnify();
		
		setupBitSets(sourceMarkings, targetMarkings);
		
		System.out.println("\nThere were:");
		System.out.println("    " + sources + " source states");
		System.out.println("    " + targets + " target states");

	}
	
	
	public BitSet getSourceStates() { return sourceStates; }
	
	public BitSet getTargetStates() { return targetStates; }
	
	
	private void setupBitSets(Vector<Integer> sources, Vector<Integer> targets){
		sourceStates = new BitSet(tangible.intValue());
		targetStates = new BitSet(tangible.intValue());
		sourceStates.clear();
		targetStates.clear();
		
		for(int i=0; i<sources.size();i++){
			sourceStates.set(sources.get(i).intValue());
		}

		for(int i=0; i<targets.size();i++){
			targetStates.set(targets.get(i).intValue());
		}
	
	}
	
}
