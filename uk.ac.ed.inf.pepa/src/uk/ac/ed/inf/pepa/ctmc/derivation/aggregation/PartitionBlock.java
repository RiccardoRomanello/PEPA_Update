/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * An interface that represents blocks of a Partition Refinement data structure.
 * 
 * Each block of the partition contains states that can be marked or not.
 * This class provides operations to mark states and also the ability to set
 * a value for them. It also provides operations that partition the states of
 * this block in multiple blocks depending on whether the states where marked
 * or not or on the value assigned to each state.
 * 
 * A block keeps also track of whether or not it was used as a splitter
 * to refine the other blocks in the partition.
 * 
 * @author Giacomo Alzetta
 * 
 * @param S The type of the states.
 */
public interface PartitionBlock<S> extends Iterable<S> {
	
	/**
	 * Return the number of states in this block.
	 * 
	 * @return
	 */
	public int size();
	
	/**
	 * Add a non-marked state to the block.
	 * 
	 * @param state
	 */
	public void addState(S state);
	
	/**
	 * True if this block of the partition doesn't contain any state.
	 * 
	 * @return True if the block is empty, False otherwise.
	 */
	public boolean isEmpty();
	
	/**
	 * True if this block contains at least one marked state,
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean hasMarkedStates();
	
	/**
	 * Iterate over all the states contained in this block of the partition.
	 * 
	 * @return
	 */
	public Iterator<S> getStates();
	
	/**
	 * Iterate over all the states that were marked in this block.
	 * @return
	 */
	public Iterator<S> getMarkedStates();
	
	/**
	 * Splits the block into two sub-blocks: one containing the marked states
	 * and one containing the non-marked states.
	 * 
	 * Note that the block containing the marked states is returned while
	 * the current instance is modified and will contain only states that
	 * were <b>not</b> marked.
	 * 
	 * @return The sub-block consisting of all marked states.
	 */
	public PartitionBlock<S> splitMarkedStates();
	
	/**
	 * Splits the block into a certain number of blocks grouping together
	 * states depending on the value associated with them.
	 * 
	 * @return Partition of the current block into sub-blocks.
	 */
	public Collection<PartitionBlock<S>> splitBlock();
	
	/**
	 * Splits the block into two sub-blocks: one with the states that have
	 * been assigned the given value and the other that have been assigned a
	 * different value.
	 * 
	 * The block is modified and will only contains state that have been
	 * assigned to that value, while the sub-block containing the
	 * states with a different value is returned.
	 * 
	 * NOTE: this splitting should be performed after splitting the
	 * marked states.
	 * 
	 * @param value
	 * @return The sub-block whose states have a value *different* from <code>value</code>.
	 */
	public PartitionBlock<S> splitBlockOnValue(double value);
	
	/**
	 * Marks the given state in the block.
	 * If the state is already marked does nothing.
	 * Raises a <code>StateNotFoundException</code> if the state
	 * could not be found.
	 * 
	 * @param state	A state of the block.
	 */
	public void markState(S state) throws StateNotFoundException;
	
	/**
	 * Return true if the given state is a marked state in the block.
	 * 
	 * @param state a state of the block.
	 * @return
	 */
	public boolean isMarked(S state) throws StateNotFoundException;
	
	/**
	 * Sets the value associated with a state.
	 * The state should <b>not</b> be a marked state.
	 * 
	 * @param state A state of the block
	 * @param value The value to be associated with this state.
	 */
	public void setValue(S state, double value)
			throws StateNotFoundException, StateIsMarkedException;
	
	/**
	 * Return the value associated with a state.
	 * The state should <b>not</b> be a marked state.
	 * 
	 * @param state A state of the block
	 * @return The value associated with the state or <code>null</code> if no value was set.
	 */
	public double getValue(S state)
			throws StateNotFoundException, StateIsMarkedException;
	
	/**
	 * Return <code>true</code> if this block was already used as a splitter,
	 * <code>false</code> otherwise.
	 * 
	 * @return true if this block was used as a splitter.
	 */
	public boolean wasUsedAsSplitter();
	
	/**
	 * After calling this method the calls to <code>wasUsedAsSplitter</code>
	 * should return <code>true</code>.
	 */
	public void usingAsSplitter();
	
	/**
	 * After calling this method the calls to <code>wasUsedAsSplitter</code>
	 * should return <code>false</code>.
	 */
	public void toBeUsedAsSplitter();
	
	/**
	 * This is a low level operation needed to make efficient some algorithms.
	 * 
	 * After this call the <code>this</code> block and <code>block</block> will
	 * share the same underlying sets of states. The <code>block</code> should
	 * initially be empty and should be the value returned.
	 * 
	 * This operation is meant to be called as in:
	 * <code>
	 * if (block.isEmpty()) {
	 *     otherBlock = otherBlock.shareIdentity(block)
	 * }
	 * </code>
	 * 
	 * From that point onwards we have <code>otherBlock == block</code>.
	 * 
	 * @param block An empty block from the partition
	 * @return      The block passed as argument
	 */
	public PartitionBlock<S> shareIdentity(PartitionBlock<S> block);
}
