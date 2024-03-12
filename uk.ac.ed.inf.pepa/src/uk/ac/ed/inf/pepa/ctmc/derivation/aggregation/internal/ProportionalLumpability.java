package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Aggregated;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Partition;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.PartitionBlock;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DefaultHashMap;

/**
 * @author Riccardo Romanello
 *
 */
public class ProportionalLumpability<S extends Comparable<S>>
	extends ContextualLumpability<S> {
	
	private HashMap<S, Double> apparentRates;
	
	// build an object that incapsulates LTS so that we can modify it.
	
	public ProportionalLumpability(AggregationAlgorithm.Options options) {
		super(options);
	}
	
	@Override
	public LTS<Aggregated<S>> aggregate(LTS<S> initial) {
		double rate = 0.0d;
		// apparentRates[s] = sum of outgoing rates from state s.
		apparentRates = new HashMap<S, Double>();
		
		for (S source: initial) {
			rate = 0.0d; // sum of all outoging rates for current source
			for (S target: initial) {
				// !target.equals(source)?
				
				for (Short action: initial.getActions(source, target)) {
					rate += initial.getApparentRate(source, target, action);
				}
				
			}
			apparentRates.put(source, rate);
			
		}
		
//		for(S s : apparentRates.keySet()) {
//			for (Short a : apparentRates.get(s).keySet()) {
//				System.out.println("Sum of rates for state: " + s + " with action " + a + " is " + apparentRates.get(s).get(a));
//			}
//		}
		
		return super.aggregateLts(initial, super.findPartition(initial));
	}
	
	public void prepareAggregatedData(LTS<S> initial, Partition<S, PartitionBlock<S>> partition,
			final int numActions, List<Aggregated<S>> aggrLtsStates, HashMap<S, HashMap<S, double[]>> aggrTrans,
			HashMap<PartitionBlock<S>, Aggregated<S>> blocksToAggr) {
		for (Aggregated<S> aggrState: aggrLtsStates) {
			S aggrSRepr = aggrState.getRepresentative();

			for (S state: aggrState) {
				
				for (S target: initial.getImage(state)) {
					PartitionBlock<S> b = partition.getBlockOf(target);
					Aggregated<S> targetAggr = blocksToAggr.get(b);
					S targetRepr = targetAggr.getRepresentative();
					HashMap<S, double[]> trans = aggrTrans.get(aggrSRepr);
					double[] rates = trans.get(targetRepr);
					for (short act: initial.getActions(state, target)) {
						if (rates == null) {
							rates = new double[numActions];
							trans.put(targetRepr, rates);
						}
						
						rates[act] += initial.getApparentRate(state, target, act)/apparentRates.getOrDefault(state, 1.0d);
					}
					
				}
				
			}
			
		}
		
	}
	
	public ArrayList<S> computeWeights(
			final LTS<S> initial,
			DefaultHashMap<S, Double> weights,
			final PartitionBlock<S> splitter,
			final HashMap<S, HashMap<Short, HashSet<S>>> preIm,
			final short act) {
		ArrayList<S> seenStates = new ArrayList<>();
		for (S state : splitter) {
			HashMap<Short, HashSet<S>> trans = preIm.get(state);
			if (trans == null) {
				continue;
			} 
			HashSet<S> tStates = trans.get(act);
			if (tStates == null) {
				continue;
			}
			
			for (S source : tStates) {
				if (!weights.containsKey(source)) {
					seenStates.add(source);
				}
				double w = weights.get(source);
				w += initial.getApparentRate(source, state, act)/apparentRates.getOrDefault(source, 1.0);
				weights.put(source, w);
			}
		}
		return seenStates;
	}
}
