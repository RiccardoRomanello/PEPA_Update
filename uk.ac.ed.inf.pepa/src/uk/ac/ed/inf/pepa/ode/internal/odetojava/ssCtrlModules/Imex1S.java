package uk.ac.ed.inf.pepa.ode.internal.odetojava.ssCtrlModules;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Btableau;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODE;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.StdMet;
import Jama.Matrix;

/*
   class does one step of any IMEX scheme
*/
public class Imex1S
{
     // constructors
   
     public Imex1S(Btableau butcher)
     {
          this.s = butcher.getbl();   // store how many stages this Runge-Kutta
             // scheme will execute in
		
          this.a = new double[butcher.getah()][butcher.getal()];   // initialize
          this.b = new double[butcher.getbl()];   // a,b, ahat and bhat of the
          this.ahat = new double[butcher.getahath()][butcher.getahatl()];
          this.bhat = new double[butcher.getbhatl()];   // Butcher tableau for
             // the doOneStep method using arrays of length specified by the
             // Butcher tableau passed to it

          StdMet.matrixcpy(this.a, butcher.get_a());   // fill these a,b, ahat and bhat
          StdMet.arraycpy(this.b, butcher.get_b());   // arrays using Butcher tableau
          StdMet.matrixcpy(this.ahat, butcher.get_ahat());  // passed to constructor
          StdMet.arraycpy(this.bhat, butcher.get_bhat());
     }
   
     // methods

