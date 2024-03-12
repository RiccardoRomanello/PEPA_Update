package uk.ac.ed.inf.pepa.ode.internal.odetojava.ssCtrlModules;

import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.StdMet;

/*
  class contains 2 error estimation schemes, one for embedded methods, and the other for
  methods with step doubling
*/
public class ErrorEstimator
{
    public ErrorEstimator()
    {
    }

    public static double[] embedded_estimate(double h, double[] xold, double[] xnew, double[] xe, double[] atol, double[] rtol, double p, double aMax, double aMin, double alpha)
    {
	// initializations

	int n = xold.length;

	double[] tau = new double[n];
	double[] ad1 = new double[n];
	double[] dq1 = new double[n];
	double hOpt;
	double epsilon;
	double hNew;

	// calculations

	StdMet.tau(tau, xold, xnew, atol, rtol);   // get the array of
	   // tolerances for this step

	StdMet.arraydiff(ad1, xnew, xe);   // (higer order solution - lower order solution)
	StdMet.dotQuo(dq1, ad1, tau);   // ad1/tau

	epsilon = StdMet.rmsNorm(dq1);   // epsilon = rmsNorm(ad1/tau)

	// calculate h and see if it is optimal

	hOpt = h * Math.pow((1.0/epsilon), 1.0/p);   // calculate
	   // optimal stepsize for next step

	hNew = Math.min(aMax * h, Math.max(aMin * h, alpha*hOpt));
	   // calculate hNew with h and hOpt

	double[] estimation = new double[2];

	estimation[0] = epsilon;
	estimation[1] = hNew;

	return(estimation);
    }

    public static double[] stepdoubling_estimate(double h, double[] eta1, double[] eta2, double[] xtemp, double atol, double rtol, double p, double amax, double alpha)
    {
	// initializations

	int n = eta1.length;

	double[] diff1 = new double[n];
	double[] err = new double[n];
	double[] eps = new double[n];
	double[] est = new double[n];
	double norm;
	double hopt;
	double hNew;

	// calculations

	StdMet.arraydiff(diff1, eta2, eta1);   // get error
	StdMet.stam(err, 1/(Math.pow(2, p) - 1), diff1);    

	StdMet.epsilon(eps, xtemp, eta1, atol, rtol);   // get epsilon
	StdMet.dotQuo(est, err, eps);

	norm = StdMet.rmsNorm(est);   // take norm of est

	// calculate h and see if it is optimal

	hopt = h/(Math.pow(Math.pow(2, p)*StdMet.rmsNorm(est), (1/(p + 1))));

	hNew = Math.min(amax * h, Math.max((1/amax) * h, alpha*2*hopt));
	   // calculate hNew with h and hOpt

	double[] estimation = new double[3];

	estimation[0] = hNew;
	estimation[1] = hopt;
	estimation[2] = norm;

	return(estimation);
    }
}
