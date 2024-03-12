/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Solver;
import uk.ac.ed.inf.pepa.analysis.StaticAnalyser;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.sba.Mapping;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBAParseException;
import uk.ac.ed.inf.pepa.sba.SBAReaction;
import uk.ac.ed.inf.pepa.sba.SBASimulatorException;

/**
 * This interface represents a PEPA Model.
 * <p>
 * It exposes methods for operations such as state space derivation, solution of
 * underlying CTMCs, etc. After each of these is performed notification is sent
 * to <code>IPepaModelChangedListener</code>.
 * <p>
 * If support for a new operation has to be added, this interface has to be
 * changed in order to expose the service, a new type has to be added to the
 * list of event types in <code>PepaModelChangedEvent</code>.
 * 
 * @author mtribast
 * @see ProcessAlgebraModelChangedEvent
 * @see IProcessAlgebraModelChangedListener
 */
public interface IPepaModel extends IProcessAlgebraModel {

	public enum PEPAForm {
		
		PEPA("PEPA"), REAGENT_CENTRIC("Reagent-centric approach (high-low)");
		
		String displayName;
		
		PEPAForm(String name) {displayName = name;}
		
		public String toString() {return displayName;}
	}

	/**
	 * Return the abstract syntax tree for this model, or null if it is not
	 * available yet
	 * 
	 * @return the AST of the model or null if it is not ready yet
	 */
	public ModelNode getAST();
	
	/**
	 * Gets the static analyser, or <code>null</code>.
	 * @return
	 */
	public StaticAnalyser getStaticAnalyser();

	/**
	 * Parse this model. Parser-related errors/warnings are included in the
	 * problems of the parsed Abstract Syntax Tree.
	 * 
	 * @throws CoreException
	 *             if some resource-related problem occurred
	 */
	public void parse() throws CoreException;

	public boolean isParsable();

	/**
	 * Return a graph representation of the Kronecker state space
	 */
	public KroneckerDisplayModel getDisplayModel();
	
	/**
	 * Return a CSL model checker for the model.
	 */
	public PEPAModelChecker getModelChecker(double boundAccuracy);
	
	public boolean sbaParse() throws SBAParseException;
	
	public Set<SBAReaction> getReactions();
	
	public void updateReactions(Set<SBAReaction> updatedReactions);
	
	/**
	 * 
	 * @return
	 * @throws SBAParseException
	 */
	public boolean generateReactions() throws SBAParseException;
	
	/**
	 * 
	 * @param newForm
	 */
	public void setForm(IPepaModel.PEPAForm newForm);
	
	/**
	 * 
	 * @param apparentRate
	 */
	public void setApparentRateUse(boolean apparentRate);
	
	/**
	 * 
	 * @return
	 */
	public Solver[] getValidTimeSeriesSolvers();
	
	/**
	 * 
	 * @return
	 */
	public String getCMDL();
	
	/**
	 * 
	 * @return
	 */
	public String getMatlab();
	
	/**
	 * 
	 * @return
	 */
	public Mapping getMapping();
	
	/**
	 * 
	 * @param options
	 * @param monitor
	 * @throws SBASimulatorException
	 */
	public void generateTimeSeries(OptionsMap options, IProgressMonitor monitor) throws SBASimulatorException;
	
	/**
	 * 
	 * @return
	 */
	public Results getTimeSeries();
	
	/**
	 * 
	 * @return
	 */
	public PEPAForm[] isSBAParseable();
}
