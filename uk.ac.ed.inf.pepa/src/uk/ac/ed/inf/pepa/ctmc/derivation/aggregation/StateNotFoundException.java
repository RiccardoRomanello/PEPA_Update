/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

/**
 * Exception thrown whenever someone tries to perform an operation
 * on a state of the state space that either doesn't exist or
 * isn't referenced from this data structure.
 * 
 * @author Giacomo Alzetta
 *
 */
public class StateNotFoundException extends PartitioningException {

	/**
	 * Auto-generated serial UID number.
	 */
	private static final long serialVersionUID = 8122028244251504693L;

	public StateNotFoundException(String message) {
		super(message);
	}
	
	public StateNotFoundException(Throwable exc) {
		super(exc);
	}
	
	public StateNotFoundException(String message, Throwable exc) {
		super(message, exc);
	}
}
