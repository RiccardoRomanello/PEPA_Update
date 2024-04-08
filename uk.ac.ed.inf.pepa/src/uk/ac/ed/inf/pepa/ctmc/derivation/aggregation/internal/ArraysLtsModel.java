/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DoubleArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * @author Giacomo Alzetta
 *
 */
public class ArraysLtsModel implements LTS<Integer> {
	
	
	private IntegerArray stateRow;
	private IntegerArray transitionColumn;
	private ShortArray actionColumn;
	private ArrayList<ActionLevel> action_levels;
	private IntegerArray preStateRow = null;
	private IntegerArray preImageColumn = null;
	private DoubleArray rates;
	private int numActionTypes;

	
	public ArraysLtsModel(int numActionTypes, IntegerArray row, IntegerArray column,
						  ShortArray actions, ArrayList<ActionLevel> action_levels, DoubleArray rates) {
		stateRow = row;
		transitionColumn = column;
		actionColumn = actions;
		this.action_levels = action_levels;
		this.rates = rates;
		this.numActionTypes = numActionTypes;
		
		computePreImageColumn();
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private int i=0;
			
			public boolean hasNext() {
				return i < stateRow.size();
			}
			
			public Integer next() {
				return i++;
			}
		};
	}

	@Override
	public int numberOfStates() {
		return stateRow.size();
	}

	@Override
	public int numberOfActionTypes() {
		return numActionTypes;
	}

	@Override
	public Iterable<Short> getActions(Integer source, Integer target) {
		// TODO: check if the transitions are ordered by target,
		// if so this could be optimized quite a bit.
		ArrayList<Short> acts = new ArrayList<>();
		int startCol = stateRow.get(source);
		int endCol = (source == stateRow.size() - 1 ? transitionColumn.size() : stateRow.get(source+1));
		for (int i=startCol; i < endCol; i += 2) {
			int tState = transitionColumn.get(i);
			
			if (tState != target) continue;
			
			int startTrans = transitionColumn.get(i+1);
			int endTrans = i < transitionColumn.size() -3 ? transitionColumn.get(i+3) : actionColumn.size();
			//ArrayList<Short> acts = new ArrayList<>(endTrans - startTrans);
			for (int j=startTrans; j < endTrans; ++j) {
				acts.add(actionColumn.get(j));
			}
			
			//return acts;
		}
		
		// FIXME: maybe we should avoid this?
		HashSet<Short> actsUnique = new HashSet<>(acts);
		acts.clear();
		acts.addAll(actsUnique);
		return acts;
		//return new ArrayList<>(0);
	}

	@Override
	public Iterable<Short> getActions(Integer source, Integer target, ActionLevel level) {
		ArrayList<Short> actTypes = new ArrayList<>();

		for (Short actionid : getActions(source, target)) {
			if (action_levels.get(numActionTypes) == level) {
				actTypes.add(actionid);
			}
		}

		return actTypes;
	}

	@Override
	public double getApparentRate(Integer source, Integer target, short actionId) {
		if (actionId == ISymbolGenerator.TAU_ACTION && source.equals(target)) {
			System.err.println("found tau action! (self loop!)");
		}
		
		//System.err.println("Found action: " + actionId);
 		// FIXME: we could pre-compute these sums.
		int aId = actionId;
		double rate = 0.0d;
		int startCol = stateRow.get(source);
		int endCol = (source == stateRow.size() - 1 ? transitionColumn.size() : stateRow.get(source+1));
		for (int i=startCol; i < endCol; i += 2) {
			int tState = transitionColumn.get(i);
			
			if (tState != target) continue;
			
			int startTrans = transitionColumn.get(i+1);
			int endTrans = i < transitionColumn.size() -3 ? transitionColumn.get(i+3) : actionColumn.size();
			for (int j=startTrans; j < endTrans; ++j) {
				if (actionColumn.get(j) == aId) {
					rate += rates.get(j);
				}
			}
		}
		
		return rate;
	}

	@Override
	public Iterable<Integer> getImage(Integer source) {
		HashSet<Integer> targets = new HashSet<>();
		int startCol = stateRow.get(source);
		int endCol = (source == stateRow.size() - 1 ? transitionColumn.size() : stateRow.get(source+1));
		for (int i=startCol; i < endCol; i += 2) {
			int tState = transitionColumn.get(i);
			
			targets.add(tState);
		}
		
		return targets;
	}

	@Override
	public Iterable<Integer> getPreImage(Integer target) {
		int rangeStart = preStateRow.get(target);
		int rangeEnd = target == preStateRow.size()-1 ? preImageColumn.size() : preStateRow.get(target+1);
		
		ArrayList<Integer> preIm = new ArrayList<>(rangeEnd-rangeStart);
		for (int i=rangeStart; i < rangeEnd; ++i) {
			preIm.add(preImageColumn.get(i));
		}
		
		return preIm;
 	}
	

	@Override
	public Iterable<Integer> getImage(Integer source, ActionLevel level) {
		HashSet<Integer> targets = new HashSet<>();
		int startCol = stateRow.get(source);
		int endCol = (source == stateRow.size() - 1 ? transitionColumn.size() : stateRow.get(source+1));
		for (int i=startCol; i < endCol; i += 2) {
			if (action_levels.get(actionColumn.get(i)) == level) {
				int tState = transitionColumn.get(i);

				targets.add(tState);
			}
		}
		
		return targets;
	}

	@Override
	public ActionLevel getActionLevel(short actionId) {
		return action_levels.get(actionId);
	}
	
	@Override
	public Iterable<Integer> getPreImage(Integer target, ActionLevel level) {
		int rangeStart = preStateRow.get(target);
		int rangeEnd = target == preStateRow.size()-1 ? preImageColumn.size() : preStateRow.get(target+1);
		
		ArrayList<Integer> preIm = new ArrayList<>(rangeEnd-rangeStart);
		for (int i=rangeStart; i < rangeEnd; ++i) {
			if (action_levels.get(actionColumn.get(i)) == level) {
				preIm.add(preImageColumn.get(i));
			}
		}
		
		return preIm;
 	}
	
	private void computePreImageColumn() {
		HashMap<Integer, HashSet<Integer>> pre = new HashMap<>(stateRow.size());
		
		for (int source=0; source < stateRow.size(); ++source) {
			int rangeStart = stateRow.get(source);
			int rangeEnd = source == stateRow.size() - 1? transitionColumn.size(): stateRow.get(source+1);
			for (int t=rangeStart; t < rangeEnd; t += 2) {
				int tState = transitionColumn.get(t);
				HashSet<Integer> trans = pre.get(tState);
				if (trans == null) {
					trans = new HashSet<>();
					pre.put(tState, trans);
				}
				
				trans.add(source);
			}
		}

		int total = 0;
		HashSet<Integer> empty = new HashSet<>(0);
		
		for (int i=0; i < stateRow.size(); i++) {
			HashSet<Integer> ts = pre.get(i);
			int val = 0;
			if (ts == null) {
				System.err.println("Happened!");
				// this shouldn't normally happen
				pre.put(i, empty);
			} else {
				val = ts.size();
			}
			total += val;
		}
		
		preStateRow = new IntegerArray(stateRow.size());
		preImageColumn = new IntegerArray(total);
		
		int curIndex = 0;
		for (int target=0; target < stateRow.size(); ++target) {
			HashSet<Integer> sources = pre.get(target);
			preStateRow.add(curIndex);
			//if (sources == null) continue;
			for (int source: sources) {
				preImageColumn.add(source);
				++curIndex;
			}
		}
	}


	@Override
	public int numberOfTransitions() {
		return rates.size();
	}

	
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("LTS:\n");
		builder.append("States are:\n");
		for (int state=0; state < stateRow.size(); ++state) {
			builder.append("State: " + state + "\n");
		}
		builder.append("Transitions are:\n");
		for (int source=0; source < stateRow.size(); ++source) {
			builder.append("State: " + source + " to:\n");
			int rangeStart = stateRow.get(source);
			int rangeEnd = source == stateRow.size()-1 ? transitionColumn.size() : stateRow.get(source+1);
			for (int t=rangeStart; t < rangeEnd; t+=2) {
				int target = transitionColumn.get(t);
				builder.append("\tTarget " + target + "\n");
				int startCol = transitionColumn.get(t+1);
				int endCol = t < transitionColumn.size()-3 ? transitionColumn.get(t+3) : rates.size(); 
				for (int c=startCol; c < endCol; ++c) {
					builder.append("\t\tLabel " + actionColumn.get(c) + " rate " + rates.get(c) + "\n");
				}
			}
		}
		
		return builder.toString();
	}


	@Override
	public LTS<Integer> variantView() {
		return new View(this);
	}
	
	
	
	/**
	 * Lightweight variant view of the LTS.
	 * 
	 * @author Giacomo Alzetta
	 *
	 */
	private class View implements LTS<Integer> {
		
		/**
		 * The underlying LTS.
		 */
		ArraysLtsModel model;
		
		/**
		 * The tau self-loop rates. The value at index i corresponds to the rate
		 * of the self-loop for state i.
		 */
		DoubleArray selfLoops;
		
		public View(ArraysLtsModel model) {
			this.model = model;
			
			// We compute all self loops now.
			selfLoops = new DoubleArray(model.numberOfStates());
			for (int i=0; i < model.numberOfStates(); ++i) {
				selfLoops.add(computeSelfLoopRate(i));
			}
		}
		
		/**
		 * Get the rate of the tau self loop for state i.
		 * 
		 * @param i
		 * @return
		 */
		private double computeSelfLoopRate(int i) {
			double rate = 0.0d;
			for (int j=0; j < model.stateRow.size(); ++j) {
				if (i != j) {
					rate += model.getApparentRate(i, j, ISymbolGenerator.TAU_ACTION);
				}
			}
			
			return -rate;
		}

		/**
		 * Iterate over all the states of the underlying LTS.
		 * @return
		 */
		@Override
		public Iterator<Integer> iterator() {
			return model.iterator();
		}

		/**
		 * The number of states of the underlying LTS.
		 */
		@Override
		public int numberOfStates() {
			return model.numberOfStates();
		}

		/**
		 * The number of transitions in the underlying LTS.
		 */
		@Override
		public int numberOfTransitions() {
			// FIXME: add self loops? 
			return model.numberOfTransitions();
		}

		/**
		 * The number of action types in the underlying LTS.
		 */
		@Override
		public int numberOfActionTypes() {
			return model.numberOfActionTypes();
		}

		@Override
		public ActionLevel getActionLevel(short actionid) {
			return model.getActionLevel(actionid);
		}
		
		/**
		 * Get the actions that label transitions between <code>source</code>
		 * and <code>target</code>.
		 * 
		 * Note that if <code>source.equals(target)</code> then tau is always
		 * included.
		 * 
		 * @param source
		 * @param target
		 * @param level
		 * @return
		 */
		@Override
		public Iterable<Short> getActions(Integer source, Integer target, ActionLevel level) {
			ArrayList<Short> acts = (ArrayList<Short>) model.getActions(source, target, level);
			
			if (source.equals(target) && !acts.contains(ISymbolGenerator.TAU_ACTION)) {
				acts.add(ISymbolGenerator.TAU_ACTION);
			}
			return acts;
		}		
		
		/**
		 * Get the actions that label transitions between <code>source</code>
		 * and <code>target</code>.
		 * 
		 * Note that if <code>source.equals(target)</code> then tau is always
		 * included.
		 * 
		 * @param source
		 * @param target
		 * @return
		 */
		@Override
		public Iterable<Short> getActions(Integer source, Integer target) {
			ArrayList<Short> acts = (ArrayList<Short>) model.getActions(source, target);
			
			if (source.equals(target) && !acts.contains(ISymbolGenerator.TAU_ACTION)) {
				acts.add(ISymbolGenerator.TAU_ACTION);
			}
			return acts;
		}

		/**
		 * Get the apparent rate of the transitions from source to target
		 * with the given actionId in the underlying LTS.
		 * 
		 * If <code>source.equals(target)</code> and the <code>actionId</code>
		 * refers to the tau transition then the self-loop rate is returned.
		 * 
		 * @param source
		 * @param target
		 * @param actionId
		 * @return
		 */
		@Override
		public double getApparentRate(Integer source, Integer target, short actionId) {
			if (actionId == ISymbolGenerator.TAU_ACTION && source.equals(target)) {
				return selfLoops.get(source);
			}
			
			return model.getApparentRate(source, target, actionId);
		}

		/**
		 * Get the states that are reachable via transitions from <code>source</code>.
		 * 
		 * Note that <code>source</code> itself is always included since
		 * it has a tau self-loop.
		 * 
		 * @param source
		 * @return
		 */
		@Override
		public Iterable<Integer> getImage(Integer source) {
			HashSet<Integer> targets = (HashSet<Integer>) model.getImage(source);
			targets.add(source);
			
			return targets;
		}

		/**
		 * Get the states that can reach via transitions the given
		 * state <code>target</code>.
		 * 
		 * Note that <code>target</code> itself is always included since
		 * it has a tau self-loop.
		 * 
		 * @param target
		 * @return
		 */
		@Override
		public Iterable<Integer> getPreImage(Integer target) {
			ArrayList<Integer> sources = (ArrayList<Integer>) model.getPreImage(target);
			
			if (!sources.contains(target)) sources.add(target);
			
			return sources;
		}

		/**
		 * Get the states that are reachable via transitions from <code>source</code>.
		 * 
		 * Note that <code>source</code> itself is always included since
		 * it has a tau self-loop.
		 * 
		 * @param source
		 * @param level
		 * @return
		 */
		@Override
		public Iterable<Integer> getImage(Integer source, ActionLevel level) {
			HashSet<Integer> targets = (HashSet<Integer>) model.getImage(source, level);
			targets.add(source);
			
			return targets;
		}

		/**
		 * Get the states that can reach via transitions the given
		 * state <code>target</code>.
		 * 
		 * Note that <code>target</code> itself is always included since
		 * it has a tau self-loop.
		 * 
		 * @param target
		 * @param level
		 * @return
		 */
		@Override
		public Iterable<Integer> getPreImage(Integer target, ActionLevel level) {
			ArrayList<Integer> sources = (ArrayList<Integer>) model.getPreImage(target, level);
			
			if (!sources.contains(target)) {
				sources.add(target);
			}
			
			return sources;
		}

		/**
		 * Returns itself.
		 */
		@Override
		public LTS<Integer> variantView() {
			return this;
		}
	}
}
