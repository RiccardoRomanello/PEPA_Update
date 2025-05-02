/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.MeasurementData;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTSBuilder;
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

/**
 * Computes the aggregated state space.
 * 
 *
 */
public class LTSDeriver {
	
	protected final ISymbolGenerator generator;
	protected IStateExplorer explorer;
	
	private final static int REFRESH_MONITOR = 20000;
	
	private final boolean useArraysModel = true;
	

	public LTSDeriver(
			IStateExplorer explorer,
			ISymbolGenerator generator) {
		this.explorer = explorer;
		this.generator = generator;
	}

	public LTS<Integer> derive(IProgressMonitor monitor) 
			throws DerivationException {
		return derive(monitor, new ArrayList<State>());
	}

	public LTS<Integer> derive(IProgressMonitor monitor, ArrayList<State> states) 
			throws DerivationException {
		
		if (monitor == null) 
			monitor = new DoNothingMonitor();
		
		long startTime = System.nanoTime();
		long endTime = 0;
		double commonDeriveTimeMillis = -1;
		double obtainLtsTimeMillis = -1;
		monitor.beginTask(IProgressMonitor.UNKNOWN);
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
			ArrayList<Boolean> action_reversibilities = callback.getReversibleActions();
			
			endTime = System.nanoTime();
			commonDeriveTimeMillis = (endTime - startTime)/1000000;
			
			startTime = System.nanoTime();
			lts = deriveLTS(states, row, col, rates, actionIds, action_levels, action_reversibilities);
			endTime = System.nanoTime();
			obtainLtsTimeMillis = (endTime-startTime)/1000000;
		}
		
		System.out.println(String.format("#states %1$d transitions %2$d", lts.numberOfStates(), lts.numberOfTransitions()));
		startTime = System.nanoTime();

		String msg = "#time explore %1$.3f derive %2$.3f (ms)";
		
		System.out.println(
				String.format(msg, commonDeriveTimeMillis, obtainLtsTimeMillis));

		return lts;
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
	 * @param action_levels 
	 * @param action_reversibilities 
	 * @return
	 */
	private LTS<Integer> deriveLTS(ArrayList<State> states,
			IntegerArray row, IntegerArray col, DoubleArray rates,
			ShortArray actionIds, ArrayList<ActionLevel> action_levels, ArrayList<Boolean> action_reversibilities) {
		
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
			lts = new ArraysLtsModel(numActIds, row, col, actionIds, action_levels, rates);
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
						ActionLevel level = action_levels.get(k);
						ltsBuilder.addTransition(s.stateNumber, targetId, rate, actionId,
												 level);
						// REVERSIBILI: e poi qui si controlla se la reversibilità è stata rispettata (un arco entrante)
						// aggiungere arco all'indietro se l'azione è reversibile (con stesso ID)
						
						Boolean reversible = action_reversibilities.get(k);
						if (reversible) {
							ltsBuilder.addTransition(targetId, s.stateNumber, rate, actionId,
									 level);
						}
						
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
	
}
