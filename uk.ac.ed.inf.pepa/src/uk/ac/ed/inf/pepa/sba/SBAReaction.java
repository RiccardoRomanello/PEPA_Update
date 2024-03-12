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
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * Represents a single reaction (lists of reactants and products) in a
 * intermediate form, allowing for easy transformation to a final
 * representation.
 * 
 * @author ajduguid
 * 
 */
public class SBAReaction {
	String name, sourceDefinition;

	boolean passive;

	CompiledRate numerator = null, denominator = null, overall = null;

	LinkedList<SBAComponent> reactants, products;

	SBAReaction() {
		name = null;
		reactants = new LinkedList<SBAComponent>();
		products = new LinkedList<SBAComponent>();
	}

	SBAReaction(String name) {
		this();
		this.name = name;
	}
	
	void hide() {
		name = SilentAction.TAU;
	}

	boolean addProduct(SBAComponent c) {
		if (c == null)
			throw new NullPointerException(
					"All components must be defined in the reaction "
							+ (name == null ? "." : name + "."));
		if (c.catalyst || c.inhibitor)
			throw new IllegalArgumentException("Products in "
					+ (name == null ? "a reaction" : "reaction " + name)
					+ " cannot act as catalysts or inhibitors.");
		if (products.contains(c))
			return false;
		return products.add(c);
	}

	boolean addReactant(SBAComponent c) {
		if (c == null)
			throw new NullPointerException(
					"All components must be defined in the reaction "
							+ (name == null ? "." : name + "."));
		if (reactants.contains(c))
			return false;
		return reactants.add(c);
	}

	public SBAReaction clone() {
		SBAReaction clone = new SBAReaction(name);
		for (SBAComponent c : reactants)
			clone.reactants.add(c.clone());
		for (SBAComponent c : products)
			clone.products.add(c.clone());
		clone.passive = passive;
		if (numerator != null)
			clone.numerator = numerator.clone();
		if (denominator != null)
			clone.denominator = denominator.clone();
		if (overall != null)
			clone.overall = overall.clone();
		return clone;
	}

