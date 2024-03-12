package uk.ac.ed.inf.pepa.largescale;

import java.util.BitSet;

import uk.ac.ed.inf.pepa.largescale.expressions.Expression;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricStateExplorer;
import uk.ac.ed.inf.pepa.largescale.internal.ParametricTransition;

public interface IParametricStructuralElement {
	
	public void init(ParametricStateExplorer parametricStateExplorer);
	
	public ParametricTransition[] getDerivatives();
	
	public Expression getApparentRate(short actionId);
	
	public void setHidingSet(BitSet createBitSet);
	
	public int getOffset();
	
	public int getLength();
	
	public void setOffset(int offset);
	
	public void setLength(int length);
	
}
