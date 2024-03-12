/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.*;

import uk.ac.ed.inf.pepa.model.SilentAction;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode;
import uk.ac.ed.inf.pepa.parsing.RateNode;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * 
 * @author ajduguid
 * 
 */
class ReactionBuilder implements Cloneable {
	
	String sourceDefinition;

	ReactionBuilder from;

	List<ReactionBuilderAction> moves;

	ReactionBuilder() {
		from = null;
		moves = new LinkedList<ReactionBuilderAction>();
	}

	/**
	 * Used to create the further reactions in a sequence.<br>
	 * i.e. <b>(a,r1).</b>(b,r2).P
	 * 
	 * @param product
	 * @param action
	 * @param rate
	 */
	void addReaction(ReactionBuilder product, String action, RateNode rate) {
		ReactionBuilderAction rba = new ReactionBuilderAction();
		rba.setGoesTo(product);
		rba.setAction(action);
		rba.setRate(rate);
		moves.add(rba);
	}

	/**
	 * Used to create the first reaction of a sequence.<br>
	 * i.e (b,r2).P
	 * 
	 * @param constant
	 * @param action
	 * @param rate
	 */
	void addReaction(String constant, String action, RateNode rate) {
		ReactionBuilderAction rba = new ReactionBuilderAction();
		rba.setGoesTo(constant);
		if (action == null && rate == null)
			rba.noPrefix();
		else {
			rba.setAction(action);
			rba.setRate(rate);
		}
		moves.add(rba);
	}

	public ReactionBuilder clone() {
		ReactionBuilder clone = new ReactionBuilder();
		for (ReactionBuilderAction rba : moves)
			clone.moves.add(rba.clone());
		ReactionBuilder rb;
		for (ReactionBuilderAction rba : clone.moves) {
			rb = rba.next;
			if (rb != null)
				clone.link(rb);
		}
		clone.sourceDefinition = sourceDefinition;
		return clone;
	}

	ReactionsSet generateReactions() {
		return generateReactions(null);
	}

	ReactionsSet generateReactions(String constant) {
		// HashMap<String, RateNode> apparentRates = new HashMap<String, RateNode>();
		// HashSet<String> apparentActions = new HashSet<String>();
		BinaryOperatorRateNode addNode;
		ReactionsSet reactions = new ReactionsSet();
		SBAReaction r, original;
		SBAComponent c;
		ReactionBuilder rb;
		for (ReactionBuilderAction rba : moves) {
			if (rba.noPrefix == true)
				continue;
			r = new SBAReaction();
			r.sourceDefinition = sourceDefinition;
			// product
			c = new SBAComponent(rba.product, rba.rate);
			r.addProduct(c);
			// reactant
			c = new SBAComponent((constant == null ? rba.getReactant()
					: constant), rba.rate);
			r.addReactant(c);
			r.setName(rba.action);
			if(reactions.reactions.contains(r)) {
				// augment rate here for duplicate entries???
				original = reactions.reactions.get(reactions.reactions.indexOf(r));
				addNode = ASTFactory.createBinaryOperationRate();
				addNode.setOperator(Operator.PLUS);
				addNode.setLeft(original.reactants.getFirst().rate);
				addNode.setRight(c.rate);
				original.reactants.getFirst().setRate(addNode);
			}else
				reactions.reactions.add(r);
		}
		for (ReactionBuilderAction rba : moves) {
			if (rba.noPrefix == true) {
				reactions.reactionsToIterate.put(this.getName(), rba.product);
				continue;
			}
			// recursive
			rb = rba.next;
			if (rb != null) {
				ReactionsSet result = rb.generateReactions();
				reactions.reactions.addAll(result.reactions);
				reactions.reactionsToIterate.putAll(result.reactionsToIterate);
			} else
				reactions.reactionsToIterate.put(rba.product, null);
		}
		return reactions;
	}

	/**
	 * Generates the name of this stage of the sequence of a PEPA definition.
	 * 
	 * @return
	 */
	String getName() {
		StringBuilder s = new StringBuilder();
		if (moves.size() > 1)
			s.append("(");
		for (ReactionBuilderAction rba : moves)
			s.append(rba.getReactant()).append(" + ");
		s.delete(s.length() - 3, s.length());
		if (moves.size() > 1)
			s.append(")");
		return s.toString();
	}

	/**
	 * Recursive method which turns all instances of actions in hideSet to tau
	 * actions.
	 * 
	 * @param hideSet
	 *            The Set of actions that you wish to become 'hidden' from
	 *            co-operation.
	 */
	void hideActions(Set<String> hideSet) {
		ReactionBuilder rb;
		for (ReactionBuilderAction rba : moves) {
			rb = rba.next;
			if (rb != null)
				rb.hideActions(hideSet);
			if (rba.action != null && hideSet.contains(rba.action))
				rba.setAction(SilentAction.TAU);
		}
	}

	/**
	 * This reaction forms the new head (or first stage) of the sequence, the
	 * next stage being the previousState
	 * 
	 * @param previousFrontState
	 */
	void link(ReactionBuilder previousFrontState) {
		previousFrontState.from = this;
	}

	/**
	 * Merges two reactions (choice combinator in PEPA).
	 * 
	 * @param secondReaction
	 * @return new ReactionBuilder. The two being merged are left in their
	 *         original state.
	 * @throws IllegalStateException
	 *             if either reaction is not the current first stage of the
	 *             sequence.
	 */
	ReactionBuilder merge(ReactionBuilder secondReaction)
			throws IllegalStateException {
		if (this.from != null || secondReaction.from != null)
			throw new IllegalStateException(
					"One or both of the reactions selected to be merged is not the head of a sequence. Cannot merge.");
		ReactionBuilder mergedReaction = new ReactionBuilder();
		mergedReaction.moves.addAll(this.moves);
		mergedReaction.moves.addAll(secondReaction.moves);
		// Updates next chained ReactionBuilders, pointing to merged reaction.
		ReactionBuilder rb = null;
		for (ReactionBuilderAction rba : mergedReaction.moves)
			rb = rba.next;
		if (rb != null)
			mergedReaction.link(rb);
		return mergedReaction;
	}
	
	void setSource(String name) {
		sourceDefinition = name;
		for(ReactionBuilderAction rba : moves)
			if(rba.next != null)
				rba.next.setSource(name);
	}

}
