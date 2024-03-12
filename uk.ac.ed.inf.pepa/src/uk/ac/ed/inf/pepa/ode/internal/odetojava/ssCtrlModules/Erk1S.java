package uk.ac.ed.inf.pepa.ode.internal.odetojava.ssCtrlModules;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Btableau;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODE;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.StdMet;

/*
   class does one step of any ERK routine
*/
public class Erk1S
{
     // constructors
   
     public Erk1S(Btableau butcher, int n)
     {

          this.n = n;   // dimension of the ODE
          this.s = butcher.getbl();   // store how many stages this Runge-Kutta
             // scheme will execute in
		
          this.a = new double[butcher.getal()][butcher.getal()];   // initialize
          this.b = new double[butcher.getbl()];   // a,b and c of the Butcher
          this.c = new double[butcher.getcl()];   // tableau for the doOneStep
             // method using arrays of length specified by the Butcher tableau
             // passed to it

          StdMet.matrixcpy(this.a, butcher.get_a());   // fill these a,b and c arrays
          StdMet.arraycpy(this.b, butcher.get_b());   // using Butcher tableau passed to 
          StdMet.arraycpy(this.c, butcher.get_c());  // constructor

          this.FSALenabled = false;   // get from the Butcher
             // tableau whether the scheme is first same as last or not

          /* note that FSAL functionality does not work so always false */

          K = new double[s][n];   // a matrix of K values (s rows of size n)
             // this is initialized up here to make use of FSAL functionality
     }

     // methods

     /*
        method does one step of an ERK method given the ODE, the time,
        the function, and the stepsize (h), and whether the routine is on the first
        step or not (if it is not, it can use FSAL functionality)
     */
     public double[] doOneStep(ODE function, double t, double[] x, double h, boolean firstStep, boolean justAccepted) throws DifferentialAnalysisException
     {
          this.t = t;   // initialize the starting time
          this.h = h;   // initialize the stepsize

          if(x.length != n)
          {
               throw new IllegalArgumentException("length of vector disagrees with dimension of the problem");
          }

          f = function;   // the function
		
          xold = new double[n];   // initialize the arrays xold, and xnew,
          xnew = new double[n];   // the solutions on either side of a step

          // initialize some temporary variables to compute solution of the ODE
		
          double[] sigma = new double[n];   // the sum for the matrix of K values
          double[] sigma2 = new double[n];   // the sum for the solution (xnew)
          double[] as1 = new double[n];   // temporary variable for an array sum
          double[] stam1 = new double[n];   // temporary variable for a scalar*array
          double[] fp = new double[n];   // temporary variable to store function evaluation
		
          StdMet.arraycpy(xold, x);   // pass x to xold (initial value)
		
          /*
             this loop calculates each row i in the K matrix using the
             Butcher tableau, its inner loop (the sum), and function
             evaluations
          */
          for(int i= 0; i< s; i++)   // loop for the matrix of K values
          {
               /*
                  this loop calculates the ith row of the matrix of K values
                  using the ith row of matrix a of the given Butcher tableau
                  and all of the rows of K before this ith row
               */
               for(int j= 0; j< i; j++)   // loop for each array
               {
                    StdMet.stam(stam1, a[i][j], K[j]);   // a[i][j]*K[j]
                    StdMet.arraysum(sigma, sigma, stam1);  // sigma = sigma + a[i][j]*K[j]
               }

               if(!((i == 0) && !firstStep && FSALenabled))
               {
                    StdMet.stam(stam1, h, sigma);  // stam1 = sigma*h
                    StdMet.arraysum(as1, xold, stam1);   // as1 = xold + stam1
                    fp = f.f(t + h*c[i], as1);   // fp = f(t + h*c[i], as1)
                    StdMet.arraycpy(K[i], fp);   // set ith row of the matrix of K values to function evaluation
                    StdMet.zero_out(sigma);   // set sigma to array of zeros
               }
               else
                    if(justAccepted)   // do this only if previous step was accepted
                         StdMet.arraycpy(K[0], K[s - 1]);   // else we copy the last row of
                            // the matrix of K values from previous step into first row
                            // of the matrix of K values of the present step               
          }
			
          /*
             this loop takes the weighted average of all of the vectors in the
             matrix of K values using the b vector of the Butcher tableau
          */
          for(int i= 0; i< s; i++)   // for xnew
          {
               StdMet.stam(stam1, h*b[i], K[i]);   // h*b[i]*K[i]
               StdMet.arraysum(sigma2, sigma2, stam1);   // sigma2 = sigma2 + h*b[i]*K[i]
          }

          StdMet.arraysum(xnew, xold, sigma2);   // xnew = xold + sigma2

          return(xnew);   // return xnew
     }
   
     // instance variables

     private int s;   // number of stages of the ERK method
     private double[][] a;   // the matrix a of the Butcher tableau
     private double[] b;   // the vector b of the Butcher tableau
     private double[] c;   // the vector c of the Butcher tableau
     private boolean FSALenabled;   // whether first same as last functionality of the
        // scheme (if this scheme has the property to begin with) is enabled

     private ODE f;   // the ODE
     private int n;   // dimension of the ODE
     private double t;   // the time of the step
     private double h;   // the stepsize of the iteration
     private double[] xold;   // initial solution value
     private double[] xnew;   // final solution value
     private double[][] K;   // matrix of K values
}