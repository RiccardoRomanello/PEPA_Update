package uk.ac.ed.inf.pepa.cpt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.ISequentialComponent;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class Utils {
	
	private static int modelCandidateNodeUID = -2;
	private static int hillClimbingLabCandidateNodeUID = -1;
	private static int particleSwarmOptimisationLabCandidateNodeUID = -1;
	private static int hcNodeUID = -1;
	private static int psoNodeUID = -1;

	public static String[] getSystemEquation(IParametricDerivationGraph graph) {
		
		ArrayList<String> components = new ArrayList<String>();

		for (ISequentialComponent c : graph.getSequentialComponents()){
			components.add(c.getName());
		}

		return components.toArray(new String[0]);
	}
	
	public static Integer[] getInitialPopulation(IParametricDerivationGraph graph) {
		
		ArrayList<Integer> components = new ArrayList<Integer>();

		for (ISequentialComponent c : graph.getSequentialComponents()){
			components.add(c.getInitialPopulationLevel());
		}

		return components.toArray(new Integer[0]);
	}
	
	
	public static HashMap<Short,String> getActionIds(IParametricDerivationGraph graph){
		
		HashMap<Short,String> actionIdMap = new HashMap<Short,String>();
		
		for(int i = 0; i < graph.getSequentialComponents().length;i++){
			ISequentialComponent c = graph.getSequentialComponents()[i];
			for (short actionId : c.getActionAlphabet()){
				actionIdMap.put(actionId, graph.getSymbolGenerator().getActionLabel(actionId));
			}
		}
		
		return actionIdMap;
		
	}

	public static IParametricDerivationGraph getDevGraphFromAST(ModelNode node){

		IParametricDerivationGraph dGraph;

		try{

			//so this is how to make the graph :)
			dGraph = ParametricDerivationGraphBuilder
					.createDerivationGraph(node, null);

		} catch (InterruptedException e) {
			System.out.println(e);
			dGraph = null;

		} catch (DifferentialAnalysisException e) {
			System.out.println(e);
			dGraph = null;

		}

		return dGraph;

	}
	
	public static HashMap<String, Double> copyHashMap(HashMap<String, Double> map){

		HashMap<String, Double> newMap = new HashMap<String,Double>();

		if(map != null){

			for(Entry<String, Double> entry : map.entrySet()){
				newMap.put(entry.getKey(), entry.getValue());
			}
		} else {
			System.out.println("UTILS: map is null");
		}

		return newMap;

	}
	
	public static int getModelCandidateNodeUID(){
		Utils.modelCandidateNodeUID++;
		return Utils.modelCandidateNodeUID;
	}
	
	public static int getHillClimbingLabCandidateNodeUID(){
		Utils.hillClimbingLabCandidateNodeUID++;
		return Utils.hillClimbingLabCandidateNodeUID;
	}
	
	public static int getParticleSwarmOptimisationLabCandidateNodeUID(){
		Utils.particleSwarmOptimisationLabCandidateNodeUID++;
		return Utils.particleSwarmOptimisationLabCandidateNodeUID;
	}
	
	public static int getHCNodeUID(){
		Utils.hcNodeUID++;
		return Utils.hcNodeUID;
	}
	
	public static int getPSONodeUID(){
		Utils.psoNodeUID++;
		return Utils.psoNodeUID;
	}
	
	public static Double returnRandomInRange(Double min, Double max, String type){
		
		Random generator = new Random();
		
		if(type.equals(Config.NATURAL) || type.equals(Config.INTEGER)){
			
			return generator.nextInt((int)(max - min) + 1) + min; 
			
		} else if (type.equals(Config.DOUBLE)) {
			
			return generator.nextDouble() * (max - min) + min; 
			
		} else if (type.equals(Config.PERCENT)) {
			
			return generator.nextDouble() * (max - min) + min;
			
		} else {
			//problem!
			return 0.0;
			
		}
		
	}
	
	public static Double returnRandom(){
		Random generator = new Random();
		Double next = generator.nextDouble();
		return next; 
	}
	
	public static boolean rollDice(Double p){
		return (returnRandom() < p);
	}
	
	public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	public static void printMap(HashMap<String,Double> map){

		for(Entry<String, Double> entry : map.entrySet()){
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

	}
}
