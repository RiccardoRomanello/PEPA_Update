package uk.ac.ed.inf.pepa.largescale;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;

public interface IParametricDerivationGraph {
	
	public IParametricTransitionTriple[] getTransitionTriples();
	
	public IGeneratingFunction[] getGeneratingFunctions();
	
	public ISymbolGenerator getSymbolGenerator();
	
	/** The initial state, in the numerical vector format */
	double[] getInitialState();
	
	/** Process id corresponding to each coordinate of the
	 * numerical vector form
	 */
	public short[] getProcessMappings();
	
	/** Returns the array of sequential components as found
	 * by visiting the cooperation tree in depth-first order
	 * @return
	 */
	public ISequentialComponent[] getSequentialComponents();
}
