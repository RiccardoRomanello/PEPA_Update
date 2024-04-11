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
	private HashMap<S, HashMap<S, HashMap<Short, Double>>> transitionMap;
	private HashMap<S, HashMap<ActionLevel, TreeSet<S>>> preImageMap;
	
	private int numActionIds;
	
	public LtsModel(int numActionIds) {
		this.numActionIds = numActionIds;
		
		states = new ArrayList<>();
		preImageMap = new HashMap<>();
		transitionMap = new HashMap<>();

		action_levels = new ActionLevel[numActionIds];
		
		for (int i=0; i<numActionIds; ++i) {
			action_levels[i] = ActionLevel.UNDEFINED;
		}
	}
	
	public LtsModel(LTS<S> lts) {
		this(lts.numberOfActionTypes());

		for (S state : lts.getStates()) {
			this.addState(state);
		}

		for (S src : lts.getStates()) {
			for (S dst : lts.getImage(src)) {
				for (short actionid : lts.getActions(src, dst)) {
					double rate = lts.getApparentRate(src, dst, actionid);
					ActionLevel level = lts.getActionLevel(actionid);

					this.addTransition(src, dst, rate, actionid, level);
				}
			}
		}
	}

	@Override
	public int numberOfStates() {
		return states.size();
	}

	@Override
	public Iterable<S> getStates() {
		return states;
	}

	@Override
	public double getApparentRate(S source, S target, short actionId) {
		HashMap<S, HashMap<Short, Double>> dest_map = transitionMap.get(source);
		if (!dest_map.containsKey(target)) {
			return 0.0d;
		}

		HashMap<Short, Double> dest_action_map = dest_map.get(target);
		if (!dest_action_map.containsKey(actionId)) {
			return 0.0d;
		}

		return dest_action_map.get(actionId);
	}

	@Override
	public ActionLevel getActionLevel(short actionId) {
		return action_levels[actionId];
	}

	@Override
	public Iterable<S> getImage(S source) {
		HashMap<S, HashMap<Short, Double>> targetsMap = transitionMap.get(source);
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
	
	private boolean any_action_at_level(HashMap<Short, Double> dest_action_map, ActionLevel level)
	{
		for (Map.Entry<Short, Double> entry : dest_action_map.entrySet()) {
			if (action_levels[entry.getKey()] == level) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Iterable<S> getImage(S source, ActionLevel level) {
		HashMap<S, HashMap<Short, Double>> targetsMap = transitionMap.get(source);
		
		TreeSet<S> total = new TreeSet<S>();
		for (S target : targetsMap.keySet()) {
			if (any_action_at_level(targetsMap.get(target), level)) {
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
		HashMap<S, HashMap<Short, Double>> acts = transitionMap.get(source);
		assert acts != null;
		
		HashMap<Short, Double> actionTypes = acts.get(target);
		ArrayList<Short> actTypes = new ArrayList<>();
		if (actionTypes == null) {
			assert false :  "found null action types!";
			return actTypes;
		}
		
		for (Map.Entry<Short, Double> entry : actionTypes.entrySet()) {
			actTypes.add(entry.getKey());
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
		transitionMap.put(state, new HashMap<S, HashMap<Short, Double>>());
		preImageMap.put(state, new HashMap<ActionLevel, TreeSet<S>>());
	}

	@Override
	public void addTransition(S source, S target, double rate, short actionId, ActionLevel level) {
		HashMap<Short, Double> targetMap = getRates(source, target);
		action_levels[actionId] = level;
		targetMap.put(actionId, rate);
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
	
	
	private HashMap<Short, Double> getRates(S source, S target) {
		HashMap<S, HashMap<Short, Double>> targetsMap = transitionMap.get(source);
		// targetsMap cannot be null. If it is then source is not in the LTS.
		assert targetsMap != null;
		
		HashMap<Short, Double> map = targetsMap.get(target);
		if (map == null) {
			map = new HashMap<Short, Double>();
			//Arrays.fill(map, 0.0d);
			targetsMap.put(target, map);
		}
		
		return map;
	}

	@Override
	public int numberOfTransitions() {
		int total = 0;

		for (HashMap<S, HashMap<Short, Double>> targetsMap : transitionMap.values()) {
			for (HashMap<Short, Double> map : targetsMap.values()) {
				total += 1;
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
		builder.append("LTS:\n States: {");
		String sep = "";
		for (S state: this) {
			builder.append(sep + state);
			sep = ",";
		}

		builder.append("}\n Transitions:");
		Iterable<S> local_states = getStates();
		for (S src : local_states) {
			for (S dst : local_states) {
				for (short action : getActions(src, dst)) {
					double rate = getApparentRate(src, dst, action);
					if (rate != 0) {
						ActionLevel level = getActionLevel(action);
						builder.append("\n  " + src + "-[" + action + ","
										+ rate + "," + level + "]->" + dst);
					}
				}
			}
		}

		return builder.toString();
	}

	@Override
	public LTS<S> getLts() {
		// TODO Auto-generated method stub
		return this;
	}
	
}
