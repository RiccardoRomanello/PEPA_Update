package uk.ac.ed.inf.pepa.ode.internal.odetojava;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Btableau;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.IWriterCallback;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODE;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODEFileWriter;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.RootFinder;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Span;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.StdMet;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.StiffnessDetector;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.ssCtrlModules.ErrorEstimator;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.ssCtrlModules.Initsss;

/*
 class does does the special explicit Runge Kutta scheme known as the
 Dormand-Prince routine, which is 5th order/4th order, a 7-stage method that can solve
 for an ODE.  It also features stiffness detection and event location
 */
public class DormandPrince {
	// static methods (used as the interface to the solver)

	public static double[] dormand_prince(ODE function, Span tspan,
			double[] x0, double h0, double[] atol, double[] rtol,
			String fileName, String stiffnessDet, String eventLoc,
			String stats, IWriterCallback callback)
			throws DifferentialAnalysisException, InterruptedException {
		DormandPrince dopr = new DormandPrince(function, tspan, x0, h0, atol,
				rtol, fileName, stiffnessDet, eventLoc, stats, callback); // construct
		// the
		// DormandPrince
		// object

		dopr.setAppend(false); // whether to append to a non-empty file defaults
		// to false

		dopr.routine(); // run the routine

		return (dopr.getProfile()); // return profile (for event location
		// purposes)
	}

	// helper methods (for the static methods)

	public void setAppend(boolean append) {
		this.append = append;
	}

	// constructors
	public DormandPrince(ODE function, Span tspan, double[] x0, double h0,
			double[] atol, double[] rtol, String fileName, String stiffnessDet,
			String eventLoc, String stats, IWriterCallback callback)
			throws DifferentialAnalysisException {
		this(function, tspan, x0, h0, atol, rtol, fileName, stiffnessDet,
				eventLoc, stats);
		if (callback == null)
			writer = new ODEFileWriter();
		else
			writer = callback;

	}

