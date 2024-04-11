/*******************************************************************************
 * Copyright (c) 2024 Alberto Casagrande <alberto.casagrande@uniud.it>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.model.ActionLevel;

/**
 * An implementation for the lumpable bisimilarity on high contexts (see Def. 5 [1])
 * 
 * [1] Hillston J., Marin A., Piazza C., Rossi S., Persistent Stochastic Non-Interference
 *     (2021) Fundamenta Informaticae, 181 (1), pp. 1 - 35, DOI: 10.3233/FI-2021-2049
 */
public class HighContextualLumpability<S extends Comparable<S>> extends ContextualLumpability<S> {

	public HighContextualLumpability(AggregationAlgorithm.Options options) {
		super(options);
	}

	@Override
	public LTS<S> getLumpingGraph(LTS<S> lts) {
		LtsModel<S> lgraph = (LtsModel<S>)super.getLumpingGraph(lts);

		for (short actionid=0; actionid<lgraph.numberOfActionTypes(); ++actionid) {
			if (lgraph.getActionLevel(actionid) == ActionLevel.HIGH) {
				for (S state : lgraph.getStates()) {
					double rate = 0.0d;
					for (S target: lgraph.getImage(state)) {
						if (!state.equals(target)) {
							rate -= lts.getApparentRate(state, target, actionid);
						}
					}

					lgraph.addTransition(state, state, rate, actionid, ActionLevel.HIGH);
				}
			}
		}
		
		return lgraph;
	}
}
