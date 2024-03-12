package uk.ac.ed.inf.pepa.ode.internal.odetojava;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Btableau;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.IWriterCallback;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODE;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODEFileWriter;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Span;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.StdMet;
import Jama.Matrix;

/*
 class contains an algorithm for integrating an ODE using an
 Implicit-Explicit Runge-Kutta routine
 */
public class Imex {
	// static methods (used as the interface to solver)

	public static void imex(ODE function, Span tspan, double[] u0, double h,
			Btableau butcher, String fileName, String stats,
			IWriterCallback callback) throws DifferentialAnalysisException {
		Imex.imex(function, tspan, u0, h, butcher, fileName, stats, 1000,
				callback);
	}

	public static void imex(ODE function, Span tspan, double[] u0, double h,
			Btableau butcher, String fileName, String stats, int nPoints,
			IWriterCallback callback) throws DifferentialAnalysisException {
		Imex imex = new Imex(function, tspan, u0, h, butcher, fileName, stats,
				callback); // initialize
		// the
		// IMEX
		// object

		imex.setNPoints(nPoints); // amount of points written to file set to
		// value user specifies

		imex.routine(); // run the routine
	}

	private IWriterCallback writer;

	// helper methods (for static methods)

	public void setNPoints(int nPoints) {
		this.nPoints = nPoints;
	}

	// constructors

	/*
	 * constructor sets up the class to do a IMEX scheme given the ODE, an
	 * interval of (temporal) integration, an initial value, a stepsize, a
	 * Butcher tableau, and a few strings for special features
	 */
	public Imex(ODE function, Span tspan, double[] u0, double h,
			Btableau butcher, String fileName, String stats,
			IWriterCallback callback) {
		// span check
		if (writer == null)
			this.writer = new ODEFileWriter();
		else
			this.writer = callback;

		if (!tspan.get_property()) // if the span is out of order halt
		// immediately
		{ // with message
			throw new IllegalArgumentException("Improper span: times are out of order");
		}

		this.f = function; // store the function
		this.t0 = tspan.get_t0(); // store the initial time
		this.tf = tspan.get_tf(); // store the final time
		this.h = h; // get the stepsize
		this.s = butcher.getbl(); // store how many stages this Runge-Kutta
		// method will execute in (the explicit part is one step more)

		if (h <= 0) // error testing for h (must be greater than zero)
		{
			System.out.println("Stepsize must be greater than zero");
			System.exit(0);
		}

		if ((tf - t0) < h) // test to see if stepsize is smaller than time span
		{
			System.out.println("Stepsize is larger than tspan");
			System.exit(0);
		}

		this.u0 = new double[u0.length]; // store the initial value
		StdMet.arraycpy(this.u0, u0);
		this.n = u0.length; // get dimension of ODE

		// Butcher tableau calculations

		this.a = new double[butcher.getah()][butcher.getal()]; // initialize
		this.b = new double[butcher.getbl()]; // arrays a, b, ahat & bhat
		this.ahat = new double[butcher.getahath()][butcher.getahatl()];
		this.bhat = new double[butcher.getbhatl()];

		StdMet.matrixcpy(this.a, butcher.get_a()); // store Butcher arrays a, b,
		StdMet.arraycpy(this.b, butcher.get_b()); // ahat and bhat
		StdMet.matrixcpy(this.ahat, butcher.get_ahat());
		StdMet.arraycpy(this.bhat, butcher.get_bhat());

		this.steps = (int) (Math.floor((tf - t0) / h)); // get approximation on
		// number of steps
		this.count = 0; // routine will execute in

		// initializations for miscellaneous special features

		if (stats.equals("Stats_On")) // the statistics feature
		{
			this.stats_on = true;
			this.stats_intermediate = false;
		} else if (stats.equals("Stats_Intermediate")) {
			this.stats_on = true;
			this.stats_intermediate = true;
		} else if (stats.equals("Stats_Off")) {
			this.stats_on = false;
			this.stats_intermediate = false;
		} else {
			System.out
					.println("String parameter must be either: 1) \"Stats_On\" 2) \"Stats_Intermediate\" or 3) \"Stats_Off\"");
			System.exit(0);
		}

		this.fileName = fileName; // the file writing feature

		// output warnings for cases that are not worth stopping the program for

		if (tspan.get_timesLength() > 0) // if user has entered in a time span
		// that
		{ // suggests interpolation, notify user that interpolation will not be
			// done
			System.out.println();
			System.out
					.println("note that this solver does not do interpolation . . .");
			System.out.println();
		}
	}

	// methods

