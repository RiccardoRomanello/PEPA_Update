/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import uk.ac.ed.inf.pepa.DoNothingMonitor;
import uk.ac.ed.inf.pepa.IResourceManager;
import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Solver;
import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.analysis.StaticAnalyser;
import uk.ac.ed.inf.pepa.ctmc.LTSDeriver;
import uk.ac.ed.inf.pepa.ctmc.PSNI.PSNIVerifier;
import uk.ac.ed.inf.pepa.ctmc.PSNI.PSNIVerifierBuilder;
import uk.ac.ed.inf.pepa.ctmc.derivation.*;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.LTS;
import uk.ac.ed.inf.pepa.ctmc.derivation.aggregation.internal.LtsModel;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.IStateExplorer;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.State;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.StateExplorerBuilder;
import uk.ac.ed.inf.pepa.ctmc.kronecker.IKroneckerStateSpace;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ICSLModelChecker;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.*;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.parsing.EqualityVisitor;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.sba.Mapping;
import uk.ac.ed.inf.pepa.sba.PEPAtoSBA;
import uk.ac.ed.inf.pepa.sba.ReagentCentricPEPAtoSBA;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBAInterface;
import uk.ac.ed.inf.pepa.sba.SBAParseException;
import uk.ac.ed.inf.pepa.sba.SBAReaction;
import uk.ac.ed.inf.pepa.sba.SBASimulatorException;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;
import uk.ac.ed.inf.pepa.tools.PepaTools;


/**
 * Concrete class describing a PEPA Model.
 * <p>
 * It employs a chain-based notification mechanism. Operations on PEPA models
 * are organised hierarchically. At the top is the AST building which any other
 * operation depends to. When a new AST is created, notifications are sent to
 * all its dependent operations, such as state space derivation, CTMC solution,
 * etc., the purpose being to keep the whole state of the PEPA model consistent
 * at any point in time.
 * <p>
 * A save operation in the editor of a PEPA model triggers the parser which
 * notifies a PARSED event and clears the internal state of the state space.
 * This in turn notifies a STATE_SPACE_DERIVED event and clears the state of the
 * solution, and so on.
 * <p>
 * However, it would be very expensive to calculate steady-state pdf when only a
 * new line is added to the model! In fact, notifications don't take place when
 * an operation produces a result which is not semantically different from the
 * previous one.
 * <p>
 * For example, when a new line or a comment is added to the source code of a
 * model, the parsed event is triggered by the notification chain is interrupted
 * if the new AST is semantically equal to the previous one, i.e. they both
 * would produce the same state space.
 * <p>
 * Instead, if the state space needs changing then the value is cleared
 * (i.e. <code>getStateSpace()</code> would return <code>null</code>. If
 * a client called <code>derive()</code>, the new state space would be tested
 * against the old one, no notification being sent if they are semantically
 * equal, i.e. they would produce the same solution.
 * 
 * @author mtribast
 * 
 */

/*
 * TODO Synchronise interface methods, because long-running operations are
 * likely to be working in a multi-threaded environment
 */
public class PepaModel extends ProcessAlgebraModel implements IPepaModel {

	protected ModelNode fAstModel = null;
	
	protected StaticAnalyser fStaticAnalyser = null;

	protected IStateSpace fStateSpace = null;
	
	// Added by msmith
	protected IKroneckerStateSpace fKroneckerStateSpace = null;
	private boolean fKroneckerDerivationProblem = false;

	// Added by AJD
	protected SBAInterface fSBAModel = null;
	protected SBAtoISBJava fReactionModel = null;
	private long sbaParsetime;
	
	protected Results fResults = null; // same pointer as held in SBAtoISBJava
	protected PEPAForm current = null, lastConversion = null;
	protected boolean apparentRate = true;
	protected List<PEPAForm> possibleConversions = null;

	private MarkerManager markerCreator;

	protected Boolean PSNI = null;

	protected Boolean isReversible = null;

	/**
	 * Create a PepaModel from a given resource. The resource has already been
	 * tested for (1) existing and (2) being a file with a supported extension.
	 * 
	 * @param source
	 *            resource containing the model source code
	 */
	public PepaModel(IResource resource) {
		super(resource);
		this.markerCreator = new MarkerManager(this);
	}