     /*
        method does one step of an IMEX method given the ODE, the time,
        the initial value, and the stepsize (h)
     */
     public double[] doOneStep(ODE function, double t, double[] u, double h) throws DifferentialAnalysisException
     {
          // initializations

          this.t = t;   // initialize the starting time
          this.h = h;   // initialize the stepsize
          this.n = u.length;   // dimension of ODE

          this.f = function;   // the function

          uold = new double[n];   // initialize the vectors uold, and unew,
          unew = new double[n];   // the solutions on either side of a step

          this.jacobian = new double[n][n];   // initialize jacobian matrix
          this.I = new double[n][n];   // initialize an n-by-n matrix

          for(int i= 0; i< n; i++)   // make I an identity matrix
               for(int j= 0; j< n; j++)
                    if(i == j)
                         I[i][j] = 1.0;

          // initialize some temporary variables to be used in the integration

          double[] f1 = new double[n];   // store function evaluation 1
          double[] f2 = new double[n];   // store function evaluation 2
          double deltaX;   // a factor of each element of the u vector
          double[] yTemp = new double[n];   // store uold with an element changed by
             // deltaX

          double[] ad1 = new double[n];   // store an array difference
          double[] stam1 = new double[n];   // store an array * scalar
          double[] stam2 = new double[n];
          double[] as1 = new double[n];   // store an array + array
          double[] mtam1 = new double[n];   // store a matrix * array

          double[] fn = new double[n];   // store nonstiff part of equation
          double[] g = new double[n];   // store stiff part of equation

          double[] temp = new double[n];   // temporary arrays
          double[] a1 = new double[n];
          double[][] m1 = new double[n][n];   // temporary matrices
          double[][] m2 = new double[n][1];

          StdMet.arraycpy(uold, u);   // current u, doing 1 integration step

          // now we do one step of the integration 

          f1 = f.f(t, uold);   // evaluate function
         
          for(int i= 0; i< n; i++)   // loop to compute a jacobian (of finite
          {   // difference derivatives)
               deltaX = deltaY * Math.abs(uold[i]);   // get element * factor
            
               if(deltaX < deltaMin)   // deltaX must not go below threshold
                    deltaX = deltaMin;   // value deltaMin
            
               StdMet.arraycpy(yTemp, uold);   // let yTemp equal uold
               yTemp[i] += deltaX;  // then increment ith element by deltaX
               f2 = f.f(t, yTemp);   // evaluate function where ith element
                  // is incremented by deltaX

               StdMet.arraydiff(ad1, f2, f1);   // ad1 = f2 - f1
               StdMet.stam(stam1, 1/deltaX, ad1);   // (f2 - f1) / deltaX

               for(int j= 0; j< n; j++)   // fill column i with stam1
                    jacobian[j][i] = stam1[j];
          }

          this.k = new double[s][n];   // initialize matrix k for implicit part
          this.khat = new double[s + 1][n];   // init khat for explicit part

          f1 = f.f(t, uold);   // evaluate function
          StdMet.mtam(g, jacobian, uold);   // evaluate stiff part of function
          StdMet.arraydiff(fn, f1, g);   // evaluate nonstiff part of function
         
          StdMet.arraycpy(khat[0], fn);   // start filling in array of k values,
             // given khat[0]
          for(int i= 0; i< s; i++)
          {
               StdMet.zero_out(temp);
            
               for(int j= 0; j< i; j++)
               {
                    StdMet.stam(stam1, a[i][j], k[j]);   // get stiff factor
                    StdMet.stam(stam2, ahat[i + 1][j], khat[j]);   // get nonstiff factor
                    StdMet.arraysum(as1, stam1, stam2);   // add the 2 factors together
                    StdMet.arraysum(temp, temp, as1);   // add them to temp
               }

               StdMet.stam(stam1, ahat[i + 1][i], khat[i]);   // get last of the non-
                  // stiff factor (due to the fact that there's one more)
               StdMet.arraysum(temp, temp, stam1);   // add this to temp
               StdMet.stam(temp, h, temp);   // multiply temp by h
               StdMet.arraysum(temp, temp, uold);   // add uold to temp

               for(int j= 0; j< n; j++)   // m1 = h*a[i][i]*jacobian
               {
                    StdMet.stam(stam1, h * a[i][i], jacobian[j]);   // fill in a row at
                    StdMet.arraycpy(m1[j], stam1);   // a time
               }

               for(int j= 0; j< n; j++)   // I - h*a[i][j]*jacobian
                    StdMet.arraydiff(m1[j], I[j], m1[j]);
           
               StdMet.mtam(mtam1, jacobian, temp);   // jacobian * temp
           
               Matrix A = new Matrix(m1);   // let m1 represent A

               for(int j= 0; j< n; j++)   // transpose mtam1 (m2 = mtam1')
                    m2[j][0] = mtam1[j];

               Matrix B = new Matrix(m2);   // let m2 represent B

               Matrix X = A.solve(B);   // solve A*X = B
               a1 = X.getColumnPackedCopy();   // turn X back to an array
            
               StdMet.arraycpy(k[i], a1);   // last row of k = a1

               StdMet.stam(stam1, h * a[i][i], k[i]);   // h*a[i][i]*k[i]            
               StdMet.arraysum(temp, temp, stam1);   // temp = temp + h*a[i][i]*k[i]
           
               f1 = f.f(t, temp);   // evaluate function
               StdMet.mtam(g, jacobian, temp);   // evaluate stiff part of function
               StdMet.arraydiff(fn, f1, g);   // evaluate nonstiff part of function

               StdMet.arraycpy(khat[i + 1], fn);   // khat[i + 1] = function evaluation[i]
          }

          StdMet.zero_out(temp);   // reset the temporary array
         
          for(int i= 0; i< s; i++)
          {
               StdMet.stam(stam1, b[i], k[i]);   // b[i]*k[i]
               StdMet.stam(stam2, bhat[i], khat[i]);   // bhat[i]*khat[i]
               StdMet.arraysum(as1, stam1, stam2);   // b[i]*k[i] + bhat[i]*khat[i]
               StdMet.arraysum(temp, temp, as1);   // temp = temp + as1
          }

          StdMet.stam(stam1, bhat[s], khat[s]);   // bhat[s+1]*khat[s+1]
          StdMet.arraysum(temp, temp, stam1);   // temp = temp + stam1
          StdMet.stam(temp, h, temp);   // temp = temp * h

          StdMet.arraysum(unew, uold, temp);   // increment uold by temp for next step

          return(unew);   // return the results
     }

     // instance variables

     private int s;   // number of stages of the IMEX method

     private double[][] a;   // matrix a of Butcher tableau: implicit part
     private double[] b;   // array b of Butcher tableau: implicit part
     private double[][] ahat;   // matrix ahat of Butcher tableau: explicit part
     private double[] bhat;   // array bhat of Butcher tableau: explicit part

     private ODE f;   // the ODE
     private double t;   // the time of the step
     private double h;   // the stepsize of the iteration
     private int n;   // dimension of the ODE

     private double[] uold;   // initial solution value
     private double[] unew;   // final solution value
     private double[][] jacobian;   // finite difference jacobian matrix for a step
     private double[][] I;   // an n-by-n identity matrix

     private double[][] k;   // the matrix of k values
     private double[][] khat;   // the matrix of khat values
   
     private final double deltaMin = 0.0001;   // constants used in finite difference
     private final double deltaY = Math.sqrt(0.0001);   // jacobian calculation
}