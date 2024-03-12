/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.common;

import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationStateSpaceBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.ContextualLumpability;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.ExactEquivalence;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.ProportionalLumpability;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.StrongEquivalence;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.StateExplorerBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.NewParallelBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.hbf.SequentialBuilder;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.KroneckerBuilder;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * Factory for state space builders
 * 
 * @author mtribast
 * 
 */
public class StateSpaceBuilderFactory {
	
	/**
	 * Create the state space builder with the given product identifier
	 * 
	 * @param productId
	 *            product identifier as specified in the public fields of this
	 *            class
	 * @return the requested state space builder, or <code>null</code> if none
	 *         is available
	 */
	public static IStateSpaceBuilder createStateSpaceBuilder(ModelNode model,
			OptionMap map, IResourceManager manager) {

		boolean aggregate = (Boolean) map.get(OptionMap.AGGREGATE_ARRAYS);
		int aggregationAlgorithm = (Integer) map.get(OptionMap.AGGREGATION);
		boolean hasAggregation = (boolean)map.get(OptionMap.AGGREGATION_ENABLED);

		Model cModel = new Compiler(aggregate, model).getModel();

		int kind = (Integer) map.get(OptionMap.DERIVATION_KIND);
		int storage = (Integer) map.get(OptionMap.DERIVATION_STORAGE);
		System.out.println("Storage requested: " + storage);
		int numWorkers = 1;
		if (kind == OptionMap.DERIVATION_PARALLEL) {
			numWorkers = (Integer) map.get(OptionMap.DERIVATION_PARALLEL_NUM_WORKERS);
			if (numWorkers < 1)
				throw new IllegalArgumentException();
		}
		IStateExplorer[] explorers = new IStateExplorer[numWorkers];
		ISymbolGenerator sg = null;
		for (int i = 0; i < numWorkers; i++) {
			StateExplorerBuilder seb = new StateExplorerBuilder(cModel);
			if (i == 0)
				sg = seb.getSymbolGenerator();
			explorers[i] = seb.getExplorer();
		}
		
		// delegates storage to implementors
		if (hasAggregation && aggregationAlgorithm != OptionMap.AGGREGATION_NONE) {
			System.out.println("#aggregation");
			System.out.println("Creating aggregating sequential tool");
			AggregationAlgorithm<Integer> alg;
			AggregationAlgorithm.Options algOptions = new AggregationAlgorithm.Options();
			int partitionType = (Integer) map.get(OptionMap.PARTITION_TYPE);
			if (partitionType == OptionMap.USE_LINKED_PARTITION) {
				algOptions.useArrayBlocks = false;
			}
			
			if (aggregationAlgorithm == OptionMap.AGGREGATION_CONTEXTUAL_LUMPABILITY) {
				System.out.println("#contextual-lumpability");
				alg = new ContextualLumpability<>(algOptions);
			} else if (aggregationAlgorithm == OptionMap.AGGREGATION_EXACT_EQUIVALENCE) {
				System.out.println("#exact-equivalence");
				alg = new ExactEquivalence<>(algOptions);
			} else if (aggregationAlgorithm == OptionMap.AGGREGATION_STRONG_EQUIVALENCE) {
				System.out.println("#strong-equivalence");
				alg = new StrongEquivalence<>(algOptions);
			} else if (aggregationAlgorithm == OptionMap.AGGREGATION_PROPORTIONAL_LUMPABILITY) {
				System.out.println("#proportional-lumpability");
				alg = new ProportionalLumpability<>(algOptions);
			} else {
				System.err.println("Invalid aggregation algorithm");
				throw new IllegalArgumentException();
			}
			
			return new AggregationStateSpaceBuilder(explorers[0], sg, alg);
		} else if (kind == OptionMap.DERIVATION_SEQUENTIAL) {
			System.out.println("#sequential");
			System.out.println("Creating sequential tool");
			return new
			 SequentialBuilder(explorers[0], sg, storage, manager);
		} else if (kind == OptionMap.DERIVATION_PARALLEL) {
			System.out.println("Creating parallel (" + numWorkers + ")");
			return new NewParallelBuilder(
					explorers, sg, storage, manager);
		} else if (kind == OptionMap.DERIVATION_KRONECKER) {
			System.out.println("Creating Kronecker tool");
			return new
			 KroneckerBuilder(explorers[0], sg, model.getSystemEquation(), storage, manager);
		} else {
			throw new IllegalArgumentException();
		}
		
	}

}