	/**
	 * Equality is based on having the same components as both reactants and
	 * products in both reactions and the same name (action).
	 * 
	 * @param r
	 * @return
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof SBAReaction))
			return false;
		SBAReaction r = (SBAReaction) o;
		if ((this.name == null ? r.name == null : this.name.equals(r.name))
				&& this.reactants.containsAll(r.reactants)
				&& r.reactants.containsAll(this.reactants)
				&& this.products.containsAll(r.products)
				&& r.products.containsAll(this.products))
			return true;
		return false;
	}

	public int hashCode() {
		return (name == null ? 0 : name.hashCode()) ^ reactants.hashCode()
				^ products.hashCode();
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public List<SBAComponent> getProducts() {
		return (LinkedList<SBAComponent>) products.clone();
	}

	@SuppressWarnings("unchecked")
	public List<SBAComponent> getReactants() {
		return (LinkedList<SBAComponent>) reactants.clone();
	}

	SBAReaction merge(SBAReaction r) {
		if (!name.equals(r.name))
			throw new IllegalArgumentException(
					"Reaction names must agree to allow merge. " + name
							+ " != " + r.name);
		SBAReaction merged = new SBAReaction();
		merged.name = name;
		for (SBAComponent c : reactants)
			merged.reactants.add(c.clone());
		for (SBAComponent c : products)
			merged.products.add(c.clone());
		for (SBAComponent c : r.reactants)
			if (!merged.addReactant(c.clone()))
				throw new IllegalArgumentException("Error. Component " + c.name
						+ " exists in both reaction " + name + " and " + r.name
						+ ".");
		for (SBAComponent c : r.products)
			if (!merged.addProduct(c.clone()))
				throw new IllegalArgumentException("Error. Component " + c.name
						+ " exists in both reaction " + name + " and " + r.name
						+ ".");
		if (numerator == null && denominator == null && overall == null
				&& r.numerator == null && r.denominator == null
				&& r.overall == null)
			return merged;
		merged.passive = passive && r.passive;
		merged.overall = new CompiledRate(1);
		/*
		 * Use of 1-theta(numerator) is for numerical stability. The numerator
		 * can be used instead of the denominator as the numerator will always
		 * be a sub-expression of the denominator and we're only concerned about
		 * when the denominator is equal to zero.
		 */
		if (passive) {
			merged.overall = CompiledRate.theta(numerator);
			if (!numerator.equals(denominator))
				// merged.overall = merged.overall.op(Operator.MULT, numerator).op(Operator.DIV,denominator.op(Operator.PLUS,(new CompiledRate(1)).op(Operator.MINUS,CompiledRate.theta(numerator))));
				merged.overall = merged.overall.op(Operator.MULT,numerator.stabilisedRatio(denominator));
		} else if (!numerator.equals(denominator))
			// merged.overall = numerator.op(Operator.DIV, denominator.op(Operator.PLUS,(new CompiledRate(1)).op(Operator.MINUS,CompiledRate.theta(numerator))));
			merged.overall = numerator.stabilisedRatio(denominator);
		if (r.passive) {
			merged.overall = merged.overall.op(Operator.MULT, CompiledRate.theta(r.numerator));
			if (!r.numerator.equals(r.denominator))
				// merged.overall = merged.overall.op(Operator.MULT, r.numerator).op(Operator.DIV, r.denominator.op(Operator.PLUS,(new CompiledRate(1)).op(Operator.MINUS,CompiledRate.theta(r.numerator))));
				merged.overall = merged.overall.op(Operator.MULT, r.numerator.stabilisedRatio(r.denominator));
		} else if (!r.numerator.equals(r.denominator))
			// merged.overall = merged.overall.op(Operator.MULT, r.numerator).op(Operator.DIV, r.denominator.op(Operator.PLUS,(new CompiledRate(1)).op(Operator.MINUS,CompiledRate.theta(r.numerator))));
			merged.overall = merged.overall.op(Operator.MULT, r.numerator.stabilisedRatio(r.denominator));
		if (overall != null)
			merged.overall = merged.overall.op(Operator.MULT, overall);
		if (r.overall != null)
			merged.overall = merged.overall.op(Operator.MULT, r.overall);
		if (passive == r.passive)
			merged.numerator = merged.denominator = CompiledRate.min(
					denominator, r.denominator);
		else
			merged.numerator = merged.denominator = (passive ? r.denominator
					: denominator);
		return merged;
	}

	boolean removeProduct(SBAComponent c) {
		return products.remove(c);
	}

	boolean removeProduct(String s) {
		for (Iterator<SBAComponent> i = products.iterator(); i.hasNext();)
			if (i.next().name.equals(s)) {
				i.remove();
				return true;
			}
		return false;
	}

	boolean removeReactant(SBAComponent c) {
		return reactants.remove(c);
	}

	boolean removeReactant(String s) {
		for (Iterator<SBAComponent> i = reactants.iterator(); i.hasNext();)
			if (i.next().name.equals(s)) {
				i.remove();
				return true;
			}
		return false;
	}

	void setName(String name) {
		this.name = name;
	}

	String toCMDL() {
		StringBuilder s = new StringBuilder();
		if (name != null)
			s.append(name).append(", ");
		for (SBAComponent r : reactants)
			s.append(r.toCMDL()).append(" + ");
		if (reactants.size() > 0)
			s.delete(s.length() - 3, s.length());
		s.append(" -> ");
		for (SBAComponent p : products)
			s.append(p.toCMDL()).append(" + ");
		if (products.size() > 0)
			s.delete(s.length() - 3, s.length());
		return s.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (SBAComponent r : reactants)
			s.append(r.toString()).append(" + ");
		if (reactants.size() > 0)
			s.delete(s.length() - 3, s.length());
		s.append(" -> ");
		for (SBAComponent p : products)
			s.append(p.toString()).append(" + ");
		if (products.size() > 0)
			s.delete(s.length() - 3, s.length());
		return s.toString();
	}
}
