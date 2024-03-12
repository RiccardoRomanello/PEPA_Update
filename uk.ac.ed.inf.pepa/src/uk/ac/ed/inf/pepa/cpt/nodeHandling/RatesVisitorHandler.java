package uk.ac.ed.inf.pepa.cpt.nodeHandling;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class RatesVisitorHandler {
	
	private GetRatesASTVisitor grav = new GetRatesASTVisitor();
	private SetRatesASTVisitor srav = new SetRatesASTVisitor();
	private ModelNode node;
	
	public RatesVisitorHandler(ModelNode node){
		this.node = node;
	}
	
	public HashMap<String,Double> get(){
		grav.visitModelNode(node);
		return grav.getRatePopulationMap();
	}
	
	public void set(HashMap<String,Double> map){
		srav.setRatePopulation(map);
		srav.visitModelNode(node);
	}
	
	

}
