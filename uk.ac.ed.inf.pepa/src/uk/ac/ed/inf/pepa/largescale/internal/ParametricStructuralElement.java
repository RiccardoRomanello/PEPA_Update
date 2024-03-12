package uk.ac.ed.inf.pepa.largescale.internal;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.largescale.IParametricStructuralElement;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;

public abstract class ParametricStructuralElement implements IParametricStructuralElement {
	
	/**
	 * Position of this component in the state vector
	 */
	private int offset;

	/**
	 * Number of positions taken up by the component
	 */
	private int length;


	/**
	 * Hiding set
	 */
	protected BitSet hidingSet;
	
	private ParametricStateExplorer parametricStateExplorer;
	
	protected ArrayList<ParametricTransition> derivatives = new ArrayList<ParametricTransition>();
	
	protected HashMap<Short, Expression> apparentRates = new HashMap<Short, Expression>();
	
	public ParametricStructuralElement() {
	}
	
	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	public abstract void compose(short[] state);

	public final ParametricTransition[] getDerivatives() {
		return derivatives.toArray(new ParametricTransition[derivatives.size()]);
	}
	
	public final Expression getApparentRate(short actionId) {
		return apparentRates.get(actionId);
	}

	public void init(ParametricStateExplorer parametricStateExplorer) {
		this.parametricStateExplorer = parametricStateExplorer;
	}
	
	public ParametricStateExplorer getStateExplorer() {
		return this.parametricStateExplorer;
	}

	public final void setHidingSet(BitSet hidingSet) {
		this.hidingSet = hidingSet;
	}

}