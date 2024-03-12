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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PepaCore extends AbstractUIPlugin {
	
	/**
	 * Eclipse-level unique ID for this plugin 
	 */
	public static final String ID = "uk.ac.ed.inf.pepa.eclipse.core";
	
	/**
	 * Contains file extensions for PEPA models
	 */
	public static final List<String> SUPPORTED_EXTENSIONS;
	
	/**
	 * File extension for text-based model description 
	 */
	public static final String PEPA_EXTENSION = "pepa";
	
	/**
	 * File extension for XMI-based model description
	 */
	public static final String EMFPEPA_EXTENSION = "emfpepa";
	
	static {
		SUPPORTED_EXTENSIONS = new ArrayList<String>();
		SUPPORTED_EXTENSIONS.add(PEPA_EXTENSION);
		SUPPORTED_EXTENSIONS.add(EMFPEPA_EXTENSION);
	}
	
	//The shared instance.
	private static PepaCore plugin;
	
	private PepaManager fPepaManager = new PepaManager();
	
	/**
	 * The constructor.
	 */
	public PepaCore() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		fPepaManager.shutdown();
		plugin = null;
	}
	
	public PepaManager getPepaManager() {
		return fPepaManager;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static PepaCore getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("uk.ac.ed.inf.pepa.eclipse.core", path);
	}
	
	
}
