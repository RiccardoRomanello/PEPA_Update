/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.LTSDeriver;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.MeasurementData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DoubleArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IntegerArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.MemoryStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;

/**
 * Computes the aggregated state space.
 * 
 * Note: in order to use this builder you must first create a StateSpaceBuilder
 * and pass the explorer and symbol generator from that builder to this one.
 * @author Giacomo Alzetta
 *
 */
public class AggregationStateSpaceBuilder extends LTSDeriver implements IStateSpaceBuilder {
	private AggregationAlgorithm<Integer> algorithm;

	public AggregationStateSpaceBuilder(
			IStateExplorer explorer,
			ISymbolGenerator generator,
			AggregationAlgorithm<Integer> algorithm) {

		super(explorer, generator);

		this.algorithm = algorithm;
	}

	@Override
	public IStateSpace derive(boolean allowPassiveRates, IProgressMonitor monitor)
			throws DerivationException {
		
		if (monitor == null) 
			monitor = new DoNothingMonitor();
		
		long startTime = System.nanoTime();
		long endTime = 0;

		ArrayList<State> states = new ArrayList<State>();
		LTS<Integer> lts = super.derive(monitor, states);

		startTime = System.nanoTime();
		// Aggregate the LTS here
		LTS<Aggregated<Integer>> aggrLts = algorithm.aggregate(lts);
		endTime = System.nanoTime();
		double aggregationLtsTimeMillis = (endTime-startTime)/1000000;
		System.out.println(String.format("#states %1$d transitions %2$d", aggrLts.numberOfStates(), aggrLts.numberOfTransitions()));

		startTime = System.nanoTime();

		IStateSpace result = createStateSpace(states, aggrLts);
		monitor.done();
		endTime = System.nanoTime();
		double aggrLtsToSSTimeMillis = (endTime-startTime)/1000000;
		
		String msg = "#time aggregate %1$.3f derive %2$.3f (ms)";
		
		System.out.println(
				String.format(msg, aggregationLtsTimeMillis,aggrLtsToSSTimeMillis));
		
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

	@Override
	public MeasurementData getMeasurementData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
