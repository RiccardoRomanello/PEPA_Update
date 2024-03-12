package uk.ac.ed.inf.pepa.cpt.nodeHandling;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.parsing.ASTVisitor;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.ActivityNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PassiveRateNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.parsing.UnknownActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.VariableRateNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * I can not find any previous work that gives me access to the system equation
 * so I wrote/borrowed this so I can get the systemEquation into a HashMap
 * @author twig
 *
 */
public class GetSystemEquationASTVisitor implements ASTVisitor {
	
	private String component;
	private Double population;
	HashMap<String, Double> systemEquation;
	
	public GetSystemEquationASTVisitor(){
		this.systemEquation = new HashMap<String, Double>();
	}
	
	public HashMap<String, Double>  getSystemEquation(){
		return this.systemEquation;
	}
	
	@Override
	public void visitConstantProcessNode(ConstantProcessNode constant) {
		component = constant.getName();
	}
	
	@Override
	public void visitRateDoubleNode(RateDoubleNode doubleRate) {
		population = doubleRate.getValue();
		systemEquation.put(component,population);
	}
	
	@Override
	public void visitAggregationNode(AggregationNode aggregation) {
		aggregation.getProcessNode().accept(this);
		aggregation.getCopies().accept(this);
		
	}

	@Override
	public void visitCooperationNode(CooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
		
	}

	@Override
	public void visitWildcardCooperationNode(
			WildcardCooperationNode cooperation) {
		cooperation.getLeft().accept(this);
		cooperation.getRight().accept(this);
		
	}		

	@Override
	public void visitModelNode(ModelNode model) {
		model.getSystemEquation().accept(this);
		
	}
	
	public void visitPassiveRateNode(PassiveRateNode passive) {}
	public void visitPrefixNode(PrefixNode prefix) {}
	public void visitProcessDefinitionNode(ProcessDefinitionNode processDefinition) {}
	public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {}
	public void visitVariableRateNode(VariableRateNode variableRate) {}
	public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {}
	public void visitActionTypeNode(ActionTypeNode actionType){}
	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {}
	public void visitChoiceNode(ChoiceNode choice) {}
	public void visitHidingNode(HidingNode hiding) {}
	public void visitActivityNode(ActivityNode activity) {}
	
}
