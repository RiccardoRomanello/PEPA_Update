package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

public class FoxGlynn {

	// Inputs (under and overflow values taken from PRISM)
	double lambda;
	double error;
	double underflow = 1.0e-300;
	double overflow = 1.0e+300;
	
	// Outputs
	int leftTruncation;
	int rightTruncation;
	double[] weights;
	double totalWeight;
	boolean flag;
	
	public FoxGlynn(double lambda, double error) {
		this.lambda = lambda;
		this.error = error;
		double w_m = computeTruncations();
		computeWeights(w_m);
		normaliseWeights();
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public double getTotalWeight() {
		return totalWeight;
	}
	
	public double getWeight(int index) {
		return weights[index];
	}
	
	public double psi(int index) {
		if (index < leftTruncation || index > rightTruncation) {
			return 0;
		} else {
			return weights[index-leftTruncation];
		}
	}
	
	public int getLeftTruncation() {
		return leftTruncation;
	}
	
	public int getRightTruncation() {
		return rightTruncation;
	}
	
	public boolean getFlag() {
		return flag;
	}
	
	// FINDER routine from Fox-Glynn 88
	// returns w_m
	private double computeTruncations() {
		int m = (int)Math.floor(lambda);
		
		if (lambda <= 0) {
			// Case 1
			leftTruncation = 0;
			rightTruncation = 0;
			flag = false;
			return 0;
		} else if (lambda < 25) {
			// Case 2
			leftTruncation = 0;
			if (Math.exp(-lambda) < underflow) {
				flag = false;
				return 0;
			}
		} else {
			// lambda >= 25 - last part of Case 3
			// Use corollary 2 of section 4 to find L
			// *** (note: code transcribed from PRISM. this should be fixed)
			double sqrt2 = Math.sqrt(2.0);
			double sqrtl = Math.sqrt(lambda);
			double b = (1 + 1/lambda) * Math.exp (0.125/lambda);
			double startk = 1/(sqrt2 * sqrtl);
			double stopk = (m - 1.5)/(sqrt2 * sqrtl);
			double k;
			for (k = startk; k <= stopk; k += 3.0) {
				if (b * Math.exp(-0.5*k*k)/(k * Math.sqrt(2.0 * Math.PI)) <= error/2) break;
			}
			if (k > stopk) k = stopk;	
			leftTruncation = (int) Math.floor(m - k*sqrtl - 1.5);
			// ***
			flag = true;
		}
		
		// Now look at the right truncation
		if (lambda < 400) {
			// Second part of Case 2
			// use corollary 1 of section 4, with lambda = 400, to find R
			// *** (note: code transcribed from PRISM. this should be fixed)
			double sqrt2 = Math.sqrt(2.0);
			double sqrtl = 20;
			double a = 1.0025 * Math.exp(0.0625) * sqrt2;
			double startk = 1.0/(2 * sqrt2 * 400);
			double stopk = sqrtl/(2 * sqrt2);
			double k;
			for (k = startk; k <= stopk; k += 3.0) {
				double d = 1.0/(1 - Math.exp((-2.0/9.0)*(k*sqrt2*sqrtl + 1.5)));
				double f = a * d * Math.exp (-0.5*k*k) / (k * Math.sqrt(2.0 * Math.PI));
				if (f <= error/2.0) break;
			}
			if (k > stopk) k = stopk;
			rightTruncation = (int) Math.ceil(m + k*sqrt2*sqrtl + 1.5);
			// ***
			flag = true;
		} else {
			// Case 3 (lambda >= 400)
			// use corollary 1 of section 4, with actual lambda, to find R
			// *** (note: code transcribed from PRISM. this should be fixed)
			double sqrt2 = Math.sqrt(2.0);
			double sqrtl = Math.sqrt(lambda);
			double a = (1 + 1/lambda) * Math.exp(0.0625) * sqrt2;
			double startk = 1/(2 * sqrt2 * lambda);
			double stopk = sqrtl/(2 * sqrt2);
			double k;
			for (k = startk; k <= stopk; k += 3.0) {
				double d = 1.0/(1 - Math.exp((-2.0/9.0)*(k*sqrt2*sqrtl + 1.5)));
				double f = a * d * Math.exp(-0.5*k*k) / (k * Math.sqrt(2.0 * Math.PI));
				if (f <= error/2.0) break;
			}
			if (k > stopk) k = stopk;
			rightTruncation = (int) Math.ceil(m + k*sqrt2*sqrtl + 1.5);
			// ***
			flag = true;
		}
		
		if (leftTruncation < 0) leftTruncation = 0;
		
		//System.out.println("L = " + leftTruncation);
		//System.out.println("R = " + rightTruncation);
		
		// allocate weights and compute w_m:
		weights = new double[rightTruncation - leftTruncation + 1];
		double w_m = overflow / (Math.pow(10, 10) * (rightTruncation - leftTruncation));
		weights[m-leftTruncation] = w_m;
		return w_m;
	}
	
	// WEIGHTER routine from Fox-Glynn 88
	private void computeWeights(double w_m) {
		// Initialise
		double m = Math.floor(lambda);
		if (!flag) return;
		
		// Down
		int j = (int) m;
		while (j > leftTruncation) {
			weights[j-1-leftTruncation] = (j / lambda) * weights[j-leftTruncation];
			j = j - 1;
		}
		
		// Up
		if (lambda >= 400) {
			j = (int) m;
			while (j < rightTruncation) {
				weights[j+1-leftTruncation] = (lambda / (j+1)) * weights[j-leftTruncation];
				j = j + 1;
			}
		} else {
			// Special
			if (rightTruncation > 600) {
				flag = false;
				return;
			}
			j = (int) m;
			while (j < rightTruncation) {
				double q = lambda / (j + 1);
				if (weights[j-leftTruncation] > (underflow / q)) {
					weights[j+1-leftTruncation] = q * weights[j-leftTruncation];
					j = j + 1;
				} else {
					rightTruncation = j;
					break;
				}
			}
		}
		
		// Compute W
		totalWeight = 0;
		int s = leftTruncation;
		int t = rightTruncation;
		while (s < t) {
			if (weights[s-leftTruncation] <= weights[t-leftTruncation]) {
				totalWeight += weights[s-leftTruncation];
				s = s + 1;
			} else {
				totalWeight += weights[t-leftTruncation];
				t = t - 1;
			}
		}
		totalWeight += weights[s-leftTruncation];
		
	}
	
	private void normaliseWeights() {
		if (!flag) return;
		for (int i = 0; i < weights.length; i++) {
			weights[i] /= totalWeight;
		}
	}
	
}
