/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.AggregationAlgorithm;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;

/**
 * @author Giacomo Alzetta
 */
public class StrongEquivalence<S extends Comparable<S>> extends ContextualLumpability<S> {

	public StrongEquivalence(AggregationAlgorithm.Options options) {
		super(options);
	}

	@Override
	public LTS<S> getLumpingGraph(LTS<S> initial) {
		return initial;
	}
}
