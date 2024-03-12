package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Giacomo Alzetta
 *
 */
public class Partition<S extends Comparable<S>, P extends PartitionBlock<S>> {
	
	HashSet<P> blocks;
	HashMap<S, P> stateToBlock;
	
	public Partition() {
		blocks = new HashSet<>();
		stateToBlock = new HashMap<>();
	}

	public void addBlock(P block) {
		// this should never be called with a block already in the partition.
		assert !blocks.contains(block);
		if (blocks.add(block)) {
			for (S state: block) {
				stateToBlock.put(state, block);
			}
		}
	}
	
	public void addBlocks(Iterable<P> blocks) {
		for (P block : blocks) {
			addBlock(block);
		}
	}
	
	public Collection<P> getBlocks() {
		return blocks;
	}
	
	public P getBlockOf(S state) {
		return stateToBlock.get(state);
	}
	
	public void updateWithSplit(Iterable<P> subBlocks) {
		addBlocks(subBlocks);
		ArrayList<P> emptyBlocks = new ArrayList<>();
		for (P block: subBlocks) {
			if (block.isEmpty()) {
				emptyBlocks.add(block);
			}
		}
		
		blocks.removeAll(emptyBlocks);
	}
	
	public int size() {
		return blocks.size();
	}
	
	@Override
	public String toString() {
		return "Partition(" + blocks.toString() + ")";
	}
}
