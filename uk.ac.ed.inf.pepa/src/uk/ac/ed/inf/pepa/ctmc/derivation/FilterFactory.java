/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation;

import uk.ac.ed.inf.pepa.ctmc.derivation.filters.ActionFilter;
import uk.ac.ed.inf.pepa.ctmc.derivation.filters.NegationFilter;
import uk.ac.ed.inf.pepa.ctmc.derivation.filters.PatternMatchingFilter;
import uk.ac.ed.inf.pepa.ctmc.derivation.filters.SequentialComponentFilter;
import uk.ac.ed.inf.pepa.ctmc.derivation.filters.SteadyStateThresholdFilter;

/**
 * This factory creates filters for the state space.
 * 
 * @author mtribast
 * 
 */
public class FilterFactory {

	/**
	 * Used by pattern matching filters, component separator
	 */
	public static final String VERTICAL_BAR = ":";

	/**
	 * Used by pattern matching filters, wildcard
	 */
	public static final String WILDCARD = "*";

	public enum Operator {

		LT("<"), GT(">"), GET(">="), LET("<="), EQ("=="), NEQ("!=");

		private String fValue;

		Operator(String value) {
			this.fValue = value;
		}

		public String toString() {
			return fValue;
		}
	};

	/**
	 * Creates a filter that filters according to the local states of the
	 * system.
	 * 
	 * @param stateSpace
	 * @param componentName
	 * @param operator
	 * @param numCopies
	 * @return
	 * @throws IllegalArgumentException
	 *             the component is invalid
	 * @throws NullPointerException
	 *             if state space is null
	 */
	public static IStateSpaceFilter createSequentialComponentFilter(
			String componentName, Operator operator, int numCopies) {
		return new SequentialComponentFilter(componentName, operator, numCopies);
	}

	/**
	 * Has no effect if the state space has not been solved.
	 * 
	 * @param stateSpace
	 * @param operator
	 * @param threshold
	 * @return
	 * @throws NullPointerException
	 *             if operator or state space is null
	 */
	public static IStateSpaceFilter createSteadyStateThreshold(
			Operator operator, double threshold) {
		return new SteadyStateThresholdFilter(operator, threshold);
	}

	/**
	 * All the sequential components in the pattern must exist.
	 * 
	 * @param stateSpace
	 * @param pattern
	 * @return
	 * @throws IllegalArgumentException
	 *             if state space is null, or the components specified in the
	 *             pattern do not exist
	 */
	public static IStateSpaceFilter createPatternMatchingFilter(String pattern) {
		return new PatternMatchingFilter(pattern);
	}

	/**
	 * Creates a filter that negates the given one.
	 * 
	 * @param filter
	 * @return
	 * @throws IllegalArgumentException
	 *             if filter is null
	 */
	public static IStateSpaceFilter createNegation(IStateSpaceFilter filter) {
		if (filter == null)
			throw new NullPointerException("Filter is null");
		return new NegationFilter(filter);
	}

	/**
	 * Creates a filter that OR's the given filters.
	 * 
	 * @param filters
	 * @return
	 * @throws NullPointerException
	 *             if the array is null
	 */
	public static IStateSpaceFilter createOr(final IStateSpaceFilter[] filters) {
		if (filters == null)
			throw new NullPointerException("Filter array is null");
		return new IStateSpaceFilter() {
		
			public IFilterRunner getRunner(final IStateSpace ss) {
				
				return new OrRunner(ss, filters);
			}

		};
	}
	
	private static class OrRunner implements IFilterRunner {
		
		private IFilterRunner[] runners;
		
		public OrRunner(IStateSpace stateSpace, IStateSpaceFilter[] filters) {
			runners = new IFilterRunner[filters.length];
			for (int  i = 0; i < filters.length; i++) {
				runners[i] = filters[i].getRunner(stateSpace);
			}
		}

		public boolean select(int stateIndex) {
			for (IFilterRunner r : runners)
				if (r.select(stateIndex))
					return true;
			return false;
		}
		
	}

	/**
	 * Creates a filter that filters states with unnamed local components.
	 * 
	 * @param stateSpace
	 * @return
	 */
	public static IStateSpaceFilter createUnnamedStatesFilter() {
		return new IStateSpaceFilter() {
	
			public IFilterRunner getRunner(final IStateSpace ss) {

				return new IFilterRunner() {
	
					public boolean select(int stateIndex) {
						for (int i = 0; i < ss
								.getNumberOfSequentialComponents(stateIndex); i++)
							if (ss.isUnnamed(stateIndex, i))
								return true;
						return false;
					}

				};
			}

		};
	}

	/**
	 * Creates a filter for state which have an incoming or an outgoing
	 * transition of a particular type.
	 * 
	 * @param stateSpace
	 *            the state space to filter
	 * @param actionType
	 *            the action type
	 * @param incoming
	 *            the direction of the transition. If <code>true</code>, filter
	 *            states with incoming transition. Else it filter states with
	 *            outgoing transitions.
	 * @return
	 */
	public static IStateSpaceFilter createActionFilter(String actionType,
			boolean incoming) {
		if (actionType == null)
			throw new NullPointerException();
		return new ActionFilter(actionType, incoming);
	}

}
