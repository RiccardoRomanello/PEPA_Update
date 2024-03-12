/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

/**
 * @author Giacomo Alzetta
 *
 */
public class PartitioningException extends Exception {

	/**
	 * Automatically generated serial version UID.
	 */
	private static final long serialVersionUID = 2303797446084690903L;

	/**
	 * Construct an exception from a message string.
	 * @param message
	 */
	public PartitioningException(String message) {
		super(message);
	}
	
	/**
	 * Support for exception chaining.
	 */
	public PartitioningException(Throwable exc) {
		super(exc);
	}
	
	/**
	 * Support for exception chaining.
	 */
	public PartitioningException(String message, Throwable exc) {
		super(message, exc);
	}
}
