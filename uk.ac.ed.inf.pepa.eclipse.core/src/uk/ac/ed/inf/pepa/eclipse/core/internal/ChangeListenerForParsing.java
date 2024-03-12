/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;

public class ChangeListenerForParsing implements IResourceChangeListener {

	private IPepaModel model;

	private boolean ignoreChange = false;

	public ChangeListenerForParsing(IPepaModel model) {
		this.model = model;
	}

	public boolean isIgnoreChange() {
		return ignoreChange;
	}

	public void setIgnoreChange(boolean ignoreChange) {
		this.ignoreChange = ignoreChange;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {

				public boolean visit(IResourceDelta delta) {
					//only interested in this model's resource
					if (!delta.getResource().equals(model.getUnderlyingResource()))
						return true;
					// not interested in removed nor added
					if (delta.getKind() != IResourceDelta.CHANGED)
						return true;
					// only interested in change
					if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
						return true;
					try {
						model.parse();
					} catch (CoreException e) {
						PepaLog.logError(e);
					}
					return true;
				}

			});
		} catch (CoreException e1) {
			PepaLog.logError(e1);
		}
	}

}
