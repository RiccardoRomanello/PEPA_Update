/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.model.internal;

import java.util.Map;

import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Process;

public class Cloner {

	public static Aggregation clone(Aggregation aggregation) {
		// ClonerVisitor v = new ClonerVisitor();
		// process.accept(v);
		// return v.cloned;
		return lightweigthClone(aggregation);
	}

	private static Aggregation lightweigthClone(Aggregation aggregation) {
		Aggregation newAggregation = DoMakePepaProcess.getInstance()
				.createAggregation();
		for (Map.Entry<Process, Integer> entry : aggregation.getSubProcesses()
				.entrySet()) {
			((AggregationImpl) newAggregation).add(entry.getKey(), entry
					.getValue());
		}
		return newAggregation;
	}

	// static class ClonerVisitor implements Visitor {
	//
	// private final DoMakePepaProcess factory = DoMakePepaProcess
	// .getInstance();
	//
	// Process cloned;
	//
	// public void visitAggregation(Aggregation aggregation) {
	// cloned = factory.createAggregation();
	//
	// for (Map.Entry<Process, Integer> entry : aggregation
	// .getSubProcesses().entrySet()) {
	// Process toBeCloned = entry.getKey();
	// ClonerVisitor v = new ClonerVisitor();
	// toBeCloned.accept(v);
	// ((Aggregation) cloned).add(v.cloned, entry.getValue());
	// }
	//
	// // ((Aggregation) cloned).setActionSet(clone(aggregation
	// // .getActionSet()));
	//
	// }
	//
	// public void visitChoice(Choice choice) {
	// ClonerVisitor v = new ClonerVisitor();
	// choice.getLeftHandSide().accept(v);
	// Process lhs = v.cloned;
	//
	// ClonerVisitor v2 = new ClonerVisitor();
	// choice.getRightHandSide().accept(v2);
	// Process rhs = v2.cloned;
	//
	// cloned = factory.createChoice(lhs, rhs);
	// }
	//
	// public void visitConstant(Constant constant) {
	// /*
	// * ClonerVisitor v = new ClonerVisitor();
	// * constant.getResolvedProcess().accept(v); Process resolved =
	// * v.cloned;
	// */
	//
	// cloned = factory.createConstant(constant.getName());
	// ((Constant) cloned).resolve(constant.getResolvedProcess());
	//
	// }
	//
	// public void visitCooperation(Cooperation cooperation) {
	// ClonerVisitor v = new ClonerVisitor();
	// cooperation.getLeftHandSide().accept(v);
	// Process lhs = v.cloned;
	//
	// ClonerVisitor v2 = new ClonerVisitor();
	// cooperation.getRightHandSide().accept(v2);
	// Process rhs = v2.cloned;
	//
	// cloned = factory.createCooperation(lhs, rhs, clone(cooperation
	// .getActionSet()));
	//
	// }
	//
	// public void visitHiding(Hiding hiding) {
	// // TODO Auto-generated method stub
	// throw new IllegalStateException("Not implemented yet");
	// }
	//
	// public void visitPrefix(Prefix prefix) {
	// ClonerVisitor v = new ClonerVisitor();
	// prefix.getTargetProcess().accept(v);
	// cloned = factory
	// .createPrefix(clone(prefix.getActivity()), v.cloned);
	//
	// }
	//
	// private Activity clone(Activity activity) {
	// // TODO do proper cloning, lose named rates
	// Rate cloned = RateMath.mult(activity.getRate(), 1);
	// return factory.createActivity(clone(activity.getAction()), cloned);
	// }
	//
	// private Action clone(Action action) {
	// Action cloned = null;
	// if (action instanceof SilentAction) {
	// /*cloned = factory.createSilentAction();
	// Action underlyingAction = ((SilentAction) action)
	// .getHiddenAction();
	// NamedAction clonedUnderlyingAction = factory
	// .createNamedAction(((NamedAction) underlyingAction)
	// .getName());
	// ((SilentAction) cloned).setHiddenAction(clonedUnderlyingAction)*/;
	// } else {
	// cloned = factory.createNamedAction(((NamedAction) action)
	// .getName());
	// }
	// return cloned;
	// }
	//
	// private ActionSet clone(ActionSet actionSet) {
	// ActionSet cloned = factory.createActionSet();
	//
	// Iterator<Action> iterator = actionSet.iterator();
	// while (iterator.hasNext()) {
	// Action action = iterator.next();
	// cloned.add(clone(action));
	// }
	// return cloned;
	// }
	//
	// }

}
