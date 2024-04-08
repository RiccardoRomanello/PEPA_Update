package uk.ac.ed.inf.pepa.tests;

import java.io.IOException;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class SteadyStateAnalysis {

	public static void main(String[] args) throws IOException,
			DerivationException {
		String fileName = args[0];
		// get abstract syntax tree of PEPA
		ModelNode modelNode = (ModelNode) PepaTools.parse(PepaTools.readText(fileName));
		// get state space
		IStateSpace stateSpace = PepaTools.derive(new OptionMap(), modelNode, null, null);
		// print transitions
		for (int i = 0; i < stateSpace.size(); i++) {
			String source = getProcessString(stateSpace, i);
			int[] targetStateIndices = stateSpace.getOutgoingStateIndices(i);
			for (int target : targetStateIndices) {
				StringBuffer message = new StringBuffer();
				message.append(source);
				message.append(" --> ");
				message.append(getProcessString(stateSpace, target));
				message.append(" via ");
				for (NamedAction action : stateSpace.getAction(i, target)) 
					message.append(action.prettyPrint() + " ");
				System.out.println(message.toString());
			}
		}
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
