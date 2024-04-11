/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverException;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.eclipse.core.internal.OptionHandler;

public abstract class ProcessAlgebraModel implements IProcessAlgebraModel {

	protected boolean isSolved = false;

	private IResource resource = null;

	protected IStateSpace fStateSpace = null;

	protected OptionHandler fOptionHandler;

	private List<IProcessAlgebraModelChangedListener> fListeners = null;

	public ProcessAlgebraModel(IResource resource) {
		this.resource = resource;
		fOptionHandler = new OptionHandler(resource);
		fListeners = new ArrayList<IProcessAlgebraModelChangedListener>();

	}

	public void addModelChangedListener(
			IProcessAlgebraModelChangedListener listener) {
		if (listener == null || fListeners.contains(listener))
			return;
		fListeners.add(listener);
	}
	
	public void dispose() {
		
	}

	public abstract void derive(IProgressMonitor monitor)
			throws DerivationException;

	public abstract void PSNI_verify(IProgressMonitor monitor)
			throws DerivationException;

	public IStateSpace getStateSpace() {
		return fStateSpace;
	}

	public IResource getUnderlyingResource() {
		return resource;
	}

	public abstract boolean isDerivable();

	public boolean isSolvable() {
		return (fStateSpace != null);
	}

	public boolean isSolved() {
		return this.isSolved;
	}

	public void removeModelChangedListener(
			IProcessAlgebraModelChangedListener listener) {
		fListeners.remove(listener);
	}

	public void setSolution(double[] solution) throws DerivationException {
		if (isSolvable())
			if (solution != null)
				if (fStateSpace.size() == solution.length) {
					fStateSpace.setSolution(solution);
					setSolution(0, null);
					return;
				}
		throw new IllegalStateException();
	}

	public void solveCTMCSteadyState(IProgressMonitor monitor)
			throws SolverException {
		if (!isSolvable())
			return; // no-effect rule
			// double[] solution = null;
		SolverException e = null;
		long elapsed = 0;
		try {
			ISolver solver = SolverFactory.createSolver(fStateSpace,
					fOptionHandler.getOptionMap());
			long tic = System.currentTimeMillis();
			double[] solution = solver.solve((monitor == null) ? null
					: new PepatoProgressMonitorAdapter(monitor,
							"Steady state pdf solution"));
			elapsed = System.currentTimeMillis() - tic;
			fStateSpace.setSolution(solution);
		} catch (SolverException se) {
			e = se;
		} finally {
			// do not interrupt chain
			setSolution(elapsed, e); // start the notification chain
		}
		// rethrow the exception to client
		if (e != null)
			throw e;
	}

	public OptionMap getOptionMap() {
		return fOptionHandler.getOptionMap();
	}

	public void setOptionMap(OptionMap map) {
		fOptionHandler.setOptionMap(map);

	}

	public Object getOption(String key) {
		return fOptionHandler.getOption(key);
	}

	public IResource getResource() {
		return fOptionHandler.getResource();
	}

	/* Notifies all registered listeners */
	protected void notify(ProcessAlgebraModelChangedEvent event) {
		for (IProcessAlgebraModelChangedListener listener : fListeners)
			listener.processAlgebraModelChanged(event);
	}

	protected void setSolution(long elapsed, SolverException e) {
		/* set field */
		if (this.fStateSpace == null || !fStateSpace.isSolutionAvailable())
			this.isSolved = false;
		else
			this.isSolved = true;
		/* notify listeners */
		notify(new ProcessAlgebraModelChangedEvent(
				ProcessAlgebraModelChangedEvent.CTMC_SOLVED, this, e, elapsed));
	}
	
	protected void setStateSpace(IStateSpace stateSpace,
			DerivationException exception, long elapsed) {
		/* set field */
		this.fStateSpace = stateSpace;
		/* clear depending fields */
		setSolution(0, null);
		/* notify listeneners */
		notify(new ProcessAlgebraModelChangedEvent(
				ProcessAlgebraModelChangedEvent.STATE_SPACE_DERIVED, this, exception, elapsed));
	}
}
