package uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver;

import uk.ac.ed.inf.pepa.jhydra.matrix.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.BitSet;

public class PassageTimeSolver {

	private Matrix myMatrix;

	// If we change this it's a bit inconvenient the
	// results of the cdf/pdf are calculated at the same time
	// so it makes little sense to do one without the other.
	private boolean doPassage = true;
	private boolean doTransient = false;

	private long trans, tangible;
	private long MAX_N;

	private double _q;
	private double t_bottom, t_top, t_step;

	private BitSet target_states;
	private Vector<Double> diagonal, rhs, sum;
	private double[] weight;

	public PassageTimeSolver(Matrix m, double[] s, BitSet t, double start,
			double stop, double step) {

		System.out.println("Starting construction of PassageTimeSolver...");

		myMatrix = m;
		trans = 0;

		// MAX_N = info->getHops();

		MAX_N = -1;

		/*
		 * doPassage = info->getPassage(); doTransient = info->getTransient();
		 */

		t_bottom = start;
		t_top = stop;
		t_step = step;

		tangible = myMatrix.getTangible();

		target_states = t;

		weight = s;

		System.out.println("Finished construction of PassageTimeSolver...");

	}

	public PassageTimeResults uniformise(String basename, String cdfName, String pdfName) {
		double[] pi, result;

		_q = myMatrix.uniformiseMatrix(target_states, doPassage);

		System.out.println("_q = " + _q);

		pi = new double[(int) tangible];
		result = new double[(int) tangible];

		// Rather than setting these all to length MAX_N in the case where no
		// maximum has been specified in the model file, we will instead do one
		// pass
		// through the Erlang terms to determine the max hops for each t, and
		// then
		// a second pass to actually store the values (moved further down code)

		double[] sum_pi_target;

		long pdf_max_hops = -1;
		long cdf_max_hops = -1;
		long max_hops = -1;

		// this is where we work out the number of hops for each t in the case
		// where
		// the modeller has not specified it in the .mod file.

		// when doing both the pdf and cdf at the same time, we don't know if
		// the max_hops will be the same for the pdf and cdf terms, so we
		// calculate both and take the maximum to be on the safe side - this
		// may lead to some extra calculation for one, but we'll have to go
		// to max_hops anyway for the other.....

		System.out.println("Preparing to calculate a value for max_hops...");

		if (MAX_N == -1) {

			int temp_max;

			if (10000000 / tangible > 10000)
				temp_max = 10000000 / (int) tangible;
			else
				temp_max = 10000;

			pdf_max_hops = calc_left_terms(temp_max, t_top, false);

			if (doPassage) {
				// cdf_max_hops = calc_left_terms(NULL, MAX_N, t_top, true,
				// false);
				cdf_max_hops = calc_left_terms(temp_max, t_top, true);
			}

			if (pdf_max_hops > cdf_max_hops)
				max_hops = pdf_max_hops;
			else
				max_hops = cdf_max_hops;

		} else {

			max_hops = MAX_N;
		}

		System.out.println("max_hops has been set to " + max_hops + "...");

		sum_pi_target = new double[(int) (max_hops) + 1];

		for (int n = 0; n < tangible; n++)
			pi[n] = weight[n];

		int converged = 0;
		for (int hop = 1; hop <= max_hops; hop++) {

			if (converged == 0)
				myMatrix.transMultiply(pi, result);

			sum_pi_target[hop] = 0;

			converged = 1;
			for (int n = 0; n < tangible; n++) {
				if (Math.abs(pi[n] - result[n]) > 1e-10)
					converged = 0;
				pi[n] = result[n];
				if (target_states.get(n)) {
					sum_pi_target[hop] += pi[n];
				}
			}

			if ((hop % 100) == 0) {
				System.out.println("done matrix multiplication " + hop + " of "
						+ max_hops + " ...");
			}
		}

		double timePoint = t_bottom;
		int numberTimePoints = 0;
		// Work out the number of time points that there will be
		for (numberTimePoints = 0; timePoint <= t_top; timePoint += t_step) {
			numberTimePoints++;
		}
		// Don't forget to reset timePoint;
		timePoint = t_bottom;

		// Set up the passage results
		PassageTimeResults ptResults = new PassageTimeResults(numberTimePoints, cdfName, pdfName);

		for (int count = 0; timePoint <= t_top; count++, timePoint += t_step) {

			double pdf_answer = 0.0;
			double cdf_answer = 0.0;
			
			
			// double timePoint = (count+1) * t_step ;
			// If we haven't reached the start time then just
			// loop back until we do.
			// if (timePoint < t_bottom)
			// continue ;

			int had_non_zero = 0;

			for (int hop = 1; hop <= max_hops; hop++) {

				double term = log_erlang(hop, _q, timePoint)
						* sum_pi_target[hop];

				if (term > 1e-20)
					had_non_zero = 1;

				pdf_answer += term;

				if (doPassage) {
					cdf_answer += log_erlang_cdf(hop, _q, timePoint)
							* sum_pi_target[hop];
				}

				if (MAX_N != -1 && hop > 5 && had_non_zero == 1 && term < 1e-30) {
					break;
				}

			}
			
			// We are getting cdf answers that are slightly above 1.0
			// such as: 1.0000000006130552
            // I suspect that there is a slight mis-calculation going on
			// somewhere. However for now we will paper over the cracks by
			// setting the cdf_answer to the minimum of itself and 1.0.
			// First checking that it isn't WAY over 1.0, in which case we
			// have no choice but to baulk.
			if (cdf_answer > 1.00001){
				System.out.println("Serious error, cdf value much larger than 1, it is: " + cdf_answer);
				System.exit(1);
			}
			cdf_answer = Math.min(1.0, cdf_answer);
			
			// Update the passage time results, we take special consideration
			// for the case in which the time point is 0.0 since this will
			// cause the cdf to return NaN, but really it should be just 0.0
			// as there is no chance you can complete before any time as
			// elapsed, I add this in because it makes graphs look much nicer
			// if they can go from 0.0
			//@TODO: the pdf_answer (last one) is wrong, this also produces
			// NaN, but the answer is not zero (in some cases).
			if (timePoint == 0.0){
				ptResults.updateTimePoint(count, 0.0, 0.0, pdf_answer);
			} else {
				ptResults.updateTimePoint(count, timePoint, cdf_answer, pdf_answer);
			}
		}
		// For now print out the results, though this should
		// probably be taken out.
		// ptResults.printOutResults(basename);

		return ptResults;
	}