	/*
	 * Parse the model, notify listeners for parsed event and set all depended
	 * objects to null
	 */
	public void parse() throws CoreException {
		long tic = System.currentTimeMillis();
		boolean hasChanged = doParse();
		// REVERSIBILI: qui secondo me si può costruire l'LTS al volo e controllare se le azioni reversibili sono state fatte bene
		long toc = System.currentTimeMillis();
		markerCreator.createStaticAnalysisMarkers();
		if (hasChanged) {
			// clear depending fields
			setStateSpace(null, null, 0);
			// SBA integration
			setSBA(0);
			lastConversion = null;
			possibleConversions = null;
			// Clear any current Kronecker representation we might have
			fKroneckerStateSpace = null;
			fKroneckerDerivationProblem = false;
			// notify listeners
			notify(new ProcessAlgebraModelChangedEvent(ProcessAlgebraModelChangedEvent.PARSED, this, toc - tic));
		}
	}

	/**
	 * Parses the PEPA model and returns true if the abstract syntax tree has changed
	 */
	protected boolean doParse() throws CoreException {
		String source = null;
		try {
			source = getStringFromResource(getUnderlyingResource());
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, PepaCore.ID,
					IStatus.OK, "Input/Output problem", e));
		}
		/* set field */
		if (source != null) {
			ModelNode oldAstModel = fAstModel;
			fAstModel = (ModelNode) PepaTools.parse(source);
			fStaticAnalyser = PepaTools.doStaticAnalysis(fAstModel);
			if (oldAstModel == null || fAstModel == null) {
				return true;
			} else {
				return !EqualityVisitor.compare(oldAstModel, fAstModel);
			}
		}
		return true;
	}
	
	private void setKroneckerStateSpace(IProgressMonitor monitor) {
		// Since this gets called when the model is parsed, we don't bother
		// to notify the listeners
		if (!isDerivable() || fKroneckerDerivationProblem) {
			fKroneckerStateSpace = null;
		} else {
			Exception de = null;
			long tic = System.currentTimeMillis();
			OptionMap map = new OptionMap(fOptionHandler.getOptionMap());
			map.put(OptionMap.DERIVATION_KIND, OptionMap.DERIVATION_KRONECKER);
			try {
				fKroneckerStateSpace = (IKroneckerStateSpace) PepaTools.derive(map,
						                                                       fAstModel,
						                                                       (monitor == null) ? null
						                                                       : new PepatoProgressMonitorAdapter(monitor, "Kronecker derivation"),
						                                                       IResourceManager.TEMP);
			} catch (DerivationException e) {
				fKroneckerDerivationProblem = true;
				de = e;
			} catch (ClassCastException e) {
				fKroneckerDerivationProblem = true;
				de = e;
			}
			long elapsed = System.currentTimeMillis() - tic;
			notify(new ProcessAlgebraModelChangedEvent(
					ProcessAlgebraModelChangedEvent.KRONECKER_DERIVED, this, de, elapsed));
		}
	}
	
	public KroneckerDisplayModel getDisplayModel() {
		if (fKroneckerStateSpace == null) setKroneckerStateSpace(null);
		if (fKroneckerStateSpace != null) {
			return fKroneckerStateSpace.getDisplayModel();
		} else {
			return null;
		}
	}
	
	public PEPAModelChecker getModelChecker(double boundAccuracy) {
		if (fKroneckerStateSpace == null) setKroneckerStateSpace(null);
		if (fKroneckerStateSpace != null) {
			ICSLModelChecker modelChecker = fKroneckerStateSpace.getModelChecker(fOptionHandler.getOptionMap(), new PepatoProgressMonitorAdapter(null, "Model Checker Generation"), boundAccuracy);
			return new PEPAModelChecker(this, modelChecker);
		} else {
			return null;
		}
	}
	
	/**
	 * This is a bit of a hack - it allows the model checker to communicate events back to us
	 */
	public void modelCheckingEvent(ProcessAlgebraModelChangedEvent event) {
		notify(event);
	}
	
	public StaticAnalyser getStaticAnalyser() {
		return this.fStaticAnalyser;
	}

	public ModelNode getAST() {
		return fAstModel;
	}

	public void derive(IProgressMonitor monitor) throws DerivationException {
		if (!isDerivable())
			return; // no-effect rule
		DerivationException de = null;
		IStateSpace stateSpace = null;
		// TODO Insert manager
		IStateSpaceBuilder derivator = PepaTools.getBuilder(fAstModel,
				fOptionHandler.getOptionMap(), IResourceManager.TEMP);

		long elapsed = 0;
		try {
			long tic = System.currentTimeMillis();
			stateSpace = derivator.derive(false, (monitor == null) ? null
					: new PepatoProgressMonitorAdapter(monitor,
							"State space derivation"));
			elapsed = System.currentTimeMillis() - tic;

		} catch (DerivationException e) {
			de = e; // pass the exception to the client
		} finally {
			setStateSpace(stateSpace, de, elapsed);
		}
		// rethrow exception
		if (de != null)
			throw de;

		// do dead code detection
		// DeadCode[] deadCode = derivator.getDynamicAnalyser().getDeadCode();

		// ISequentialComponentAnalyser sca = PepaTools
		// .getSequentialComponentAnalyser(derivator.getDynamicAnalyser());

		// String[] ts = sca.analyse();

		// markerCreator.createStateSpaceMarkers(deadCode, ts);

	}

	public void PSNI_verify(IProgressMonitor monitor) throws DerivationException {
		if (!isDerivable())
			return; // no-effect rule

		PSNI = null;
		StringBuilder log = new StringBuilder();

		Exception de = null;
		long tic = System.currentTimeMillis();
		try {
			PSNIVerifier verifier = PSNIVerifierBuilder.createVerifier(fAstModel);

			PSNI = verifier.verify((monitor == null) ? null
								: new PepatoProgressMonitorAdapter(monitor,
								"PSNI verification"), log);
		} catch (DerivationException e) {
			fKroneckerDerivationProblem = true;
			de = e;
		}
		long elapsed = System.currentTimeMillis() - tic;

		notify(new ProcessAlgebraModelChangedEvent(
				ProcessAlgebraModelChangedEvent.PSNI_CHECKED, this, de, elapsed, log.toString()));
	}

	public Boolean isPSNI() {
		return PSNI;
	}

	public void reversible_verify(IProgressMonitor monitor) {
		ModelNode model = fAstModel; 
		
		Model cModel = new Compiler(model).getModel();
		
		StateExplorerBuilder seb = new StateExplorerBuilder(cModel);
		
		ISymbolGenerator sg = seb.getSymbolGenerator();
		IStateExplorer explorer = seb.getExplorer();
		
		LTSDeriver deriver = new LTSDeriver(explorer, sg);
		ArrayList<State> states = new ArrayList<State>();
		
		try {
			LTS<Integer> lts = deriver.derive(new DoNothingMonitor(), states);
			LtsModel<Integer> ltsModel = new LtsModel<Integer>(lts);
			isReversible = ltsModel.isReversible();
		} catch (DerivationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Boolean isReversible() {
		return isReversible;
	}

	public boolean isDerivable() {
		if (fAstModel == null || isAstCarryingErrors(fAstModel))
			return false;
		return true;
	}

	private boolean isAstCarryingErrors(ModelNode ast) {
		for (IProblem problem : ast.getProblems())
			if (problem.isError())
				return true;
		return false;
	}

	/* extract the source code */
	private String getStringFromResource(IResource resource)
			throws CoreException, IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				((IFile) resource).getContents()));
		StringBuffer buf = new StringBuffer();
		char[] cbuf = new char[4096];
		int c;
		while ((c = in.read(cbuf)) != -1) {
			buf.append(cbuf, 0, c);
		}
		return buf.toString();

	}

	public boolean isParsable() {
		return (getUnderlyingResource()!= null && getUnderlyingResource().exists());
	}
	
	private final void setSBA(long duration) {
		sbaParsetime = duration;
		fReactionModel = null;
		setISBJava(0);
	}
	
	private final void setISBJava(long duration) {
		ProcessAlgebraModelChangedEvent pmce = null;
		if(duration == 0)
			pmce = new ProcessAlgebraModelChangedEvent(ProcessAlgebraModelChangedEvent.REACTIONS_GENERATED,this, duration);
		else
			pmce = new ProcessAlgebraModelChangedEvent(ProcessAlgebraModelChangedEvent.REACTIONS_GENERATED,this, sbaParsetime + duration);
		notify(pmce);
		fResults = null;
		setTimeSeries(0);
	}
	
	private final void setTimeSeries(long duration) {
		notify(new ProcessAlgebraModelChangedEvent(ProcessAlgebraModelChangedEvent.TIME_SERIES_ANALYSED,this, duration));
	}
	
	public PEPAForm[] isSBAParseable() {
		if(fReactionModel != null)
			return possibleConversions.toArray(new PEPAForm[]{});
		possibleConversions = new ArrayList<PEPAForm>();
		if((new PEPAtoSBA(fAstModel)).isParseable())
			possibleConversions.add(PEPAForm.PEPA);
		if((new ReagentCentricPEPAtoSBA(fAstModel)).isParseable())
			possibleConversions.add(PEPAForm.REAGENT_CENTRIC);
		return possibleConversions.toArray(new PEPAForm[]{});
	}
	
	public boolean sbaParse() throws SBAParseException {
		if(possibleConversions == null)
			isSBAParseable();
		if (possibleConversions.size() == 0 || !possibleConversions.contains(current)) {
			fSBAModel = null;
			fReactionModel = null;
			fResults = null;
			throw new SBAParseException("Model not parseable for " + current.toString());
		}
		if(fSBAModel != null && current.equals(lastConversion))
			return false; // no change in model
		long tic = System.currentTimeMillis();
		try {
			fReactionModel = null;
			if(current.equals(PEPAForm.PEPA)) {
				PEPAtoSBA pts = new PEPAtoSBA(fAstModel);
				pts.parseModel();
				fSBAModel = pts;
				apparentRate = true;
			} else if(current.equals(PEPAForm.REAGENT_CENTRIC)) {
				ReagentCentricPEPAtoSBA pts = new ReagentCentricPEPAtoSBA(fAstModel);
				pts.parseModel();
				fSBAModel = pts;
				apparentRate = false;
			}
		} catch(Exception e) {
			throw new SBAParseException(e.getMessage());
		} finally {
			setSBA(System.currentTimeMillis() - tic);
		}
		return true;
	}
	
	public Set<SBAReaction> getReactions() {
		return fSBAModel.getReactions();
	}
	
	public void updateReactions(Set<SBAReaction> updatedReactions) throws IllegalArgumentException{
		fSBAModel.updateReactions(updatedReactions);
	}
	
	public boolean generateReactions() throws SBAParseException {
		if(fSBAModel == null)
			throw new SBAParseException("Model has not been parsed.");
		long tic = System.currentTimeMillis();
		try {
			fReactionModel = null;
			fReactionModel = new SBAtoISBJava(fSBAModel);
			fReactionModel.generateISBJavaModel("", apparentRate);
			lastConversion = current;
		} catch(Exception e) {
			throw new SBAParseException(e.getMessage());
		} finally {
			setISBJava(System.currentTimeMillis() - tic);
		}
		return true;
	}
	
	public void setForm(IPepaModel.PEPAForm newForm) {
		current = newForm;
	}
	
	public void setApparentRateUse(boolean apparentRate) {
		this.apparentRate = apparentRate;
	}
	
	public void generateTimeSeries(OptionsMap options, IProgressMonitor monitor) throws SBASimulatorException{
		if(fReactionModel == null)
			throw new SBASimulatorException("", null);
		long tic = System.currentTimeMillis();
		try { // not complete... maybe it is now...
			fReactionModel.initialiseSimulator(options);
			fResults = null;
			fResults = fReactionModel.runModel((monitor == null ? null : new PepatoProgressMonitorAdapter(monitor,"Time Series Analysis")));
		} catch(SBASimulatorException e) {
			throw e;
		} catch(Exception e) {
			throw new SBASimulatorException(e.getMessage(), e);
		} finally {
			setTimeSeries(System.currentTimeMillis() - tic);
		}
	}
	
	public Results getTimeSeries() {
		return fResults;
	}
	
	public Solver[] getValidTimeSeriesSolvers() {
		if(fReactionModel == null)
			return new Solver[0];
		else
			return fReactionModel.getPermissibleSolvers();
	}
	
	public String getCMDL() {
		if(fReactionModel == null)
			return null;
		return fReactionModel.writeCMDL();
	}
	
	public String getMatlab() {
		if(fReactionModel == null)
			return null;
		return fReactionModel.writeMatlab();
	}
	
	public Mapping getMapping() {
		if(fReactionModel == null)
			return null;
		else
			return fReactionModel.getMapping();
	}

}
