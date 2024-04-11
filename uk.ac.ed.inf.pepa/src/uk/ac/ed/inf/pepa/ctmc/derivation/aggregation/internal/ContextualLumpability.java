/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Aggregated;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTSBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Partition;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.PartitionBlock;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.StateIsMarkedException;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.StateNotFoundException;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.CommonDefaulters;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.DefaultHashMap;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.model.ActionLevel;


/**
 * @author Giacomo Alzetta
 *
 */
public class ContextualLumpability<S extends Comparable<S>>
	implements AggregationAlgorithm<S> {
	protected AggregationAlgorithm.Options options;
	
	public ContextualLumpability(AggregationAlgorithm.Options options) {
		this.options = options;
	}

	/**
	 * @param initial
	 * @return
	 */
	@Override
	public Partition<S, PartitionBlock<S>> findPartition(LTS<S> initial) {
		Partition<S, PartitionBlock<S>> partition = initialPartition(initial);
		LinkedList<PartitionBlock<S>> splitters = new LinkedList<>(partition.getBlocks());
		LinkedList<PartitionBlock<S>> touchedBlocks = new LinkedList<>();
		DefaultHashMap<S, Double> weights = new DefaultHashMap<>(
				new CommonDefaulters.Basic<Double>(0.0d)
		);
		
		LTS<S> lumpingGraph = getLumpingGraph(initial);

		while (!splitters.isEmpty()) {
			PartitionBlock<S> splitter = splitters.pollFirst();
			splitter.usingAsSplitter();
			
			
			HashMap<S, HashMap<Short, HashSet<S>>> preIm = new HashMap<>();
			
			//HashMap<S, HashMap<Short, HashSet<S>>> preIm = new HashMap<>();
			HashSet<Short> allActions = computeAllPreimages(lumpingGraph, splitter, preIm);
			
			for (short act: allActions) {
				ArrayList<S> seenStates = computeWeights(lumpingGraph, weights, splitter, preIm, act);
				markVisitedStates(partition, touchedBlocks, seenStates);
				
				for (PartitionBlock<S> b: touchedBlocks) {
					assert !b.isEmpty();
				}
				
				while (!touchedBlocks.isEmpty()) {
					PartitionBlock<S> block = touchedBlocks.pollFirst();
					performSplitting(partition, splitters, weights, block);
				}
				
				weights.clear();
			}
		}
		
		return partition;
	}

	/**
	 * Get the lumping graph of a PEPA LTS
	 *
	 * Depending on the relation between LTS states, the partition algorithm
	 * must distinguish the actions according to their names. For instance,
	 * the lumpable bisimulation (i.e., the contextual lumpability [1])
	 * handles the tau actions differently from the other actions.
	 * In some cases, the action processing can be standardized and the
	 * algorithm can be simplified by dealing with a variant of the
	 * original LTS, named lumping graph, which is relation specific.
	 * In the case of the lumpable bisimulation, the lumping graph of an
	 * LTS contains all its states and transitions, but it it introduces
	 * self-loops for every state with label tau (see Def. 6.4 in [1]).
	 * The rate of such self-loops is equal to *minus* the sum of all outgoing
	 * tau transitions from the state.
	 *
	 * [1] Alzetta G., Marin A., Piazza C., Rossi S., Lumping-based
	 *     equivalences in Markovian automata: Algorithms and applications
	 *     to product-form analyses, (2018) Information and Computation, 260,
	 *     pp. 99 - 125, DOI: 10.1016/j.ic.2018.04.002.
	 *
	 * @param lts is a PEPA LTS
	 * @return The lumping graph of lts
	 */
	public LTS<S> getLumpingGraph(LTS<S> lts) {
		LtsModel<S> lgraph = new LtsModel<S>(lts);

		for (S state : lgraph.getStates()) {
			double rate = 0.0d;
			for (S target: lgraph.getImage(state)) {
				if (!state.equals(target)) {
					rate -= lts.getApparentRate(state, target, ISymbolGenerator.TAU_ACTION);
				}
			}

			ActionLevel level = lgraph.getActionLevel(ISymbolGenerator.TAU_ACTION);
			lgraph.addTransition(state, state, rate, ISymbolGenerator.TAU_ACTION, level);
		}

		return lgraph;
	}
	

	/**
	 * Given a partition and a LTS computes the aggregated LTS corresponding
	 * to the given partition.
	 * 
	 * @param initial
	 * @param partition
	 * @return
	 */
	@Override
	public LTS<Aggregated<S>> aggregateLts(
			LTS<S> initial,
			Partition<S, PartitionBlock<S>> partition) {
		
		// TODO: move this as default implementation in the interface.
		
		final int numActions = initial.numberOfActionTypes();
		List<Aggregated<S>> aggrLtsStates = new ArrayList<>(partition.size());
		HashMap<S, HashMap<S, double[]>> aggrTrans = new HashMap<S, HashMap<S, double[]>>();
		HashMap<PartitionBlock<S>, Aggregated<S>> blocksToAggr = new HashMap<PartitionBlock<S>, Aggregated<S>>(partition.size());
		ArrayList<ActionLevel> action_level = new ArrayList<ActionLevel>();
		
		for (PartitionBlock<S> block: partition.getBlocks()) {
			Aggregated<S> aggrState = new Aggregated<>(block);
			aggrLtsStates.add(aggrState);
			aggrTrans.put(aggrState.getRepresentative(), new HashMap<S, double[]>());
			blocksToAggr.put(block, aggrState);
		}
		
		prepareAggregatedData(initial, partition, numActions, aggrLtsStates, aggrTrans, blocksToAggr, action_level);
		 
		return makeAggregatedLts(partition, numActions, aggrLtsStates, aggrTrans, blocksToAggr, action_level);
	}

	@Override
	public LTS<Aggregated<S>> aggregate(LTS<S> initial) {
		return aggregateLts(initial, findPartition(initial));
	}

	/**
	 * @param partition
	 * @param numActions
	 * @param aggrLtsStates
	 * @param aggrTrans
	 * @param blocksToAggr
	 * @return
	 */
	private LTS<Aggregated<S>> makeAggregatedLts(Partition<S, PartitionBlock<S>> partition,
			final int numActions, List<Aggregated<S>> aggrLtsStates, HashMap<S, HashMap<S, double[]>> aggrTrans,
			HashMap<PartitionBlock<S>, Aggregated<S>> blocksToAggr, ArrayList<ActionLevel> action_level) {
		LTSBuilder<Aggregated<S>> aggrLts = new LtsModel<>(numActions);

		
		for (Aggregated<S> aggrS: aggrLtsStates) {
			aggrLts.addState(aggrS);
		}
		
		for (S source: aggrTrans.keySet()) {
			HashMap<S, double[]> sourceImage = aggrTrans.get(source);
			for (S target: sourceImage.keySet()) {
				double[] targetMap = sourceImage.get(target);
				short act = 0;
				for (double value: targetMap) {
					if (value != 0.0d) {
						aggrLts.addTransition(
								blocksToAggr.get(partition.getBlockOf(source)),
								blocksToAggr.get(partition.getBlockOf(target)),
								value, act, action_level.get(act));
					}
					++act;
				}
			}
		}
		return aggrLts.getLts();
	}


	/**
	 * @param initial
	 * @param partition
	 * @param numActions
	 * @param aggrLtsStates
	 * @param aggrTrans
	 * @param blocksToAggr
	 * @param action_level
	 */
	private void prepareAggregatedData(LTS<S> initial, Partition<S, PartitionBlock<S>> partition,
			final int numActions, List<Aggregated<S>> aggrLtsStates, HashMap<S, HashMap<S, double[]>> aggrTrans,
			HashMap<PartitionBlock<S>, Aggregated<S>> blocksToAggr, ArrayList<ActionLevel> action_level) {
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
						
						while (act >= action_level.size()) {
							action_level.add(ActionLevel.UNDEFINED);
						}
						
						action_level.set(act, initial.getActionLevel(act));
						
						rates[act] += initial.getApparentRate(state, target, act);
					}
				}
			}
		}
	}

	/**
	 * Split a block into its sub-blocks based on the marked states
	 * and the weights provided.
	 * 
	 * @param partition
	 * @param splitters
	 * @param weights
	 * @param block
	 */
	private void performSplitting(
			Partition<S, PartitionBlock<S>> partition,
			LinkedList<PartitionBlock<S>> splitters,
			DefaultHashMap<S, Double> weights,
			PartitionBlock<S> block) {
		
		PartitionBlock<S> markedBlock = block.splitMarkedStates();
		if (block.isEmpty()) {
			markedBlock = markedBlock.shareIdentity(block);
		}
		
		assert !markedBlock.isEmpty() && !block.isEmpty();
		
		List<Double> allWeights = new ArrayList<>(markedBlock.size());
		for (S s: markedBlock) {
			allWeights.add(weights.get(s));
		}
		
		for (S s: markedBlock) {
			try {
				markedBlock.setValue(s, weights.get(s));
			} catch (StateIsMarkedException e) {
				e.printStackTrace();
			} catch (StateNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		Double pmc = PartitioningUtils.pmc(allWeights);
		PartitionBlock<S> nonPmcBlock = markedBlock.splitBlockOnValue(pmc);
		Collection<PartitionBlock<S>> subBlocks = nonPmcBlock.isEmpty()
				? new ArrayList<PartitionBlock<S>>()
				: nonPmcBlock.splitBlock();
		/*
		 * This line can replace the previous three statements and remove the pmc
		 * optimization:
		 *
		 * Collection<PartitionBlock<S>> subBlocks = markedBlock.splitBlock();
		 * 
		 * The algorithm is still correct but should take more time,
		 * as in O(nlog^2 n) vs O(nlog n).
		 */
		ArrayList<PartitionBlock<S>> interestingBlocks = new ArrayList<PartitionBlock<S>>(2 + subBlocks.size());
		
		interestingBlocks.addAll(subBlocks);
		if (block == markedBlock) {
			partition.updateWithSplit(interestingBlocks);
			interestingBlocks.add(markedBlock);
		} else {
			interestingBlocks.add(markedBlock);
			partition.updateWithSplit(interestingBlocks);
		}
		
		if (block.wasUsedAsSplitter()) {
			// In this case it is safe to avoid using one
			// of the subblocks as a splitter.
			// we then remove the biggest one.
			if (block != markedBlock) {
				interestingBlocks.add(block);
			}

			PartitionBlock<S> fatBlock = Collections.max(
					interestingBlocks,
					new Comparator<PartitionBlock<S>>() {

						@Override
						public int compare(PartitionBlock<S> o1, PartitionBlock<S> o2) {
							return o1.size() - o2.size();
						}
						
					}
			);
			interestingBlocks.remove(fatBlock);
		} else if (markedBlock == block) {
			// Avoid inserting the same block multiple times in the splitting queue.
			interestingBlocks.remove(block);
		}
		
		for (PartitionBlock<S> b: interestingBlocks) {
			b.toBeUsedAsSplitter();
		}
		splitters.addAll(interestingBlocks);
	}

	/**
	 * Marks all visited states.
	 * 
	 * @param partition
	 * @param touchedBlocks
	 * @param seenStates
	 */
	private void markVisitedStates(
			Partition<S, PartitionBlock<S>> partition,
			LinkedList<PartitionBlock<S>> touchedBlocks,
			final ArrayList<S> seenStates) {
		for (S state: seenStates) {
			// FIXME: only if it has a weight != 0...
			PartitionBlock<S> block = partition.getBlockOf(state);
			if (!block.hasMarkedStates()) {
				touchedBlocks.add(block);
			}
			
			try {
				block.markState(state);
			} catch (StateNotFoundException e) {
				// This should never happen.
				e.printStackTrace();
			}
		}
	}

	/**
	 * Compute the weights of the transitions with action act that reach
	 * the splitter.
	 * 
	 * @param initial
	 * @param weights The map of weights that will be updated by the method.
	 * @param splitter The splitter block.
	 * @param preIm  The map that stores the preimage of splitter.
	 * @param act
	 * @return The states that have transitions with action act to the splitter.
	 */
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
				w += initial.getApparentRate(source, state, act);
				weights.put(source, w);
			}
		}
		return seenStates;
	}

	/**
	 * Computes the preimages of a splitter block and returns the
	 * set of actions seen in the preimages.
	 * 
	 * @param lts
	 * @param splitter The block of the LTS currently considered as a splitter.
	 * @param preIm The map that will be updated with the preimage of splitter.
	 * @return The set of actions such that have transitions to the splitter.
	 */
	private HashSet<Short> computeAllPreimages(
			final LTS<S> lts,
			final PartitionBlock<S> splitter,
			HashMap<S, HashMap<Short, HashSet<S>>> preIm) {
		HashSet<Short> allActions = new HashSet<>();
		
		for (S state: splitter) {
			for (S source: lts.getPreImage(state)) {
				for (short act: lts.getActions(source, state)) {
					allActions.add(act);

					HashMap<Short, HashSet<S>> trans = preIm.get(state);
					if (trans == null) {
						trans = new HashMap<>();
						preIm.put(state, trans);
					}
					HashSet<S> tStates = trans.get(act);
					if (tStates == null) {
						tStates = new HashSet<>();
						trans.put(act, tStates);
					}
					
					tStates.add(source);
				}
			}
		}
		return allActions;
	}

	/**
	 * The initial partition for contextual lumpability is just a single
	 * block containing every state of the LTS.
	 * 
	 * @param initial The LTS to aggregate.
	 * @return A singleton partition containing all states.
	 */
	public Partition<S, PartitionBlock<S>> initialPartition(LTS<S> initial) {
		PartitionBlock<S> p;
		if (options.useArrayBlocks) {
			p = new ArrayPartitionBlock<>();
		} else {
			p = new LinkedPartitionBlock<>();
		}
		
		Partition<S, PartitionBlock<S>> partition = new Partition<>();
		
		for (S state: initial) {
			p.addState(state);
		}
		partition.addBlock(p);
		
		return partition;
	}
}