	private double log_erlang(int n, double q, double t) {
		if (doPassage) {
			double logn = 0;
			for (int k = 2; k < n; k++)
				logn += Math.log((double) k);
			logn += q * t;
			double logp = (double) n * Math.log(q) + (double) (n - 1)
					* Math.log(t) - logn;
			return Math.exp(logp);
		} else {
			double logn = 0;
			for (int k = 2; k <= n; k++)
				logn += Math.log((double) k);
			logn += q * t;
			double logp = (double) n * Math.log(q) + (double) (n) * Math.log(t)
					- logn;
			return Math.exp(logp);
		}
	}

	private double log_erlang_cdf(int n, double q, double t) {

		double sum = 0;

		double logn = 0;

		for (int i = 0; i < n; i++) {
			// long double logn = 0;
			// for (int k=2; k<=i; k++)
			// logn += log(k);
			if (i >= 2)
				logn += Math.log((double) i);
			// assert(fabs(logn2 - logn) < 1e-05);

			double logc = i * (Math.log(q) + Math.log(t)) - (logn + q * t);
			sum += Math.exp(logc);
		}
		if (sum > 1.0)
			sum = 1.0;
		return 1.0 - sum;
	}

	private int calc_left_terms(int max, double t, boolean doCDF) {

		int n;

		//double cdfterm;

		// In C++ we initialise cdfterm, this is mostly to get rid of the
		// warning
		// about the possible use of it uninitialised
		// cdfterm = log_erlang_cdf(1, _q, t);

		int had_non_zero = 0;

		for (n = 1; n < max + 2; n++) {

			double term = log_erlang(n, _q, t); // _q*a[n-1]*exp(-_q*t);
												// /*_q*a[n-1]*exp(-_q*t)**/

			//if (doCDF) {
				//cdfterm = log_erlang_cdf(n, _q, t);
			//}

			if (term > 1e-20)
				had_non_zero = 1;

			if (n > 5 && (had_non_zero == 1) && term < 1e-30)
				break;

			if ((n % 100) == 0)
				System.out.println("term t = " + t + " n = " + n);

			if (n == max) {
				System.out.println("Erlang terms have not decayed to 0 by n="
						+ n + "...");
				System.out
						.println("Suggest lowering the t-range or reducing rates...");
				System.exit(-1);
			}

		}

		return n;
	}

}
