/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import uk.ac.ed.inf.pepa.analysis.DeadCode;
import uk.ac.ed.inf.pepa.analysis.IProblem;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.parsing.ASTNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * It is responsible for creating markers on the
 * underlying resource of a PEPA model. It is notified
 * of changes in the state of the model directly by the
 * <code>IPepaModel</code>, i.e. it doesn't listen
 * to event. This is because dead code detection and
 * dynamic transient state detection are not exposed
 * via the API. When the state space is derived and
 * the dynamic analysis is carried out, the model sends
 * a {@link #createStateSpaceMarkers(DeadCode[], String[])}
 * request to the marker manager.
 * <p>
 * This manager keeps the list of markers it adds to the
 * resource.
 * 
 * @author mtribast
 * 
 */
public class MarkerManager {

	private IPepaModel model;

	private List<IMarker> staticAnalysisMarkers = new ArrayList<IMarker>();

	private List<IMarker> dynamicAnalysisMarkers = new ArrayList<IMarker>();

	public MarkerManager(IPepaModel model) {
		this.model = model;
	}

	public void createStaticAnalysisMarkers() {
		final ModelNode ast = model.getAST();
		if (ast == null) {
			throw new NullPointerException("No AST Available!");
		}
		
		// everything is cleared
		clear(staticAnalysisMarkers);
		clear(dynamicAnalysisMarkers);

		/* Wrap in to a runnable to collect notifications all in once */
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker;
				/* create new ones */
				for (IProblem problem : ast.getProblems()) {
					marker = model.getUnderlyingResource().createMarker(
							IMarker.PROBLEM);
					marker.setAttribute(IMarker.LINE_NUMBER, problem.getStartLine());
					marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
					marker.setAttribute(IMarker.CHAR_START, problem.getChar());
					marker.setAttribute(IMarker.CHAR_END, problem.getChar()
							+ problem.getLength());
					marker.setAttribute(IMarker.SEVERITY,
							problem.isError() ? IMarker.SEVERITY_ERROR
									: IMarker.SEVERITY_WARNING);
					marker.setAttribute(IMarker.TRANSIENT, true);
					staticAnalysisMarkers.add(marker);
				}
			}
		};

		try {
			model.getUnderlyingResource().getWorkspace().run(runnable, null);
		} catch (CoreException e) {
			PepaLog.logError(e);
		}

	}

	public void createStateSpaceMarkers(final DeadCode[] deadCode,
			final String[] transientStates) {

		clear(dynamicAnalysisMarkers);

		IWorkspaceRunnable addPostDerivationAnalysisMarkersOperation = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {

				for (DeadCode dc : deadCode) {
					createDeadCodeMarker(dc);
				}

				for (String processName : transientStates)
					createTransientStateMarker(processName);
			}

		};

		try {
			ResourcesPlugin.getWorkspace().run(
					addPostDerivationAnalysisMarkersOperation, null);
		} catch (CoreException e) {
			PepaLog.logError(e);
		}
	}

	private void createTransientStateMarker(String processName)
			throws CoreException {
		ASTNode sensibleNode = model.getAST().getResolver()
				.getProcessDefinition(processName);
		createDynamicAnalysisMarker(sensibleNode, "Transient process. Process: "
				+ processName);
	}

	private void createDeadCodeMarker(DeadCode deadCode) {
		for (Action action : deadCode.actions) {
			String actionName = action.prettyPrint();
			ASTNode[] usage = model.getAST().getResolver().getActionUsage(
					deadCode.process.getName(), actionName);
			for (ASTNode node : usage) {
				try {
					createDeadCodeMarker(node, actionName);
				} catch (CoreException e) {
					PepaLog.logError(e);
				}
			}

		}

	}

	private void createDeadCodeMarker(ASTNode node, String action)
			throws CoreException {
		createDynamicAnalysisMarker(node, "Dead code. Action: " + action);
	}

	private void createDynamicAnalysisMarker(ASTNode node, String message)
			throws CoreException {
		IMarker marker = model.getUnderlyingResource().createMarker(
				IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, node.getLeftLocation()
				.getLine());
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.CHAR_START, node.getLeftLocation()
				.getChar());
		marker
				.setAttribute(IMarker.CHAR_END, node.getRightLocation()
						.getChar());
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		marker.setAttribute(IMarker.TRANSIENT, true);

		dynamicAnalysisMarkers.add(marker);
	}

	private void clear(final List<IMarker> markers) {
		IWorkspaceRunnable clearDynamicAnalysisMarkersOperation = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				for (IMarker marker : markers)
					try {
						marker.delete();
					} catch (CoreException e) {
						PepaLog.logError(e);
					}
				markers.clear();
			}
		};

		try {
			ResourcesPlugin.getWorkspace().run(
					clearDynamicAnalysisMarkersOperation, null);
		} catch (CoreException e1) {
			PepaLog.logError(e1);
		}
	}
}
