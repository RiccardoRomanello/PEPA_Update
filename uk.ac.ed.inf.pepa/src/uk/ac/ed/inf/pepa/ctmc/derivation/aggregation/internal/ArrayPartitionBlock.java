/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.PartitionBlock;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.StateIsMarkedException;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.StateNotFoundException;

/**
 * A partition refinement data structure implementing using an array and a mapping.
 * 
 * All the states of the block are stored in an array <code>states</code>.
 * When a state is added to the block it is appended to the array.
 * We also keep track of an index <code>markIndex</code> inside the array.
 * All states at positions <em>before</em> <code>markIndex</code> are marked
 * states. To mark a state we simply swap it with the state at position
 * <code>markIndex</code> and increase <code>markIndex</code> by <code>1</code>.
 * 
 * @author Giacomo Alzetta
 *
 */
public class ArrayPartitionBlock<S> implements PartitionBlock<S> {

	private ArrayList<S> states;
	private HashMap<S, Integer> statesToIndex;
	private int markIndex = 0;
	private boolean used = false;
	private int hash = -1;
	
	private HashMap<S, Double> mapToValues;
	
	/**
	 * Create an empty partition block.
	 */
	public ArrayPartitionBlock() {
		states = new ArrayList<S>();
		statesToIndex = new HashMap<>();
		mapToValues = new HashMap<>();
	}
	
	/**
	 * Create a partition block that contains the given states.
	 * 
	 * @param sts
	 */
	private ArrayPartitionBlock(List<S> sts, HashMap<S, Double> map) {
		states = new ArrayList<S>(sts);
		statesToIndex = new HashMap<>(sts.size());
		int i=0;
		for (S s: sts) {
			statesToIndex.put(s, i++);
		}
		
		// TODO: we might be able to just share the map and avoid
		// copying it, but we must be sure about this!
		mapToValues = new HashMap<>();
		for (S state: states) {
			mapToValues.put(state, map.get(state));
		}
	}
	
	@Override
	public PartitionBlock<S> splitBlockOnValue(double value) {
		// We must first copy the states because marking a state actually
		// disrupts iteration.
		ArrayList<S> states = new ArrayList<S>(size());
		Iterator<S> statesIter = getStates();
		
		while (statesIter.hasNext()) {
			states.add(statesIter.next());
		}
		
		
		for (S state: states) {
			try {
				if (getValue(state) != value) {
					markState(state);
				}
			} catch (StateIsMarkedException e) {
				e.printStackTrace();
			} catch (StateNotFoundException e) {
				e.printStackTrace();
			}
		}
		return splitMarkedStates();
	}
	
	
	@Override
	public void addState(S state) {
		hash = -1;
		assert !statesToIndex.containsKey(state);
		states.add(state);
		statesToIndex.put(state, statesToIndex.size());
		
	}
	
	@Override
	public boolean isEmpty() {
		return states.isEmpty();
	}
	
