package uk.ac.ed.inf.pepa.analysis.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.inf.pepa.parsing.ActionTypeNode;
import uk.ac.ed.inf.pepa.parsing.ChoiceNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * 
 * Walks the model and checks that every process P 
 * which is used as destination for a reversible action: 
 * 
 * 
 * S = ... (a, r).P , with a reversible
 * 
 * ...
 * 
 * P = ...
 * 
 * 
 * is not destination of any other action except that single one. 
 * 
 * This is implemented by checking that P only occurs once in the right side of a rule (when it is 'accessed' via (a, r).P 
 *  
 *  If it has more than one occurrence, something wrong happened. 
 *  Notice that we rely on the fact that the following constraints
 *  - each process must be correctly defined
 *  - each process must have exactly one definition (i.e., left side of definition)
 *  
 *  are checked by the currently implemented parser.
 *  
 * @author riccardoromanello
 *
 */

public class ASTReversibilityDefinitionVisitor {

	
	/**
	 *  referncesCount["P"] = k -> there are K occurrences of P in the rightside of a rule. 
	 */
	private HashMap<String, Integer> referencesCount;  
	
	/**
	 * accessedViaReversible = {P_1, P_2, ..., P_k} implies that that k processes are 
	 * accessed via reversible actions
	 */
	private Set<String> accessedViaReversible;
	
	/*
	 * In the end, all the processes inside accessedViaReversible must have a referencesCount equal to 1
	 */
	
	public ASTReversibilityDefinitionVisitor(ModelNode model) {
		
		referencesCount = new HashMap<String, Integer>();
		accessedViaReversible = new HashSet<String>();
		
		/**
		 * 
		 * Must remember when a process P is accessed via a reversible op.
		 * Must be sure that such P is accessed only in that moment and at most in its definition
		 * 
		 */
		
		model.accept(new DefaultVisitor() {
			
//			public void visitActionTypeNode(ActionTypeNode actionType) {
//				// do nothing?
//			}

			public void visitChoiceNode(ChoiceNode choice) {
				choice.getLeft().accept(this);
				choice.getRight().accept(this);
			}

			public void visitCooperationNode(CooperationNode cooperation) {
				cooperation.getLeft().accept(this);
				cooperation.getRight().accept(this);
			}

//			public void visitHidingNode(HidingNode hiding) {
//				// do nothing?
//			}
			
			public void visitModelNode(ModelNode model) {
				for (ProcessDefinitionNode procDef : model.processDefinitions()) {
					procDef.accept(this);
				}
				model.getSystemEquation().accept(this);
			}
			
			public void visitPrefixNode(PrefixNode prefix) {
				/**
				 * Node of the form (a, tau).P
				 * If a is reversible, P can appear in no other PrefixNode
				 * 
				 * I have to keep a counter for every process, how many times it is in the rightside of a definition
				 * I mark processes that are accessed via reversible operations
				 * In the end I control that every process accessed via reversible action appear only once in the rightside
				 * 
				 */
				if (prefix.getActivity().getAction() instanceof ActionTypeNode) { // what if this if fails
					String actionName = ((ActionTypeNode)prefix.getActivity().getAction()).getType();
					/**
					 * Increase the counter of the accesses for the process 
					 */
					if (prefix.getTarget() instanceof ConstantProcessNode) { // what if this if fails
						String dstName = ((ConstantProcessNode)prefix.getTarget()).getName();	
						Integer currCount = referencesCount.getOrDefault(dstName, 0);
						if(referencesCount.containsKey(dstName)) {							
							referencesCount.replace(dstName, currCount+1);
						}else {
							referencesCount.put(dstName, currCount+1);
						}
						/**
						 * If the action is reversible, need to remember the process name
						 */
						if (model.reversibleActionTypes().contains(actionName)){
							accessedViaReversible.add(dstName);
						}
					} else {
						prefix.getTarget().accept(this);
					}
				} else {
					prefix.getActivity().getAction().accept(this);
					prefix.getTarget().accept(this);
				}
			}
			
			public void visitProcessDefinitionNode(
					ProcessDefinitionNode processDefinition) {
				processDefinition.getNode().accept(this);

			}
			
			public void visitWildcardCooperationNode(WildcardCooperationNode cooperation) {
				cooperation.getLeft().accept(this);
				cooperation.getRight().accept(this);
			}	
		});
	}
	
	/**
	 * Returns the set of process names that are accessed via (a, tau).P, with P reversible, such that 
	 * there exists another access of the form (b, gamma).P
	 */
	
	public Set<String> getMultiReferencedProcesses(){
		Set<String> violations = new HashSet<String>();
		
		for (String procName : accessedViaReversible) {
			if (referencesCount.get(procName) > 1){
				violations.add(procName);
			}
		}
		return violations;
	}
	
}
