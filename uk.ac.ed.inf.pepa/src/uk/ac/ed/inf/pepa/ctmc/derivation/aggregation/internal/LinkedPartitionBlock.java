/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.PartitionBlock;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.StateIsMarkedException;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.StateNotFoundException;

/**
 * @author Giacomo Alzetta
 * @param <S> The type of the states in the block.
 * @param <B> The type of the values associated with states in the block.
 *
 */
public class LinkedPartitionBlock<S> implements PartitionBlock<S> {

	private LinkedList<S> nonMarkedStates;
	private LinkedList<S> markedStates;
	private boolean used = false;
	
	private HashMap<S, Double> mapToValues;
	
	public LinkedPartitionBlock() {
		nonMarkedStates = new LinkedList<>();
		markedStates = new LinkedList<>();
		mapToValues = new HashMap<>();
	}
	
	public LinkedPartitionBlock(LinkedList<S> states, HashMap<S, Double> values) {
		markedStates = new LinkedList<S>();
		nonMarkedStates = states;
		mapToValues = new HashMap<>();
		for (S s: states) {
			mapToValues.put(s, values.get(s));
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
		nonMarkedStates.add(state);
		
	}

	@Override
	public boolean isEmpty() {
		return nonMarkedStates.isEmpty() && markedStates.isEmpty();
	}

	@Override
	public Iterator<S> getStates() {
		Iterator<S> iterator = new Iterator<S>() {
			private Iterator<S> it1 = nonMarkedStates.iterator();
			private Iterator<S> it2 = markedStates.iterator();
			
			public boolean hasNext() {
				return it1.hasNext() || it2.hasNext();
			}
			
			public S next() {
				if (it1.hasNext()) {
					return it1.next();
				} else {
					return it2.next();
				}
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
		return iterator;
		
	}

	@Override
	public Iterator<S> getMarkedStates() {
		return new Iterator<S>() {
			Iterator<S> it = markedStates.iterator();
			
			public boolean hasNext() {
				return it.hasNext();
			}
			
			public S next() {
				return it.next();
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}		
		};
	}

	@Override
	public PartitionBlock<S> splitMarkedStates() {
		PartitionBlock<S> marked = new LinkedPartitionBlock<>(markedStates, mapToValues);
		this.markedStates = new LinkedList<>();
		this.mapToValues.clear();
		return marked;
	}

	@Override
	public Collection<PartitionBlock<S>> splitBlock() {		
		ArrayList<Double> values = new ArrayList<>(mapToValues.values());
		
		double pmc = PartitioningUtils.pmc(values);
		
		
		HashMap<S, Double> mappingNotPmc = new HashMap<>(mapToValues);
		HashMap<S, Double> mappingOfPmc = PartitioningUtils.splitMapOnValue(mappingNotPmc, pmc);
		
		PartitionBlock<S> pmcBlock = new LinkedPartitionBlock<S>();
		for (Map.Entry<S, Double> entry: mappingOfPmc.entrySet()) {
			pmcBlock.addState(entry.getKey());
		}
		
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
				blocks.put(val, new LinkedPartitionBlock<S>());
			}
			blocks.get(val).addState(entry.getKey());
		}
		
		// TODO: We do not want to allow modifications...
		return blocks.values();
	}

	@Override
	public void markState(S state) throws StateNotFoundException {
		boolean found = false;
		for (int i=0; i < nonMarkedStates.size(); i++) {
			if (nonMarkedStates.get(i).equals(state)) {
				nonMarkedStates.remove(i);
				markedStates.add(state);
				found = true;
				break;
			}
		}
		
		if (!found) {
			throw new StateNotFoundException("Could not find the state: " + state.toString() + " in block.");
		}
		
	}

	@Override
	public void setValue(S state, double value)
			throws StateNotFoundException, StateIsMarkedException {
		if (nonMarkedStates.contains(state)) {
			mapToValues.put(state, value);
		} else if (markedStates.contains(state)) {
			throw new StateIsMarkedException("State: " + state.toString() + " is marked.");
		} else {
			throw new StateNotFoundException("State: " + state.toString() + " could not be found in the block.");
		}
	}

	@Override
	public double getValue(S state)
			throws StateNotFoundException, StateIsMarkedException {
		Double val = mapToValues.get(state);
		if (val == null) {
			if (mapToValues.containsKey(state)) {
				throw new RuntimeException("Impossible has happened: a state mapped to null.");
			} else if (nonMarkedStates.contains(state)) {
				throw new StateIsMarkedException("State: " + state.toString() + " is marked.");
			} else {
				throw new StateNotFoundException("State: " + state.toString() + " could not be found in the block.");
			}
		}
		return val;
	}

	@Override
	public boolean isMarked(S state) throws StateNotFoundException {
		for (S s: markedStates) {
			if (state.equals(s)) {
				return true;
			}
		}
		
		for (S s: nonMarkedStates) {
			if (state.equals(s)) {
				return false;
			}
		}
		
		throw new StateNotFoundException("The state: " + state.toString() + " could not be found.");
	}
	
	@Override
	public int size() {
		return markedStates.size() + nonMarkedStates.size();
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
	public PartitionBlock<S> shareIdentity(PartitionBlock<S> block) {
		assert block.isEmpty() && !isEmpty();
		//assert markedStates.isEmpty();
		
		if (block instanceof LinkedPartitionBlock) {
			LinkedPartitionBlock<S> myBlock = (LinkedPartitionBlock<S>)block;
			myBlock.nonMarkedStates = nonMarkedStates;
			myBlock.markedStates = markedStates;
		} else {
			for (S s: nonMarkedStates) {
				block.addState(s);
			} 
		}
		
		assert !block.isEmpty();
		return block;
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
		// TODO Auto-generated method stub
		return !markedStates.isEmpty();
	}
}
