/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * This class manages image retrieval for the PEPA Eclipse plug-in
 * 
 * @author mtribast
 * 
 */
public class ImageManager {

	private static ImageManager manager = null;

	
	/* Plug-in relative icon path */
	private static final String ICON_PATH = "icons/";

	/** Image for retrieval errors */
	// private static final String ERROR_RETRIEVAL = "error_ov.gif";
	
	
	public static final String DERIVE = "derive.gif";

	public static final String ERROR = "error.gif";

	public static final String EXPORT = "export_wiz.gif";

	/** Image representing filtering image */
	public static final String FILTER = "filter_ps.gif";

	public static final String REFRESH = "refresh.gif";
	
	public static final String RUN = "run_exc.gif";
	
	/** Image representing ascending ordering of table-based views */
	public static final String TABLE_UP = "order_point.gif";

	/** Image representing descending ordering of table based views */
	public static final String TABLE_DOWN = "order_point.gif";
	
	/** Standard Eclipse Icon for synchronising resources */
	public static final String SYNC = "synced.gif";
	
	/** Expand-all option for tree viewers */
	public static final String EXPAND_ALL = "expandall.gif";
	
	/** Collapse-all option for tree viewers */
	public static final String COLLAPSE_ALL = "collapseall.gif";
	
	public static final String CHART = "chart.gif";
	
	public static final String WARNING_SMALL = "warning_small.gif";
	
	public static final String WARNING = "warning.gif";
	
	public static final String COOP = "sync.gif";
	
	public static final String CLEAR = "clear.gif";
	
	public static final String IMPORT = "import.gif";
	
	/** Font size in the abstraction view */
	public static final String ZOOM_IN = "zoom-in.gif";
	public static final String ZOOM_OUT = "zoom-out.gif";
	
	private ImageRegistry registry;

	private ImageManager() {
		this.registry = Activator.getDefault().getImageRegistry();
	}

	public static ImageManager getInstance() {
		if (manager == null)
			manager = new ImageManager();
		return manager;
	}

	public Image getImage(String imageString) {
		return getImageDescriptor(imageString).createImage();
	}
	
	public ImageDescriptor getImageDescriptor(String imageString) {
		ImageDescriptor descriptor = registry.getDescriptor(imageString);
		if (descriptor == null) {
			descriptor = Activator.getImageDescriptor(ICON_PATH + imageString);
			registry.put(imageString, descriptor);
		}
		return descriptor;
		
			
	}
	
	public void dispose() {
		registry.dispose();
	}
}
