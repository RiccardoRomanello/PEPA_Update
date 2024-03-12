/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal.recursive;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.model.Process;

/**
 * TODO Get rid of legacy code!
 * @author mtribast
 *
 */
public class StateSpaceMap {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(StateSpaceMap.class);

	private LinkedHashMap<Process, TransitionList> map = new LinkedHashMap<Process, TransitionList>();

	public Map<Process, TransitionList> getMap() {
		return map;
	}

	/*
	 * <b>This is legacy comment</b>
	 * <P>
	 * This is a modified version of containsKey for a Map. It first checks
	 * existance according to the <code>containsKey(Object)</code> contract of
	 * the private Map. This method may fail, for example, when a process like P ||
	 * Q is already in the map, and <code>state</code> is P1 || P2 || Q and P
	 * is defined as P = P1 || P2. This problem arises because the
	 * implementation of <code>equals(Object)</code> for a constant does not
	 * take into account the resolved process. Hence,
	 * <code>P.equals(P1 || P2)</code> returns false.
	 * <p>
	 * In this implementation, when map.containsKey return false, further action
	 * is taken. This second branch has linear complexity. Undergoing
	 * measurement studies are considering performance penalty.
	 */
	public boolean containsState(Process state) {
		return map.containsKey(state);
		// FIXME does IT slow down
		/* else { */
		// logger.info("Slow branch for " + state.prettyPrint());
		// /*
		// * This branch is for constant equality check. It solves the problem
		// * when a model is defined by a constant, such as in the active
		// * badge example. In that example, the first state is Sys, whereas
		// * the third state is what Sys resolves. As constant and its
		// * resolution are not equal according to the equals(Object) method,
		// * containsKey() returns false when the resolved process is searched
		// * in the state space. This branch simply looks for constants in the
		// * state space and check the process against the resolved process of
		// * each constant. Although the complessity is linear, this branch
		// * should be executed rarely!
		// *
		// * FIXME In reality, the two branches are executed the same
		// * FIXME The below code does nothing!!!
		// */
		// for (Process entry : this.map.keySet())
		// if (Matcher.match(entry, state) == true) {
		// logger.info("Slow branch found!!!");
		// /*
		// * There is a problem with the active badge example.
		// * Originally, this branch throws an exception. However, if
		// * the exception is commented out, the result of the state
		// * space derivation is correct. This is because the model
		// * has two states which are equals. See definition R0 and
		// * R4. R0 could be rewritten as (recv, infty).R4; When state
		// * S1 <..> M0 <...> R4 is inserted in the stack and the
		// * anonymous prefix (cm, def).(a0, def).R1 is requested,
		// * then the exception is thrown. But the two states are
		// * actually different?
		// *
		// * FIXME It is likely that this branch is useless!!!
		// */
		// //System.err.println("Entry: " + entry.prettyPrint());
		// //System.err.println("State: " + state.prettyPrint());
		// //throw new IllegalStateException();
		// }
		// return false;
		//
		// }
	}

	/**
	 * Safely adds a new state. Its presence in the state space has been tested
	 * already.
	 * 
	 * @param state
	 * @param transitions
	 */
	public void put(Process state, TransitionList transitions) {
		this.map.put(state, transitions);
	}

}

// class Matcher {
// public static boolean match(Process p, Process q) {
// if (p instanceof Prefix) {
// if (q instanceof Prefix)
// return p.equals(q);
// else if (q instanceof Constant)
// return p.equals(((Constant) q).getResolvedProcess());
// else
// return false;
// }
// if (p instanceof Choice) {
// if (q instanceof Choice)
// return match(((Choice) p).getLeftHandSide(), ((Choice) q)
// .getLeftHandSide())
// && match(((Choice) p).getRightHandSide(), ((Choice) q)
// .getRightHandSide());
// else if (q instanceof Constant)
// return p.equals(((Constant) q).getResolvedProcess());
// else
// return false;
// }
// if (p instanceof Cooperation) {
// if (q instanceof Cooperation)
// return match(((Cooperation) p).getLeftHandSide(),
// ((Cooperation) q).getLeftHandSide())
// && match(((Cooperation) p).getRightHandSide(),
// ((Cooperation) q).getRightHandSide());
// else if (q instanceof Constant)
// return p.equals(((Constant) q).getResolvedProcess());
// else
// return false;
// }
// if (p instanceof Constant) {
// if (q instanceof Constant)
// return p.equals(q);
// else
// return ((Constant) p).getResolvedProcess().equals(q);
// }
// if (p instanceof Aggregation) {
// if (q instanceof Aggregation)
// return p.equals(q);
// else if (q instanceof Constant)
// return p.equals(((Constant) q).getResolvedProcess());
// else
// return false;
// }
// if (p instanceof Hiding) {
// if (q instanceof Hiding)
// return p.equals(q);
// else if (q instanceof Constant)
// return p.equals(((Constant) q).getResolvedProcess());
// else
// return false;
// }
// throw new IllegalArgumentException();
// }
// }
