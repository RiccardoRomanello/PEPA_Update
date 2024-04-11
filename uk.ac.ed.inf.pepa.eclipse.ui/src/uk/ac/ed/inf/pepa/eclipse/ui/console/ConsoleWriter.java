/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.console;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.*;

import uk.ac.ed.inf.common.ui.plotview.views.PlotView;
import uk.ac.ed.inf.pepa.eclipse.core.*;
import uk.ac.ed.inf.pepa.eclipse.ui.PerspectiveFactory;
import uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer.ProcessAlgebraModelPage;

public class ConsoleWriter implements IProcessAlgebraModelChangedListener,
		IPepaConsoleWriter {

	private static final String MODEL_ADDED = "Model added.";

	private static final String MODEL_SOLVED = "Model solved.";

	private static final String MODEL_DERIVED = "Model derived.";

	private static final String MODEL_PARSED = "Model parsed.";
	
	private static final String MODEL_ANALYSED = "Model analysed.";
	
	private static final String SHOW_STATE_SPACE = "Click to show state space";
	
	private static final String SHOW_RESULTS = "Click to show results";
	
	private static final String STATE_SPACE_VIEW_ID = "uk.ac.ed.inf.pepa.eclipse.ui.stateSpaceView";

	private static final String KRONECKER_DERIVED = "Kronecker state space derived.";
	
	private static final String MODEL_CHECKING_INFO = "<Model Checker> ";
	
	private static final String PSNI_CHECKED = "PSNI checked. ";

	private MessageConsoleStream stream;
	
	private IDocument console;
	
	private final IHyperlink showStateSpace, showGraphs;
	
	private ConsoleLinker listener;

	private IPepaModel pepaModel;

	private String modelName;

	private DateFormat dateFormat;

	public ConsoleWriter(MessageConsoleStream stream, IPepaModel model) {
		this.stream = stream;
		console = stream.getConsole().getDocument();
		// AJD IHyperlink and Listener for user notification
		showStateSpace = new IHyperlink() {
			public void linkActivated() {ProcessAlgebraModelPage.revealStateSpace();}
			public void linkEntered() {}
			public void linkExited() {}
		};
		showGraphs = new IHyperlink() {
			public void linkActivated() {
				try {
					IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					dw.getActivePage().showView(PlotView.ID);
				} catch(Exception e) {}
			}
			public void linkEntered() {}
			public void linkExited() {}
		};
		listener = new ConsoleLinker();
		console.addDocumentListener(listener);
		pepaModel = model;
		modelName = pepaModel.getUnderlyingResource().getName();
		dateFormat = DateFormat.getTimeInstance();
		append(MODEL_ADDED);
	}

	public void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event) {
		switch (event.getType()) {
		case ProcessAlgebraModelChangedEvent.PARSED:
			if (((IPepaModel) event.getProcessAlgebraModel()).getAST() != null)
				notify(MODEL_PARSED);
			break;
		case ProcessAlgebraModelChangedEvent.STATE_SPACE_DERIVED:
			if (event.getProcessAlgebraModel().getStateSpace() != null
					&& event.getException() == null) {
				notify(MODEL_DERIVED + " Elapsed time: " + event.getElapsedTimeMillis() + " ms. " + SHOW_STATE_SPACE);
				listener.linkWhenReady(SHOW_STATE_SPACE, showStateSpace, STATE_SPACE_VIEW_ID);
			}
			break;
		case ProcessAlgebraModelChangedEvent.CTMC_SOLVED:
			if (event.getProcessAlgebraModel().getStateSpace() != null && event.getProcessAlgebraModel().getStateSpace().isSolutionAvailable()
					&& event.getException() == null) {
				notify(MODEL_SOLVED + " Elapsed time: "
						+ event.getElapsedTimeMillis() + " ms. " + SHOW_RESULTS);
				listener.linkWhenReady(SHOW_RESULTS, showStateSpace, STATE_SPACE_VIEW_ID);
			}
			break;
		case ProcessAlgebraModelChangedEvent.TIME_SERIES_ANALYSED:
			if(((IPepaModel) event.getProcessAlgebraModel()).getTimeSeries() != null && event.getException() == null) {
				notify (MODEL_ANALYSED + "Elapsed time:" + event.getElapsedTimeMillis() + " ms. " + SHOW_RESULTS);
				listener.linkWhenReady(SHOW_RESULTS, showGraphs, null);
			}
			break;
		case ProcessAlgebraModelChangedEvent.KRONECKER_DERIVED:
			if (event.getException() != null) {
				notify("Warning: " + event.getException().getMessage());
			} else if (((IPepaModel) event.getProcessAlgebraModel()).getDisplayModel() != null)
				notify(KRONECKER_DERIVED + " Elapsed time: " + event.getElapsedTimeMillis() + " ms. ");
			break;
		case ProcessAlgebraModelChangedEvent.MODEL_CHECKED:
			if (event.getException() == null) {
				notify("Property \"" + event.getInformation() + "\" was checked in " + event.getElapsedTimeMillis() + " ms. ");
			} else {
				notify("An error occurred: " + event.getException().getMessage());
			}
			break;
		case ProcessAlgebraModelChangedEvent.MODEL_CHECKING_INFO:
			if (event.getException() == null)
				notify(MODEL_CHECKING_INFO + event.getInformation());
			break;
		case ProcessAlgebraModelChangedEvent.PSNI_CHECKED:
			if (event.getException() != null) {
				notify("Warning: " + event.getException().getMessage());
			} else {
				notify(PSNI_CHECKED + " Elapsed time: " + event.getElapsedTimeMillis() + " ms. "
						+ (event.getInformation() == "" ? "" : "\n"+event.getInformation()));
			}
			break;
		default:
			break;
		}

	}

	public void append(String message) {
		stream.println(this.dateFormat.format(new Date()) + " [" + modelName + "] " + message);
	}

	/*
	 * Refactored out of the PEPA CTMC listener
	 */
	private void notify(String basicMessage) {
		append(basicMessage);
	}
	
	private class ConsoleLinker implements IDocumentListener {
		
		String string, pageID;
		IHyperlink linkToAdd = null;
		
		public void documentAboutToBeChanged(DocumentEvent event) {}

		public void documentChanged(DocumentEvent event) {
			if(linkToAdd != null) {
				try {
					stream.getConsole().addHyperlink(linkToAdd, console.get().lastIndexOf(string), string.length());
				} catch (BadLocationException e) {
					return;
				} finally {
					linkToAdd = null;
				}
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if(pageID != null && !page.isPartVisible(page.findView(pageID)) && page.getPerspective().getId().equals(PerspectiveFactory.PERSPECTIVE_ID))
					ConsolePlugin.getDefault().getConsoleManager().showConsoleView(stream.getConsole());
			}
		}
		
		public void linkWhenReady(String string, IHyperlink link, String pageID) {
			this.string = string;
			linkToAdd = link;
			this.pageID = pageID;
		}
		
	}

}
