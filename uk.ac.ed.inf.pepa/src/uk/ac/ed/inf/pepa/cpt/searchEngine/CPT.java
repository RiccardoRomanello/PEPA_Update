package uk.ac.ed.inf.pepa.cpt.searchEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.PriorityQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.searchEngine.candidates.HillClimbingLab;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.ResultNode;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer.Leaf;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer.Node;


public class CPT {
	
	private HillClimbingLab root;
	private IProgressMonitor monitor;
	private PriorityQueue<ResultNode> resultsQueue;
	private ArrayList<String> outputStrings;
	private IStatus myStatus;
	private boolean higherIsGood;
	
	public CPT (IProgressMonitor monitor){
		
		this.monitor = monitor;
		this.outputStrings = new ArrayList<String>();
		CPTAPI.setTime(System.currentTimeMillis());
		
	}
	
	public IStatus start() {
		
		try{
		
			this.monitor.beginTask("Searching", CPTAPI.toPrint().length + 2*(CPTAPI.totalHCLabWork() *
					CPTAPI.totalHCWork() * CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork()));
			
			this.root = new HillClimbingLab(new SubProgressMonitor(monitor, (CPTAPI.totalHCLabWork() *
					CPTAPI.totalHCWork() * CPTAPI.totalPSOLabWork() * CPTAPI.totalPSOWork())));
			
			createResultsQueue();
			
			generateCSVFile();
		} finally {
			this.monitor.done();
		}
		
		return myStatus;
		
	}
	
