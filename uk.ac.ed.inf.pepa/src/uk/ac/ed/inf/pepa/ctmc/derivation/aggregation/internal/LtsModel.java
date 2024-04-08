package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTSBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.model.ActionLevel;

public class LtsModel<S> implements LTS<S>, LTSBuilder<S> {

	private ArrayList<S> states;
	private ActionLevel action_levels[];
	private HashMap<S, HashMap<S, double[]>> transitionMap;
	private HashMap<S, HashMap<ActionLevel, TreeSet<S>>> preImageMap;
	
	private int numActionIds;
	
	public LtsModel(int numActionIds) {
		this.numActionIds = numActionIds;
		
		states = new ArrayList<>();
		preImageMap = new HashMap<>();
		transitionMap = new HashMap<>();

		action_levels = new ActionLevel[numActionIds];
		
		for (int i=0; i<numActionIds; ++i) {
			action_levels[i] = ActionLevel.UNDEFINDED;
		}
	}
	
	@Override
	public int numberOfStates() {
		return states.size();
	}

	@Override
	public double getApparentRate(S source, S target, short actionId) {
		if (actionId == ISymbolGenerator.TAU_ACTION && source.equals(target)) {
			System.err.println("found tau action! (self loop)");
		}

		// FIXME: this may throw a NPE if the LTS is built incorrectly...
		double[] labels = transitionMap.get(source).get(target);
		if (labels == null) return 0.0d;
		return labels[actionId];
	}

	@Override
	public ActionLevel getActionLevel(short actionId) {
		return action_levels[actionId];
	}

	@Override
	public Iterable<S> getImage(S source) {
		HashMap<S, double[]> targetsMap = transitionMap.get(source);
		return targetsMap.keySet();
	}

	@Override
	public Iterable<S> getPreImage(S target) {
		TreeSet<S> total = new TreeSet<S>();
		
		for (TreeSet<S> level_pre_images : preImageMap.get(target).values()) {
			total.addAll(level_pre_images);
		}
		return total;
	}
	
	private Boolean any_label_at_level(double[] rates, ActionLevel level)
	{
		for (int i=0; i<numActionIds; ++i) {
			if (rates[i] != 0.0 && action_levels[i] == level) {
				return true;
			}
		}
		
		return false;
	}


	@Override
	public Iterable<S> getImage(S source, ActionLevel level) {
		HashMap<S, double[]> targetsMap = transitionMap.get(source);
		
		TreeSet<S> total = new TreeSet<S>();
		for (S target : targetsMap.keySet()) {
			if (any_label_at_level(targetsMap.get(target), level)) {
				total.add(target);
			}
		}
		return total;
		
	}

	@Override
	public Iterable<S> getPreImage(S target, ActionLevel level) {
		return preImageMap.get(target).get(level);
	}
	
	@Override
	public Iterable<Short> getActions(S source, S target) {
		HashMap<S, double[]> acts = transitionMap.get(source);
		assert acts != null;
		
		double[] actionTypes = acts.get(target);
		ArrayList<Short> actTypes = new ArrayList<>();
		if (actionTypes == null) {
			assert false :  "found null action types!";
			return actTypes;
		}
		
		for (short i=0; i < actionTypes.length; ++i) {
			if (actionTypes[i] != 0.0d) {
				actTypes.add(i);
			}
		}
		return actTypes;
	}

	@Override
	public Iterable<Short> getActions(S source, S target, ActionLevel level) {
		ArrayList<Short> actTypes = new ArrayList<>();

		for (Short actionid : getActions(source, target)) {
			if (action_levels[actionid] == level) {
				actTypes.add(actionid);
			}
		}

		return actTypes;
	}
	

	@Override
	public void addState(S state) {
		states.add(state);
		transitionMap.put(state, new HashMap<S, double[]>());
		preImageMap.put(state, new HashMap<ActionLevel, TreeSet<S>>());
	}

	@Override
	public void addTransition(S source, S target, double rate, short actionId, ActionLevel level) {
		double[] targetMap = getRates(source, target);
		action_levels[actionId] = level;
		targetMap[actionId] = rate;
		HashMap<ActionLevel, TreeSet<S>> preImTarget = preImageMap.get(target);
		if (preImTarget == null) {
			preImTarget = new HashMap<ActionLevel, TreeSet<S>>();
			preImageMap.put(target, preImTarget);
		}
		TreeSet<S> preImTargetLevel = preImTarget.get(level);
		if (preImTargetLevel == null) {
			preImTargetLevel = new TreeSet<S>();
			preImTarget.put(level, preImTargetLevel);
		}
		preImTargetLevel.add(source);
	}
	
	@Override
	public Iterator<S> iterator() {
		return states.iterator();
	}
	
	
	private double[] getRates(S source, S target) {
		HashMap<S, double[]> targetsMap = transitionMap.get(source);
		// targetsMap cannot be null. If it is then source is not in the LTS.
		assert targetsMap != null;
		
		double[] map = targetsMap.get(target);
		if (map == null) {
			map = new double[numActionIds];
			//Arrays.fill(map, 0.0d);
			targetsMap.put(target, map);
		}
		
		return map;
	}

	@Override
	public int numberOfTransitions() {
		int total = 0;
		for (HashMap<S, double[]> trans : transitionMap.values()) {
			for (double[] m : trans.values()) {
				for (double d: m) {
					if (d != 0.0d) {
						total += 1;
					}
				}
			}
		}
		
		return total;
	}
	
	@Override
	public int numberOfActionTypes() {
		return numActionIds;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LTS:\n");
		builder.append("States are:\n");
		for (S state: this) {
			builder.append("State: " + state + "\n");
			
		}
		for (S source: this) {
			builder.append("State: " + source + " to:\n");
			builder.append("Transitions are:\n");
			for (S target: getImage(source)) {
				builder.append("\tTarget " + target + "\n");
				for (short action: getActions(source, target)) {
					builder.append("\t\tLabel " + action + " rate "
								   + getApparentRate(source, target, action) + "\n");
				}
		    }
		}
		
		return builder.toString();
		
	}

	@Override
	public LTS<S> variantView() {
		return new VariantView<>(this);
	}

	@Override
	public LTS<S> getLts() {
		// TODO Auto-generated method stub
		return this;
	}
	
}
