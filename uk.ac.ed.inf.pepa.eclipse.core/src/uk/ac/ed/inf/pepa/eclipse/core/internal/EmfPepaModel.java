/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.emf.util.EmfTools;
import uk.ac.ed.inf.pepa.tools.PepaTools;

/**
 * Internal model for PEPA descriptions coming from EMF. The actual
 * parsing only is modified.
 * 
 * @author mtribast
 *
 */
public class EmfPepaModel extends PepaModel {

	public EmfPepaModel(IResource resource) {
		super(resource);
	}
	
	protected boolean doParse() throws CoreException {
		
		try {
			this.fAstModel = EmfTools.convertToAST(EmfTools.deserialise((IFile) getUnderlyingResource()));
		} catch (Throwable t) {
			throw new CoreException(new Status(IStatus.ERROR, PepaCore.ID,
					IStatus.OK, "Conversion problem", t));
		}
		PepaTools.doStaticAnalysis(this.fAstModel);

		return true;
	}

}
