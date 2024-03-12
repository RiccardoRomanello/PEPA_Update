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

public class GetRatesASTVisitor implements ASTVisitor {

	
	private String label;
	private Double value;
	private HashMap<String,Double> ratePopulationMap;
	
	public GetRatesASTVisitor(){
		this.ratePopulationMap = new HashMap<String,Double>();
	}
	
	public HashMap<String,Double> getRatePopulationMap(){
		return this.ratePopulationMap;
	}
	
	@Override
	public void visitActionTypeNode(ActionTypeNode actionType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitActivityNode(ActivityNode activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAggregationNode(AggregationNode aggregation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitChoiceNode(ChoiceNode choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConstantProcessNode(ConstantProcessNode constant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCooperationNode(CooperationNode cooperation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHidingNode(HidingNode hiding) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * the 'top'
	 */
	@Override
	public void visitModelNode(ModelNode model) {
		for (RateDefinitionNode rtn : model.rateDefinitions()) {
			rtn.accept(this);
		}
		
	}

	@Override
	public void visitPassiveRateNode(PassiveRateNode passive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitPrefixNode(PrefixNode prefix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitProcessDefinitionNode(
			ProcessDefinitionNode processDefinition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitRateDefinitionNode(RateDefinitionNode rateDefinition) {
		this.label = rateDefinition.getName().getName();
		rateDefinition.getRate().accept(this);
		
	}

	@Override
	public void visitRateDoubleNode(RateDoubleNode doubleRate) {
		this.value = doubleRate.getValue();
		this.ratePopulationMap.put(this.label, this.value);
		
	}

	@Override
	public void visitUnknownActionTypeNode(
			UnknownActionTypeNode unknownActionTypeNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitVariableRateNode(VariableRateNode variableRate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
		// TODO Auto-generated method stub
		
	}

}
