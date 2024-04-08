/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.MeasurementData;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.ArraysLtsModel;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.LtsModel;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DoubleArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.MemoryCallback;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.OptimisedHashMap;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.MemoryStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.OptimisedHashMap.InsertionResult;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.model.ActionLevel;
import uk.ac.ed.inf.pepa.model.NamedRate;
import uk.ac.ed.inf.pepa.model.RateMath;
import uk.ac.ed.inf.pepa.model.internal.NamedRateImpl;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.parsing.WildcardCooperationNode;

/**
 * Computes the aggregated state space.
 * 
 * Note: in order to use this builder you must first create a StateSpaceBuilder
 * and pass the explorer and symbol generator from that builder to this one.
 * @author Giacomo Alzetta
 *
 */
public class AggregationStateSpaceBuilder implements IStateSpaceBuilder {
	
	private final ISymbolGenerator generator;
	private IStateExplorer explorer;
	
	private AggregationAlgorithm<Integer> algorithm;
	
	private final static int REFRESH_MONITOR = 20000;
	
	private final boolean useArraysModel = true;
	

	public AggregationStateSpaceBuilder(
			IStateExplorer explorer,
			ISymbolGenerator generator,
			AggregationAlgorithm<Integer> algorithm) {
		this.explorer = explorer;
		this.generator = generator;
		this.algorithm = algorithm;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder#derive(boolean, uk.ac.ed.inf.pepa.IProgressMonitor)
	 */
	@Override
	public IStateSpace derive(boolean allowPassiveRates, IProgressMonitor monitor)
			throws DerivationException {
		
		if (monitor == null) 
			monitor = new DoNothingMonitor();
		
		long startTime = System.nanoTime();
		long endTime = 0;
		double commonDeriveTimeMillis = -1;
		double obtainLtsTimeMillis = -1;
		monitor.beginTask(IProgressMonitor.UNKNOWN);
		ArrayList<State> states = new ArrayList<>(1000);
		LTS<Integer> lts;
		{
			// TODO: we should use a custom callback here...
			MemoryCallback callback = new MemoryCallback();
			
			
			if (!exploreStateSpace(monitor, callback, states)) {
				return null;
			}

			IntegerArray row = callback.getRow();
			IntegerArray col = callback.getColumn();
			DoubleArray rates = callback.getRates();
			ShortArray actionIds = callback.getActions();
			ArrayList<ActionLevel> action_levels = callback.getActionLevels();
			
			endTime = System.nanoTime();
			commonDeriveTimeMillis = (endTime - startTime)/1000000;
			
			startTime = System.nanoTime();
			lts = deriveLts(states, row, col, rates, actionIds, action_levels);
			endTime = System.nanoTime();
			obtainLtsTimeMillis = (endTime-startTime)/1000000;
		}
		/*
		System.out.println("Derived an initial LTS with: " + (lts.numberOfStates()) + " states and "
						   + lts.numberOfTransitions() + " transitions.");*/
		System.out.println(String.format("#states %1$d transitions %2$d", lts.numberOfStates(), lts.numberOfTransitions()));
		startTime = System.nanoTime();
		// Aggregate the LTS here
		LTS<Aggregated<Integer>> aggrLts = algorithm.aggregate(lts);
		endTime = System.nanoTime();
		double aggregationLtsTimeMillis = (endTime-startTime)/1000000;
		System.out.println(String.format("#states %1$d transitions %2$d", aggrLts.numberOfStates(), aggrLts.numberOfTransitions()));
		/*
		System.out.println("Obtained an aggregated LTS with: " + aggrLts.numberOfStates() + " states and "
						   + aggrLts.numberOfTransitions() + " transitions");*/
		
		/*
		System.out.println("States are: ");
		for (Aggregated<Integer> s: aggrLts) {
			System.out.println("State: " + s);
		}
		*/
		
		/*
		for (Aggregated<Integer> aggrS: aggrLts) {
			System.out.println("One aggregated state contains: ");
			for (Integer x : aggrS) {
				System.out.println(x.toString());
			}
		} */
		/*
		System.out.println("LTS is: ");
		System.out.println(aggrLts.toString());
		*/
		
		startTime = System.nanoTime();
		IStateSpace result = createStateSpace(states, aggrLts);
		monitor.done();
		endTime = System.nanoTime();
		double aggrLtsToSSTimeMillis = (endTime-startTime)/1000000;
		
		String msg = "#time explore %1$.3f derive %2$.3f aggregate %3$.3f derive %4$.3f (ms)";
		
		System.out.println(
				String.format(msg, commonDeriveTimeMillis, obtainLtsTimeMillis,
						aggregationLtsTimeMillis,aggrLtsToSSTimeMillis));
		/*
		String msg = "Time to explore state space: %1$.3f ms\n"
				+ "Time to derive the LTS: %2$.3f ms\n"
				+ "Time to aggregate the LTS: %3$.3f ms\n"
				+ "Time to derive the final state space: %4$.3f ms\n";
		System.out.println(
				String.format(msg, commonDeriveTimeMillis, obtainLtsTimeMillis,
						aggregationLtsTimeMillis, aggrLtsToSSTimeMillis)
		);*/
		
		return result;
	}

	/**
	 * @param states
	 * @param aggrLts
	 * @return
	 */
	private IStateSpace createStateSpace(ArrayList<State> states,
			LTS<Aggregated<Integer>> aggrLts) {
		ArrayList<Aggregated<Integer>> newStatesToRepr = new ArrayList<>(aggrLts.numberOfStates());
		ArrayList<Integer> reprToNewStates = new ArrayList<>(states.size());
		
		for (int i=0; i < states.size(); i++) {
			reprToNewStates.add(-1);
		}
		
		int i=0;
		for (Aggregated<Integer> s: aggrLts) {
			newStatesToRepr.add(s);
			reprToNewStates.set(s.getRepresentative(), i);
			++i;
			
		}
		
		IntegerArray newRow = new IntegerArray(aggrLts.numberOfStates());
		IntegerArray newCol = new IntegerArray(2*aggrLts.numberOfTransitions());
		ShortArray newActions = new ShortArray(aggrLts.numberOfTransitions());
		DoubleArray newRates = new DoubleArray(aggrLts.numberOfTransitions());
		
		int maxSize = 0;
		boolean hasVariableSize = false;
		
		int colIndex=0;
		for (Aggregated<Integer> s: newStatesToRepr) {
			newRow.add(colIndex);
			for (Aggregated<Integer> target: aggrLts.getImage(s)) {
				for(short actionId: aggrLts.getActions(s, target)) {
					colIndex += 2;
					double rate = aggrLts.getApparentRate(s, target, actionId);
					newCol.add(reprToNewStates.get(target.getRepresentative()));
					newCol.add(newActions.size());
					newActions.add(actionId);
					newRates.add(rate);
				}
			}
		}
		assert newRow.size() == aggrLts.numberOfStates();

		ArrayList<State> newStates = new ArrayList<>(aggrLts.numberOfStates());
		
		// FIXME: this is checked only on representatives.
		// it may be enough, but we have to check that.
		
		for (Aggregated<Integer> state: newStatesToRepr) {
			int repr = state.getRepresentative();
			State s = states.get(repr);
			s.stateNumber = reprToNewStates.get(repr);
			newStates.add(s);
			if (s.fState.length > maxSize) {
				int oldMaxSize = maxSize;
				maxSize = s.fState.length;
				if (oldMaxSize != 0 && maxSize != oldMaxSize) {
					hasVariableSize = true;
				}
			}
		}
		
		/*
		System.err.println("Row:" + newRow);
		System.err.println("Col:" + newCol);
		System.err.println("Rates:" + newRates);
		System.err.println("Actions" + newActions);
		*/
		
		// Derive the CTMC here
		IStateSpace result = new MemoryStateSpace(
				generator,
				newStates,
				newRow,
				newCol,
				newActions,
				newRates,
				hasVariableSize,
				maxSize);
		return result;
	}

	/**
	 * @param monitor
	 * @param callback
	 * @param states
	 * @throws DerivationException
	 */
	private boolean exploreStateSpace(IProgressMonitor monitor, MemoryCallback callback, ArrayList<State> states)
			throws DerivationException {
		OptimisedHashMap map = new OptimisedHashMap();
		Queue<State> queue = new LinkedList<State>();
		
		short[] initialState = generator.getInitialState();
		int hashCode = Arrays.hashCode(initialState);
		
		State initState = map.putIfNotPresentUnsync(initialState, hashCode).state;
		queue.add(initState);
		Transition[] found;
		
		while (!queue.isEmpty()) {
			if (monitor.isCanceled()) {
				monitor.done();
				return false;
			}
			
			State s = queue.remove();
			
			if (s.stateNumber % REFRESH_MONITOR == 0) {
				monitor.worked(REFRESH_MONITOR);
			}
			
			try {
				found = explorer.exploreState(s.fState);
				
			} catch (DerivationException e) {
				throw createException(s, e.getMessage());
			}
			
			if (found.length == 0) {
				monitor.done();
				throw createException(s, "Deadlock found.");
			}
			
			for (Transition t: found) {
				if (t.fRate <= 0) {
					throw createException(s,
							"Incomplete model with respect to action: "
									+ generator.getActionLabel(t.fActionId)
									+ ". ");
				}
				
				hashCode = Arrays.hashCode(t.fTargetProcess);
				InsertionResult result = map.putIfNotPresentUnsync(
						t.fTargetProcess,
						hashCode
				);
				//tocAdded += System.nanoTime() - ticMap;
				
				t.fState = result.state;
				
				if (!result.wasPresent) {
					queue.add(result.state);
					//result.state.stateNumber = rowNumber++;
				}
			}
			
			callback.foundDerivatives(s, found);
			states.add(s);
		}
		
		explorer.dispose();
		states.trimToSize();
		callback.done(generator, states);
		
		return true;
	}

	/**
	 * Derive the LTS model from the lists of states, and transitions produced
	 * by the StateExplorerBuilder and the MemoryCallback.
	 * 
	 * @param states
	 * @param row
	 * @param col
	 * @param rates
	 * @param actionIds
	 * @return
	 */
	private LTS<Integer> deriveLts(ArrayList<State> states,
			IntegerArray row, IntegerArray col, DoubleArray rates,
			ShortArray actionIds, ArrayList<ActionLevel> action_level) {
		
		int numActIds = 0;
		{
			int max = -1;
			for (int i=0; i < actionIds.size(); ++i) {
				int act = actionIds.get(i);
				if (act > max) max = act;
			}
			
			numActIds = max+1;
		}

		LTS<Integer> lts;
		
		if (useArraysModel) {
			lts = new ArraysLtsModel(numActIds, row, col, actionIds, action_level, rates);
		} else {
			LTSBuilder<Integer> ltsBuilder = new LtsModel<>(numActIds);
		
			// Add all states into the LTS.
			for (State s: states) {
				ltsBuilder.addState(s.stateNumber);
			}
			
			int i = 0;
			for (State s: states) {
				// The i-th position in row contains the index t inside the
				// col array that contains the transitions from the state s.
				int rangeStart = row.get(i);
				int rangeEnd = (i == row.size()-1 ? col.size() : row.get(i+1));
				for (int j=rangeStart; j < rangeEnd; j += 2) {
					// The j-th position inside col contains the state number
					// of the target node in the transition. The index j+1
					// contains the starting index in the rates and actionIds
					// arrays that refer to transitions between state s
					// and state target.
					int targetId = col.get(j);
					int colRangeStart = col.get(j+1);
					int colRangeEnd  = (j < col.size() - 3 ? col.get(j+3): rates.size());
					
					// For each these transitions from state s to target
					// and for each label, we add these to the Lts.
					for (int k=colRangeStart; k < colRangeEnd; k++) {
						double rate = rates.get(k);
						short actionId = actionIds.get(k);
						ltsBuilder.addTransition(s.stateNumber, targetId, rate, actionId,
												 lts.getActionLevel(actionId));
					}
				}
				
				
				++i;
			}
			
			lts = ltsBuilder.getLts();
		}
		
		return lts;
	}
	
	private DerivationException createException(State state, String message) {
		StringBuilder buf = new StringBuilder();
		buf.append(message);
		buf.append(" State number: ");
		buf.append(String.valueOf(state.stateNumber));
		buf.append(". ");
		buf.append("State: ");
		for (int i = 0; i < state.fState.length; i++) {
			buf.append(generator.getProcessLabel(state.fState[i]));
			if (i != state.fState.length - 1)
				buf.append(",");
		}
		return new DerivationException(buf.toString());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder#getMeasurementData()
	 */
	@Override
	public MeasurementData getMeasurementData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
