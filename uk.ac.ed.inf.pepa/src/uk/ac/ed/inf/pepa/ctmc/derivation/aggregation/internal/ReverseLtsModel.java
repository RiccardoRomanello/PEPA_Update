/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;

/**
 * @author Giacomo Alzetta
 *
 */
public class ReverseLtsModel<S> implements LTS<S> {

	
	LTS<S> lts;
	
	public ReverseLtsModel(LTS<S> lts) {
		this.lts = lts;
	}
	
	// Check if it is correct!
	@Override
	public double getApparentRate(S source, S target, short actionId) {
		return lts.getApparentRate(target, source, actionId);
	}
	
	@Override
	public Iterable<S> getImage(S source) {
		return lts.getPreImage(source);
	}
	
	@Override
	public Iterable<S> getPreImage(S target) {
		return lts.getImage(target);
	}
	
	@Override
	public Iterable<Short> getActions(S source, S target) {
		return lts.getActions(target, source);
	}

	@Override
	public Iterator<S> iterator() {
		return lts.iterator();
	}

	@Override
	public int numberOfStates() {
		return lts.numberOfStates();
	}

	@Override
	public int numberOfTransitions() {
		return lts.numberOfTransitions();
	}

	@Override
	public int numberOfActionTypes() {
		return lts.numberOfActionTypes();
	}

	@Override
	public LTS<S> variantView() {
		return new VariantView<>(this);
	}
}
