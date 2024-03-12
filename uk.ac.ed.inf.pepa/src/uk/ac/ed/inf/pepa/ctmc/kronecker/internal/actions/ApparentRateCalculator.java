package uk.ac.ed.inf.pepa.ctmc.kronecker.internal.actions;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.KroneckerUtilities;

/**
 * This handles the correct computation of apparent rates on the Kronecker form.
 * 
 * @author msmith
 */
public class ApparentRateCalculator {
	
	private Operator operator;
	
	public ApparentRateCalculator() {
		operator = new LeafOperator();
	}
	
	private ApparentRateCalculator(Operator operator) {
		this.operator = operator;
	}
	
	public ApparentRateCalculator plus(ApparentRateCalculator calculator) {
		return new ApparentRateCalculator(new PlusOperator(operator, calculator.operator));
	}
	
	public ApparentRateCalculator min(ApparentRateCalculator calculator) {
		return new ApparentRateCalculator(new MinOperator(operator, calculator.operator));
	}
	
	public double compute(double[] rates) throws DerivationException {
		assert rates.length == operator.size();
		//System.out.println("Computing: " + Arrays.toString(rates));
		//System.out.println("  with: " + operator.toString());
		return operator.compute(rates);
	}
	
	public double compute(double[] rates, boolean[] enabled) throws DerivationException {
		assert rates.length == operator.size() && enabled.length == operator.size();
		//System.out.println("Computing: " + Arrays.toString(rates) + ", " + Arrays.toString(enabled));
		//System.out.println("  with: " + operator.toString());
		return operator.compute(rates, enabled);
	}
	
	public boolean syncWithBound(int component, boolean[] boundedComponents) {
		return operator.synchronises(component, boundedComponents);
	}
	
	public boolean[][] getChoices(double[] rates) {
		assert rates.length == operator.size();
		return operator.constructChoices(rates);
	}
	
	public String toString() {
		return operator.toString();
	}
	
	private static abstract class Operator {
		
		protected int offset = 0;
		protected int leftComponents;
		protected int rightComponents;
		
		protected Operator leftOperator;
		protected Operator rightOperator;
		
		Operator(Operator left, Operator right) {
			this.leftOperator = left;
			this.leftComponents = left == null ? 0 : left.size();
			this.rightOperator = right;
			this.rightComponents = right == null ? 0 : right.size();
			setOffset(0);
		}
		
		/**
		 * Called to create a leaf component, with no sub-operators.
		 */
		Operator() {
			this.leftComponents = 1;
			this.rightComponents = 0;
			this.leftOperator = null;
			this.rightOperator = null;
		}
		
		protected abstract double compute(double[] rates) throws DerivationException;
		
		protected abstract double compute(double[] rates, boolean[] enabled) throws DerivationException;
		
		protected abstract boolean synchronises(int component, boolean[] boundedComponents);
		
		protected abstract boolean[][] constructChoices(double[] rates);
		
		private void setOffset(int offset) {
			this.offset = offset;
			int leftSize = 0;
			if (leftOperator != null) {
				leftOperator.setOffset(offset);
				leftSize = leftOperator.size();
			}
			if (rightOperator != null) {
				rightOperator.setOffset(offset + leftSize);
			}
		}
		
		protected int size() {
			return leftComponents + rightComponents;
		}
		
	}
	
	private static class MinOperator extends Operator {
		
		MinOperator(Operator left, Operator right) {
			super(left,right);
		}

		protected double compute(double[] rates) throws DerivationException {
			double left = leftOperator.compute(rates);
			double right = rightOperator.compute(rates);
			return KroneckerUtilities.rateMin(left, right);
		}
		
		protected double compute(double[] rates, boolean[] enabled) throws DerivationException {
			double leftApparent = leftOperator.compute(rates);
			double rightApparent = rightOperator.compute(rates);
			double left = leftOperator.compute(rates, enabled);
			double right = rightOperator.compute(rates, enabled);
			if (leftApparent == 0 || rightApparent == 0) return 0;
			double answer = (left / leftApparent) * (right / rightApparent) *
				KroneckerUtilities.rateMin(leftApparent, rightApparent);
			//System.out.println(leftApparent + ", " + rightApparent + ", " + left + ", " + right + " => " + answer);
			return answer;
		}
		
