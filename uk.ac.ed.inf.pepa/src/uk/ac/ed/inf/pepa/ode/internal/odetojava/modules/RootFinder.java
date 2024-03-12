package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;


/*
  class contains methods for finding the root of a function using the safeguarded secant
  method, that is: the secant root finding method, safeguarded by bisection.  This class
  was made to be used in particular with event location feature of ODE solvers
*/
public class RootFinder
{
    // constructors

    public RootFinder(ODE function, Btableau butcher, double h, double told, double[] xold, double[][] K)
    {
	this.f = function;
	this.butcher = butcher;
	this.h = h;
	this.told = told;
	this.n = xold.length;	

	this.xold = new double[n];
	for(int j= 0; j< n; j++)
	    this.xold[j] = xold[j];

	this.s = butcher.getbl();

	this.K = new double[s][n];
	for(int j= 0; j< s; j++)
	    for(int k= 0; k< n; k++)
		this.K[j][k] = K[j][k];
    }

    // methods

    public void setup(int i)
    {
	this.i = i;
    }

    public double safeguarded_secant(double thetaA, double thetaB, double ga, double gb)
    {                                             
	this.thetaA = thetaA;
	this.thetaB = thetaB;
	this.ga = ga;
	this.gb = gb;

	sigmaInterp = new double[n];
	x_interp = new double[n];
	stam1 = new double[n];
	
	thetaStarFound = false;   // whether we found thetaStar yet (or are close enough to it)

	// check if either endpoint is the event
                                             
	if(ga == 0)
	{
	    thetaStarFound = true;
	    thetaStar = thetaA;
	}
	else
	    if(gb == 0)
	    {
		thetaStarFound = true;
		thetaStar = thetaB;
	    }
	
	secError = Math.abs(thetaB - thetaA);  // error is simply the bracket that the root exists in

	/*
	  therefore the event must occur somewhere in between, this is essentially
	  root finding problem that we are going to solve with safeguarded secant
	  method, a hybrid method of secant and bisection root finding methods, so
	  we loop until we converge to a thetaStar that is as close as the tolerance
	*/ 
	while(!thetaStarFound && (secError > SECTOL))
	{
	    thetaStar = thetaB - gb*(thetaB - thetaA)/(gb - ga);   // attemt a secant method iteration

	    /*
	      if the approximation falls outside of the
	      bracket, fall back on the reliability of
	      bisection
	    */
	    if((thetaStar <= thetaA) || (thetaStar >= thetaB))
	    {
		thetaStar = thetaA + (thetaB - thetaA)/2.0;   // a bisection iteration
                                                          
		/*
		  this loop takes the weighted average of all of the rows in the
		  K matrix using the functions of theta of the Butcher tableau
		  -> this loop is the weighted average for an ERK method and it
		  is used to interpolate two solution points
		*/
		for(int j= 0; j< s; j++)   // loop for interpolant
		{
		    StdMet.stam(stam1, h*butcher.get_btheta().f(thetaStar)[j], K[j]);   // h*f(thetaStar)[i]*K[i]

		    StdMet.arraysum(sigmaInterp, sigmaInterp, stam1);   // sigmaInterp = sigmaInterp + h*f(thetaStar)[i]*K[i]
		}
 
		StdMet.arraysum(x_interp, xold, sigmaInterp);   // x_interp = xold + sigmaInterp
                                                    
		StdMet.zero_out(sigmaInterp);   // clear out sigmaInterp for the next sigmaInterp
                                                       
		// now we have the interpolated point (x_interp) due to interpolation
                                                       
		gAtTStar = f.g(told + h*thetaStar, x_interp)[i];   // evaluate g at above point

		if(gAtTStar == 0)   // if interpolated point is the event point exactly, it has gone far enough
		    thetaStarFound = true;

		if(ga*gAtTStar >= 0)   // else we see which side that event fell on and close
		    thetaA = thetaStar;   // a side of the bracket accordingly
		else
		    thetaB = thetaStar;

		/*
		  here, error is based on the bracket
		  size only (safer than just saying
		  error is cut in half)
		*/
		secError = thetaB - thetaA;
	    }
	    else   // else go on with the faster secant iteration
	    {
		/*
		  error is estimated as the difference between the iterates
		  (thetaB of last step and thetaStar of this step) added to a small
		  value dependent on thetaStar so that error does not equal 0,
		  (because that is impossible)
		*/
		secError = Math.abs(thetaStar - thetaB) + SECTOL/4.0;
		
		/*
		  this loop takes the weighted average of all of the rows in the
		  K matrix using the functions of theta of the Butcher tableau
		  -> this loop is the weighted average for an ERK method and it
		  is used to interpolate two solution points
		*/
		for(int j= 0; j< s; j++)   // loop for interpolant
		{
		    StdMet.stam(stam1, h*butcher.get_btheta().f(thetaStar)[j], K[j]);   // h*f(thetaStar)[i]*K[i]
		    StdMet.arraysum(sigmaInterp, sigmaInterp, stam1);   // sigmaInterp = sigmaInterp + h*f(thetaStar)[i]*K[i]
		}

		StdMet.arraysum(x_interp, xold, sigmaInterp);   // x_interp = xold + sigmaInterp
                                                      
		StdMet.zero_out(sigmaInterp);   // clear out sigmaInterp for the next sigmaInterp
                                                       
		// now we have the interpolated point (x_interp) due to interpolation
                                                       
		gAtTStar = f.g(told + h*thetaStar, x_interp)[i];

		if(gAtTStar == 0)   // if interpolated point is the event point exactly, it has gone far enough
		    thetaStarFound = true;

		// else bring the bracket over to the new approx of the root (closing in on the root)

		if(ga*gAtTStar >= 0)
		    thetaA = thetaStar;
		else
		    thetaB = thetaStar;
	    }
	}

	return(thetaStar);
    }

    // instance variables

    private ODE f;
    private Btableau butcher;
    private double h;
    private double told;
    private int n;
    private double[] xold;
    private int s;
    private double[][] K;

    // looping variable

    private int i;
 
    // secant iteration

    private double thetaA;   // theta on one side of the root
    private double thetaB;   // theta on the other side of the root
    private double ga;   // function evaluation on one side of the root
    private double gb;   // function evaluation on the other side of the root
                                        
    private boolean thetaStarFound;   // whether we found thetaStar yet (or are close enough to it)
    private double thetaStar;   // the value of thetaStar (as we converge to true answer)
    private double secError;  // current error in the safeguarded secant method
                                                  
    private double gAtTStar;   // stores an evalutaion of g

    // interpolation

    private double[] sigmaInterp;
    private double[] x_interp;
    private double[] stam1;

    // finals

     private final double SECTOL = 1.0E-14;   // tol = 10^-14 for secant method (sufficient for most problems)
}
