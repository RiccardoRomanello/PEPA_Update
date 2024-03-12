package uk.ac.ed.inf.pepa.cpt.nodeHandling;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class ComponentVisitorHandler {
	
	private GetSystemEquationASTVisitor gcav = new GetSystemEquationASTVisitor();
	private SetSystemEquationASTVisitor scav = new SetSystemEquationASTVisitor();
	private ModelNode node;
	
	public ComponentVisitorHandler(ModelNode node){
		this.node = node;
	}
	
	public HashMap<String,Double> get(){
		gcav.visitModelNode(node);
		return gcav.getSystemEquation();
	}
	
	public void set(HashMap<String,Double> map){
		scav.setSystemEquation(map);
		scav.visitModelNode(node);
	}
	
	

}