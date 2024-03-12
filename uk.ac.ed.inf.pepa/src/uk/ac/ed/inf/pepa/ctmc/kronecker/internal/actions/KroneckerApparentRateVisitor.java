package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.UnknownActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * Generates an apparent rate calculator for each action type
 * 
 * @author msmith
 */
public class KroneckerApparentRateVisitor extends DefaultVisitor {

	private ISymbolGenerator generator;	
	private ASTNode node;
	private short actionID;
	private ApparentRateCalculator calculator;
	
	public KroneckerApparentRateVisitor(ASTNode node, short actionID, ISymbolGenerator generator) {
		this.node = node;
		this.actionID = actionID;
		this.generator = generator;
		this.calculator = new ApparentRateCalculator();
	}
	
	private KroneckerApparentRateVisitor(ASTNode node, KroneckerApparentRateVisitor visitor) {
		this.node = node;
		this.actionID = visitor.actionID;
		this.generator = visitor.generator;
		this.calculator = new ApparentRateCalculator();
	}
		
	public ApparentRateCalculator getCalculator() {
		node.accept(this);
		return calculator;
	}
	
	public void visitConstantProcessNode(ConstantProcessNode constant) {
		// ignore - the default calculator is an identity operator
	}

	// We will ignore this for now, I guess
	public void visitHidingNode(HidingNode hiding) {
		// don't deal with for now
		assert false;
	}
	
	// Process P[n]
	public void visitAggregationNode(AggregationNode aggregation) {
		// don't deal with for now
		assert false;
	}
	
	
	public void visitActionTypeNode(ActionTypeNode actionType) {
		// ignore - handle directly in cooperation node
	}
	
	// tau action
	public void visitUnknownActionTypeNode(UnknownActionTypeNode unknownActionTypeNode) {
		// we shouldn't have any tau actions in the system equation
		assert false;
	}

	public void visitCooperationNode(CooperationNode cooperation) {
		KroneckerApparentRateVisitor leftVisitor = new KroneckerApparentRateVisitor(cooperation.getLeft(), this);
		KroneckerApparentRateVisitor rightVisitor = new KroneckerApparentRateVisitor(cooperation.getRight(), this);
		ApparentRateCalculator leftCalculator = leftVisitor.getCalculator();
		ApparentRateCalculator rightCalculator = rightVisitor.getCalculator();
		
		boolean containsAction = false;
		for (ActionTypeNode action : cooperation.getActionSet()) {
			short id = generator.getActionId(action.getType());
			if (actionID == id) {
				containsAction = true;
				break;
			}
		}
		if (containsAction) {
			// Need to synchronise
			calculator = leftCalculator.min(rightCalculator);
		} else {
			// Independent
			calculator = leftCalculator.plus(rightCalculator);
		}
	}
	
	// <*> sync over shared action names?
	public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
		// don't deal with for now
		assert false;
	}
	
	
	
}
