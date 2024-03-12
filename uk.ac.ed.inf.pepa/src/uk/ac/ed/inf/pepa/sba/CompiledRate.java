/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import java.util.Map;

import org.systemsbiology.math.Expression;
import org.systemsbiology.math.Symbol;
import org.systemsbiology.math.Value;
import org.systemsbiology.math.Expression.Element;
import org.systemsbiology.math.Expression.ElementCode;

import uk.ac.ed.inf.pepa.parsing.*;
import uk.ac.ed.inf.pepa.parsing.BinaryOperatorRateNode.Operator;

/**
 * 
 * @author ajduguid
 * 
 */
public class CompiledRate implements Cloneable {

	private static class PassiveRateVisitor extends VEVisitor {

		public int getWeight(RateNode rate) {
			weight = 0;
			try {
				rate.accept(this);
			} catch (Exception e) {
				weight = -1;
			}
			return weight;
		}

		int weight;

		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			if (rate.getOperator() != Operator.PLUS)
				throw new IllegalStateException(
						"Passive apparent rates in SBA models should only use addition.");
			rate.getLeft().accept(this);
			rate.getRight().accept(this);
		}

		public void visitPassiveRateNode(PassiveRateNode passive) {
			weight += passive.getMultiplicity();
		}
	}

	private static class RatesVisitor extends VEVisitor {

		Operator currentOp;
		Element element;
		StringBuilder textualForm;
		double lastRate;

		Map<String, RateNode> sbaRates;

		private String[] splitRate;

		/**
		 * Simplifies as much as possible. Removes all mathematical operators
		 * between hard-coded numbers and computes mathematical expressions for
		 * Element object where arguments are static. Simplifies textual
		 * representation if hard-coded numbers are used and the result is
		 * easily represented by certain precision.
		 */
		public void visitBinaryOperatorRateNode(BinaryOperatorRateNode rate) {
			Element leftElement, rightElement;
			StringBuilder rightTextualForm;
			Operator leftOp, rightOp;
			double leftRate, rightRate;

			rate.getRight().accept(this);
			rightOp = currentOp;
			rightRate = lastRate;
			rightTextualForm = textualForm;
			rightElement = element;
			rate.getLeft().accept(this);
			leftOp = currentOp;
			leftRate = lastRate;
			leftElement = element;
			currentOp = rate.getOperator();
			element = currentOp.getElement();
			element.mFirstOperand = leftElement;
			element.mSecondOperand = rightElement;
			if (!(Double.isNaN(rightRate) || Double.isNaN(leftRate))) {
				// Simplify as much as possible for internal rate tree
				lastRate = currentOp.evaluate(leftRate, rightRate);
				element = new Element(lastRate);
				splitRate = Double.toString(lastRate).split("\\.");
				// Value can be precisely printed
				if ((splitRate[1].length() <= precision) && textualForm == null
						&& rightTextualForm == null) {
					currentOp = null;
					return;
				}
			} else
				lastRate = Double.NaN;
			if (textualForm == null)
				textualForm = (new StringBuilder()).append(leftRate);
			if (leftOp != null
					&& (leftOp.precedence() < currentOp.precedence() || leftOp
							.equals(Operator.DIV)))
				textualForm.insert(0, "(").append(")");
			textualForm.append(currentOp);
			if (rightTextualForm == null)
				rightTextualForm = (new StringBuilder()).append(rightRate);
			if (rightOp != null
					&& (rightOp.precedence() <= currentOp.precedence() || rightOp
							.equals(Operator.DIV)))
				rightTextualForm.insert(0, "(").append(")");
			textualForm.append(rightTextualForm);
		}

		public void visitRateDoubleNode(RateDoubleNode doubleRate) {
			lastRate = doubleRate.getValue();
			element = new Element(lastRate);
			currentOp = null;
			textualForm = null;
		}

		public void visitVariableRateNode(VariableRateNode variableRate) {
			if (sbaRates != null) {
				RateNode rate = sbaRates.get(variableRate.getName());
				if (rate == null)
					super.visitVariableRateNode(variableRate);
				rate.accept(this);
			} else {
				element = new Element(ElementCode.SYMBOL);
				element.mSymbol = new Symbol(variableRate.getName());
				currentOp = null;
				lastRate = Double.NaN;
			}
			textualForm = new StringBuilder(variableRate.getName());
		}
	}

	private static class StringVisitor extends RatesVisitor {

		public void visitPassiveRateNode(PassiveRateNode passive) {
			currentOp = null;
			textualForm = new StringBuilder();
			int i = passive.getMultiplicity();
			if (i > 1) {
				textualForm.append(i).append("*");
				currentOp = Operator.MULT;
			}
			textualForm.append(PassiveRateNode.INFTY);
		}
	}

	private static final int precision = 5;

	private static PassiveRateVisitor prv = new PassiveRateVisitor();
	private static RatesVisitor rv = new RatesVisitor();
	private static StringVisitor sv = new StringVisitor();

	public synchronized static CompiledRate compileRate(RateNode rateNode,
			Map<String, RateNode> rates) {
		rv.sbaRates = rates;
		rateNode.accept(rv);
		CompiledRate compiledRate = new CompiledRate();
		if (!Double.isNaN(rv.lastRate))
			compiledRate.numerator = rv.lastRate;
		else
			compiledRate.lastOp = rv.currentOp;
		compiledRate.textualForm = rv.textualForm;
		compiledRate.element = rv.element;
		return compiledRate;
	}

	public synchronized static CompiledRate passive(SBAComponent component) {
		int i = prv.getWeight(component.rate);
		if (i == -1)
			return null;
		CompiledRate c = new CompiledRate(i);
		c = c.op(Operator.MULT, new CompiledRate(component));
		return c;
	}

	public synchronized static boolean isPassive(SBAComponent component) {
		return prv.getWeight(component.rate) > 0;
	}

	public synchronized static CompiledRate min(CompiledRate first,
			CompiledRate second) {
		CompiledRate c = new CompiledRate();
		if(first.lastOp != null && first.lastOp.equals(second.lastOp)) {
			String ll, lr, rl, rr, common = null, ul = null, ur = null;
			Element ce = null, te = new Element(ElementCode.MIN);
			ll = first.textualForm.substring(0, first.opIndex);
			lr = first.textualForm.substring(first.opIndex+1);
			rl = second.textualForm.substring(0, second.opIndex);
			rr = second.textualForm.substring(second.opIndex+1);
			if(ll.equals(rl)) {
				common = ll;
				ce = (Element) first.element.mFirstOperand.clone();
				ul = lr;
				te.mFirstOperand = (Element) first.element.mSecondOperand.clone();
				ur = rr;
				te.mSecondOperand = (Element) second.element.mSecondOperand.clone();
			} else if(ll.equals(rr)) {
				common = ll;
				ce = (Element) first.element.mFirstOperand.clone();
				ul = lr;
				te.mFirstOperand = (Element) first.element.mSecondOperand.clone();
				ur = rl;
				te.mSecondOperand = (Element) second.element.mFirstOperand.clone();
			} else if(lr.equals(rl)) {
				common = lr;
				ce = (Element) first.element.mSecondOperand.clone();
				ul = ll;
				te.mFirstOperand = (Element) first.element.mFirstOperand.clone();
				ur = rr;
				te.mSecondOperand = (Element) second.element.mSecondOperand.clone();
			} else if(lr.equals(rr)) {
				common = lr;
				ce = (Element) first.element.mSecondOperand.clone();
				ul = ll;
				te.mFirstOperand = (Element) first.element.mFirstOperand.clone();
				ur = rl;
				te.mSecondOperand = (Element) second.element.mFirstOperand.clone();
			}
			// Can simplify
			if(common != null) {
				c.textualForm.append(common).append(first.lastOp).append("min(");
				c.textualForm.append(ul).append(",").append(ur).append(")");
				c.element = first.lastOp.getElement();
				c.opIndex = common.length();
				c.element.mFirstOperand = ce;
				c.element.mSecondOperand = te;
				c.lastOp = first.lastOp;
				return c;
			}
		}
		c.textualForm.append("min(");
		c.textualForm.append((first.textualForm == null ? (first.numerator/first.denominator) : first.textualForm));
		c.textualForm.append(",");
		c.textualForm.append((second.textualForm == null ? (second.numerator/second.denominator) : second.textualForm));
		c.textualForm.append(")");
		if(Double.isNaN(first.numerator) || Double.isNaN(second.numerator)) {
			c.element = new Element(ElementCode.MIN);
			c.element.mFirstOperand = (Element) first.element.clone();
			c.element.mSecondOperand = (Element) second.element.clone();
		} else {
			c.numerator = Math.min((first.numerator/first.denominator), (second.numerator/second.denominator));
			c.element = new Element(c.numerator);
			if(first.textualForm == null && second.textualForm == null)
				c.textualForm = null;
		}
		return c;
	}

	public synchronized static CompiledRate pow(CompiledRate compiledRate,
			double power) {
		CompiledRate c = new CompiledRate();
		if(compiledRate.textualForm != null)
			c.textualForm.append(compiledRate.textualForm).append("^").append(power);
		else
			c.textualForm = null;
		if(Double.isNaN(compiledRate.numerator)) {
			c.element = new Element(ElementCode.POW);
			c.element.mFirstOperand = (Element) compiledRate.element.clone();
			c.element.mSecondOperand = new Element(power);
		} else {
			c.numerator = Math.pow((compiledRate.numerator/compiledRate.denominator), power);
			c.element = new Element(c.numerator);
		}
		return c;
	}

	public synchronized static CompiledRate theta(CompiledRate compiledRate) {
		CompiledRate c = new CompiledRate();
		if(compiledRate.textualForm != null)
			c.textualForm.append("theta(").append(compiledRate.textualForm).append(")");
		else
			c.textualForm = null;
		if(Double.isNaN(compiledRate.numerator)) {
			c.element = new Element(ElementCode.THETA);
			c.element.mFirstOperand = (Element) compiledRate.element.clone();
		} else {
			c.numerator = (compiledRate.numerator > 0 ? 1 : 0);
			c.element = new Element(c.numerator);
		}
		return c;
	}

	public synchronized static String toString(RateNode rateNode) {
		sv.sbaRates = null;
		rateNode.accept(sv);
		if (!Double.isNaN(sv.lastRate))
			return Double.toString(sv.lastRate);
		return sv.textualForm.toString();
	}

	private Element element;

	// If set to null by a method no variables are used and so it can be represented numerically.
	private StringBuilder textualForm = new StringBuilder();

	private Operator lastOp = null;

	// NaN represents a value that can not be statically evaluated.
	private double numerator = Double.NaN, denominator = 1.0;
	
	private int opIndex;

	private CompiledRate() {
	}

	CompiledRate(int value) {
		numerator = value;
		textualForm = null;
		element = new Element(value);
	}

	CompiledRate(SBAComponent component) {
		textualForm.append(component.getName());
		element = new Element(ElementCode.SYMBOL);
		element.mSymbol = new Symbol(textualForm.toString());
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof CompiledRate))
			return false;
		CompiledRate rate = (CompiledRate) o;
		if (!(Double.isNaN(numerator) || Double.isNaN(rate.numerator)) && numerator != rate.numerator)
			return false;
		if (denominator != rate.denominator)
			return false;
		if (!textualForm.toString().equals(rate.textualForm.toString()))
			return false;
		return true;
	}

	// TODO is this correct?
	public boolean isExpression() {
		return Double.isNaN(numerator);
	}

	public boolean isFraction() {
		return !Double.isNaN(numerator) && denominator != 1.00;
	}

	public CompiledRate op(Operator operator, CompiledRate rightRate) {
		CompiledRate newRate = new CompiledRate();
		// Both are represented as simple numbers
		if(textualForm == null && rightRate.textualForm == null) {
			newRate.textualForm = null;
			newRate.numerator = operator.evaluate((numerator / denominator),(rightRate.numerator / rightRate.denominator));
			newRate.element = new Element(newRate.numerator);
			return newRate;
		}
		// Removal of common factors when adding and subtracting
		if((operator.equals(Operator.MINUS) || operator.equals(Operator.PLUS)) && (Operator.MULT.equals(lastOp) || Operator.DIV.equals(lastOp) && lastOp.equals(rightRate.lastOp))) {
			String ll, lr, rl, rr, common = null, ul = null, ur = null;
			Element ce = null, te = operator.getElement();
			ll = textualForm.substring(0, opIndex);
			lr = textualForm.substring(opIndex+1);
			rl = rightRate.textualForm.substring(0, rightRate.opIndex);
			rr = rightRate.textualForm.substring(rightRate.opIndex+1);
			if(ll.equals(rl)) {
				common = ll;
				ce = (Element) element.mFirstOperand.clone();
				ul = lr;
				te.mFirstOperand = (Element) element.mSecondOperand.clone();
				ur = rr;
				te.mSecondOperand = (Element) rightRate.element.mSecondOperand.clone();
			} else if(ll.equals(rr)) {
				common = ll;
				ce = (Element) element.mFirstOperand.clone();
				ul = lr;
				te.mFirstOperand = (Element) element.mSecondOperand.clone();
				ur = rl;
				te.mSecondOperand = (Element) rightRate.element.mFirstOperand.clone();
			} else if(lr.equals(rl)) {
				common = lr;
				ce = (Element) element.mSecondOperand.clone();
				ul = ll;
				te.mFirstOperand = (Element) element.mFirstOperand.clone();
				ur = rr;
				te.mSecondOperand = (Element) rightRate.element.mSecondOperand.clone();
			} else if(lr.equals(rr)) {
				common = lr;
				ce = (Element) element.mSecondOperand.clone();
				ul = ll;
				te.mFirstOperand = (Element) element.mFirstOperand.clone();
				ur = rl;
				te.mSecondOperand = (Element) rightRate.element.mFirstOperand.clone();
			}
			// Can simplify
			if(common != null) {
				newRate.textualForm.append(common).append(lastOp).append("(");
				newRate.textualForm.append(ul).append(operator).append(ur);
				newRate.textualForm.append(")");
				newRate.element = lastOp.getElement();
				newRate.opIndex = common.length();
				newRate.element.mFirstOperand = ce;
				newRate.element.mSecondOperand = te;
				newRate.lastOp = lastOp;
				if(!(Double.isNaN(numerator) || Double.isNaN(rightRate.numerator)))
					newRate.numerator = operator.evaluate((numerator / denominator),(rightRate.numerator / rightRate.denominator));
				return newRate;
			}
		}
		if(operator.equals(Operator.DIV) && Operator.MULT.equals(lastOp) && Operator.MULT.equals(rightRate.lastOp)) {
			String ll, lr, rl, rr, ul = null, ur = null;
			newRate.element = operator.getElement();
			ll = textualForm.substring(0, opIndex);
			lr = textualForm.substring(opIndex+1);
			rl = rightRate.textualForm.substring(0, rightRate.opIndex);
			rr = rightRate.textualForm.substring(rightRate.opIndex+1);
			if(ll.equals(rl)) {
				ul = lr;
				newRate.element.mFirstOperand = (Element) element.mSecondOperand.clone();
				ur = rr;
				newRate.element.mSecondOperand = (Element) rightRate.element.mSecondOperand.clone();
			} else if(ll.equals(rr)) {
				ul = lr;
				newRate.element.mFirstOperand = (Element) element.mSecondOperand.clone();
				ur = rl;
				newRate.element.mSecondOperand = (Element) rightRate.element.mFirstOperand.clone();
			} else if(lr.equals(rl)) {
				ul = ll;
				newRate.element.mFirstOperand = (Element) element.mFirstOperand.clone();
				ur = rr;
				newRate.element.mSecondOperand = (Element) rightRate.element.mSecondOperand.clone();
			} else if(lr.equals(rr)) {
				ul = ll;
				newRate.element.mFirstOperand = (Element) element.mFirstOperand.clone();
				ur = rl;
				newRate.element.mSecondOperand = (Element) rightRate.element.mFirstOperand.clone();
			}
			if(ul != null) {
				newRate.textualForm.append(ul).append(operator).append(ur);
				newRate.lastOp = operator;
				newRate.opIndex = ul.length();
				return newRate;
			}
		}
		boolean first = !(operator.equals(Operator.MULT) && !Double.isNaN(numerator) && denominator == numerator);
		boolean second = !((operator.equals(Operator.MULT) || operator.equals(Operator.DIV))
				&& !Double.isNaN(rightRate.numerator) && rightRate.denominator == rightRate.numerator);
		if (first) {
			if (textualForm == null)
				if (denominator != 1.0)
					newRate.textualForm.append("(").append(numerator).append("/").append(denominator).append(")");
				else
					newRate.textualForm.append(numerator);
			else {// else expression
				if (lastOp != null
						&& (lastOp.precedence() < operator.precedence() || operator
								.equals(Operator.DIV)))
					newRate.textualForm.append("(").append(textualForm).append(")");
				else
					newRate.textualForm.append(textualForm);
			}
		}
		if (first && second) {
			newRate.opIndex = newRate.textualForm.length();
			newRate.textualForm.append(operator);
			newRate.lastOp = operator;
			newRate.element = operator.getElement();
			newRate.element.mFirstOperand = (Element) this.element.clone();
			newRate.element.mSecondOperand = (Element) rightRate.element
					.clone();
		} else
			newRate.element = (first ? (Element) this.element.clone()
					: (Element) rightRate.element.clone());
		if (second) {
			if (rightRate.textualForm == null)
				if (rightRate.denominator != 1.0)
					newRate.textualForm.append("(").append(rightRate.numerator).append("/").append(rightRate.denominator).append(")");
				else
					newRate.textualForm.append(rightRate.numerator);
			else {// else expression
				if (rightRate.lastOp != null
						&& (rightRate.lastOp.precedence() < operator
								.precedence() || operator.equals(Operator.DIV)))
					newRate.textualForm.append("(").append(rightRate.textualForm).append(")");
				else
					newRate.textualForm.append(rightRate.textualForm);
			}
		}
		return newRate;
	}
	
	public CompiledRate stabilisedRatio(CompiledRate denominator) {
		CompiledRate ratio = new CompiledRate();
		ratio.element = Operator.DIV.getElement();
		String sn = null, sd = null;
		if(Operator.MULT.equals(lastOp) && Operator.MULT.equals(denominator.lastOp)) {
			String ll = textualForm.substring(0, opIndex);
			String lr = textualForm.substring(opIndex+1);
			String rl = denominator.textualForm.substring(0, denominator.opIndex);
			String rr = denominator.textualForm.substring(denominator.opIndex+1);
			if(ll.equals(rl)) {
				sn = lr;
				ratio.element.mFirstOperand = (Element) element.mSecondOperand.clone();
				sd = rr;
				ratio.element.mSecondOperand = (Element) denominator.element.mSecondOperand.clone();
			} else if(ll.equals(rr)) {
				sn = lr;
				ratio.element.mFirstOperand = (Element) element.mSecondOperand.clone();
				sd = rl;
				ratio.element.mSecondOperand = (Element) denominator.element.mFirstOperand.clone();
			} else if(lr.equals(rl)) {
				sn = ll;
				ratio.element.mFirstOperand = (Element) element.mFirstOperand.clone();
				sd = rr;
				ratio.element.mSecondOperand = (Element) denominator.element.mSecondOperand.clone();
			} else if(lr.equals(rr)) {
				sn = ll;
				ratio.element.mFirstOperand = (Element) element.mFirstOperand.clone();
				sd = rl;
				ratio.element.mSecondOperand = (Element) denominator.element.mFirstOperand.clone();
			}
		}
		if( sn == null) {
			sn = textualForm.toString();
			sd = denominator.textualForm.toString();
			ratio.element.mFirstOperand = (Element) element.clone();
			ratio.element.mSecondOperand = (Element) denominator.element.clone();
		}
		ratio.lastOp = Operator.DIV;
		ratio.opIndex = sn.length();
		Element stab = Operator.PLUS.getElement();
		stab.mFirstOperand = ratio.element.mSecondOperand;
		stab.mSecondOperand = Operator.MINUS.getElement();
		stab.mSecondOperand.mFirstOperand = new Element(1);
		stab.mSecondOperand.mSecondOperand = new Element(ElementCode.THETA);
		stab.mSecondOperand.mSecondOperand.mFirstOperand = (Element) ratio.element.mFirstOperand.clone();
		ratio.element.mSecondOperand = stab;
		ratio.textualForm.append(sn).append(Operator.DIV).append("(");
		ratio.textualForm.append(sd).append("+1-theta(").append(sn).append("))");
		return ratio;
	}

	public Value returnAsValue() {
		if (!Double.isNaN(numerator))
			return new Value(numerator / denominator);
		return new Value(new Expression(element));
	}

	public double toDouble() {
		if (Double.isNaN(numerator))
			throw new IllegalStateException(
					"CompiledRate cannot simultaneously be an exact value and an expression.");
		return (numerator / denominator);
	}

	public CompiledRate clone() {
		CompiledRate c = new CompiledRate();
		if(textualForm != null)
			c.textualForm = new StringBuilder(textualForm);
		c.numerator = numerator;
		c.denominator = denominator;
		c.lastOp = lastOp;
		c.opIndex = opIndex;
		if (element != null)
			c.element = (Element) element.clone();
		return c;
	}

	public String toString() {
		if(textualForm != null)
			return textualForm.toString();
		return Double.toString(numerator / denominator);
	}
}