/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.IOptionHandler;
import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoOptionForwarder;

public class OptionHandler implements IOptionHandler {

	private OptionMap fCurrentOptionMap;

	private IResource fResource;

	public OptionHandler(IResource resource) {
		Assert.isNotNull(resource);
		fResource = resource;
		initOptionMap();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.core.internal.IOptionHandler#getResource()
	 */
	public IResource getResource() {
		return fResource;
	}

	private void initOptionMap() {
		try {
			fCurrentOptionMap = PepatoOptionForwarder
					.getOptionMapFromPersistentResource(fResource);
		} catch (CoreException e) {
			PepaCore.getDefault().getLog().log(e.getStatus());
			fCurrentOptionMap = new OptionMap();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.core.internal.IOptionHandler#getOptionMap()
	 */
	public OptionMap getOptionMap() {
		// a copy of the current option map
		return new OptionMap(this.fCurrentOptionMap);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.core.internal.IOptionHandler#setOptionMap(uk.ac.ed.inf.pepa.ctmc.solution.OptionMap)
	 */
	public void setOptionMap(OptionMap map) {
		if (map == null) {
			initOptionMap();
		} else {
			this.fCurrentOptionMap = new OptionMap(map);
		}
		// save persistent resources
		try {

			for (Object key : this.fCurrentOptionMap.keySet())
				PepatoOptionForwarder.saveOptionInPersistentResource(fResource,
						(String) key, fCurrentOptionMap.get(key));
		} catch (CoreException e) {
			PepaCore.getDefault().getLog().log(e.getStatus());
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.core.internal.IOptionHandler#getOption(java.lang.String)
	 */
	public Object getOption(String key) {
		return fCurrentOptionMap.get(key);
	}

}
