/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import uk.ac.ed.inf.pepa.parsing.ASTSupport;
import uk.ac.ed.inf.pepa.parsing.RateNode;

/**
 * Defines a single component for a reaction. Use of this class only makes sense
 * in connection to a Reaction object. Equality and comparison are based on the
 * assumption that the Component objects are involved in reactions.
 * 
 * @author ajduguid
 *
 */
public class SBAComponent implements Comparable<SBAComponent> {
	boolean catalyst, inhibitor;

	final String name;

	RateNode rate;
	
	// Map<String, List<RateNode>> apparentRates;

	int stoichiometry;

	SBAComponent(String name, RateNode rate) {
		if (name == null)
			throw new NullPointerException(
					"Component must have a defined name. Cannot pass null argument.");
		this.name = name;
		catalyst = false;
		inhibitor = false;
		stoichiometry = 1;
		setRate(rate);
	}

	public SBAComponent clone() {
		SBAComponent c = new SBAComponent(name, (RateNode) ASTSupport
				.copy(rate));
		c.stoichiometry = stoichiometry;
		c.catalyst = catalyst;
		c.inhibitor = inhibitor;
		/* if (apparentRates != null) {
			c.apparentRates = new HashMap<String, List<RateNode>>();
			for(Map.Entry<String, List<RateNode>> me : apparentRates.entrySet())
				c.apparentRates.put(me.getKey(), new LinkedList<RateNode>(me.getValue()));
		} */
		return c;
	}

	/**
	 * Comparison is based on Component names, just like equality. Implemented
	 * for purposes of ordering components in ODEs or similar.
	 * 
	 */
	public int compareTo(SBAComponent c) {
		return name.compareTo(c.name);
	}

	/**
	 * Equality is based on the name of the component only. Properties such as
	 * rate or behaviour (catalyst, inhibitor or normal component) are
	 * irrelevant for comparison as a component can only exist once in any
	 * reaction reactant list.
	 * 
	 * @param c
	 * @return result of comparison
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof SBAComponent))
			return false;
		return name.equals(((SBAComponent) o).name);
	}

	public String getName() {
		return name;
	}

	public int getStoichiometry() {
		return stoichiometry;
	}
	
	public int hashCode() {
		return name.hashCode();
	}

	public boolean isCatalyst() {
		return catalyst;
	}

	public boolean isInhibitor() {
		return inhibitor;
	}

	void makeCatalyst() {
		catalyst = true;
		inhibitor = false;
	}

	void makeInhibitor() {
		inhibitor = true;
		catalyst = false;
	}

	void makeReactant() {
		inhibitor = false;
		catalyst = false;
	}

	void setRate(RateNode rate) {
		if (rate == null)
			throw new NullPointerException("Invalid rate for component " + name
					+ ". Rates must be defined.");
		this.rate = rate;
	}

	public void setStoichiometry(int stoichiometry) {
		if ((catalyst || inhibitor) && stoichiometry != 1)
			throw new IllegalArgumentException(
					"Illegal Argument for Component "
							+ name
							+ ". Stoichiometric rate for catalysts or inhibitors is not legal.");
		if (stoichiometry < 1)
			throw new IllegalArgumentException(
					"Illegal Argument for Component "
							+ name
							+ ". Stoichiometric values must greater than or equal to 1.");
		this.stoichiometry = stoichiometry;
	}

	String toCMDL() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stoichiometry; i++) {
			if (catalyst || inhibitor)
				sb.append("$");
			sb.append(name).append(" + ");
		}
		sb.delete(sb.length() - 3, sb.length());
		return sb.toString();
	}

	public String toString() {
		if (stoichiometry > 1)
			return stoichiometry + "." + name;
		else
			return name;
	}

}