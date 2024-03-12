/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.ui.console.PepaConsole;
import uk.ac.ed.inf.pepa.eclipse.ui.internal.FilterManagerProvider;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator extends AbstractUIPlugin {
	
	/**
	 * Eclipse-level unique ID for this plugin 
	 */
	public static final String ID = "uk.ac.ed.inf.pepa.eclipse.ui";
	
	//The shared instance.
	private static Activator plugin;
	
	private PepaConsole pepaConsole;
	
	private FilterManagerProvider fFilterManagerProvider;
	
	
	/**
	 * The constructor.
	 */
	public Activator() {
		plugin = this;
	}
	
	/**
	 * Returns the filter manager provider.
	 * @return the filter manager provider. 
	 */
	public IFilterManagerProvider getFilterManagerProvider() {
		return fFilterManagerProvider;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		/* Activates the console */
		pepaConsole = PepaConsole.getDefault();
		fFilterManagerProvider = new FilterManagerProvider(PepaCore.getDefault().getPepaManager());
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		fFilterManagerProvider.shutdown();
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("uk.ac.ed.inf.pepa.eclipse.ui", path);
	}
}