	/*
	 * constructor sets up the class to do the Dormand-Prince scheme given an
	 * ODE, an interval of (temporal) integration, an initial value, an initial
	 * stepsize, two tolerance arrays, and a few strings for special features
	 */
	public DormandPrince(ODE function, Span tspan, double[] x0, double h0,
			double[] atol, double[] rtol, String fileName, String stiffnessDet,
			String eventLoc, String stats) throws DifferentialAnalysisException {
		/*
		 * section containing general calculations for a Dormand-Prince solver
		 * with automatic stepsize control (essentially dealing with the first 5
		 * parameters)
		 */

		// general initializations / calculations
		if (!tspan.get_property()) // if the span is out of order halt
		// immediately
		{ // with message
			throw new IllegalArgumentException(
					"Improper span: times are out of order");
		}

		this.f = function; // store the function
		this.t0 = tspan.get_t0(); // store initial and final
		this.tf = tspan.get_tf(); // points of time span
		this.butcher = new Btableau("dopr54"); // default Dormand-Prince Butcher
		// tableau
		this.s = butcher.getbl(); // store how many stages this Runge-Kutta
		// scheme will execute in

		// initial value and tolerances

		this.x0 = new double[x0.length];
		StdMet.arraycpy(this.x0, x0); // store the initial value
		this.atol = new double[atol.length];
		StdMet.arraycpy(this.atol, atol); // get the array of absolute
		// tolerances
		this.rtol = new double[rtol.length];
		StdMet.arraycpy(this.rtol, rtol); // get the array of relative
		// tolerances

		// Butcher array calculations / initializations

		this.a = new double[butcher.getal()][butcher.getal()]; // initialize
		this.b = new double[butcher.getbl()]; // a,b, bhat and c of the
		this.bhat = new double[butcher.getbEmbl()]; // Butcher tableau for
		this.c = new double[butcher.getcl()]; // the routine method using
		// arrays of length specified by the Butcher tableau passed to it

		StdMet.matrixcpy(this.a, butcher.get_a()); // fill these a,b, bhat and c
		StdMet.arraycpy(this.b, butcher.get_b()); // arrays using Butcher
		// tableau
		StdMet.arraycpy(this.bhat, butcher.get_bEmb()); // passed to constructor
		StdMet.arraycpy(this.c, butcher.get_c());

		this.FSALenabled = butcher.get_FSALenabled(); // get from the Butcher
		// tableau whether the scheme is first same as last or not

		// general calculations

		this.n = x0.length; // dimension of ODE

		if (atol.length != n) // error test the length of atol (must be equal to
		// the
		{ // the length of the initial value)
			throw new IllegalArgumentException(
					"Improper absolute tolerance array size");

		}

		if (rtol.length != n) // error test the length of rtol (must be equal to
		// the
		{ // the length of the initial value)
			throw new IllegalArgumentException(
					"Improper relative tolerance array size");
		}

		for (int i = 0; i < n; i++) // error test the values in atol and rtol
		// arrays
		{ // (all values must be greater than zero)
			if (atol[i] <= 0.0) {
				throw new IllegalArgumentException(
						"All abolute tolerances in array must be greater than zero");
			}

			if (rtol[i] <= 0.0) {
				throw new IllegalArgumentException(
						"All relative tolerances in array must be greater than zero");
			}
		}

		if (h0 <= 0.0) // h calculation depends on the value of h0
		{ // if h0 is less than or equal to 0 we do not have a useful value of
			// h so we call the initial step size selection routine and get h
			// from it for use of h, the idea here is that if the user wishes
			// to use the initial step size selection routine, he/she just
			// enters
			// and h <= 0 (also helps foolproof the system)
			Initsss init = new Initsss(function, tspan, x0, atol, rtol); // call
			this.h = init.get_h(); // initial step size selection routine and
			// get intitial step from it
		} else
			// else we assume the user wants to define his/her own intial
			this.h = h0; // step size, so we assign h to this value

		/*
		 * section dealing with the more specialized features of the solver,
		 * essentially dealing with the rest of the parameters
		 */

		// initializations for interpolation
		this.timesLength = tspan.get_timesLength();

		if (timesLength == 0)
			interpolant_on = false;
		else {
			interpolant_on = true;

			this.times = new double[timesLength]; // fill times array
			for (int i = 0; i < timesLength; i++)
				times[i] = tspan.get_times()[i];
		}

		// initializations for stiffness detection

		if (stiffnessDet.equals("StiffDetect_Halt"))
			this.sdetect_on = true;
		else if (stiffnessDet.equals("StiffDetect_Off"))
			this.sdetect_on = false;
		else {
			throw new IllegalArgumentException(
					"String parameter must be either: 1) \"StiffDetect_Halt\" or 2) \"StiffDetect_Off\"");
		}

		this.fevalTotal = 0; // so far no function evaluations

		// initializations for event location

		if (eventLoc.equals("EventLoc_Halt"))
			this.eventLoc_on = true;
		else if (eventLoc.equals("EventLoc_Off"))
			this.eventLoc_on = false;
		else {
			throw new IllegalArgumentException(
					"String parameter must be either: 1) \"EventLoc_Halt\" or 2) \"EventLoc_Off\"");
		}

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
			throw new IllegalArgumentException(
					"String parameter must be either: 1) \"Stats_On\" 2) \"Stats_Intermediate\" or 3) \"Stats_Off\"");
		}

