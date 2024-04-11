/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Aggregated;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Partition;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.PartitionBlock;

/**
 * Computes the exact equivalence relation over an LTS.
 * 
 * @author Giacomo Alzetta
 *
 */
public class ExactEquivalence<S extends Comparable<S>>
	extends ContextualLumpability<S> {
	
	public ExactEquivalence(AggregationAlgorithm.Options options) {
		super(options);
	}

	@Override
	public LTS<Aggregated<S>> aggregate(LTS<S> initial) {
		LTS<S> lts = new ReverseLtsModel<>(initial);
		Partition<S, PartitionBlock<S>> partition = super.findPartition(initial);
		// I believe that this should just work.
		return super.aggregateLts(lts, partition);
	}
	
	/**
	 * The initial partition for ExactEquivalenceis a partition such that
	 * all states in each block have equal outgoing apparent rates for
	 * every action type.
	 */
	@Override
	public Partition<S, PartitionBlock<S>> initialPartition(LTS<S> initial) {
		HashMap<S, Double> apparentRates = new HashMap<>(initial.numberOfStates());
		for (S source: initial) {
			double rate = 0.0d;
			for (S target: initial) {
				// !target.equals(source)?
				
				for (Short action: initial.getActions(source, target)) {
					rate += initial.getApparentRate(source, target, action);
				}
			}
			
			apparentRates.put(source, rate);
		}
		
		ArrayList<Map.Entry<S, Double>> entries = new ArrayList<>(apparentRates.entrySet());
		entries.sort(new Comparator<Map.Entry<S, Double>>() {

			@Override
			public int compare(Entry<S, Double> o1, Entry<S, Double> o2) {
				double res = o1.getValue() - o2.getValue();
				if (res == 0.0d) {
					return 0;
				} else {
					return res < 0 ? -1 : 1;
				}
			}
			
		});
		
		Partition<S, PartitionBlock<S>> partition = new Partition<>();
		Double curVal = null;
		PartitionBlock<S> curBlock = null;
		for (Map.Entry<S, Double> entry : entries) {
			S state = entry.getKey();
			Double val = entry.getValue();
			if (curVal == null) {
				curVal = val;
				if (options.useArrayBlocks) {
					curBlock = new ArrayPartitionBlock<>();
				} else {
					curBlock = new LinkedPartitionBlock<>();
				}
				curBlock.addState(state);
			} else if (curVal.equals(val)){
				curBlock.addState(state);
			} else {
				partition.addBlock(curBlock);
				curVal = val;
				if (options.useArrayBlocks) {
					curBlock = new ArrayPartitionBlock<>();
				} else {
					curBlock = new LinkedPartitionBlock<>();
				}
				curBlock.addState(state);
			}
		}
		
		if (curBlock != null) {
			partition.addBlock(curBlock);
		}
		return partition;
	}
	
	@Override
	public LTS<S> getLumpingGraph(LTS<S> initial) {
		return initial;
	}
 }