		protected boolean synchronises(int component, boolean[] boundedComponents) {
			boolean inLeft = component >= offset && component < offset + leftComponents;
			if (inLeft) {
				for (int i = 0; i < rightComponents; i++) {
					if (boundedComponents[i + leftComponents + offset]) return true;
				}
			} else {
				for (int i = 0; i < leftComponents; i++) {
					if (boundedComponents[i + offset]) return true;
				}
			}
			return false;
		}
		
		protected boolean[][] constructChoices(double[] rates) {
			int numLeft = leftOperator.size();
			int numRight = rightOperator.size();
			boolean[][] left = leftOperator.constructChoices(rates);
			boolean[][] right = rightOperator.constructChoices(rates);
			boolean[][] choices = new boolean[left.length * right.length][numLeft + numRight];
			for (int i1 = 0; i1 < left.length; i1++) {
				int offset = right.length * i1;
				for (int i2 = 0; i2 < right.length; i2++) {
					for (int j = 0; j < numLeft; j++) {
						choices[offset + i2][j] = left[i1][j];
					}
					for (int j = 0; j < numRight; j++) {
						choices[offset + i2][numLeft + j] = right[i2][j];
					}
				}
			}
			return choices;
		}
		
		public String toString() {
			return "min(" + leftOperator + "," + rightOperator + ")";
		}
				
	}
	
	private static class PlusOperator extends Operator {
		
		PlusOperator(Operator left, Operator right) {
			super(left,right);
		}

		protected double compute(double[] rates) throws DerivationException {
			double left = leftOperator.compute(rates);
			double right = rightOperator.compute(rates);
			return KroneckerUtilities.ratePlus(left, right);
		}
		
		protected double compute(double[] rates, boolean[] enabled) throws DerivationException {
			double left = leftOperator.compute(rates, enabled);
			double right = rightOperator.compute(rates, enabled);
			return KroneckerUtilities.ratePlus(left, right);
		}
		
		protected boolean synchronises(int component, boolean[] boundedComponents) {
			boolean inLeft = component >= offset && component < offset + leftComponents;
			if (inLeft) {
				return leftOperator.synchronises(component, boundedComponents);
			} else {
				return rightOperator.synchronises(component, boundedComponents);
			}
		}
		
		protected boolean[][] constructChoices(double[] rates) {
			int numLeft = leftOperator.size();
			int numRight = rightOperator.size();
			boolean[][] left = leftOperator.constructChoices(rates);
			boolean[][] right = rightOperator.constructChoices(rates);
			boolean[][] choices = new boolean[left.length + right.length][numLeft + numRight];
			for (int i = 0; i < left.length; i++) {
				for (int j = 0; j < numLeft; j++) {
					choices[i][j] = left[i][j];
				}
			}
			for (int i = 0; i < right.length; i++) {
				for (int j = 0; j < numRight; j++) {
					choices[left.length + i][numLeft + j] = right[i][j];
				}
			}
			return choices;
		}
		
		public String toString() {
			return "(" + leftOperator + "+" + rightOperator + ")";
		}
				
	}
	
	private static class LeafOperator extends Operator {
		
		LeafOperator() {
			super();
		}

		protected double compute(double[] rates) throws DerivationException {
			return rates[offset];
		}
		
		protected double compute(double[] rates, boolean[] enabled) throws DerivationException {
			if (enabled[offset]) {
				return rates[offset];
			} else {
				return 0;
			}
		}
		
		protected boolean synchronises(int component, boolean[] boundedComponents) {
			return false;
		}
		
		protected boolean[][] constructChoices(double[] rates) {
			if (rates[offset] != 0) {
				boolean[][] choices = new boolean[1][1];
				choices[0][0] = true;
				return choices;
			} else {
				boolean[][] choices = new boolean[0][0];
				return choices;
			}
		}
		
		public String toString() {
			return Integer.toString(offset);
		}
				
	}
	
	
}