		this.fileName = fileName; // the file writing feature
	}

	// methods

	/*
	 * method computes the solution to the ODE depending on parameters and
	 * calculations given to and done by the constructor
	 */
	public void routine() throws DifferentialAnalysisException,
			InterruptedException {
		// beginning message

		// System.out.println();
		// System.out.println("Begin Dormand-Prince Routine . . .");
		// System.out.println(); // leave a space at start (for screen output)

		// general initializations

		told = t0; // initialize told to t0 (the starting time)

		xold = new double[n]; // initialize the arrays xold, and xnew, two
		StdMet.arraycpy(xold, x0); // pass x0 to xold (initial value)
		xnew = new double[n]; // arrays that will represent each of the
		// arrays of the solution as it integrates
		xe = new double[n]; // initialize error estimate arrays

		K = new double[s][n]; // a matrix of K values

		nreject = 0; // no step rejections yet
		naccept = 0; // nor step acceptions

		firstStep = true; // we will be starting first step soon
		lastStep = false; // we are not done nor at our last step
		done = false;

		// interpolation initializations

		int times_inc = 0; // starting at the first time
		int times_limit = timesLength; // the final time

		double theta; // where a specified time lies between 2 calculated time
		// points
		double[] sigmaInterp = new double[n]; // stores an interpolated sum
		double[] x_interp = new double[n]; // stores an interpolated solution

		// stiffness detection initializations

		tFirst = told; // start tFirst off at t0

		// event location initializations

		double thetaA; // theta on one side of the root
		double thetaB; // theta on the other side of the root
		double ga; // function evaluation on one side of the root
		double gb; // function evaluation on the other side of the root

		double thetaStarMin; // this stores the smallest thetaStar found in
		// event interval
		double thetaStar; // stores the current thetaStar found in event
		// interval

		double toldWhen; // stores the time when the last thetaStar was computed
		double signDet; // determines if there is an event or not

		// temporary variable inititializations

		double[] sigma = new double[n]; // the sum of for each row of K
		double[] sigma2 = new double[n]; // the sum for 4th order solution point
		double[] sigma3 = new double[n]; // the sum for 5th order solution point
		double[] as1 = new double[n]; // temporary variable for an array sum
		double[] ad1 = new double[n]; // temporary variable for an array
		// difference
		double[] dq1 = new double[n]; // temporary variable for a piecewise
		// array division
		double[] stam1 = new double[n]; // temporary variable for a scalar*array
		double[] fp = new double[n]; // temporary variable to store function
		// evaluation

		double[] g1 = new double[n]; // for calculating rho for stiffness
		double[] g2 = new double[n]; // detection

		// open the ODE file writer

		// open a file with this writer for the solution at each step

		writer.openFile(fileName, append);

		/* begin integration loop */

		while (!done) // outer loop integrates the ODE
		{
			if (writer.isCanceled()) {
				writer.closeFile();
				throw new InterruptedException("ODE integration was canceled");
			}

			if (lastStep) // if we are at the last step then next it will be
				done = true; // done

			// double loop to calculate K matrix

			/*
			 * this loop calculates each row i in the K matrix using the Butcher
			 * tableau, its inner loop (the sum), and function evaluations
			 */
			for (int i = 0; i < s; i++) // loop for the K matrix
			{
				/*
				 * this loop calculates the ith row of the K matrix using the
				 * ith row of the a array of the given Butcher tableau and all
				 * of the rows of K before it
				 */
				for (int j = 0; j < i; j++) // the loop for each row
				{
					StdMet.stam(stam1, a[i][j], K[j]); // a[i][j]*K[j]
					StdMet.arraysum(sigma, sigma, stam1); // sigma = sigma +
					// a[i][j]*K[j]
				}

				if (!((i == 0) && !firstStep && FSALenabled)) {
					StdMet.arraycpy(g1, as1); // this effectively gets the
					// second last
					// g of the loop calculations

					StdMet.stam(stam1, h, sigma); // sigma = sigma*h
					StdMet.arraysum(as1, xold, stam1); // as1 = xold + stam1
					fp = f.f(told + h * c[i], as1); // fp = f(told + h*c[i],
					// as1)
					StdMet.arraycpy(K[i], fp); // set ith row of the K matrix to
					// function evaluation
					StdMet.zero_out(sigma); // set sigma array to array of zeros

					StdMet.arraycpy(g2, as1); // this gets the the last g of the
					// loop calculations
				} else if (justAccepted) // do this only if previous step was
					// accepted
					StdMet.arraycpy(K[0], K[s - 1]); // else we copy the last
				// row
				// from previous step into first row of present step
			}

			// get xnew and xe respectively

			/*
			 * this loop takes the weighted average of all of the rows in the K
			 * matrix using the b array of the Butcher tableau -> this loop is
			 * the weighted average for a 5th order ERK method and is used to
			 * compute xnew
			 */
			for (int i = 0; i < s; i++) // loop for xnew
			{
				StdMet.stam(stam1, h * b[i], K[i]); // h*b[i]*K[i]
				StdMet.arraysum(sigma2, sigma2, stam1); // sigma2 = sigma2 +
				// h*b[i]*K[i]
			}

			/*
			 * this loop computes a similar weighted average as above except it
			 * is done using bhat instead of b, it is for a 4th order ERK method
			 * and it is used to compare to the 5th order method for error
			 * estimation
			 */
			for (int i = 0; i < s; i++) // loop for error estimation
			{
				StdMet.stam(stam1, h * bhat[i], K[i]); // h*bhat[i]*K[i]
				StdMet.arraysum(sigma3, sigma3, stam1); // sigma3 = sigma3 +
				// h*bhat[i]*K[i]
			}

			StdMet.arraysum(xe, xold, sigma3); // xe = xold + sigma3
			StdMet.arraysum(xnew, xold, sigma2); // xnew = xold + sigma2

			StdMet.zero_out(sigma2); // set sigma2 array to array of zeros
			StdMet.zero_out(sigma3); // set sigma3 array to array of zeros

			firstStep = false; // whether accepted or rejected, first step is
			// over
			justAccepted = false; // default false (will toggle on if step is
			// accepted)

			// event location routine

			if (eventLoc_on) {
				RootFinder rootFinder = new RootFinder(f, butcher, h, told,
						xold, K);

				thetaStarMin = 1.1; // this stores the smallest thetaStar found
				// (make sure it is beyond the value that any theta can be)
				thetaStar = 1.1; // set thetaStar to this value as well
				toldWhen = -1.0; // assign this to -1 so that no times can be
				// mistaken for event times

				/*
				 * we do a loop here to find the thetaStar for every component
				 * of g that an event occured, since this is just simple event
				 * location, if several components had events we take the
				 * smallest thetaStar to be the event (i.e., a maximum of one
				 * event per step)
				 */
				for (int i = 0; i < f.g(told, xold).length; i++) {
					signDet = f.g(told, xold)[i] * f.g(told + h, xnew)[i];

					if (signDet < 0) {
						toldWhen = told; // if there is an event, get the time
						// that this event
						// occured at so that we can interpolate the right
						// endpoints

						// g function evaluations and thetas on either side of
						// event

						thetaA = 0.0; // theta on the left
						thetaB = 1.0; // theta on the right
						ga = f.g(told, xold)[i]; // g function evaluation on one
						// side
						gb = f.g(told + h, xnew)[i]; // g function evaluation on
						// the other side

						rootFinder.setup(i); // let the root finder know what
						// iteration the event location
						// routine is on

						thetaStar = rootFinder.safeguarded_secant(thetaA,
								thetaB, ga, gb); // find thetaStar with the
						// safeguarded secant method

					}

					if (thetaStar <= thetaStarMin) // if we find a smaller
						// thetaStar
						thetaStarMin = thetaStar; // assign tMin to that smaller
					// thetaStar
				}

				/*
				 * here is where we actually act upon the event: if the time
				 * when the last thetaStar calculation was this time, then we do
				 * something with this time
				 */
				if (toldWhen == told) {
					h = thetaStarMin * h; // we adjust h back to event

					/*
					 * now that we know that an event has occured, we adjust h
					 * back to exactly where the event occured, then we go
					 * through another RK loop to recompute xnew and xe with the
					 * intention that they will fall right on the event . . . we
					 * then halt the integration an output a message and final
					 * solution
					 */

					// System.out.println("an event occured at t = " + (told +
					// h));
					// double loop to calculate K matrix
					/*
					 * this loop calculates each row i in the K matrix using the
					 * Butcher tableau, its inner loop (the sum), and function
					 * evaluations
					 */
					for (int i = 0; i < s; i++) // loop for the K matrix
					{
						/*
						 * this loop calculates the ith row of the K matrix
						 * using the ith row of the a array of the given Butcher
						 * tableau and all of the rows of K before it
						 */
						for (int j = 0; j < i; j++) // the loop for each row
						{
							StdMet.stam(stam1, a[i][j], K[j]); // a[i][j]*K[j]
							StdMet.arraysum(sigma, sigma, stam1); // sigma =
							// sigma +
							// a[i][j]*K[j]
						}

						if (!((i == 0) && !firstStep && FSALenabled)) {
							StdMet.arraycpy(g1, as1); // this effectively gets
							// the second last
							// g of the loop calculations

							StdMet.stam(stam1, h, sigma); // sigma = sigma*h
							StdMet.arraysum(as1, xold, stam1); // as1 = xold +
							// stam1
							fp = f.f(told + h * c[i], as1); // fp = f(told +
							// h*c[i], as1)
							StdMet.arraycpy(K[i], fp); // set ith row of the K
							// matrix to function
							// evaluation
							StdMet.zero_out(sigma); // set sigma arrays to array
							// of zeros

							StdMet.arraycpy(g2, as1); // this gets the the last
							// g of the
							// loop calculations
						} else if (justAccepted) // do this only if previous
							// step was accepted
							StdMet.arraycpy(K[0], K[s - 1]); // else we copy the
						// last row
						// from previous step into first row of present scheme
					}

					// get xnew and xe

					/*
					 * this loop takes the weighted average of all of the rows
					 * in the K matrix using the b array of the Butcher tableau
					 * -> this loop is the weighted average for a 5th order ERK
					 * method and is used to compute xnew
					 */
					for (int i = 0; i < s; i++) // loop for xnew
					{
						StdMet.stam(stam1, h * b[i], K[i]); // h*b[i]*K[i]
						StdMet.arraysum(sigma2, sigma2, stam1); // sigma2 =
						// sigma2 +
						// h*b[i]*K[i]
					}

					/*
					 * this loop computes a similar weighted average as above
					 * except it is done using bhat instead of b, it is for a
					 * 4th order ERK method and it is used to compare to the 5th
					 * order method for error estimation
					 */
					for (int i = 0; i < s; i++) // loop for error estimation
					{
						StdMet.stam(stam1, h * bhat[i], K[i]); // h*bhat[i]*K[i]
						StdMet.arraysum(sigma3, sigma3, stam1); // sigma3 =
						// sigma3 +
						// h*bhat[i]*K[i]
					}

					StdMet.arraysum(xe, xold, sigma3); // xe = xold + sigma3
					StdMet.arraysum(xnew, xold, sigma2); // xnew = xold + sigma2

					StdMet.zero_out(sigma2); // set sigma2 array to array of
					// zeros
					StdMet.zero_out(sigma3); // set sigma3 array to array of
					// zeros

					done = true; // we stop integration upon an event, so we
					// treat
					tf = told + h; // tStar as we would tf and output the
					// solution at tStar
				}
			}

			// do the embedded error estimation

			double[] estimation = ErrorEstimator.embedded_estimate(h, xold,
					xnew, xe, atol, rtol, P, aMax, AMIN, ALPHA);

			epsilon = estimation[0]; // get required information from this
			// estimation
			hNew = estimation[1];

			if (((1.1 * hNew) >= (tf - (told + h))) && (epsilon <= 1.0)) { // stretch
				// the
				// last
				// step
				// if
				// it
				// is
				// within
				// 10%
				// of
				// tf
				// -
				// (told
				// +
				// h)
				hNew = tf - (told + h);
				lastStep = true;
			}

			if (epsilon != epsilon) // check to see if error is NaN, if
			{ // so, something has gone wrong, solver is unstable
				throw new DifferentialAnalysisException("Solution is unstable");

			}

			// based on the error estimation, make the decision to finish,
			// accept or reject a step

			if (done) // when done, output all of the statistics involved in
			{ // the embedded method and program is done
				if (epsilon > 1.0) // in the very odd case that the stretched
				// step
				{ // is rejected:
					done = false; // set done and last step to false, because it
					// is
					lastStep = false; // doing at least two more steps
					nreject++; // we reject a step and up the counter to
					nfailed++; // keep track for stiffness
					h = h / 2; // we cut h in half, seeing as either half will
					// be too small

					if (stats_on) // output statistics (if user has chosen so),
					// note that
					{ // time and solution do not change so we do not output
						// such
						if (!stats_intermediate) // do not output if only on
						{ // intermediate statistics mode
							// System.out.println("rejected");
							// System.out.println("new h = " + h);
							// System.out.println();
						}
					}
				} else {
					naccept++; // last step is obviously accepted
					avgStepSize = (tf - t0) / naccept;
					// System.out.println("done");
					// System.out.println("final t = " + (told + h)); // output
					// final t
					// System.out.println("final solution ="); // and solution
					// StdMet.arrayprt(xnew);
					// System.out.println();

					if (sdetect_on) {
						// System.out.println("# of checks due to MAXFCN: "
						// + checks1);
						// System.out.println("# of checks due to ratio: "
						// + checks2);
					}

					// System.out.println("# of rejections = " + nreject);
					// System.out.println("# of accepted steps = " + naccept);
					// System.out.println("average step size = " + avgStepSize);

					// profile filling

					this.profile = new double[n + 2]; // profile contians h, t
					// and xnew

					profile[0] = avgStepSize; // give it h (avg)
					profile[1] = (told + h); // give it t (final)

					for (int i = 0; i < n; i++)
						// fill the rest with xnew
						profile[i + 2] = xnew[i];

					// file writing/interpolation stuff

					if (!interpolant_on) {
						writer.writeToFile(told + h, xnew);
					} else {
						while ((times_inc < times_limit)
								&& (told <= times[times_inc])
								&& ((told + h) >= times[times_inc])) { // while
							// there
							// are
							// points
							// left,
							// and a
							// point
							// in
							// array
							// falls
							// between
							// point
							// in
							// solution

							theta = (times[times_inc] - told) / h; // generate a
							// theta for
							// this
							// point

							/*
							 * this loop takes the weighted average of all of
							 * the rows in the K matrix using the functions of
							 * theta of the Butcher tableau -> this loop is the
							 * weighted average for a 5th order rk method and it
							 * is used to interpolate two solution points
							 */
							for (int i = 0; i < s; i++) // loop for interpolant
							{
								StdMet.stam(stam1, h
										* butcher.get_btheta().f(theta)[i],
										K[i]); // h*f(theta)[i]*K[i]
								StdMet
										.arraysum(sigmaInterp, sigmaInterp,
												stam1); // sigma = sigma +
								// h*f(theta)[i]*K[i]
							}

							StdMet.arraysum(x_interp, xold, sigmaInterp); // x_interp
							// =
							// xold
							// +
							// sigmaInterp

							writer.writeToFile(times[times_inc], x_interp);

							times_inc++; // go to next time in the times array
							StdMet.zero_out(sigmaInterp); // clear out x_interp
							// for the next
							// sigmaInterp
						}
					}
				}
			} else {
				fevalTotal += s; // another s function evaluations are done
				if ((fevalTotal > MAXFCN) && sdetect_on) {
					checks1++;

					// check for stiffness

					double hRho = StiffnessDetector.calc_hRho(h, K[K2], K[K1],
							g2, g1);

					if (stats_on) // output some statistics (if user has chosen
					{ // such)
						if (!stats_intermediate) // do not output if only on
						{ // intermediate statistics mode
							// System.out
							// .println("a check due to MAXFCN was done");
							// System.out.println("h*rho = " + hRho);
							// System.out.println();
						}
					}

					if (hRho > BOUND) {
						throw new DifferentialAnalysisException(
								"Problem is stiff at t = " + told);
					}

					fevalTotal = 0; // reset counter
				}
				if (lastStep || (epsilon <= 1.0)) // if on our last step
				{ // (where step is usually very small):

					if (interpolant_on) {
						while ((times_inc < times_limit)
								&& (told <= times[times_inc])
								&& ((told + h) >= times[times_inc])) { // while
							// there
							// are
							// points
							// left,
							// and a
							// point
							// in
							// array
							// falls
							// between
							// points
							// in
							// solution

							theta = (times[times_inc] - told) / h; // generate a
							// theta for
							// this
							// point

							/*
							 * this loop takes the weighted average of all of
							 * the rows in the K matrix using the functions of
							 * theta of the Butcher tableau -> this loop is the
							 * weighted average for an 5th order ERK method and
							 * it is used to interpolate two solution points
							 */
							for (int i = 0; i < s; i++) // loop for interpolant
							{
								StdMet.stam(stam1, h
										* butcher.get_btheta().f(theta)[i],
										K[i]); // h*f(theta)[i]*K[i]
								StdMet
										.arraysum(sigmaInterp, sigmaInterp,
												stam1); // sigmaInterp =
								// sigmaInterp +
								// h*f(theta)[i]*K[i]
							}

							StdMet.arraysum(x_interp, xold, sigmaInterp); // x_interp
							// =
							// xold
							// +
							// sigmaInterp

							writer.writeToFile(times[times_inc], x_interp);

							times_inc++; // go to next time in the times array
							StdMet.zero_out(sigmaInterp); // clear out
							// sigmaInterp for
							// the next
							// sigmaInterp
						}
					}

					StdMet.arraycpy(xold, xnew); // accept step

					if (nsuccess == 0) // if check was just done, update
						tFirst = told; // the time

					told += h; // increment related counters
					h = hNew;
					aMax = 5.0; // restore amax to 5 after step acceptance
					naccept++;

					nsuccess++; // keep track for stiffness detection
					justAccepted = true; // toggle for FSAL functionality

					if (!interpolant_on) {
						writer.writeToFile(told, xnew);
					}

					if (stats_on) // output statistics (if user has chosen
					{ // such)
						if (!stats_intermediate) // do not output if only on
						{ // intermediate statistics mode
							// System.out.println("accepted");
							// System.out.println("new h =" + h);
						}

						// System.out.println("t =" + told);

						if (!stats_intermediate) // do not output if only on
						{ // intermediate statistics mode
							// System.out.println("solution =");
							// StdMet.arrayprt(xold);
						}

						// System.out.println();
					}
				} else // else we reject the step and increment related counters
				{
					h = hNew;
					aMax = 1.0; // set amax to 1 after a step rejection
					nreject++;

					nfailed++; // keep track for stiffness

					if ((nfailed >= NFAILED) && sdetect_on) {
						if (nsuccess <= NSUCCESS) {
							avgStepSize = (told - tFirst) / nsuccess;

							if (avgStepSize == 0) // in the event that there are
								// only rejected steps from
								avgStepSize = h; // the start, make it so a
							// check can be done

							if (((h <= AMAX * avgStepSize) && (h >= avgStepSize
									/ AMAX))
									&& (fevalTotal > MAXFCN * (told - t0)
											/ (tf - t0))) {
								checks2++;

								// check for stiffness

								double hRho = StiffnessDetector.calc_hRho(h,
										K[K2], K[K1], g2, g1);

								if (stats_on) // output statistics (if user has
								// chosen
								{ // such)
									if (!stats_intermediate) // do not output if
									// only on
									{ // intermediate statistics mode
										// System.out
										// .println("a check due to ratio was done");
										// System.out.println("h*rho = " +
										// hRho);
										// System.out.println();
									}
								}

								if (hRho > BOUND) {
									throw new DifferentialAnalysisException(
											"Problem is stiff due to ratio at t = "
													+ told);
									/*
									 * System.out
									 * .println("solution at this time:");
									 * StdMet.arrayprt(xold); System.out
									 * .println("# of checks due to MAXFCN: " +
									 * checks1); System.out
									 * .println("# of checks due to ratio: " +
									 * checks2); System.out.println("accepted: "
									 * + naccept);
									 * System.out.println("rejected: " +
									 * nreject); System.exit(0);
									 * 
									 * writer.closeFile(); // close the writer
									 * // before halting
									 * 
									 * return; // halt
									 */
								}
							}
						}

						nsuccess = 0; // reset the counters
						nfailed = 0;
					}

					if (stats_on) // output statistics (if user has chosen so),
					// note that
					{ // time and solution do not change so we do not output
						// such
						if (!stats_intermediate) // do not output if only on
						{ // intermediate statistics mode
							// System.out.println("rejected");
							// System.out.println("new h =" + h);
							// System.out.println();
						}
					}
				}
			}
		}

		/* end integration loop */

		writer.closeFile(); // now that we are done, close the writer
	}

	/*
	 * gets profile of statistics from an integration
	 */
	public double[] getProfile() {
		return (profile); // return profile array
	}

	// instance variables

	// variables dealing with general parameters (section 1 of constructor)

	private ODE f; // the function of the differential equation
	private double t0; // the starting time
	private double tf; // the stopping time
	private double[] x0; // the initial value
	private Btableau butcher; // the Butcher tableau for the scheme
	private int s; // the number of stages of this Runge-Kutta scheme

	private double[][] a; // the matrix a of the given Butcher tableau
	private double[] b; // the array b of the given Butcher tableau
	private double[] bhat; // the array bhat of the given Butcher tableau
	private double[] c; // the array c of the given Butcher tableau

	private boolean FSALenabled; // whether first same as last functionality of
	// the
	// scheme (if this scheme has the property to begin with) is enabled

	private double[] atol; // absolute tolerances for each solution array entry
	private double[] rtol; // relative tolerances for each solutionn array entry

	private double h; // the stepsize of the integration
	private int n; // dimension of ODE

	// variables that are used in the integration loop

	private double told; // stores the current t value
	private double[] xold; // stores the current x value (xold
	private double[] xnew; // stores the next x value (xnew)
	private double[] xe; // the error estimation for embedded method
	private double[][] K; // matrix of K values (s rows of size n)

	// error control variables

	private double epsilon; // determines whether we accept or reject next
	// step

	private double hNew; // the stepsize to take for the next step
	private double aMax = 5.0; // maximum growth limit for h

	private int nreject; // number of rejected steps counter
	private int naccept; // number of accepted steps counter
	private double avgStepSize; // the average stepsize throughout integration

	// iteration affected variables

	private boolean done; // termination switch
	private boolean firstStep; // switch verifies if loop of routine is on first
	// step
	private boolean lastStep; // switch verifies if loop of routine is on last
	// step
	private boolean justAccepted; // switch verifies whether previous step was
	// an
	// accepted step or not (for the purpose of FSAL functionality)

	// variables for interpolation

	private double[] times; // array of times user can use to interpolate
	// solution to
	private int timesLength; // length of the times array (length = 0 if times =
	// null)
	private boolean interpolant_on; // whether to do interpolation or not

	// variables for stiffness detection

	private boolean sdetect_on; // whether to detect stiffness or not

	private int fevalTotal; // number of function evaluations thus far
	private double tFirst; // the first time after a check is done
	private int nsuccess; // number of successful steps thus far (from start or
	// last check)
	private int nfailed; // number of failed steps thus far (from start or last
	// check)
	private int checks1; // number of stiffness checks due to function
	// evaluations > MAXFCN
	private int checks2; // number of stiffness checks due to 10 fail b/f 50
	// succeed

	// variables for event location

	private boolean eventLoc_on; // whether to locate events or not
	private double[] profile; // a capsule of a few statistics from this routine
	// to use in others

	// variables for miscellaneous special features

	private String fileName; // name of file solution is written to (each step)
	private boolean append; // whether to append solution to a non-empty file,
	// or overwrite it
	private boolean stats_on; // whether to report status at each step
	private boolean stats_intermediate; // whether to report just a few
	// statistics

	// finals (constants)

	private final double P = 5.0; // // the higher order method of a
	// Dormand-Prince scheme
	private final double AMIN = 1.0 / 5.0; // minimum growth limit for h
	private final double AMAX = 5.0; // maximum growth limit for h
	private final double ALPHA = 0.9; // safety factor

	private final int MAXFCN = 14000; // default amount of function evaluations
	// before we say problem is stiff
	private final int NSUCCESS = 50; // after 50 succesfull steps check ratio
	private final int NFAILED = 10; // if 10 failed after 50 successful, check
	// for stiffness
	private final int K1 = 5; // last 2 rows of matrix of K values of
	// Dormand-Prince scheme
	private final int K2 = 6;
	private final double BOUND = 3.25; // stability region boundary for dopr

	private IWriterCallback writer;
}