	public void generateCSVFile(){
		
		try{
			
			@SuppressWarnings("resource")
			FileWriter writer = new FileWriter(CPTAPI.getFileName());
			
			int rqs = this.resultsQueue.size();
			int qSize = this.resultsQueue.size();
			
			this.monitor.subTask("scanning results: " + rqs + " left");
			rqs--;
			
			if(this.monitor.isCanceled()){
				this.myStatus = Status.CANCEL_STATUS;
				throw new OperationCanceledException();
			}
			
			ResultNode rn = this.resultsQueue.poll();
			writer.append(rn.heading());
			outputStrings.add(rn.toString() + "\n");
			
			int converged = 0; 
			Double totalPopulation  = 0.0;
			Double runTime = 0.0;
			boolean metPerformanceTarget = false;
			int numberWhoMetTheTarget = 0;
			
			if(CPTAPI.getEvaluationControls().getValue().equals(Config.EVALARPT)){
				this.higherIsGood = false;
			} else {
				this.higherIsGood = true;
			}
			
			Node tempNode = new Node(rn.getName(), CPTAPI.getResultNode());
			
			Double targetPercent = rn.getPercentageOfMetPerformanceTargets(this.higherIsGood);
			
			metPerformanceTarget = metPerformanceTarget || targetPercent >= 100.0;
			if(targetPercent >= 100.0)
				numberWhoMetTheTarget++;
			
			addNodes(rn.COMPONENT + ": ", rn.getPopulationMapAsNodeString(), tempNode);
			addNodes(rn.TOTAL + ": ", rn.getTotalCostString(), tempNode);
			addNodes(rn.MEASURED + ": ", rn.peformanceMapAsNodeString(), tempNode);
			addNodes("Performance target: ", CPTAPI.getTargetControl().getValue(Config.LABTAR), tempNode);
			addNodes("Percentage of targets met: ",targetPercent.toString() , tempNode);
			addNodes(rn.PER + ": ", rn.getPerformanceCostString(), tempNode);
			addNodes(rn.POP + ": ", rn.getPopulationCostString(), tempNode);
			addNodes(rn.TPOP + ": ", rn.getTotalPopulationString(), tempNode);
			addNodes(rn.PSO + ": ", rn.psoMapAsNodeString(), tempNode);
			addNodes(rn.CONVERGED + ": ", rn.hasConverged(), tempNode);
			addNodes(rn.RUNTIME + ": ", "" + rn.getRunTime(), tempNode);
			
			CPTAPI.addResult(tempNode);
			
			if(rn.hasConverged().equals("True"))
				converged++;
			
			totalPopulation += rn.getTotalPopulation();
			runTime += rn.getRunTime();
			
			CPTAPI.getPACS().addPAC(rn.getPopulationMapAsNodeString(), 
					rn.getTotalCostString4SF(), 
					rn.peformanceMapAsNodeString4SF(), 
					rn.getTotalPopulationString());
			
			this.monitor.worked(1);
			
			int i = 1;
			
			
			
			while(this.resultsQueue.size() > 0){
				
				rn = this.resultsQueue.poll();
				
				this.monitor.subTask("scanning results: " + rqs + " left");
				rqs--;
				
				if(this.monitor.isCanceled()){
					this.myStatus = Status.CANCEL_STATUS;
					throw new OperationCanceledException();
				}
				
				outputStrings.add(rn.toString() + "\n");
				
				tempNode = new Node(rn.getName(), CPTAPI.getResultNode());
				
				targetPercent = rn.getPercentageOfMetPerformanceTargets(higherIsGood);
				
				metPerformanceTarget = metPerformanceTarget || targetPercent >= 100.0;
				if(targetPercent >= 100.0)
					numberWhoMetTheTarget++;
				
				addNodes(rn.COMPONENT + ": ", rn.getPopulationMapAsNodeString(), tempNode);
				addNodes(rn.TOTAL + ": ", rn.getTotalCostString(), tempNode);
				addNodes(rn.MEASURED + ": ", rn.peformanceMapAsNodeString(), tempNode);
				addNodes("Performance target: ", CPTAPI.getTargetControl().getValue(Config.LABTAR), tempNode);
				addNodes("Percentage of targets met: ",targetPercent.toString() , tempNode);
				addNodes(rn.PER + ": ", rn.getPerformanceCostString(), tempNode);
				addNodes(rn.POP + ": ", rn.getPopulationCostString(), tempNode);
				addNodes(rn.TPOP + ": ", rn.getTotalPopulationString(), tempNode);
				addNodes(rn.PSO + ": ", rn.psoMapAsNodeString(), tempNode);
				addNodes(rn.CONVERGED + ": ", rn.hasConverged(), tempNode);
				addNodes(rn.RUNTIME + ": ", "" + rn.getRunTime(), tempNode);
				
				if(rn.hasConverged().equals("True"))
					converged++;
				
				totalPopulation += rn.getTotalPopulation();
				runTime += rn.getRunTime();
				
				CPTAPI.addResult(tempNode);
				
				CPTAPI.getPACS().addPAC(rn.getPopulationMapAsNodeString(), 
						rn.getTotalCostString4SF(), 
						rn.peformanceMapAsNodeString4SF(), 
						rn.getTotalPopulationString());
				
				i++;
				this.monitor.worked(1);
			}
			
			for(i = outputStrings.size() - 1; i >= 0; i--){
				writer.append(outputStrings.get(i));
			}
			
			outputStrings = new ArrayList<String>();
			
			this.monitor.subTask("saving configuration...");
			
			writer.append("\n");
			writer.append("Configuration \n");
			
			String[] configuration = CPTAPI.toPrint();
			
			for(String s : configuration){
				
				if(this.monitor.isCanceled()){
					this.myStatus = Status.CANCEL_STATUS;
					throw new OperationCanceledException();
				}
				
				outputStrings.add( s + "\n");
				this.monitor.worked(1);
			}
			
			for(i = 0; i < outputStrings.size(); i++){
				writer.append(outputStrings.get(i));
			}
			
			this.monitor.subTask("complete.");
			
			
			if(metPerformanceTarget)
				tempNode = new Node("Results evaluation: " + ": MET TARGET PERFORMANCE", CPTAPI.getResultNode());
			else
				tempNode = new Node("Results evaluation: " + ": FAILED MEETING PERFORMANCE TARGET", CPTAPI.getResultNode());
			
			DecimalFormat myFormat = new DecimalFormat("0.000");
			addNodesToFront("Average model total population: ", (myFormat.format((Double) ((totalPopulation)  / qSize))).toString() + "", tempNode);
			addNodesToFront("Total results: ", qSize + "", tempNode);
			addNodesToFront("Number of models that didn't converge: ", qSize-converged +"", tempNode);
			addNodesToFront("Percentage of models that converged: ", ((Double) ((converged * 100.0)  / qSize)).toString() + "%", tempNode);
			addNodesToFront("Number of model configurations which met the target: ", numberWhoMetTheTarget+"", tempNode);
			
			CPTAPI.addResultToFront(tempNode);
			
			writer.close();
			this.myStatus = Status.OK_STATUS;
			
		}
		catch(IOException e){
			e.printStackTrace();
		} 
			
	}
	
	public void addNodes(String mid, String leaf, Node root){
		//Node midN = new Node(mid,root);
		Leaf leafL = new Leaf(mid + leaf,root);
		root.addChild(leafL);
		//root.addChild(midN);
	}
	
	public void addNodesToFront(String mid, String leaf, Node root){
		//Node midN = new Node(mid,root);
		Leaf leafL = new Leaf(mid + leaf,root);
		root.addChildToFront(leafL);
		//root.addChildToFront(midN);
	}
	
	public void createResultsQueue(){
		
		//remove duplicates
		this.resultsQueue = new PriorityQueue<ResultNode>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean offer(ResultNode e){
				if(this.contains(e)){
					return false;
				} else {
					return super.offer(e);
				}
			}
			
			@Override
			public boolean add(ResultNode e){
				if(this.contains(e)){
					return false;
				} else {
					return super.offer(e);
				}
			}
			
		};
		
		fillQueue();
		
		
	}
	
	private void fillQueue(){
		
		this.monitor.subTask("Compiling results...");
		
		this.root.fillQueue(this.resultsQueue,this.monitor);
		
		this.monitor.subTask("Compiled results");
		
	}


}
