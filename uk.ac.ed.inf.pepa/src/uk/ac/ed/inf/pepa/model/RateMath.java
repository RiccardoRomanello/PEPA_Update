/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 15-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.pepa.model.internal.DoMakePepaProcess;

import java.util.Map;

/**
 * This class contains methods for performing operations on rates, according to
 * the definitions in Hillston's thesis
 * 
 * @see "A Compositional Approach to Performance Modelling" (pag. 26--30)
 * @author mtribast
 * 
 */
public class RateMath {

	private static Logger logger = Logger.getLogger(RateMath.class);
	
	public static int RATE_MATH_COUNT = 0;
	
	
	/**
	 * Computes the apparent rate of action of type
	 * <code>action<code> in a component <code>process</code>
	 * @param process the component
	 * @param action the action type
	 * @return the apparent rate
	 * @see "A Compositional Approach to Performance Modelling" Def. 3.3.1 pag. 27
	 */
	public static Rate getApparentRate(Process process, Action action) {
		
		if (process instanceof Constant) {
			return getApparentRate(((Constant) process).getBinding(), action);
		}

		if (process instanceof Prefix) {

			if (action.equals(((Prefix) process).getActivity().getAction()))
				return ((Prefix) process).getActivity().getRate();
			else
				return DoMakePepaProcess.getInstance().createFiniteRate(0.0);
		}

		if (process instanceof Choice) {
			return sum(getApparentRate(((Choice) process).getLeftHandSide(),
					action), getApparentRate(((Choice) process)
					.getRightHandSide(), action));
		}

		if (process instanceof Hiding) {
			if (!((Hiding) process).getActionSet().contains(action))
				return getApparentRate(((Hiding) process).getHiddenProcess(),
						action);
			else
				return DoMakePepaProcess.getInstance().createFiniteRate(0.0);
		}

		if (process instanceof Cooperation) {
			Cooperation coop = (Cooperation) process;
			if (coop.getActionSet().contains(action) == true) {
				return min(getApparentRate(coop.getLeftHandSide(), action),
						getApparentRate(coop.getRightHandSide(), action));
			} else {
				return sum(getApparentRate(coop.getLeftHandSide(), action),
						getApparentRate(coop.getRightHandSide(), action));
			}
		}
		/**
		 * 20/02/2007 New Implementation, the old one is commented out
		 */
		if (process instanceof Aggregation) {
			// Rate oldRate = getApparentRate(((Aggregation)
			// process).getHierarchicalRepresentation(), action);
			// System.err.println(action.prettyPrint() + " oldRate " +
			// oldRate.prettyPrint());
			Aggregation aggregation = (Aggregation) process;
			Rate newRate = null;
			for (Map.Entry<Process, Integer> entry : aggregation
					.getSubProcesses().entrySet()) {

				Process singleProcess = entry.getKey();
				Rate entryRate = getApparentRate(singleProcess, action);
				entryRate = mult(entryRate, entry.getValue());
				if (newRate == null) {
					newRate = entryRate;
				} else {
					newRate = sum(newRate, entryRate);
				}
			}
			// System.err.println(action.prettyPrint() + " newRate " +
			// newRate.prettyPrint());
			return newRate;

		}

		throw new IllegalArgumentException();

	}

	/**
	 * 
	 * @param r1
	 * @param r2
	 * @return r1 + r2
	 */
	public static Rate sum(Rate r1, Rate r2) {

		// logger.debug("Sum of " + r1.prettyPrint() + " and " +
		// r2.prettyPrint());

		if (r1 instanceof PassiveRate)
			if (r2 instanceof PassiveRate)
				return DoMakePepaProcess.getInstance().createPassiveRate(
						((PassiveRate) r1).getWeight()
								+ ((PassiveRate) r2).getWeight());
			else
			/*
			 * This part has been added in order to deal with the bug 22/08/2006
			 * When the finite rate to be added is 0.0, it means that there is
			 * no apparent rate for where r2 is from.
			 */
			if (((FiniteRate) r2).getValue() == 0.0)
				return r1;
		if (r1 instanceof FiniteRate)
			if (r2 instanceof FiniteRate)
				return DoMakePepaProcess.getInstance().createFiniteRate(
						((FiniteRate) r1).getValue()
								+ ((FiniteRate) r2).getValue());
			else if (((FiniteRate) r1).getValue() == 0.0)
				return r2;

		throw new UnsupportedOperationException(
				"Sum must be between rates of the same type!");

	}

