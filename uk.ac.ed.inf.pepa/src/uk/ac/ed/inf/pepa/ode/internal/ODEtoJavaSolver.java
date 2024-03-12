package uk.ac.ed.inf.pepa.ode.internal;

import java.util.Arrays;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.largescale.IGeneratingFunction;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.expressions.EvaluatorVisitor;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.ode.IODESolver;
import uk.ac.ed.inf.pepa.ode.ISolutionRoutineCallback;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.DormandPrince;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.Imex;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.ImexSD;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Btableau;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.IWriterCallback;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.ODE;
import uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.Span;

public class ODEtoJavaSolver implements IODESolver {

	private IParametricDerivationGraph derivationGraph;

	private ODE ode;

	public ODEtoJavaSolver(final IParametricDerivationGraph derivationGraph) {
		this.derivationGraph = derivationGraph;
		this.ode = new ODE() {

			public double[] f(double t, double[] state)
					throws DifferentialAnalysisException {
				double[] results = new double[state.length];
				for (IGeneratingFunction f : derivationGraph
						.getGeneratingFunctions()) {
					double result = new EvaluatorVisitor(f.getRate(), state)
							.getResult();
					for (int i = 0; i < state.length; i++) {
						short jumpElement = f.getJump()[i];
						results[i] += jumpElement * result;
					}

				}
				return results;
			}

			public double[] g(double arg0, double[] arg1) {
				double[] event = new double[1]; // with this function
				return (event);
			}
		};
	}

	public double[] evaluateVectorField(double t, double[] state)
			throws DifferentialAnalysisException {
		return ode.f(t, state);
	}

	public void solve(OptionMap map, final ISolutionRoutineCallback callback,
			IProgressMonitor monitor, double[] initialState)
			throws DifferentialAnalysisException, InterruptedException {
		if (callback == null)
			throw new NullPointerException("Callback cannot be null");
		if (initialState == null)
			throw new NullPointerException("Initial state cannot be null");
		if (map == null)
			map = new OptionMap();
		if (initialState.length != derivationGraph.getInitialState().length)
			throw new IllegalArgumentException();
		if (monitor == null)
			monitor = new DoNothingMonitor();
		final IProgressMonitor progressMonitor = monitor;
		final double startTime = (Double) map.get(OptionMap.ODE_START_TIME);
		final double stopTime = (Double) map.get(OptionMap.ODE_STOP_TIME);
		double relativeTolerance = (Double) map.get(OptionMap.ODE_RTOL);
		double absoluteTolerance = (Double) map.get(OptionMap.ODE_ATOL);
		// constructor schemes
		double[] x = initialState;

		int n = x.length; // indicate the dimension of the problem

		// absolute tolerance vector

		double[] atol = new double[n];
		double[] rtol = new double[n];
		Arrays.fill(atol, absoluteTolerance);
		Arrays.fill(rtol, relativeTolerance);

		String fileName = ""; // "/Users/Mirco/Documents/test.txt"; // default
		// to no file writing

		// stiffness detection option

		String stiffnessDetection = "StiffDetect_Off"; // choose 1)
		// "StiffDetect_Halt" or
		// 2) "StiffDetect_Off"

		// event location option

		String eventLocation = "EventLoc_Off"; // choose 1) "EventLoc_Halt" or
		// 2) "EventLoc_Off"

		// stats option

		String stats = "Stats_Off"; // choose 1) "Stats_On" 2)
		// "Stats_Intermediate or 3) "Stats_Off"

		/*
		 * this is where the template essentially ends
		 */

		String solver = (String) map.get(OptionMap.ODE_SOLVER);
		Span span = null;
		IWriterCallback writerCallback = new IWriterCallback() {
			private double lastTime = startTime;

			public void closeFile() {
				progressMonitor.done();
			}

			public void openFile(String fileName, boolean append) {
				progressMonitor.beginTask(100);

			}

			public void openFile(String fileName) {
				progressMonitor.beginTask(100);
			}

			public void writeToFile(double t, double[] y)
					throws DifferentialAnalysisException {
				progressMonitor.worked((int) ((t - lastTime)
						/ (stopTime - startTime) * 100));
				lastTime = t;
				callback.timePointComputed(t, y);
			}

			public boolean isCanceled() {
				return progressMonitor.isCanceled();
			}

		};
		if (solver.equals(OptionMap.ODE_DORMAND_PRINCE)) {
			// decides whether to do interpolation
			if (map.get(OptionMap.ODE_INTERPOLATION).equals(
					OptionMap.ODE_INTERPOLATION_ON)) {
				int step = (Integer) map.get(OptionMap.ODE_STEP);
				double timestep = (stopTime - startTime) / (step - 1);
				double[] times = new double[step];
				for (int i = 0; i < times.length; i++) {
					times[i] = startTime + i * timestep;
				}
				span = new Span(times);
			} else {
				span = new Span(startTime, stopTime);
			}
			double initialStepSize = -1.0; // automatic
			// run the Dormand-Prince scheme with above parameters
			DormandPrince.dormand_prince(ode, span, x, initialStepSize, atol,
					rtol, fileName, stiffnessDetection, eventLocation, stats,
					writerCallback);
		} else {
			span = new Span(startTime, stopTime);
			double initialStepSize = 1.0E-06; // automatic
			// ImexSD.imex_sd(ode, span, x, initialStepSize, new
			// Btableau("imex443"),
			// absoluteTolerance, relativeTolerance, fileName, stats,
			// writerCallback);
			int step = (Integer) map.get(OptionMap.ODE_STEP);

			Imex.imex(ode, span, x, initialStepSize, new Btableau("imex443"),
					fileName, stats, step, writerCallback);
		}
	}

	public void solve(OptionMap map, ISolutionRoutineCallback callback,
			IProgressMonitor monitor) throws DifferentialAnalysisException,
			InterruptedException {
		this.solve(map, callback, monitor, derivationGraph.getInitialState());

	}

}
