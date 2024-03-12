package uk.ac.ed.inf.pepa.largescale.simulation;

public class SimulationException extends Exception {

	public SimulationException(Throwable t) {
		super(t);
	}

	public SimulationException(String message) {
		super(message);
	}
}
