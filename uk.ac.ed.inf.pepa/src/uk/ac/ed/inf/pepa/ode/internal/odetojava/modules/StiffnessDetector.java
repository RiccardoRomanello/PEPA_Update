package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;


/*
  class contains a stiffness detector which returns a number representing how close
  to the stability region of the method the solver is, when solving the IVP
*/
public class StiffnessDetector
{
    public StiffnessDetector()
    {
    }

    public static double calc_hRho(double h, double[] K7, double[] K6, double[] g7, double[] g6)
    {
	int n = K7.length;

	double[] diff1 = new double[n];   // 2 array differences
	double[] diff2 = new double[n];
	double norm1;
	double norm2;
	double rho;
	double hRho;   // how close to boundary we are

	StdMet.arraydiff(diff1, K7, K6);   // K7 - K6
	StdMet.arraydiff(diff2, g7, g6);   // g7 - g6

	norm1 = StdMet.rmsNorm(diff1);   // ||K7 - K6||
	norm2 = StdMet.rmsNorm(diff2);   // ||g7 - g6||

	rho = norm1/norm2;
	hRho = h*rho;	
 
	return(hRho);
   }
}
