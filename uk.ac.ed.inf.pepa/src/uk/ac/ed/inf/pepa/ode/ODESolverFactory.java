package uk.ac.ed.inf.pepa.ode;

import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.ode.internal.ODEtoJavaSolver;

public class ODESolverFactory {
	
	public static IODESolver create(IParametricDerivationGraph graph) {
		return new ODEtoJavaSolver(graph);
	}
}
