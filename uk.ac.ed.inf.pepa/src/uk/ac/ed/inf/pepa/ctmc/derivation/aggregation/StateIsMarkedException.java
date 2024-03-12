package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

/**
 * Exception raised whenever someone tries to perform an operation
 * on a marked state but marked state are not supposed to be subject
 * to that operation.
 * 
 * @author Giacomo Alzetta
 *
 */
public class StateIsMarkedException extends PartitioningException {

	/**
	 * Auto-generated serial UID number.
	 */
	private static final long serialVersionUID = 6067009190750601316L;

	public StateIsMarkedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StateIsMarkedException(Throwable exc) {
		super(exc);
	}
	
	public StateIsMarkedException(String message, Throwable exc) {
		super(message, exc);
	}
	
}