	/*
	 * method computes the solution to the ODE depending on parameters and
	 * calculations given to and done by the constructor
	 */
	public void routine() throws DifferentialAnalysisException {
		// beginning message

		System.out.println();
		System.out.println("Begin Implicit-Explicit Runge-Kutta Routine . . .");
		System.out.println(); // leave a space at start (for screen output)

		// initializations

		this.told = t0; // initialize told to t0 (the starting time)

		this.uold = new double[n]; // initialize the arrays uold, and unew,
		this.unew = new double[n]; // two arrays that will represent each of
		// the arrays of the solution as it integrates

		StdMet.arraycpy(uold, u0); // current u is u0, starting integration

		this.jacobian = new double[n][n]; // initialize finite difference
		// jacobian matrix
		this.I = new double[n][n]; // initialize n-by-n matrix

		for (int i = 0; i < n; i++)
			// make I an identity matrix
			for (int j = 0; j < n; j++)
				if (i == j)
					I[i][j] = 1.0;

		// initialize some temporary variables to be used in integration

		double[] f1 = new double[n]; // store function evaluation 1
		double[] f2 = new double[n]; // store function evaluation 2
		double deltaX; // a factor of each element of the u
		double[] yTemp = new double[n]; // uold with an element changed by
		// deltaX

		double[] ad1 = new double[n]; // store an array difference
		double[] stam1 = new double[n]; // store an array * scalar
		double[] stam2 = new double[n];
		double[] as1 = new double[n]; // store an array + array
		double[] mtam1 = new double[n]; // store a matrix * array

		double[] fn = new double[n]; // store nonstiff part of equation
		double[] g = new double[n]; // store stiff part of equation

		double[] temp = new double[n]; // temporary arrays
		double[] a1 = new double[n];
		double[][] m1 = new double[n][n]; // temporary matrices
		double[][] m2 = new double[n][1];

		boolean done = false; // switch that determines loop termination
		double norm; // used to test to see if solver has gone unstable

		// open a writer for the solution file (soln at each step)

		writer.openFile(fileName);

		/*
		 * the integration loop
		 */
		while (!done) {
			if ((told + h) > tf) // if, in the next step, h takes us over the
			{ // the boundaries, reduce h to make it there exactly, and this
				h = tf - told; // will, therefore, be the last step
				done = true;
			}

			f1 = f.f(told, uold); // evaluate function

			for (int i = 0; i < n; i++) {
				deltaX = deltaY * Math.abs(uold[i]); // get element * factor

				if (deltaX < deltaMin) // deltaX must not go below threshold
					deltaX = deltaMin; // value deltaMin

				StdMet.arraycpy(yTemp, uold); // let yTemp equal uold
				yTemp[i] += deltaX; // then increment ith by deltaX
				f2 = f.f(told, yTemp); // evaluate function where ith element
				// is incremented by deltaX

				StdMet.arraydiff(ad1, f2, f1); // ad1 = f2 - f1
				StdMet.stam(stam1, 1 / deltaX, ad1); // (f2 - f1) / deltaX

				for (int j = 0; j < n; j++)
					// fill column i with stam1
					jacobian[j][i] = stam1[j];
			}

			this.k = new double[s][n]; // initialize matrix k for implicit part
			this.khat = new double[s + 1][n]; // initialize matrix khat for
			// explicit part

			f1 = f.f(told, uold); // evaluate function
			StdMet.mtam(g, jacobian, uold); // evaluate stiff part of function
			StdMet.arraydiff(fn, f1, g); // evaluate nonstiff part of function

			StdMet.arraycpy(khat[0], fn); // start filling in matrix k given
			// khat[0]

			for (int i = 0; i < s; i++) {
				StdMet.zero_out(temp);

				for (int j = 0; j < i; j++) {
					StdMet.stam(stam1, a[i][j], k[j]); // get stiff factor
					StdMet.stam(stam2, ahat[i + 1][j], khat[j]); // get nonstiff
					// factor
					StdMet.arraysum(as1, stam1, stam2); // add the 2 factors
					// together
					StdMet.arraysum(temp, temp, as1); // add them to temp
				}

				StdMet.stam(stam1, ahat[i + 1][i], khat[i]); // get last of the
				// non-
				// stiff factor (due to the fact that there is one more)
				StdMet.arraysum(temp, temp, stam1); // add this to temp
				StdMet.stam(temp, h, temp); // multiply temp by h
				StdMet.arraysum(temp, temp, uold); // add uold to temp

				for (int j = 0; j < n; j++) // m1 = h*a[i][i]*jacobian
				{
					StdMet.stam(stam1, h * a[i][i], jacobian[j]); // fill in a
					// row at
					StdMet.arraycpy(m1[j], stam1); // a time
				}

				for (int j = 0; j < n; j++)
					// I - h*a[i][j]*jacobian
					StdMet.arraydiff(m1[j], I[j], m1[j]);

				StdMet.mtam(mtam1, jacobian, temp); // jacobian * temp

				Matrix A = new Matrix(m1); // let m1 represent A

				for (int j = 0; j < n; j++)
					// transpose mtam (m2 = mtam')
					m2[j][0] = mtam1[j];

				Matrix B = new Matrix(m2); // let m2 represent B

				Matrix X = A.solve(B); // solve A*X = B
				a1 = X.getColumnPackedCopy(); // turn X back to an array

				StdMet.arraycpy(k[i], a1); // last row of k = a1

				StdMet.stam(stam1, h * a[i][i], k[i]); // h*a[i][i]*k[i]
				StdMet.arraysum(temp, temp, stam1); // temp = temp +
				// h*a[i][i]*k[i]

				f1 = f.f(told, temp); // evaluate function
				StdMet.mtam(g, jacobian, temp); // evaluate stiff part of
				// function
				StdMet.arraydiff(fn, f1, g); // evaluate nonstiff part of
				// function

				StdMet.arraycpy(khat[i + 1], fn); // khat[i + 1] = ith function
				// evaluation
			}

			StdMet.zero_out(temp); // reset the temporary array

			for (int i = 0; i < s; i++) {
				StdMet.stam(stam1, b[i], k[i]); // b[i]*k[i]
				StdMet.stam(stam2, bhat[i], khat[i]); // bhat[i]*khat[i]
				StdMet.arraysum(as1, stam1, stam2); // b[i]*k[i] +
				// bhat[i]*khat[i]
				StdMet.arraysum(temp, temp, as1); // temp = temp + as1
			}

			StdMet.stam(stam1, bhat[s], khat[s]); // bhat[s+1]*khat[s+1]
			StdMet.arraysum(temp, temp, stam1); // temp = temp + stam1
			StdMet.stam(temp, h, temp); // temp = temp * h

			StdMet.arraysum(uold, uold, temp); // increment uold by temp for
			// next
			told += h; // step, and by h for told

			norm = StdMet.rmsNorm(uold); // take norm of uold

			if (norm != norm) // check to see if norm is NaN, if
			{ // so, something has gone wrong, solver is unstable
				System.out.println("unstable . . . aborting");

				writer.closeFile(); // close the writer before halting

				return; // halt routine
			}

			if (steps <= nPoints) // if there is less than nPoints points, write
			// every time
			{
				writer.writeToFile(told, uold);
			} else {
				if (count % (steps / nPoints) == 0) // output solution (thus
				// far)
				{ // into file (but only allow ~nPoints of these point to go
					writer.writeToFile(told, uold); // in the file as time is a
					// factor)
				}
			}

			if (stats_on) // output statistics (if user has chosen such)
			{
				System.out.println("t = " + told); // output the results

				if (!stats_intermediate) // do not output if only on
				{ // intermediate statistics mode
					System.out.println("soln = ");
					StdMet.arrayprt(uold);
				}

				System.out.println();
			}

			count++;
		}

		System.out.println("done");
		System.out.println("final t = " + told); // output final time
		System.out.println("final soln  ="); // and solution
		StdMet.arrayprt(uold);

		// file stuff

		writer.writeToFile(told, uold); // put final time and solution into file

		writer.closeFile(); // now that we are done, close the writer
	}

