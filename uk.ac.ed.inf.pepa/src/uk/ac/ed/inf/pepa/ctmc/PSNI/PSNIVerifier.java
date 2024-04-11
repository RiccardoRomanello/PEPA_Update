/*******************************************************************************
 * Copyright (c) 2024 Alberto Casagrande <alberto.casagrande@uniud.it>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.PSNI;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.LTSDeriver;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.Partition;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.PartitionBlock;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.HighContextualLumpability;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.LtsModel;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * Computes the aggregated state space.
 * 
 *
 */
public class PSNIVerifier {
	
	private final ISymbolGenerator generator;
	private IStateExplorer explorer;

	public PSNIVerifier(
			IStateExplorer explorer,
			ISymbolGenerator generator) {
		this.explorer = explorer;
		this.generator = generator;
	}

	private static LTS<Integer> buildLowCompleteLTS(LTS<Integer> lts)
	{
		LtsModel<Integer> lowComplete = new LtsModel<Integer>(lts);

		int num_of_states = lts.numberOfStates();

		for (Integer state : lts.getStates()) {
			lowComplete.addState(state + num_of_states);
		}
		
		for (Integer state : lts.getStates()) {
			Integer high_state = state + num_of_states;
			
			for (Integer target : lts.getImage(state)) {
				Integer high_target = target + num_of_states;
				
				for (short actionid : lts.getActions(state, target)) {
					double rate = lts.getApparentRate(state, target, actionid);
					ActionLevel action_level = lts.getActionLevel(actionid);

					if (rate>0 && action_level != ActionLevel.HIGH) {
						lowComplete.addTransition(high_state, high_target, rate, actionid, action_level);
					}
				}
			}
		}
		
		return lowComplete;
	}

	public boolean verify(IProgressMonitor monitor, StringBuilder log)
			throws DerivationException {

		if (monitor == null) 
			monitor = new DoNothingMonitor();

		LTSDeriver deriver = new LTSDeriver(explorer, generator);

		ArrayList<State> states = new ArrayList<State>();

		LTS<Integer> lts = deriver.derive(monitor, states);

		if (log != null) {
			log.append("LTS States:");
			int i = 0;
			for (State state : states) {
				log.append("\n " + (i++) + ": " + state);
			}

			log.append("\n\nLTS:\n" + lts.toString().replace("LTS:\n",""));
		}

		LTS<Integer> lowCompleteLTS = buildLowCompleteLTS(lts);

		if (log != null) {
			log.append("\n\nLTS + LTS\\H:\n" + lowCompleteLTS.toString().replace("LTS:\n",""));
		}

		AggregationAlgorithm.Options options = new AggregationAlgorithm.Options();
		HighContextualLumpability<Integer> lumpability = new HighContextualLumpability<Integer>(options);

		Partition<Integer, PartitionBlock<Integer>> partition = lumpability.findPartition(lowCompleteLTS);

		boolean result = partition.getBlockOf(0) == partition.getBlockOf(lts.numberOfStates());
		if (log != null) {
			log.append("\n\nHigh lumpability bisimulation of LTS + LTS\\H:\n " + partition);
			log.append("\n\nThe process " + (result ? "satisfies" : "does not satisfy") + " PSNI\n");
		}

		monitor.done();

		return result;
	}

	public boolean verify(IProgressMonitor monitor)
			throws DerivationException {
		return verify(monitor, null);
	}
}
