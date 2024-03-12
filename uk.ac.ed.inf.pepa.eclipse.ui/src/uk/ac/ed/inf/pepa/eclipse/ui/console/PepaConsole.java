/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.console;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.*;

import uk.ac.ed.inf.pepa.eclipse.core.*;
import uk.ac.ed.inf.pepa.eclipse.ui.PerspectiveFactory;

/**
 * Lazily initialised singleton which creates a console for the Eclipse UI
 * plugin.
 * 
 * @author mtribast
 * 
 */
public class PepaConsole {

	private static final PepaConsole SINGLETON = new PepaConsole();

	private HashMap<IPepaModel, ConsoleWriter> map = new HashMap<IPepaModel, ConsoleWriter>();

	public static PepaConsole getDefault() {
		return SINGLETON;
	}

	private PepaConsole() {
		MessageConsole console = new MessageConsole("PEPA", null);
		final MessageConsoleStream stream = console.newMessageStream();
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		manager.addConsoles(new IConsole[] { console });
		// if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().equals(PerspectiveFactory.PERSPECTIVE_ID))
			// manager.showConsoleView(console);
		PepaCore.getDefault().getPepaManager().addListener(new IProcessAlgebraManagerListener() {

			public void modelAdded(IProcessAlgebraModel model) {
				ConsoleWriter writer = new ConsoleWriter(stream, (IPepaModel) model);
				model.addModelChangedListener(writer);
				map.put((IPepaModel) model, writer);
			}

			public void modelRemoved(IProcessAlgebraModel model) {
				ConsoleWriter writer = map.get(model);
				Assert.isNotNull(writer);
				model.removeModelChangedListener(writer);
				map.remove(model);
			}
		});
	}
	
	/**
	 * Return the console writer for the given PEPA model or
	 * <code>null</code> if no writer exists
	 * 
	 * @param model the model for which the console writer is request
	 * @return the console writer, or <code>null</code>
	 */
	public IPepaConsoleWriter getPepaConsoleWriter(IPepaModel model) {
		Assert.isNotNull(model);
		return map.get(model);
	}
}