	public static FiniteRate minus(Rate r1, Rate r2) {
		if ((r1 instanceof FiniteRate) && (r2 instanceof FiniteRate))
			return DoMakePepaProcess.getInstance()
					.createFiniteRate(
							((FiniteRate) r1).getValue()
									- ((FiniteRate) r2).getValue());
		else
			throw new UnsupportedOperationException(
					"Only finite rates accepted");

	}

	public static Rate mult(Rate r1, Rate r2) {
		if ((r1 instanceof FiniteRate) && (r2 instanceof FiniteRate))
			return DoMakePepaProcess.getInstance()
					.createFiniteRate(
							((FiniteRate) r1).getValue()
									* ((FiniteRate) r2).getValue());
		else {
			if (r1 instanceof FiniteRate && r2 instanceof PassiveRate) {
				return DoMakePepaProcess.getInstance().createPassiveRate(((FiniteRate) r1).getValue()); 
			} else {
				logger.debug("R1 :" + r1.prettyPrint());
				logger.debug("R2 :" + r2.prettyPrint());
				throw new UnsupportedOperationException("Only finite rates accepted");
			}
		}
	}

	/**
	 * 
	 * @param r1
	 * @param r2
	 * @return min(r1, r2)
	 */
	public static Rate min(Rate r1, Rate r2) {
		if (r1 instanceof FiniteRate)
			if (r2 instanceof FiniteRate)
				if (((FiniteRate) r1).getValue() <= ((FiniteRate) r2)
						.getValue())
					return r1;
				else
					return r2;
			else
				// r2 is a passive Rate
				return r1;
		if (r1 instanceof PassiveRate)
			if (r2 instanceof PassiveRate)
				if (((PassiveRate) r1).getWeight() <= ((PassiveRate) r2)
						.getWeight())
					return r1;
				else
					return r2;
			else
				// r2 is active rate
				return r2;

		throw new UnsupportedOperationException("Trying to min("
				+ r1.getClass() + "," + r2.getClass() + ")");

	}

	/**
	 * 
	 * @param r1
	 * @param r2
	 * @return r1 / r2
	 */
	public static Rate div(Rate r1, Rate r2) {
		if (r1 instanceof PassiveRate && r2 instanceof PassiveRate)
			return DoMakePepaProcess.getInstance().createFiniteRate(
					((PassiveRate) r1).getWeight()
							/ ((PassiveRate) r2).getWeight());
		if (r1 instanceof FiniteRate && r2 instanceof FiniteRate)
			return DoMakePepaProcess.getInstance()
					.createFiniteRate(
							((FiniteRate) r1).getValue()
									/ ((FiniteRate) r2).getValue());
		throw new UnsupportedOperationException(
				"Div must be between rates of the same type!");
	}

	/**
	 * Multiplies a rate. It can be applied to both finite and passive rate.
	 * This method is used when the activity rate of an <code>Aggreation</code>
	 * has to be computed
	 * 
	 * @param rate
	 *            rate to be multiplied
	 * @param i
	 *            factor
	 * @return the multiplied rate, which can be safely downcasted to the rate
	 *         passed as argument
	 */
	public static Rate mult(Rate rate, int i) {
		if (rate instanceof PassiveRate) {
			return DoMakePepaProcess.getInstance().createPassiveRate(
					((PassiveRate) rate).getWeight() * i);
		}
		if (rate instanceof FiniteRate) {
			return DoMakePepaProcess.getInstance().createFiniteRate(
					((FiniteRate) rate).getValue() * i);
		}
		throw new UnsupportedOperationException("mult applied to " + rate);

	}

}