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
	private ActionLevel[] action_levels;
	private IntegerArray preStateRow = null;
	private IntegerArray preImageColumn = null;
	private DoubleArray rates;
	private int numActionTypes;

	
	public ArraysLtsModel(int numActionTypes, IntegerArray row, IntegerArray column,
						  ShortArray actions, ArrayList<ActionLevel> action_levels, DoubleArray rates) {
		stateRow = row;
		transitionColumn = column;
		actionColumn = actions;
		this.rates = rates;
		this.numActionTypes = numActionTypes;
		
		this.action_levels = new ActionLevel[numActionTypes];
		for (int i=0; i<numActionTypes; ++i) {
			this.action_levels[i] = ActionLevel.UNDEFINED;
		}

		for (int i=0; i<actions.size(); ++i) {
			Short actionid = actions.get(i);
			this.action_levels[actionid] = action_levels.get(i);
		}

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
	public Iterable<Integer> getStates() {
		ArrayList<Integer> states = new ArrayList<Integer>();

		Iterator<Integer> state_it = iterator();

		while (state_it.hasNext()) {
			states.add(state_it.next());
		}

		return states;
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
			if (action_levels[actionid] == level) {
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
			if (action_levels[actionColumn.get(i)] == level) {
				int tState = transitionColumn.get(i);

				targets.add(tState);
			}
		}
		
		return targets;
	}

	@Override
	public ActionLevel getActionLevel(short actionId) {
		return action_levels[actionId];
	}
	
	@Override
	public Iterable<Integer> getPreImage(Integer target, ActionLevel level) {
		int rangeStart = preStateRow.get(target);
		int rangeEnd = target == preStateRow.size()-1 ? preImageColumn.size() : preStateRow.get(target+1);
		
		ArrayList<Integer> preIm = new ArrayList<>(rangeEnd-rangeStart);
		for (int i=rangeStart; i < rangeEnd; ++i) {
			if (action_levels[actionColumn.get(i)] == level) {
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
		builder.append("LTS:\n States: {");
		String sep = "";
		for (int state=0; state < stateRow.size(); ++state) {
			builder.append(sep + state);
			sep = ",";
		}

		builder.append("}\n Transitions:");
		for (int source=0; source < stateRow.size(); ++source) {
			int rangeStart = stateRow.get(source);
			int rangeEnd = source == stateRow.size()-1 ? transitionColumn.size() : stateRow.get(source+1);
			for (int t=rangeStart; t < rangeEnd; t+=2) {
				int target = transitionColumn.get(t);
				int startCol = transitionColumn.get(t+1);
				int endCol = t < transitionColumn.size()-3 ? transitionColumn.get(t+3) : rates.size(); 
				for (int c=startCol; c < endCol; ++c) {
					builder.append("\n  " + source + "-["+ actionColumn.get(c) + ","
					               + rates.get(c) + "," + action_levels[actionColumn.get(c)]
					               + "]->" + target);
				}
			}
		}
		
		return builder.toString();
	}
}