	// instance variables

	private ODE f; // stores the ODE function
	private double t0; // the initial time of integration
	private double tf; // the final time of integration
	private double[] u0; // the initial value of the function
	private double h; // the stepsize of the integration (reduced and end to
	// meet tf)

	private double told; // stores current time
	private double[] uold; // s to store solution points
	private double[] unew;

	private int n; // dimension of ODE
	private int s; // the stage of the method

	private double[][] a; // Butcher array a for implicit part
	private double[] b; // Butcher array b for implicit part
	private double[][] ahat; // Butcher array ahat for explicit part
	private double[] bhat; // Butcher array bhat for explicit part

	private int steps; // an approximation on how many steps solver will take
	private int count; // a counter of steps that solver actually takes

	private String fileName; // name of file solution is written to (each step)
	private boolean stats_on; // whether to report status at each step
	private boolean stats_intermediate; // whether to report just a few
	// statistics

	private double[][] jacobian; // the finite difference jacobian matrix for
	// each step
	private double[][] I; // an identity matrix

	private double[][] k; // matrix of slopes for the implicit part
	private double[][] khat; // matrix of slopes for the explicit part

	private final double deltaMin = 0.0001; // constants used in finite
	// difference
	private final double deltaY = Math.sqrt(0.0001); // jacobian calculation

	private int nPoints; // number of points to write to file
}