	/**
	 * Iterate over all the states in the block.
	 * 
	 * Implementation detail: the states are not ordered but currently
	 * all marked states are returned before the non-marked states.
	 */
	@Override
	public Iterator<S> getStates() {
		return new Iterator<S>() {
			private int i=0;
			
			public boolean hasNext() {
				return i < states.size();
			}
			
			public S next() {
				return states.get(i++);
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public Iterator<S> getMarkedStates() {
		return new Iterator<S>() {
			private int i=0;
			
			public boolean hasNext() {
				return i < markIndex;
			}
			
			public S next() {
				return states.get(i++);
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public PartitionBlock<S> splitMarkedStates() {
		hash = -1;
		ArrayPartitionBlock<S> newBlock = new ArrayPartitionBlock<>(
				states.subList(0, markIndex), 
				mapToValues
		);
		
		statesToIndex.clear();
		// TODO: optimization: if markIndex << size we can just swap
		//       the last markIndex states at the beginning, instead of moving
		// 	     all remaining states backwards.
		for (int i=markIndex; i < states.size(); i++) {
			S state = states.get(i);
			states.set(i-markIndex, state);
			statesToIndex.put(state, i-markIndex);
		}
		
		// Remove the moved states from the array.
		states.subList(states.size()-markIndex, states.size()).clear();
		
		states.trimToSize();
		markIndex = 0;
		return newBlock;
	}
	
	@Override
	public Collection<PartitionBlock<S>> splitBlock() {
		//hash = -1;
		ArrayList<Double> values = new ArrayList<>(mapToValues.values());
		double pmc = PartitioningUtils.pmc(values);
		HashMap<S, Double> mappingNotPmc = new HashMap<>(mapToValues);
		HashMap<S, Double> mappingOfPmc = PartitioningUtils.splitMapOnValue(mappingNotPmc, pmc);
		
		PartitionBlock<S> pmcBlock = new ArrayPartitionBlock<>();
		for (Map.Entry<S, Double> entry: mappingOfPmc.entrySet()) {
			pmcBlock.addState(entry.getKey());
		}
		
		assert !pmcBlock.isEmpty();
		
		ArrayList<Map.Entry<S, Double>> entries = new ArrayList<>(mappingNotPmc.entrySet());
		entries.sort(new Comparator<Map.Entry<S, Double>>() {
			
			@Override
			public int compare(Map.Entry<S, Double> e1, Map.Entry<S, Double> e2) {
				double v1 = e1.getValue();
				double v2 = e2.getValue();
				
				// TODO: check that this comparison is sound.
				if (v1 == v2) {
					return 0;
				} else {
					// in particular here
					return Math.min(v1, v2) == v1 ? -1 : 1;
				}
			}
		});
		
		HashMap<Double, PartitionBlock<S>> blocks = new HashMap<>();
		blocks.put(pmc, pmcBlock);
		
		for (Map.Entry<S, Double> entry : entries) {
			Double val = entry.getValue();
			if (!blocks.containsKey(val)) {
				blocks.put(val, new ArrayPartitionBlock<S>());
			}
			blocks.get(val).addState(entry.getKey());
			
			assert !blocks.get(val).isEmpty();
		}
		
		// TODO: We do not want to allow modifications...
		return blocks.values();
	}
	
	@Override
	public void markState(S state) throws StateNotFoundException {
		int i = statesToIndex.get(state);
		if (i < 0) {
			throw new StateNotFoundException("The state: " + state.toString() + " was not found.");
		}
		// We swap the first non-marked state with the state we want to mark,
		// and increase the markIndex.
		
		S firstNonMarkedState = states.get(markIndex);
		states.set(markIndex, state);
		states.set(i, firstNonMarkedState);
		statesToIndex.put(state, markIndex);
		statesToIndex.put(firstNonMarkedState, i);
		++markIndex;
	}
	
	@Override
	public boolean isMarked(S state) throws StateNotFoundException {
		Integer i = statesToIndex.get(state);
		if (i == null)
			throw new StateNotFoundException("The state: " + state.toString()
											 + " could not be found.");
		return i < markIndex;
	}
	
	
	@Override
	public void setValue(S state, double value)
			throws StateNotFoundException, StateIsMarkedException {
		hash = -1;
		checkStateExistNonMarked(state);
		
		mapToValues.put(state, value);
	}
	
	@Override
	public double getValue(S state)
			throws StateNotFoundException, StateIsMarkedException {
		Double value = mapToValues.get(state);
		if (value == null) {
			if (mapToValues.containsKey(state)) {
				throw new RuntimeException("Impossible happened: null value assigned to state.");
			}
			
			checkStateExistNonMarked(state);
			
		}
		
		return value;
	}
	
	@Override
	public int size() {
		return states.size();
	}
	
	@Override
	public boolean wasUsedAsSplitter() {
		return used;
	}
	
	@Override
	public void usingAsSplitter() {
		this.used = true;
	}
	
	
	@Override
	public String toString() {
		return "PartitionBlock(" + states.toString() + ")";
	}
	

	/**
	 * Simple parameter checking function.
	 * 
	 * @param state
	 * @throws StateNotFoundException
	 * @throws StateIsMarkedException
	 */
	private void checkStateExistNonMarked(S state)
			throws StateNotFoundException, StateIsMarkedException {
		Integer i = statesToIndex.get(state);
		if (i == null) {
			throw new StateNotFoundException("The state: " + state.toString() + " could not be found.");
		} else if (i < markIndex) {
			throw new StateIsMarkedException("The state: " + state.toString() + " is marked.");
		}
	}

	@Override
	public PartitionBlock<S> shareIdentity(PartitionBlock<S> block) {
		hash = -1;
		assert block.isEmpty();
		assert markIndex == 0;
		
		if (block instanceof ArrayPartitionBlock) {
			// Efficient implementation that shares the underlying array
			ArrayPartitionBlock<S> myBlock = (ArrayPartitionBlock<S>)block;
			myBlock.states = this.states;
			myBlock.statesToIndex = this.statesToIndex;
			myBlock.hash = -1;
		} else {
			// we manually copy the states over
			for (S s: states) {
				block.addState(s);
			} 
		}
		
		
		return block;
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof ArrayPartitionBlock)) {
			return false;
		}
		
		ArrayPartitionBlock<S> o = (ArrayPartitionBlock<S>)other;
		if (!o.states.equals(this.states)) {
			return false;
		}
		return this.mapToValues.equals(o.mapToValues);
	}
	
	@Override
	public int hashCode() {
		if (hash == -1) {
			hash = states.hashCode() + mapToValues.hashCode();
		}
		
		return hash;
	}

	@Override
	public void toBeUsedAsSplitter() {
		this.used = false;
	}

	@Override
	public Iterator<S> iterator() {
		return getStates();
	}

	@Override
	public boolean hasMarkedStates() {
		return markIndex > 0;
	}
}
