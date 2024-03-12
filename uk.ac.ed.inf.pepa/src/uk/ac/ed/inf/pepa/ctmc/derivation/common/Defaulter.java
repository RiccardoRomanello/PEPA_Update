/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

/**
 * An interface representing objects that have a default value.
 * 
 * @author Giacomo Alzetta
 *
 */
@FunctionalInterface
public interface Defaulter<V> {
	
	public V getDefault();

}
