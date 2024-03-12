/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import uk.ac.ed.inf.pepa.eclipse.core.internal.EmfPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.internal.PepaModel;

/**
 * This class manages instances of <code>IPepaModel</code>. It creates models
 * by listening to resource changes in the workspace.
 * <p>
 * This class is shared in the workbench. Exactly one instance is created during
 * an Eclipse session.
 * 
 * @author mtribast
 * 
 */
public final class PepaManager extends ProcessAlgebraManager {

	/*
	 * Contains IResource to IPepaModel association for the resoures which are
	 * currently open in the workbench. 
	 */
	private HashMap<IResource, IPepaModel> map = new HashMap<IResource, IPepaModel>();
	
	public boolean isValidPepaFile(IResource resource) {
		
		if (resource == null)
			return false;
		
		boolean result = (resource.getType() == IResource.FILE)
				&& PepaCore.SUPPORTED_EXTENSIONS.contains(resource
						.getFileExtension());
		return result;

	}

	/**
	 * Get the PEPA model associated to this resource
	 * 
	 * @param resource
	 *            the resource describing the model
	 * @return the model or null if no association is found
	 */
	public IPepaModel getModel(IResource resource) {
		if (map.containsKey(resource))
			return map.get(resource);
		else if (isValidPepaFile(resource)) {
			/* create model on the fly (lazy) */
			return lazyCreateModel(resource);
		} else
			return null;
	}

	/**
	 * This method is called by <code>PepaCore</code> when the plug-in is
	 * stopped.
	 * 
	 * @see PepaCore#stop(org.osgi.framework.BundleContext)
	 * 
	 */
	public void shutdown() {
		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceListener);
	}

	private IPepaModel lazyCreateModel(IResource resource) {

		if (!resource.exists())
			return null;

		IPepaModel model = null;
		try {
			/* validity of the file has been tested already */

			if (resource.getFileExtension().equals(PepaCore.EMFPEPA_EXTENSION))
				model = new EmfPepaModel(resource);
			else
				model = new PepaModel(resource);
			
			map.put(resource, model);
			
			notifyAdded(model);
			
			model.parse();
			
		
		} catch (Exception e) {
			PepaLog.logError(e);
		}
		return model;
	}

	private void resourceRemoved(IResource resource) {
		IPepaModel model = map.get(resource);
		
		notifyRemoved(model);
		
		model.dispose();
		map.remove(resource);	
	}

	public PepaManager() {
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceListener,
				IResourceChangeEvent.PRE_BUILD
						| IResourceChangeEvent.PRE_CLOSE);
	}
	
	/**
	 * This listener is responsible for keeping the in-memory PEPA model
	 * representation in sync with the underlying resource by parsing the
	 * model when the resource is changed.
	 * <p>
	 * It also fires notification of resources with PEPA model which are
	 * about to be deleted. 
	 * <p>
	 * New resources are added lazily, via {@link #getModel(IResource)} 
	 * 
	 */
	private IResourceChangeListener resourceListener = new IResourceChangeListener() {

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				IResourceDelta resourceDelta = event.getDelta();
				if (resourceDelta == null)
					return;

				resourceDelta.accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta)
							throws CoreException {
						if (!isValidPepaFile(delta.getResource())) {
							return true;
						}
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							break;
						case IResourceDelta.REMOVED:
							resourceRemoved(delta.getResource());
							break;
						case IResourceDelta.CONTENT:
						case IResourceDelta.CHANGED:
							// only interested in content change (not markers)
							if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
								break;
							if (map.containsKey(delta.getResource()))
								map.get(delta.getResource()).parse();
							break;
						default:
							break;
						}
						return true;
					}
				});
			
			} catch (CoreException e) {
				PepaLog.logError(e);
			}
		}
	};

}
